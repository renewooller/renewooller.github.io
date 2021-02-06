/*
 * Created on 27/10/2005
 *
 * @author Rene Wooller
 */
package ren.util;

public class ODouble {

	public static ODouble MIN = new ODouble().construct(-Double.MAX_VALUE);
	public static ODouble MAX = new ODouble().construct(Double.MAX_VALUE);
	
    public double v = 0.0;
    
    public ODouble() {
        super();
    }

    public ODouble copy() {
        ODouble ret = new ODouble();
        ret.v = v;
        return ret;
    }
    
    public ODouble construct(double v) {
        this.v = v;
        return this;
    }

    public String toString() {
    	return String.valueOf(v);
    }
}
