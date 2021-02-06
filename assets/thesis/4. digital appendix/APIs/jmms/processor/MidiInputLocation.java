/*
 * Created on 18/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import java.io.Serializable;

import ren.util.PO;
import ren.util.RMath;
import ren.util.Save;

/**
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MidiInputLocation extends ActuatorCommander implements MidiLocation, Serializable {

	//private DefaultBoundedRangeModel chan = new DefaultBoundedRangeModel(1, 16, 1, 16);
	//private DefaultBoundedRangeModel ctrlType = new DefaultBoundedRangeModel(0, 127, 1, 127);
//	private int chan, ctrlType;
	private int channel, ctrlType;
	
	public static final String ssn = "mil";
	
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		
		this.channel = RMath.boundHard(channel, 0, 15);
		
	}
	public int getCtrlType() {
		return ctrlType;
	}
	public void setCtrlType(int ctrlType) {
		this.ctrlType = RMath.boundHard(ctrlType, 0, 127);
	}
	private String name;
	
	/**
	 * 
	 */
	public MidiInputLocation() {
		super();
	}
	
	public MidiInputLocation(String name) {
		super(name);
	}
	/*

	public void setMidiController(int chan, int ctrlType) {
		this.chan = chan;//.setValue(chan);
		this.ctrlType = ctrlType; //.setValue(ctrlType); 
	}*/
	
	/*
	public BoundedRangeModel getChannelModel() {
		return chan;
	}
	
	public BoundedRangeModel getCtrlTypeModel() {
		return ctrlType;
	}
	*/
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		if(name == null)
			return new String(channel + " : " + ctrlType);
		return "ch " + channel + " | " + " ctrl " + ctrlType + " : " + name;
	}
	
	public void midiCtrlRecieved(int channel, int type, int value, int time) {
		this.fireActuators(new ActuatorEvent(0, 127, value, time));
	}
	/* (non-Javadoc)
	 * @see jmms.processor.MidiLocation#setMidiController(int, int)
	 */
	public void setMidiController(int chan, int ctrlType) {
		
		this.setChannel(chan);
		this.setCtrlType(ctrlType);
		
	//	PO.p("setting " + chan + " , " + ctrlType + " out = " + this.toString());
	}
	
	/* (non-Javadoc)
	 * @see jmms.processor.MidiLocation#isLearning()
	 *
	private boolean learning = false;
	public boolean isLearning() {
		return learning;
	}
	/* (non-Javadoc)
	 * @see jmms.processor.MidiLocation#setLearning(boolean)
	 *
	public void setLearning(boolean l) {
		learning = l;
	}
	*/
	
	
//	public int getChannel() {return this.chan;}
//	public int getCtrlType() {return this.ctrlType;}
	
	/**
	 * when being loaded, the channel and type needs
	 * to be extracted first, and then
	 */
	public static int[] readChanType(String str) {
	    int [] ret = new int [2];
	    ret[0] = Save.getIntXML(str, "c");
	    ret[1] = Save.getIntXML(str, "t");
	    return ret;
	}
	
	public void loadString(String s) {
	    //super.loadString(s.substring(s.indexOf()
	    super.loadString(s);
	}
	
	public String saveString() {
	    StringBuffer s = new StringBuffer(Save.st(ssn) +
	        "<c>" + this.getChannel() + "</c>" + 
	        "<t>" + this.getCtrlType() + "</t>");
	    
	    //  actuator commander information
	    s.append(super.saveString());
	    
	    s.append(Save.et(ssn));
	    return s.toString();
	}
	
	
}
