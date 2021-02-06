/*
 * Created on 19/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import java.io.Serializable;

import ren.util.Save;

/**
 * @author wooller
 *
 *19/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class ActuatorPath implements Serializable {

	private ActuatorContainer [] path;
	private Actuator actuator;
	
	//save name
	public static final String sn = "actPath";
	public static final String psn = "path";
	
	/**
	 * 
	 */
	public ActuatorPath() {
		super();
	}
	
	public void setPath(ActuatorContainer [] path, Actuator a) {
		this.path = path;
		this.actuator = a;
	}
	
	public boolean isValid() {
		for(int i=0; i<path.length-1; i++) {
			if(!hasSubActuatorContainer(path[i], path[i+1])) {
				return false;
			}
		}
		if(hasActuator(path[path.length-1], actuator))
			return true;
		else
			return false;
	}
	
	private transient ActuatorContainer [] aarr;
	private boolean hasSubActuatorContainer(ActuatorContainer a, ActuatorContainer b) {
		aarr = a.getSubActuatorContainers();
		boolean has = false;
		for(int i=0; i<aarr.length; i++) {
			if(aarr[i] == b)
				has = true;
		}
		return has;
	}
	
	private boolean hasActuator(ActuatorContainer a, Actuator b) {
		Actuator [] aca = a.getActuators();
		boolean has = false;
		for(int i=0; i<aca.length; i++) {
			if(aca[i] == b)
				has = true;
		}
		return has;
	}
	
	public Actuator getActuator() {
		return actuator;
	}
	
	public ActuatorContainer [] getPath() {
		return path;
	}
	
	/*// not a 'good' approach (doesn't work with vectors as I thought)
	public boolean equals(Object o) {
		System.out.println("equals");
		if(o instanceof Actuator) {
			System.out.println("actuator equals");
			if(this.getActuator() == ((Actuator)o))
				return true;
			else 
				return false;
		} else if(o instanceof ActuatorPath) {
			if(((ActuatorPath)o).getPath() == this.getPath() &&
			   ((ActuatorPath)o).getActuator() == this.getActuator()){
				return true;
			} else
				return false;
		} else {
			return false;
		}
		
	}*/

	public String toString() {
		StringBuffer sb = new StringBuffer(this.getActuator().toString() + " : ");
		for(int i=this.path.length-1; i>-1; i--) {
			sb.append(this.path[i].toString() + " : ");
		}
		return sb.toString();
	}
	
	public void loadString(String s) {
	    ActuatorContainerManager.acm.loadActuatorPath(this, s);
	    
	}
		
	public String saveString() {
	    StringBuffer sb = new StringBuffer();
	    sb.append(Save.st(sn));
	    
	    // the actuator
	    sb.append(Save.st(Actuator.sn));
	    sb.append(this.actuator.toString());
	    sb.append(Save.et(Actuator.sn));
	    
	    // the path
	    sb.append(Save.st(psn));
	    for(int i=0; i<path.length; i++) {
	        sb.append(Save.st(ActuatorContainer.sn));
	        sb.append(path[i].toString());
	        sb.append(Save.et(ActuatorContainer.sn));
	    }
	    sb.append(Save.et(psn));
	    
	    sb.append(Save.et(sn));
	    return sb.toString();
	}
	
}
