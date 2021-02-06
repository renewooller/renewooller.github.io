/*
 * Created on 11/01/2006
 *
 * @author Rene Wooller
 */
package ai;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import jm.music.data.Note;
import jm.music.data.Phrase;
import ren.util.ODouble;
import ren.util.ODoubleCompare;
import ren.util.PO;


public class MTransformat {

	private double scope = 4.0;
	private double quantise = 0.25;
    private TreeMap [] dims = new TreeMap [6];
    // rate, phase, duration, velocity, pitch
    public static final int RAT = 0, PHA = 1, DUR = 2, VEL = 3, PIT = 4,
                             POL = 5;
    
    public MTransformat() {
        super();
        for(int i=0; i< dims.length; i++) {
            dims[i] = new TreeMap(new ODoubleCompare());
        }
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Rate literally the interval (in beats) between note onsets.  EG, 1
     * corresonds to 1 note every beat. 4 is one note every bar.  0.25 is one
     * note every 1/4 beat.
     * @param time
     * @param rate
     * @throws Exception 
     */
    public void setRate(double time, double rate) throws Exception {
        setDim(RAT, time, rate);
    }
    
    /**
     * while rate is the number of notes per beat, phase is a measure of 
     * temorary deviation from a perfectly homogenous distribution of notes at
     * the specified rate. eg, if phase is 0.5, all of the notes are pushed 
     * ahead of the beat by this much.
     * 
     * @param time
     * @param phase
     * @throws Exception 
     */
    public void setPhase(double time, double phase) throws Exception {
        setDim(PHA, time, phase);
    }
    public void setDur(double time, double dur) throws Exception {
        setDim(DUR, time, dur);
    }
    public void setVel(double time, double vel) throws Exception {
        setDim(VEL, time, vel);
    }
    public void setPitch(double time, double pitch) throws Exception {
        setDim(PIT, time, pitch);
    }
    
    public void setPol(double time, Phrase p) {
        setPol(time, 
            p.getNote(0).getPitch(), 
            p.getNote(0).getDuration(),
            p.getNote(0).getDynamic());
    }
    
    /**
     * to deal with poliphony, each extra note can be added. unlike the other
     * dimensions, POL can handle multiple values at the same time.
     * @param time
     * @param pitch
     * @param dur
     * @param vel
     */
    public void setPol(double time, double pitch, double dur, double vel) {
        ODouble t = (new ODouble()).construct(time);
        Note n = new Note();
        n.setPitch((int)pitch);
        n.setDuration(dur);
        n.setDynamic((int)vel);
        if(dims[POL].containsKey(t)) {
            ((Vector)dims[POL].get(t)).add(n);
        } else {
            Vector v = new Vector();
            v.add(n);
            dims[POL].put(t, v);
        }
        
    }
    
    public void setDim(int type, double time, double value) throws Exception {
        
    	ODouble t = (new ODouble()).construct(time);
        ODouble v = (new ODouble()).construct(value);
        if(dims[type].containsKey(t)) {
            Exception ex = new Exception("not allowed to have two different" +
                "values at the same time:\n time = " + time + " value = " + value);
            ex.fillInStackTrace();
            //try{throw ex;} catch(Exception e) {e.printStackTrace();}
        }
        
        // if it isn't equal to the previous entry
        if(v.v != preV(type, t.v).v) {
        	dims[type].put(t, v);
        } // otherwise, don't need to put it in
        
    }
    
    // returns the key of the closest previous break-point
    public ODouble preK(final int type, final double time) {
    	ODouble t = (new ODouble()).construct(time);
    	if(dims[type].isEmpty()) {// if it is empty return minimum
       		return t.MIN;
    	}
    	if(dims[type].headMap(t).size() > 0) {// if there is anything before it
        		//get it 
    		return ((ODouble)(dims[type].headMap(t).lastKey()));
    	} else {//if ther is nothing before it
    		// the last key in the whole thing is closest (assuming torus)
    		return ((ODouble)dims[type].lastKey());
    	}	
    }
    
    /**
     * with one element, it returns this element
     * with two or more elements:
     * 	if there is no next element, it wraps around and chooses the first 
     * element
     * 	if there is a next element it chooses it
     * 
     */
    public ODouble nexK(final int type, final double time) {
    	ODouble t = (new ODouble()).construct(time);
    	if(dims[type].isEmpty()) // if it is empty return minimum
    		return ODouble.MIN;
    	if(dims[type].tailMap(t).size() > 0) {// if there is anything before it
    		//get it 
    		return ((ODouble)(dims[type].tailMap(t).firstKey()));
    	} else {//if there is nothing before it
    		// the last key in the whole thing is closest (assuming torus)
    		return ((ODouble)dims[type].firstKey());
    	}	
    }
    
    public ODouble preV(int type, double time) {
    	ODouble k = preK(type, time);
    	if(k.v == k.MIN.v)
    		return k;
    	return (ODouble)dims[type].get(k);
    }
    
    public ODouble nexV(int type, double time) {
    	ODouble k = nexK(type, time);
    	if(k.v == k.MIN.v)
    		return k;
    	return (ODouble)dims[type].get(nexK(type, time));
    }
    
    public void clearAll() {
        for(int i=0; i<dims.length; i++) {
            dims[i].clear();
        }
    }
    
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("\nMusic Transform:");
    	for(int i=0; i< dims.length; i++) {
    		sb.append("\ndim" + i + " times  : " + dims[i].keySet().toString() +
    				  "\n   " + i + " values : " + dims[i].values().toString());
    		
    	}
    	return sb.toString();
    }

