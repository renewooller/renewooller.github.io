/*
 * Created on 18/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

import java.io.Serializable;


import ren.gui.ParameterMap;
import ren.gui.components.LJSlider;
import ren.util.RMath;

/**
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class ProcessFactory implements Serializable {

	private static final Process [] proc = new Process [] {
			new AbsoluteAcceleration(),
			new Integrate(),
			new Display()
	};
	
	/**
	 * 
	 */
	public ProcessFactory() {
		super();
	}
	
	public static Process create(String type) {
		for(int i=0; i< proc.length; i++) {
			if(proc[i].getName().equals(type)) {
				try{
					return (Process)(proc[i].getClass()).newInstance();
				} catch(Exception e) {e.printStackTrace();}
			}
		}
		return null;
	}
	
	public static String [] getTypes() {
		String [] toRet = new String [proc.length];
		for(int i=0; i<proc.length; i++) {
			toRet[i] = proc[i].getName();
		}
		return toRet;
	}

}

class Display extends Process {
    
    public Display() {
        this.inputs = new ParameterMap [6];
        for(int i=0; i<inputs.length; i++) {
            inputs[i] = (new ParameterMap()).construct(0, 127, 63, ("disp " + i));
        }
       
        initInputs();
        
    }

    /* (non-Javadoc)
     * @see jmms.processor.Process#process(int)
     */
    public void process(int time) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see jmms.processor.Process#getVariables()
     */
    public ParameterMap[] getVariables() {
        return null;
    }

    /* (non-Javadoc)
     * @see jmms.processor.ActuatorContainer#getActuatorContainerCount()
     */
    public int getActuatorContainerCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see jmms.processor.ActuatorContainer#getIndexOfSubContainer(jmms.processor.ActuatorContainer)
     */
    public int getIndexOfSubContainer(ActuatorContainer sub) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see ren.lang.Namable#setName(java.lang.String)
     */
    public void setName(String name) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see ren.lang.Namable#getName()
     */
    public String getName() {
        return "display";
    }
    
    public String toString() {
        return this.getName();
    }
}

class Integrate extends Process {

	ParameterMap offset = new ParameterMap(); //-64, 127, 64, "offset");
	ParameterMap scale = new ParameterMap();
	
	/* (non-Javadoc)
	 * @see jmms.processor.Process#process(int)
	 */
	public Integrate() {
		offset.construct(-127, 127, 64, "offset");
		scale.construct(0, 200, 0.0, 2.0, 1.0, "scale");
		this.inputs = new ParameterMap [] {
				(new ParameterMap()).construct(0, 127, 64, "to integrate"),
				(new ParameterMap()).construct(0, 1, 0, "reset to 0"),
				(new ParameterMap()).construct(0, 1, 0, "hold")
		};
		this.outputs = new ActuatorCommander [] {
				new ActuatorCommander("integrated")
		};
		prevValue = offset.getValueInt();
		initInputs();
	}
	
	int prevTime = 0;
	int prevOutput = 0;
	int prevValue = 0;

	public void process(int time) {
		//hold
		if(this.inputs[2].getValueInt() == 1) {
			outputs[0].fireActuators(0, 127, prevValue);
			return;
		}
		
		//reset
		if(this.inputs[1].getValueInt() == 1) {
			prevOutput = 0;
		}
		
		//integration: prev + average of this value and last/time interval
		prevOutput = (int)((prevOutput + 
						   ((prevValue + inputs[0].getValue()-offset.getValue())*0.5)*(time-prevTime))
						   *scale.getValue());
		
		outputs[0].fireActuators(0, 127, (int)RMath.boundHard(0, 127, prevOutput));
		prevTime = time;
		prevValue = inputs[0].getValueInt()-offset.getValueInt();
		
	}

	/* (non-Javadoc)
	 * @see jmms.processor.Process#getVariables()
	 */
	public ParameterMap[] getVariables() {
		return new ParameterMap []{offset, scale};
	}

	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getActuatorContainerCount()
	 */
	public int getActuatorContainerCount() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see jmms.processor.ActuatorContainer#getIndexOfSubContainer(jmms.processor.ActuatorContainer)
	 */
	public int getIndexOfSubContainer(ActuatorContainer sub) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see ren.lang.Namable#setName(java.lang.String)
	 */
	String name = "Integrator";
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
		return this.getName();
	}
}

class AbsoluteAcceleration extends Process implements Serializable{

	//calibration
	
	private ParameterMap offsetX = new ParameterMap();
	private ParameterMap offsetY = new ParameterMap();
	private ParameterMap scaleFactor = new ParameterMap();
	
	public AbsoluteAcceleration() {
		offsetX.construct(0, 127, 63, "offsetX");
		offsetY.construct(0, 127, 63, "offsetY");
		scaleFactor.construct(1, 800, -4.0, 4.0, 1.0, "scale factor");
		this.inputs = new ParameterMap [] {
				(new ParameterMap()).construct(0, 127, 63, "acceleration x"),
				(new ParameterMap()).construct(0, 127, 63, "acceleration y")
		};
		this.outputs = new ActuatorCommander [] {
				new ActuatorCommander("absolute acceleration")
		};
		initInputs();
	}
	
	/* (non-Javadoc)
	 * @see jmms.processor.Process#process()
	 */
	public void process(int time) {
		outputs[0].fireActuators(0, 127, 
								 RMath.boundHard(0, 127, 
										(int)((Math.sqrt(
												  this.srqOffset(inputs[0].getValue(), offsetX.getValue()) +
											      this.srqOffset(inputs[1].getValue(), offsetY.getValue())))
												  *scaleFactor.getValue())));
	}
	
	private int srqOffset(double in, double offset) {
		return (int)((in-offset)*(in - offset));
	}
	

	/* (non-Javadoc)
	 * @see jmms.processor.Process#getVariables()
	 */
	public ParameterMap[] getVariables() {
		return new ParameterMap [] {offsetX, offsetY, scaleFactor};
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

	private String name = "Absolute Acceleration";
	
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
	
}
