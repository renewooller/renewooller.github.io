/*
 * Created on 13/01/2005
 *
 * @author Rene Wooller
 */
package music.morph;

import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.io.Factorable;

/**
 * @author wooller
 *
 *13/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public abstract class ParamMorpher implements Factorable, Domable {

	/**
	 * 
	 */
	public ParamMorpher() {
		super();
		
	}

    public abstract double morph(double from, double to, double amount);
    
	public abstract ParameterMap morph(ParameterMap from, ParameterMap to, ParameterMap morphParam, double amount);

	public abstract ParameterMap [] getPC();
	
	public abstract String getType();
	
	public String toString() {
		return getType();
	}
	
    
}
