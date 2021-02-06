/*
 * Created on 30/04/2005
 *
 * @author Rene Wooller
 */
package ai;

import ren.gui.ParameterMap;
import ren.util.PO;

/**
 * @author wooller
 *
 *30/04/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class PitchCompare extends Compare {

    // think about having:
    // a) (memory) two tonal managers or 
    // b) (speed) passing the surrounding pitch contexts
    //    for each of them
    // or at least a tuning system, so that the octave 
    // can be divided up moe easily
    
    // or maybe you pass it tonal managers, along with
    // the pitch and then get a rating for similarity 
    // between them that factors into it
    
	// Tonal compare can be used to compare it
	//private TonalCompare tcomp = new TonalCompare();
	
    
    ParameterMap [] params = new ParameterMap [] {
            (new ParameterMap()).construct(1, 10000, 1.0, 0.001, 0.25,
            							   "pitch resolution")
    };
    
    /**
     * 
     */
    public PitchCompare() {
        super();
    }

    /**
     * This approach totally ignores the way harmony is
     * percieved and could be improved in many ways. look for shepherd
     * 
     * possibly, looking at similarity in terms of 
     * interval ratio denominators and numerators (eg
     * 1/1 will be more similar to 3/2 but not to 7/5
     * 
     * @see ai.Compare#compare(double, double)
     */
    double c;
    public double compare(double a, double b) {
            	
    	// first get the absolute distance
        c = Math.abs(a-b)/128.0;
      
        // then increase the resolution of the smaller end of the scale
        c = Math.pow(c, params[0].getValue());
        
        // then invert it to get  similarity, not distance
        c = 1-c;
        return c;
    }
    
    public ParameterMap [] getParams() {
        return params;
    }
    
    
    

}
