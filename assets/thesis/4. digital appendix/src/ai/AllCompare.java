/*
 * Created on 13/02/2006
 *
 * @author Rene Wooller
 */
package ai;

import jm.music.data.Phrase;
import ren.gui.ParameterMap;
import ren.util.RMath;

public class AllCompare {
	
	private StartTimeCompare sc = new StartTimeCompare();
	private PitchCompare pc = new PitchCompare();
	private RhythmCompare rc = new RhythmCompare();
	private DynamicCompare dc = new DynamicCompare();
	
	private ParameterMap [] weights = new ParameterMap [] {
            (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
                "start time"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
                "pitch"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
                "duration"),
                (new ParameterMap()).construct(0, 100, 0.0, 1.0, 0.0,
                "dynamic")   
                };
	
	public AllCompare() {
		super();
	
	}

	/**
	 * compares phrase a to phrase b, using all the various compares here
	 * uses a weighted euclidean norm.
	 * @param a
	 * @param b
	 * @param scope (optional) if not a predefined scope use <0, otherwise
	 * it overrides the modulus parameters within the StartTimeCompare
	 * @return similarity between 0 and 1. 1 is most similar, 0 is least
	 */
	public double comp(Phrase a, Phrase b, double scope) {
		
		// compare start time, pitch, duration, dynamic
		double s = sc.compare(a.getStartTime(), b.getStartTime(), scope);
		s = s*s*weights[0].getValue();
		
		double p = pc.compare(a.getNote(0).getPitch(), 
							  b.getNote(0).getPitch());
		p = p*p*weights[1].getValue();
							 
		double r = rc.compare(a.getNote(0).getDuration(), 
					   	  b.getNote(0).getDuration());
		r = r*r*weights[2].getValue();
		
		double d = dc.compare(a.getNote(0).getDynamic(), 
					      b.getNote(0).getDynamic());
		d = d*d*weights[3].getValue();
		
		// use a euclidean distance
		double ret = Math.sqrt(s+p+r+d);
		
		
		// normalise by the maximum possible value, ie the square of all the 
		// weights (eg, if all the distances were 1.0, ret would essentially be 
		// the square root of the weights at this stage, prior to normalisation) 
		ret = ret/(Math.sqrt(weights[0].getValue() + 
							  weights[1].getValue() +
							  weights[2].getValue() + 
							  weights[3].getValue()));
		
		return ret;
	}
	
	/**
	 * returns the weights for [0]-pitch [1]-dur [2]-s
	 * 
	 * @return
	 */
	public ParameterMap [] getWeights() {
		return weights;
	}

	/**
	 * Dynamic
	 */
	public DynamicCompare getDc() {
		return dc;
	}

	/**
	 * pitch
	 * @return
	 */
	public PitchCompare getPc() {
		return pc;
	}

	/**
	 * rhythm (duration)
	 * @return
	 */
	public RhythmCompare getRc() {
		return rc;
	}

	/**
	 * Start time
	 * @return
	 */
	public StartTimeCompare getSc() {
		return sc;
	}

}