    /**
     * 
     * @param loc
     * @param rate
     * @param n
     * @throws Exception 
     */
	public void setAllNote(double loc, double rate, Note n) throws Exception {
		this.setRate(loc, rate);
		this.setPitch(loc, n.getPitch());
		this.setDur(loc, n.getDuration());
		this.setVel(loc, n.getDynamic());
		
	}

	public double getScope() {
		return scope;
	}

	public void setScope(double scope) {
		this.scope = scope;
	}

	public double getQuantise() {
		return quantise;
	}

	public void setQuantise(double quantise) {
		this.quantise = quantise;
	}
	
	/**
	 * get the value of a particular dimension eg rate, phase, pitch, dur, dyn
	 * at the specified time.
	 * 
	 * interp determines wether to interpolate (true) or to obtain the value from 
	 * the previous break-point
	 * 
	 * @param 
	 * @param time
	 * @param interp
	 * @return the rate 
	 */
	public double getDimAt(int type, double time, boolean interp) {
		time = time%scope;
		
		double pv = this.preV(type, time + 0.001).v; // 0.001 so as to include 
		// times that are exactly on it.
		
		//if interpolating
		if(interp) {
			double nk = this.nexK(type, time).v;
			double pk = this.preK(type, time + 0.001).v;
			double nv = this.nexV(type, time).v;
			if(nk <= time) // if the next bk-pt is wrapped around. = will only
				// be relevant to single element treemaps
				nk = nk + this.scope;
			
			if(pk >= time)
				pk = pk - this.scope;
		 //                m             x         c
			return ((nv-pv)/(nk-pk))*(time - pk) + pv;
			
		} else { // otherwise get value form the previous break point
			return pv;
		}
	}
	
	public double getDimAt(int type, ODouble point) {
		point = point.copy();
		
		point.v = ((point.v%scope)+scope)%scope;
		
		if(this.dims[type].containsKey(point))
			return ((ODouble)(this.dims[type].get(point))).v;
		else {
			if(this.dims[type].headMap(point).isEmpty()) {
				PO.p("point = " + point.v + " state = \n" + this.toString());
			}
			return ((ODouble)(this.dims[type].get(
					this.dims[type].headMap(point).lastKey()))).v;
		}
	}

	/**
	 * returns NaN if there isn't any data
	 * @param type
	 * @return
	 */
	public double computeMean(int type) {
		Iterator iter = this.dims[type].keySet().iterator();
		// if there isn't any data, return NaN
		if(!iter.hasNext()) {
			return Double.NaN;
		}
		ODouble prev = (ODouble) iter.next();
		
		
		// if there is only one bit of data, return it's value
		if(!iter.hasNext()) {
			return ((ODouble)(this.dims[type].get(prev))).v;
		}
		
		ODouble nex = (ODouble) iter.next();
		double dac = 0.0625/16.0; // to avoid double accuracy problems
		
		double dis = nex.v-prev.v;
		double val = ((ODouble)this.dims[type].get(prev)).v;
		int areaint = (int)((dis*val)/dac +0.5);	
		double accudis = dis;
		
		while(iter.hasNext()) {
			prev = nex;
			nex = (ODouble) iter.next();
			
			// the distance of this segment
			dis = (nex.v-prev.v);
			// taking into account wrapping
			if(nex.v <= prev.v) {
				dis += scope/dac;
			}
			
			accudis += dis;
	//		PO.p("accudis = " + accudis);
						
			val = ((ODouble)this.dims[type].get(prev)).v;
			
	//		PO.p("dis = " + dis + " val = " + val);
			
			// accumulate the area, area = distance* value
			areaint += (int)((dis*val)/dac +0.5);
			
	//		PO.p("area = " + areaint*dac);
		}
		
		dis = scope-nex.v;
		
		accudis += dis;
	//	PO.p("accudis = " + accudis);
		val = ((ODouble)this.dims[type].get(nex)).v;
	//	PO.p("dis = " + dis + " val = " + val);
		areaint += (int)((dis*val)/dac +0.5);
	//	PO.p("area = " + areaint*dac);
	//	PO.p("scope " + scope);
		
		// divide by scope to find the mean
		return (areaint*dac)/scope;
	}
}
