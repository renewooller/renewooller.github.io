/*
 * Created on 30/04/2005
 *
 * @author Rene Wooller
 */
package ai;

import ren.gui.ParameterMap;
import ren.util.RMath;

/**
 * @author wooller
 *
 *30/04/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class StartTimeCompare extends Compare {

    // later this will have a meter object which has
    // pulse information which will be useful for 
    // comparing the starttimes
    
    //weightings
    ParameterMap [] params = new ParameterMap [] {
            (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
                "mod eight"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
                "mod four"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
                "mod three"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
                "mod two"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
                "mod one"),
                (new ParameterMap()).construct(0, 10000, 1.0, 1000, 1.0,
                "start time sensitivity"),
                };
    
    /**
     * 
     */
    public StartTimeCompare() {
        super();
    }

    /**
     * @see ai.Compare#compare(double, double)
     */
    double toRet;
   // double [] weights = new double [] {3.0, 1.0, 1.0, 1.0};
    public double compare(double a, double b) {
        toRet = 0;
        // need to use relative similarity because I don't know how long the piece will be
 //       toRet = super.relativeSim(a, b, 8.0)*weights[0];
       // System.out.println(toRet);
  //      toRet += super.modulusSim(a, b, 16)*weights[1];
     //   System.out.println(toRet);
  //      toRet += super.modulusSim(a, b, 8)*weights[2];
     //  System.out.println("start toRet" + toRet);
        
        toRet += squashModSim(a, b, 8)*params[0].getValue();
        toRet += squashModSim(a, b, 4)*params[1].getValue();
        toRet += squashModSim(a, b, 3)*params[2].getValue();
        toRet += squashModSim(a, b, 2)*params[3].getValue();
        toRet += squashModSim(a, b, 1)*params[4].getValue();
       
       // System.out.println(" toRet = " + toRet);/// + "  sum = " + RMath.sum(weights));
        
        toRet = toRet/RMath.sum(params, 0, 4);
        
        return toRet;
    }

    /**
     * useful if need to compare using a scope
     * @param a
     * @param b
     * @param scope
     * @return
     */
    public double compare(double a, double b, double scope) {
    	if (scope <= 0)
    		return compare(a, b);
    	return squashModSim(a, b, scope);
    }
    
    //centered on zero
    double zc = 0;
    private double squashModSim(double a, double b, double scope) {
        zc = super.modulusSim(a, b, scope);
        
      //  System.out.println("before zc = " + zc);
        
        //center it on zero
      //  zc = zc*2 -1;
        
        // shift it by threshold
  //      zc += params[6].getValue();
        
        //squash it
        
    /*    zc = (1+params[7].getValue())/
        	 (1 + Math.pow(params[5].getValue()*10000, 
        	     -zc + params[6].getValue())) - params[7].getValue()/2.0;
      
        System.out.println("after squahs " + zc);
        zc = Math.max(zc, 0);
        zc = Math.min(zc, 1);
        
        //zc = zc/(1/(1 + Math.pow(params[5].getValue()*10000, -1 + params[6].getValue())));
        System.out.println("after squash and bordering " + zc);
        */
        zc = Math.pow(zc, params[5].getValue());
        
     //   System.out.println("after squash and bordering " + zc);
        return zc;
    }
    
    public ParameterMap [] getParams() {
        return params;
    }
    
}
