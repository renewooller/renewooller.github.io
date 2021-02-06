package jmms.processor;

import grame.midishare.Midi;

import java.io.Serializable;

import ren.gui.ParamEvent;
import ren.gui.ParamListener;
import ren.gui.ParameterMap;
import ren.util.Err;
import ren.util.Save;

/*
 * Created on 17/01/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 *
 *17/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public abstract class Process implements ActuatorContainer, ParamListener, Serializable{

	protected ParameterMap [] inputs;
	protected ActuatorCommander [] outputs;
	
	public static final String ssn = "proc";
	
	/**
	 * uses all the inputs to fire the outputs
	 *
	 */
	public abstract void process(int time);
	
	public Actuator [] getActuators() {
		return inputs;
	}
	
	protected void initInputs() {
		for(int i=0; i<inputs.length; i++) {
			inputs[i].addParamListener(this);
		}
	}
	
	private static final int MIN_TIME_GAP = 5;
	private int prevTime = Midi.GetTime();//System.currentTimeMillis();
	private int currTime = Midi.GetTime();//System.currentTimeMillis();
	public void paramFired(ParamEvent e) {
		currTime = Midi.GetTime();//System.currentTimeMillis();
		if(currTime-prevTime > MIN_TIME_GAP) {
			process(currTime);
		}
	}
	
	public abstract ParameterMap [] getVariables();
	
	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getSubActuatorContainers()
	 */
	public ActuatorContainer[] getSubActuatorContainers() {
		return null;
	}
	
	public ActuatorCommander [] getOutputs() {
		return outputs;
	}
	
	public Actuator [] getInputs() {
		return getActuators();
	}
	
	public void loadString(String s) {
	    //check to see if it is the right one
	    if(!Save.getStringXML(s, "name").equalsIgnoreCase(this.getName())) {
	        Err.e("the values of process " + 
	            Save.getStringXML(s, "name") + 
	            " are wringly being loaded into " + this.getName());
	    }
		    
	    // get the substring for inputs
	    String is = Save.getStringXML(s, "inputs");
	    
	    // get the location of the first input
	    int [] loc = Save.getSubPosXML(is, ParameterMap.ssn);
	    	    
	    // load them
	    for(int i=0;i<this.inputs.length; i++) { 
	        loadInput(i, is.substring(loc[0], loc[1]));
	        
	        //move on
	        is = is.substring(loc[1]);
	        
	        // get location of the next parametermap
	        loc = Save.getSubPosXML(is, ParameterMap.ssn);
	    }
	    
	    // get the substring for the outputs
	    String os = Save.getStringXML(s, "outputs");
	    
	    // get the location of the first output
	    loc = Save.getSubPosXML(os, ActuatorCommander.sn);
	    
	    //load them
	    for(int i=0; i<this.outputs.length; i++) {
	        loadOutput(i, os.substring(loc[0], loc[1]));
	        
	        //move on
	        os = os.substring(loc[1]);
	        
	        // get location of next ActuatorCommander
	        loc = Save.getSubPosXML(os, ActuatorCommander.sn);
	        
	    }
	    
	}
	
	// this is separate, so that if there are any changes, it can be overwritten
	protected void loadInput(int i, String s) {
	    inputs[i].loadString(s);
	}
	
	protected void loadOutput(int i, String s) {
	    outputs[i].loadString(s);
	}
	
	public String saveString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(Save.st(ssn));
	    
	    // save the type/name
	    sb.append(Save.st("name"));
	    sb.append(this.getName());
	    sb.append(Save.et("name"));
	    
	    // save the inputs (ParamMaps) and their states
	    sb.append(Save.st("inputs"));
	    for(int i=0;i<this.inputs.length; i++) {
	        sb.append(inputs[i].saveString());
	    }
	    sb.append(Save.et("inputs"));
	    
	    // the actuator commanders are specified by the 
	    //processor type
	    sb.append(Save.st("outputs"));
	    for(int i=0;i<this.outputs.length; i++) {
	        sb.append(outputs[i].saveString());
	    }
	    sb.append(Save.et("outputs"));
	    	    
	    sb.append(Save.et(ssn));
	    return sb.toString();
	}
	
}
