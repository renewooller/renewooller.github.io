/*
 * Created on 15/02/2006
 *
 * @author Rene Wooller
 */
package ren.util;

import java.math.BigDecimal;

/**
 *  AD: Acurate  Double! 
 *  thwarting double accuracy errors wherever it goes!
 *  for counting with doubles
 * @author wooller
 *
 *15/02/2006
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class AD {

	private long v;
	private int sc;

	public static final double DFACC = 0.0000001;

	public AD() {
		this(DFACC);
	}
	
	public AD(double ac) {
		this(ac, 0.0);
	}
	
	public AD(double ac, double v) {
		this.sc = (int)(Math.log(ac)/Math.log(0.1));
		this.set(v);
	}
	
	public AD(int sc, double v) {
		this.sc  = sc;
		this.set(v);
	}

	public void set(double nv) {
		//int fsc = Math.max(0, (int)(Math.log(nv)/Math.log(0.1)));
		v = (long)(nv*Math.pow(10, sc) + 0.5);
		
	}
	
	public double v() {
		return BigDecimal.valueOf(v, sc).doubleValue();
	}
	
	public void add(double nv) {
		
		v += (long)(nv*Math.pow(10, sc) + 0.5);
	//	this.v = v.a
	}
	
	public static double mult(double a, double b, int dp) {
		BigDecimal da = BigDecimal.valueOf((long)(a*Math.pow(10, dp) + 0.5), dp);
		
		BigDecimal db = BigDecimal.valueOf((long)(b*Math.pow(10, dp) + 0.5), dp);
		BigDecimal ret = da.multiply(db);
	//	PO.p("da = " + da.doubleValue() + " db = " + db + " ret = " + ret.doubleValue());
		ret.setScale(dp, BigDecimal.ROUND_HALF_EVEN);
		//PO.p("r)
		return ret.doubleValue();
	}
	
	public static double mult(double a, double b) {
		return mult(a, b, 6);
	}

	/**
	 * double to scale 
	 * converts a double to a long representation at a certain level of scale
	 * @param d
	 * @param scale
	 * @return
	 */
	public static long dts(double d, int scale) {
		return (long)(d*Math.pow(10, scale) + 0.5);
	}
	
	public String toString() {
		return String.valueOf(v());
	}
}
