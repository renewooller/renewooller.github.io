package ren.util;

import java.awt.Point;

import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 *18/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
/**
 * @author wooller
 * 
 * 18/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public class RMath {

    public static void main(String[] args) {
        System.out.println(" inv hyp = " + RMath.getInvHyp(2));
    }

    public static int getPwr(double number) {
        return getPwr(number, 2.0);
    }

    /**
     * simple method for getting powers. doensn't work
     * with fractions of powers
     */
    public static int getPwr(double num, double base) {
        int pwr = 1;
        double ch = base;
        while (ch < num) {
            ch = ch * base;
            pwr++;
        }
        return pwr;
    }

    
    /**
     * returns a number that is a smallest possible multiple of both
     * @param a
     * @param b
     * @return
     */
    public static int getMultiple(int a, int b) {
    	// see if they are equal
    	if(a == b)
    		return a;
    	
    	// see if one can be evenly divided into the other
    	
    	int [] sb = {Math.min(a, b),
    			     Math.max(a, b)};
    	    
    	// if there is no remainder in the division, return highest
    	if((sb[1]*1.0/sb[0]*1.0)%1.0 == 0) {
    		return sb[1];
    	}
    	
    	//// otherwise, need to iterate
    	
    	// temporary storage
    	int [] sbt = {sb[0], sb[1]};
    	
    	int si = 0; // index of which one is smaller;
    	
    	while(sbt[0] != sbt[1]) {
    		// add to the smallest
    		sbt[si] += sb[si];
    		
    		// check to see it it is still the smallest
    		if(sbt[si] > sbt[(si+1)%2]) {
    			// if not, change which one is considered to be smallest
    			si = (si+1)%2;
    		}
    	}
    	
    	// they are both the same, so return this number 
    	return sbt[0];
    }
    
    public static double[] getPwrArr(int to, double base) {
        double[] r = new double[to];
        r[0] = base;
        for (int i = 1; i < r.length; i++) {
            r[i] = r[i - 1] * base;
        }
        return r;
    }

    public static int absMod(int toMod, int mod) {
        if (toMod > 0)
            return toMod % mod;
        else
            return (mod + (toMod % mod)) % mod; // toMod%mod;
    }

    public static double absMod(double toMod, double mod) {
        if (toMod > 0)
            return toMod % mod;
        else
            return (mod + (toMod % mod)) % mod; // toMod%mod;
    }

    /**
     * returns the side of a symetrical triange, given
     * the hypotenuse
     */
    public static int getInvHyp(int hyp) {
        return (int) Math.sqrt(hyp / 2);
    }

    public static int getHyp(Point p1, Point p2) {
        return (int) Math.sqrt((p2.x - p1.x) ^ 2 + (p1.y - p2.y) ^ 2);
    }

    public static int getDecimalPoints(double n) {
        Double d = new Double(n);
        Integer i = new Integer((int) n);
        if (i.intValue() - d.doubleValue() == 0)
            return 0;
        System.out.println("d = " + d.toString() + " i = " + i.toString());
        return d.toString()
            .length() - i.toString()
            .length() - 1;
    }

    /**
     * 
     * @param minFrom
     * @param maxFrom
     * @param minTo
     * @param maxTo
     * @param valueFrom
     * @return the value scaled to the new range
     */
    public static double scaleValue(double minFrom, double maxFrom,
            double minTo, double maxTo, double valueFrom) {

        return ((valueFrom - minFrom) / (maxFrom - minFrom)) * (maxTo - minTo)
                + minTo;

    }

    public static int scaleValueInt(double minFrom, double maxFrom,
            double minTo, double maxTo, double valueFrom) {
        return (int) (scaleValue(minFrom, maxFrom, minTo, maxTo, valueFrom) + 0.5); // round
                                                                                    // it
                                                                                    // off
    }

    public static double snapToEdgesHard(double v, double a) {
    	return RMath.boundHard(((v-0.5)/(0.5-a/2.0) + 0.5));
    }
    
    public static double boundHard(double value, double min, double max) {
     //   PO.p("bound hard " + value + " min = " + min + " max = " + max);
        if (value > max) {
            return max;
        }else if (value < min) {
            return min;
        }
        
        return value;
    }
    
    public static double boundHard(double value) {
        return boundHard(value, 0.0, 1.0);
    }

    public static int boundHard(int value, int min, int max) {
        if (value > max)
            value = max;
        else if (value < min)
            value = min;

        return value;
    }

    public static double rem(double a, double b) {
        a = Math.abs(a);
        b = Math.abs(b);
        return a / b - (int) (a / b);
    }

    static double a;

    public static double sum(double[] d) {
        a = 0;
        for (int i = 0; i < d.length; i++) {
            a += d[i];
        }
        return a;
    }

    /**
     * if one of the element is equal to negative 1,
     * none of the elements after that will be
     * considered
     * 
     * @param d
     * @return
     */
    public static double sumQuick(double[] d) {
        a = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] == -1)
                break;
            a += d[i];
        }
        return a;
    }

    public static double sum(ParameterMap[] params) {
        a = 0;
        for (int i = 0; i < params.length; i++) {
            a += params[i].getValue();
        }
        return a;
    }
    
	public static double sum(ParameterMap[] params, int s, int e) {
		a = 0;
        for (int i = s; i <= e; i++) {
            a += params[i].getValue();
        }
        return a;
	}

    public static double mean(double[] d) {
        a = sum(d);
        return a / d.length;
    }

    public static void normalise(double[] d) {
        a = sum(d);
        for (int i = 0; i < d.length; i++) {
            d[i] = d[i] / a;
        }
    }

    public static void mult(double[] ds, double d) {
        for (int i = 0; i < ds.length; i++) {
            ds[i] *= d;
        }
    }

    public static int rndSelect(double w) {
        if (Math.random() > w) {
            return 0;
        } else {
            return 1;
        }
    }
    
    /**
     * bounds are between 0 and 1 automatically - need to add another method
     * to express the bounds
     * @param x
     * @param m
     * @param c
     * @return
     */
    public static double linearFunc(double x, double m, double c) {
        
   //     PO.p("linear func");
 //       PO.p("x1 = " + x);
        
        // gradient and shift
        x = x*m + c;
        
   //     PO.p("x2 = " + x);
        
        // centralise it
        x = x - m/2.0 + 0.5;
        
   //     PO.p("x3 = " + x);
        
        // bound it
        return RMath.boundHard(x);
     
    }
    
    /**
     * interpolate between a and b by a factor, so that then i =0 return a
     * @param a
     * @param b
     * @param i
     * @return
     */
    public static double inter(double a, double b, double i) {
    	return (a*(1-i) + b*i);
    }

    
    public static double norm(Point p) {
    	return norm(p.x, p.y);
    }
    
    public static double norm(double x, double y) {
    	return Math.pow((Math.pow(x, 2.0) + Math.pow(y, 2.0)), 0.5);
    }
    
    public static void centreToPoint(Point a, Point centre) {
    	a.x = a.x-centre.x;
    	a.y = a.y-centre.y;
    	
    }
    
    public static double distance(double x1, double y1, double x2, double y2) {
    	return norm(x2-x1, y2-y1);
    }

    /**
     * centralises uvt at the same time
     * @param uvt
     * @param fp
     * @return
     */
	public static double[] toUnitVec(Point uvt, Point fp) {
		double [] ret = new double [2];
		centreToPoint(uvt, fp);
		double f = 1.0/norm(uvt);
		ret[0] = uvt.x*f;
		ret[1] = uvt.y*f;
		return ret;
	}

	/**
	 * dot product of the two vectors
	 * they must be the same length
	 * @param uvt
	 * @param uva
	 * @return
	 */
	public static double dot(double[] a, double[] b) {
		AD ret = new AD();
		for(int i=0; i<a.length; i++ ) {
			ret.add(a[i]*b[i]);
		}
		
		return ret.v();
	}

	public static int [] sum(int[] scale, int root) {
		int [] sc = new int [scale.length];
		for(int i = 0; i< scale.length; i++) {
			sc[i] = scale[i]+root;
		}
		return sc;
	}

	/**
	 * @param scale
	 * like sum, but with a modulus
	 * @param root
	 * @param mod
	 * @return
	 */
	public static int [] sumod(int[] scale, int root, int mod) {
		int [] sc = new int [scale.length];
		for(int i = 0; i< scale.length; i++) {
			sc[i] = (scale[i]+root)%mod;
		}
		return sc;	
	}
    

}
