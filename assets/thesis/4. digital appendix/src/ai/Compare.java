/*
 * Created on 30/04/2005
 *
 * @author Rene Wooller
 */
package ai;

/**
 * @author wooller
 *
 *30/04/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public abstract class Compare {

    /**
     * 
     */
    public Compare() {
        super();
        
    }
    
    /**
     * 
     * @param a
     * @param b
     * @return double specifying how close value a is to value b 1 is the same 0 is opposite
     */
    public abstract double compare(double a, double b);
    
    /**
     * Relative Double Similarity
     * Useful for measuring similarity of numbers where the bounds is unknown
     * it uses a logarithmic series:
     * 1:1 = 0
     * 1:2 = 1/2
     * 1:3 = 2/3
     * 1:4 = 3/4
     * i:j = (j-i)/j
     * @param a
     * @param b
     * @param halfPoint the desired point of half similarity of difference 
     *       (used to scale the logarithm)
     * @return
     */
    double toRet;
    public double relativeSim(double a, double b, double halfPoint) {
        /*** Calculate Factorial Similarity ************/
        // determine the max and shift it so that min is 1
        if( a > b) {
            max = a-b+1;
        } else {
            max = b-a+1;
        }
        
   //     System.out.println("max = " + max + " min = " + 1);
        
        // scale the max so that the desired difference point of half similarity is reached
        max = (max-1)*(1/(halfPoint)) + 1;
        
  //      System.out.println("scaled max = " + max);
     
        // factor it, it will now be from 0 to lim(1)
        toRet = (max-1)/max;
 
   //     System.out.println("factored = " + toRet);
       
        // invert it to make it a measure of similarity where 1 = the same
        toRet = 1-toRet;
        /*********************************************/
        return toRet;
    }
    
    public double relativeSim(double a, double b) {
        return relativeSim(a, b, 1);
    }
    
    /**
     * absolure similarity
     * @param a
     * @param b
     * @param range
     * @return
     */
    public double abSim(double a, double b, double range) {
        return 1-(Math.abs(a-b)/range);
    }
    
    /**
     * amt is the width of the loop
     * @param a
     * @param b
     * @return
     */
    double max, min, ma, mb, norm, ret;
    public double modulusSim(double a, double b, double amt) {
        ma = a%amt;
        mb = b%amt; 
      //  System.out.println("a = " + a + " ma = " + ma + " b = " + b + " mb = " + mb);
        if(ma > mb) {
            max = ma;
        	min = mb;
        } else{
            max = mb;
        	min = ma;
        }
        norm = (amt/2.0);
  //    System.out.println("a = " + a + " b = " + b + " amt = " + amt);
        ret =  Math.min(min+amt - max, max - min);
  //     System.out.println("Math.min(" + min + " + " + amt + " - " + max + ", " + max + "-" + min + ") = " + ret);
        ret = ret/norm;
   //     System.out.println("ret 0-1 = " + ret);
        ret = 1-ret;
     //  System.out.println("mod sim = " + ret); 
        return ret;
    }
    
    
}
