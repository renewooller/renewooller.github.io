/*
 * Created on 12/01/2006
 *
 * @author Rene Wooller
 */
package ren.util;

public class Arr {

	public Arr() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * initialises a double array and returns it
	 * @param v
	 * @return
	 */
	public static double [] d(int le, double v) {
		double [] da = new double [le];
		for(int i=0; i< da.length; i++) {
			da[i] = v;
		}
		return da;
	}

	public static int[] in(int n, int v) {
		int [] ret = new int [n];
		for(int i=0; i< ret.length; i++) {
			ret[i] = v;
		}
		return ret;
	}

	public static AD[] AD(int length) {
		AD [] ret = new AD [length];
		for(int i=0; i< ret.length; i++) {
			ret[i] = new AD();
		}
		
		return ret;
	}
	
}
