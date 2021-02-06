/*
 * Created on 30/04/2005
 *
 * @author Rene Wooller
 */
package ai;

import ren.util.RMath;

/**
 * @author wooller
 *
 *30/04/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class RhythmCompare extends Compare {
   
    /**
     * 
     */
    public RhythmCompare() {
        super();
       
    }

    /***
     * 
     * 
     * @see ai.Compare#compare(double, double)
     */
    public double compare(double a, double b) {
        double c = Math.pow(relativeSim(a, b), 1);
        
        //by timeing it by 0.5 and squareing it, it makes the maximum it 
        //can be 0.25
        c += Math.pow(ratioSim(a, b)*0.5, 2);
        
        // because the maximum is now 1 + 0.25, it needs to be normalized by 1.25
        c *= 1/1.25;
        return c;
    }
    
    public double ratioSim(double a, double b) {
        return Math.abs((RMath.rem(Math.max(a, b), Math.min(a, b))-0.5))/0.5;
    }
   
}
