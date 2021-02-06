package jmms.processor;

import grame.midishare.Midi;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import jm.midi.MidiInputListener;
import jm.midi.event.CChange;
import jm.midi.event.Event;
import jmms.Sequencer;
import jmms.TickEvent;
import jmms.TickListener;
import ren.util.PO;
import ren.util.Save;

/*
 * Created on 17/01/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 * 
 * 17/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MidiProcessor implements MidiInputListener, TickListener,
		ActuatorContainer, Serializable {

	private static int num = 0;
	
	private Sequencer seq;

	//for obtaining a list of the MidiInputLocations, fast
	private Vector milVect = new Vector(160);

	//for processing midi input, fast
	private MidiInputLocation[][] milArr = new MidiInputLocation[16][128];

	private Vector processes = new Vector(160);

	/**
	 *  
	 */
	public MidiProcessor() {
		super();
		num++;
		PO.p("num of midi processors = " + num);
	}

	public MidiInputLocation createMidiInputLocation() {
		MidiInputLocation mill = null;
		// find empty slot
		int i, j = 0;
		for (i = 0; i < 16; i++) {
			for (j = 0; j < 128; j++) {
				if (milArr[i][j] == null) {
				//	PO.p(" null " + i + " + j " + j);
					mill = addMidiInputLocation(i, j); // adds it into the array
					break;
				}
			}
			if (mill != null) {
				break;
			}
		}
/*
		PO.p("midi input created " + mill.toString());
		for(i=0; i< 16; i++) {
			for(j = 0; j< 128; j++) {
				if(( milArr[i][j] != null))
					PO.p("i = " + i + " j = " + j + " null = " +( milArr[i][j] == null));
			}
		}*/
		
		return mill;
	}

	public void addMidiInputLocation(MidiInputLocation mil) {
		resolveCollision(mil); // adds it to the array in the process
		this.milVect.add(mil);

	}

	public MidiInputLocation addMidiInputLocation(int chan, int ctrlType) {
		MidiInputLocation mil = new MidiInputLocation();
	//	PO.p("mil 1 " + mil.toString());
	//	PO.p("chan = " + chan + " type = "  +ctrlType);
		mil.setMidiController(chan, ctrlType);
	//	PO.p("mil 2 " + mil.toString());
		this.addMidiInputLocation(mil);
	//	PO.p("mil 3 " + mil.toString());
		return mil;
	}

	public void removeMidiInputLocation(MidiInputLocation mil) {
		this.milVect.remove(mil);
		milArr[mil.getChannel()][mil.getCtrlType()] = null;
	}
	
	public void removeAllMils() {
	    while(this.milVect.size() > 0) {
	        removeMidiInputLocation((MidiInputLocation)milVect.get(milVect.size()-1));
	    }
	}
	
	public void removeAllProcs() {
	    this.processes.removeAllElements();
	}

	public void setMidiInputLocation(int chanFrom, int ctrlFrom, int chanTo,
			int ctrlTo) {
		
		
		if(chanFrom == chanTo && ctrlFrom == ctrlTo)
			return;
		/*
		for(int i=0; i< 16; i++) {
			for(int j = 0; j< 128; j++) {
				PO.p("i = " + i + " j = " + j + " null = " +( milArr[i][j] == null));
			}
		}*/
		
		MidiInputLocation mi = milArr[chanFrom][ctrlFrom];
		
		milArr[chanFrom][ctrlFrom] = null;
		
		if(mi == null) {
			PO.p("problem, mi is null from = " + chanFrom + " ctrlFrom " + 
					ctrlFrom + " chanTo = " + chanTo + " ctrlTo " + ctrlTo);
		}
		
		mi.setMidiController(chanTo, ctrlTo);
		milArr[chanTo][ctrlTo] = mi;
		
		/*
		//MidiInputLocation mill;
		if(milArr[chanFrom][ctrlFrom] == null) {
			System.out.println("from = "+ chanFrom + " ctrlFrom "+ctrlFrom);
		}
		milArr[chanFrom][ctrlFrom].setMidiController(chanTo, ctrlTo);
		resolveCollision(milArr[chanFrom][ctrlFrom]);
		//milArr[chanFrom][chanFrom] = null;
		 * 
		 */
	}

	public Vector getMidiInputLocations() {
		return this.milVect;
	}

	public void setMidiInputLocations(Vector v) {
		this.milVect = v;
		Enumeration enumr = milVect.elements();
		while(enumr.hasMoreElements()) {
			resolveCollision((MidiInputLocation)enumr.nextElement());
		}
	}
	
	/**
	 * resolves any collision by removing the occupying mil, and then moving the
	 * new mil into it merge may be implemented in the future
	 * 
	 * @param mil
	 */
	private void resolveCollision(MidiInputLocation mil) {
		if (milArr[mil.getChannel()][mil.getCtrlType()] != null) {
			this.milVect.remove(milArr[mil.getChannel()][mil.getCtrlType()]);
		} 
		milArr[mil.getChannel()][mil.getCtrlType()] = mil;
		
	}

	public void setSequencer(Sequencer seq) {
		this.seq = seq;
	}
	
	public Sequencer getSequencer() {
		return this.seq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jm.midi.MidiInputListener#newEvent(jm.midi.event.Event)
	 */
	private CChange tcc;

	public void newEvent(Event event) {
		if (event instanceof CChange) {
			tcc = (CChange) event;
			if (!isLearning()) { // normal - not learning
				if(milArr[tcc.getMidiChannel()][tcc.getControllerNum()] != null) {
					milArr[tcc.getMidiChannel()][tcc.getControllerNum()]
						.midiCtrlRecieved(tcc.getMidiChannel(), tcc
								.getControllerNum(), tcc.getValue(), tcc
								.getTime());
				}
			} else { // learning
				if(learnLoc instanceof MidiInputLocation) {
					this.setMidiInputLocation(learnLoc.getChannel(), learnLoc.getCtrlType(),
							tcc.getMidiChannel(), tcc.getControllerNum());
				} else if(learnLoc instanceof MidiOutputLocation) {
					learnLoc.setMidiController(tcc.getMidiChannel(), tcc.getControllerNum());
				}
			}
		}
	}

	private MidiLocation learnLoc = null;

	public void setLearning(MidiLocation ll) {
		learnLoc = ll;
	}

	public boolean isLearning() {
		return (learnLoc != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmms.TickListener#tick(jmms.TickEvent)
	 */
	
	public void tick(TickEvent e) {
		
		for (int i = 0; i < processes.size(); i++) {
			((Process)processes.get(i)).process(Midi.GetTime());//(int)e.at());
		}
	}
	
	private static final Process[] procArr = new Process[0];
	public Process[] getProcessArray() {
		return (Process[]) processes.toArray(procArr);
	}

	public Vector getProcessVector() {
		return processes;
	}
	
	public void setProcessVector(Vector to) {
		this.processes = to;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see jmms.processor.ActuatorContainer#getSubActuatorContainers()
	 */
	public ActuatorContainer[] getSubActuatorContainers() {
		return getProcessArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmms.processor.ActuatorContainer#getActuators()
	 */
	public Actuator[] getActuators() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmms.processor.ActuatorContainer#getActuatorCount()
	 */
	public int getActuatorContainerCount() {
		return processes.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmms.processor.ActuatorContainer#getIndexOfSubContainer(jmms.processor.ActuatorContainer)
	 */
	public int getIndexOfSubContainer(ActuatorContainer sub) {
		return processes.indexOf(sub);
	}

	private String name = "MIDI Processor";

	/*
	 * (non-Javadoc)
	 * 
	 * @see ren.lang.Namable#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ren.lang.Namable#getName()
	 */
	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}
	
	public void addProcess(Process toAdd) {
		this.processes.add(toAdd);
	}
	
	public void removeProcess(Process toRemove) {
		this.processes.remove(toRemove);
	}

	public String saveString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(Save.st(ssn));
	    
	    //midi input locations
	    sb.append("<milocs>");
	    // save the midi input locations
	    for(int i=0; i< milVect.size(); i++) {
	        // this will have the <mil> tag in it
	        sb.append(((MidiInputLocation)milVect.get(i)).saveString());
	       
	    }
	    sb.append("</milocs>");
	    
	    //processes
	    sb.append(Save.st("procs"));
	    for(int i=0; i< processes.size(); i++) {
	        sb.append(((Process)processes.get(i)).saveString());
	    }
	    
	    sb.append(Save.et("procs"));
	    
	    sb.append(Save.et(ssn));
	    return sb.toString();
	}
	
	public void loadString(String toLoad) {
	    //reset the list
	    this.removeAllMils();
	    this.removeAllProcs();
	   
	    //trim the name off
	    int [] loc = Save.getSubPosXML(toLoad, ssn);
	    toLoad = toLoad.substring(loc[0], loc[1]);
	     
	    // get the substring with the list of processes
	    String str = Save.getStringXML(toLoad, "procs");
	   	      
	    //	  get position of first process
	    loc = Save.getSubPosXML(str, Process.ssn);
	 
	    // go through and make all the processes
	    while(loc[0] != -1) {
	        String ts = str.substring(loc[0], loc[1]);
	
	        Process p = ProcessFactory.create(
	            Save.getStringXML(ts,"name"));
	        p.loadString(ts);
	        
	        this.addProcess(p);
	        
	        str = str.substring(loc[1]);
	        
	        // get the location of the next process
	        loc = Save.getSubPosXML(str, Process.ssn);
	    
	    }
	    	 
	    //get substring with the list of mils
	    str =  Save.getStringXML(toLoad, "milocs");
	
	    // get the first one
	    loc = Save.getSubPosXML(str, MidiInputLocation.ssn);
	
	    // go through and make all the mils   
	    while(loc[0] != -1) {
	        String ts = str.substring(loc[0], loc[1]);
	        int [] var = MidiInputLocation.readChanType(ts);
	
	        // load other aspect of the midi location(AtuatorCommander)
	        this.addMidiInputLocation(var[0], var[1]).loadString(ts);
	       
	        //get the location of the next one
	        str = str.substring(loc[1]);
	        loc = Save.getSubPosXML(str, MidiInputLocation.ssn);
	
	    }
	    
	}
	
	public static final String ssn = "mproc";
}