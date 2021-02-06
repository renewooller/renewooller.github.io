/*
 * Created on 18/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import java.io.Serializable;
import java.util.Vector;

import jmms.Sequencer;
import ren.util.Save;

/**
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MidiOutputManager implements ActuatorContainer, Serializable {

	private Vector midiOutputLocations = new Vector(160);
	
	private Sequencer seq;
	
	/**
	 * 
	 */
	public MidiOutputManager() {
		super();
	}

	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getSubActuatorContainers()
	 */
	public ActuatorContainer[] getSubActuatorContainers() {
		return null;
	}

	public MidiOutputLocation createMidiOutputLocation(int chan, int type, String name) {
		MidiOutputLocation toRet = new MidiOutputLocation();
		toRet.setMidiController(chan, type);
		toRet.setName(name);
		toRet.setMidiOutputManager(this);
		midiOutputLocations.add(toRet);
		return toRet;
	}
	
	public void linkMidiOutputLocation(MidiOutputLocation mil) {
	    mil.setMidiOutputManager(this);
	    midiOutputLocations.add(mil);
	}
	
	public MidiOutputLocation createMidiOutputLocation() {
		return createMidiOutputLocation(0, 0, "ctrl out");
	}
	
	public Vector getMidiOutputLocation() {
		return this.midiOutputLocations;
	}
	
	public void setMidiOutputLocations(Vector v) {
		this.midiOutputLocations = v;
	}
	
	public boolean remove(MidiOutputLocation mol) {
		if(mol != null) {
			this.midiOutputLocations.remove(mol);
			return true;
		} 
		return false;
	}
	
	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getActuators()
	 */
	public Actuator[] getActuators() {
	    Actuator [] aa = new Actuator [0];
		return (Actuator [])midiOutputLocations.toArray(aa);
	}
	
	public void setSequencer(Sequencer seq) {
		this.seq = seq;
	}
	
	public Sequencer getSequencer() {
		return seq;
	}

	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getActuatorCount()
	 */
	public int getActuatorContainerCount() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getIndexOfSubContainer(jmms.processor.ActuatorContainer)
	 */
	public int getIndexOfSubContainer(ActuatorContainer sub) {
		return -1;
	}

	
	private String name = "MIDI Output Manager";
	/* (non-Javadoc)
	 * @see ren.lang.Namable#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see ren.lang.Namable#getName()
	 */
	public String getName() {
		return name;
	}
	
	public String toString() {
		return getName();
	}
	
	/**
	 * don't need to worry about
	 * @return
	 */
	public String saveString() {
	    StringBuffer sb = new StringBuffer(Save.st(ssn));
	    
	    for(int i=0; i<this.midiOutputLocations.size(); i++) {
	        sb.append(((MidiOutputLocation)midiOutputLocations.get(i)).saveString());
	    }
	    //don't need to worry about super.saveString because it
	    // would just be, like, getActuators, which is, like, the 
	    // same as the list of midi inputlocations anyway
	    
	    sb.append(Save.et(ssn));
	    return sb.toString();
	}
	
	/**
	 * doesn't matter if there is crap before it
	 * @param s
	 */
	public void loadString(String s) {
	    this.midiOutputLocations.removeAllElements();
	    
	    int [] loc = Save.getSubPosXML(s, MidiOutputLocation.ssn);
	    
	    while(loc[0] != -1) {
	        // make new mil
	        MidiOutputLocation mil = new MidiOutputLocation();
	        // initialise primitive variables
	        mil.loadString(s.substring(loc[0], loc[1]));
	        // initialise this into it, and put in array
	        this.linkMidiOutputLocation(mil);
	        // find the position of the next one
	        s = s.substring(loc[1]);
	        
	        loc = Save.getSubPosXML(s, MidiOutputLocation.ssn);
	    }
	    
	}
	
	public static final String ssn = "MOM";
	
} 




