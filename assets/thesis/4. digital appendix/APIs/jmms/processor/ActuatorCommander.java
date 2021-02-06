/*
 * Created on 18/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import grame.midishare.Midi;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import ren.util.PO;
import ren.util.Save;

/**
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class ActuatorCommander implements Serializable{

	private String name;
	
	//private ActuatorPath [] actuatorPaths = new ActuatorPath [50];
	//private int acount = 0;
	private Vector actuatorPaths = new Vector(50); 
	
	
	/**
	 * 
	 */
	public ActuatorCommander() {
		super();
	}
	
	public ActuatorCommander(String name) {
		super();
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private Iterator it;
	public void fireActuators(ActuatorEvent e) {
		//it = actuatorPaths.
		for(int i = 0; i< actuatorPaths.size(); i++) {
			ActuatorPath ap = ((ActuatorPath)actuatorPaths.get(i));
			
			//PO.p("firing actuator path" + ap.toString());
			
			ap.getActuator().commandRecieved(e);
		}
	}
	
	public void fireActuators(double min, double max, double value) {
		fireActuators(new ActuatorEvent(min, max, value, Midi.GetTime()));
	}
	
	public boolean addActuatorPath(ActuatorPath a) {
		if(a == null || this.contains(a.getActuator()))
			return false;
		actuatorPaths.add(a);
		PO.p("adding" + a.toString());
		return true;
	}
	
	private transient ActuatorPath tacp;
	public boolean addActuatorPath(ActuatorContainer [] acon, Actuator a) {
		if(acon == null || a == null)
			return false;
		
		if(this.contains(a))
			return false;
		tacp = new ActuatorPath();
		tacp.setPath(acon, a);
		
		this.actuatorPaths.add(tacp);
		return true;
	}
	
	public void addActuatorPath(ActuatorPath [] a) {
		for(int i = 0; i<a.length; i++) {
			addActuatorPath(a[i]);
		}
	}
	
	public void removeActuatorPath(ActuatorPath a) {
		this.actuatorPaths.remove(a);
	}
	
	public Vector getActuatorPathVector() {
		return this.actuatorPaths;
	}
	
	
	private transient ActuatorPath [] ac = new ActuatorPath [0];
	
	public ActuatorPath [] getActuatorPathArray() {
		ac = new ActuatorPath[0];
		return (ActuatorPath [])this.actuatorPaths.toArray(ac);
	}
	
	public void validateActuatorPaths() {
		int del = 0;
		ac = this.getActuatorPathArray();
		
		for(int i=0; i<ac.length; i++) {
			if(!ac[i].isValid()) {
				this.actuatorPaths.remove(ac[i]);
			}	
		}
	}
	
	/*
	private transient Actuator [] arr;
	public Actuator [] getActuatorArray() {
		arr = new Actuator [this.acount];
		for(int i=0; i< arr.length; i++) {
			arr[i] = this.actuatorPaths[i];
		}
		return arr;
	}*/
	
	public boolean contains(Actuator a) {
		if(a == null) {
			try{Exception e = new Exception("a is null");
				e.fillInStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
		
		ac = getActuatorPathArray();
	//	System.out.println(ac.length);
		for(int i=0; i< ac.length; i++) {
	
	//		System.out.println(ac[i].toString());
			
			if(ac[i].getActuator() == a) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		if(name == null) {
			return super.toString();
		}
		return name;
	}
	
	public String saveString() {
	    StringBuffer sb = new StringBuffer(stag);
	    //set name
	    sb.append(Save.st("name"));
	    sb.append(this.name);
	    sb.append(Save.et("name"));
	    
	    sb.append(Save.st("actPaths"));
	    for(int i = 0; i<this.actuatorPaths.size(); i++) {
	        sb.append(((ActuatorPath)this.actuatorPaths.get(i)).saveString());
	    }
	    sb.append(Save.et("actPaths"));
	    
	    sb.append(etag);
	    return sb.toString();
	}
	
	/**
	 * doesn't matter if there is crap in the beginning
	 * @param s
	 */
	public void loadString(String s) {
	    // find name and set it
	    int [] loc = Save.getSubPosXML(s, "name");
	    this.setName(s.substring(loc[0], loc[1]));
	    
	    // get the first acutator path
	    loc = Save.getSubPosXML(s, ActuatorPath.sn);
	    
//	    PO.p("loading the actuator paths --: " + s);
	    
	    //	  go through and add the actutor paths
	    while(loc[0] != -1) {
	        // make a new one and add it
	        ActuatorPath a = new ActuatorPath();
	        a.loadString(s.substring(loc[0], loc[1]));
	        this.addActuatorPath(a);
	        
	        s = s.substring(loc[1]);
	        
	        // find the position of the next one
	        loc = Save.getSubPosXML(s, 
					ActuatorPath.sn);
	    }
	  
	}
	public static final String sn = "AComm";
	
	public static final String stag = Save.st(sn);
	public static final String etag = Save.et(sn);
	
}
