/*
 * Created on 13/01/2005
 *
 * @author Rene Wooller
 */
package music;

import java.io.Serializable;

import jmms.TickEvent;
import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 *13/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MorphTracker implements Serializable{

    int at = 0;

	private double ures = 0.5;
	
	boolean backwards = false;
	
	boolean updateOn = true;
	
	ParameterMap morphLength;
	
	public MorphTracker() {}
	
	/**
	 * TODO make a listener so as to update the component when it moves
	 */
	public MorphTracker construct(ParameterMap ml, double at) {
		//super();
	    this.morphLength = ml;
	    setPos(at);
		return this;
	}

	public double getPos() {
		return (at*ures)/morphLength.getValue();
	}
	
	public double getBeatPos() {
	    if(backwards)
	        return (morphLength.getValue() - at*ures);
	    else
	        return at*ures;
	}
	
	public double getUpdateRes() {
	    return ures;
	}
	
	public void setPos(double n) {
	    if(n < 0 || n > 1)
	        return;
	    
		at = (int)((n*morphLength.getValue())/ures);
	}
	
	public void setUpdateResolution(double ures) {
	    this.ures = ures;
	}
	
	public void setUpdating(boolean updateOn) {
	    this.updateOn = updateOn;
	}
	
	public boolean isUpdating() {
	    return this.updateOn;
	}
	
	/**
	 * shifts the morph position along in the right 
	 * direction.
	 * 
	 */
	public void update(TickEvent e){
	    // if updating has been disabled, or
	    // the current time isn't due for updating don't update
	    if(!this.updateOn || 
	            e.at()%ures != 0)
	        return;
	    
	    if (!backwards) {
            at = at+1;
        } else {
            at = at-1;
        }
	}
	
	public void initBackwards() {
	    this.backwards = true;
	    this.setPos(1.0);
	}
	
	public void initForwards() {
	    this.backwards = false;
	    this.setPos(0.0);
	}

	public boolean isBackwards() {
		return this.backwards;
	}
	
	public void setBackwards(boolean nb) {
		this.backwards = nb;
	}
	
}
