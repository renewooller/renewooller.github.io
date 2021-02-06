/*
 * Created on 18/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import ren.lang.Namable;

/**
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public interface ActuatorContainer extends Namable {
	
    public static final String sn = "actuatorContainer";
    
	public ActuatorContainer [] getSubActuatorContainers();
	
	public Actuator [] getActuators();
	
	public int getActuatorContainerCount();
	
	public int getIndexOfSubContainer(ActuatorContainer sub);

}
