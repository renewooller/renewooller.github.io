/*
 * Created on 18/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import ren.lang.Namable;

/**
 * An actuator is something that is to be controlled by an actuator commander (Midi input)
 * 
 * 
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public interface Actuator extends Namable {

    public static final String sn = "actuator";
    
	public void commandRecieved(ActuatorEvent e);
	
}
