/*
 * Created on 12/05/2005
 *
 * @author Rene Wooller
 */
package ren.tonal;

import java.util.Iterator;
import java.util.TreeMap;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import ren.util.AD;
import ren.util.PO;
import ai.DefaultNoteWeighter;
import ai.NoteWeighter;

/**
 * @author wooller
 *
 *12/05/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class TonalComposite {

	//TODO need to make it so that the calculation of root, (strongi), 
	//is done using meter as well.
	// the metrical weights could be done using StartTimeCompare, 
	// and comparing with 0
	
    // this stores the tones as keys, and the frequency
    // of occurance (sum of duration) as values. 
    private TreeMap pw = new TreeMap();
    
    //counts the total durations that have been composited
    //so that the weights can be normalised
    private AD durationCount = new AD();
    
    //purely a garbage saving trick
    static Integer [] pints = new Integer [1000];
    static {
        for(int i=0; i<pints.length; i++) {
            pints[i] = new Integer(i);
        }
    }
    
    int strongi;
    double strongv = 0;
    
    int spo = 12; // steps per octave
    
    private NoteWeighter weighter = new DefaultNoteWeighter();
    
    /**
     */
    public TonalComposite() {}

    //double weight;
    
    public void addNote(Note n) {
    	addNote(n, 1.0);
    }
    
    /**
     * TODO make w factor the duration
     * @param n
     * @param w
     */
    public void addNote(Note n, double w) {
        
    	
    	double weight = 0;
    	durationCount.add(n.getDuration());
        if(pw.get(pints[n.getPitch()]) == null) {
            weight = 0;
        } else {
            weight = ((Double)pw.get(pints[n.getPitch()])).doubleValue();
        }
        
        weight += n.getDuration();
        
        if(weight > strongv) {
            strongv = weight;
            strongi = n.getPitch();
        }
      
        pw.put(pints[n.getPitch()], new Double(weight));
    }
    
    public TreeMap pitchesToDegrees() {
    	return pitchesToDegrees(strongi%spo);
    }
    
    public TreeMap pitchesToDegrees(int key) {
    	
    	// store the scale degrees
    	TreeMap degs = new TreeMap();
    	
    	double total = 0;
    	
    	// go through all the pitches
    	Iterator iter = pw.keySet().iterator();
    	while(iter.hasNext()) {
    		// get the pitch
    		Integer pi = ((Integer)iter.next());
    		// make it into a scale degree, with the strongest note as root
    		Integer sd = new Integer((
    								 (pi.intValue()%spo)
    								  	+ spo 
    								  	- (key))
    								  %spo);
    								 
    		if(degs.get(sd) == null) { // initialise the pitch class in degs
    			degs.put(sd, pw.get(pi));
    			total = total + ((Double)pw.get(pi)).doubleValue();
    		} else { // or add to it
    			Double nw = new Double(((Double)pw.get(pi)).doubleValue() + 
						((Double)degs.get(sd)).doubleValue());
    			degs.put(sd, nw);
    			total = total + nw.doubleValue();
    		}
    	}
    	
    	Object [] iarr = degs.keySet().toArray();
    	for(int i=0; i< iarr.length; i++) {
    		degs.put(iarr[i], 
    				new Double(((Double)degs.get(iarr[i])).doubleValue()/
    						   total));
    	}
    	
    	return degs;
    }
    
    public int [] extractScale(TreeMap degs) {
    	
    	int [] scale = new int [degs.size()];
    	
    	Iterator iter = degs.keySet().iterator();
    	int i=0;
    	while(iter.hasNext()) {
    		scale[i++] = ((Integer)iter.next()).intValue();
    	}
    	
    	return scale;
    }
    
    public double [] extractWeights(TreeMap degs) {
    	double [] weights = new double [degs.size()];
    	Iterator iter = degs.keySet().iterator();
    	int i=0; 
    	while(iter.hasNext()) {
    		weights[i++] = ((Double)degs.get(iter.next())).doubleValue();
    	}
    	return weights;
    }
    
    
    /*
    int [] tempScale;
    public int[] extractScale() {
        int [] scale = new int[pw.size()];
        int i = 0;  
        int root = 0;
        iter = pw.keySet().iterator();
        while(iter.hasNext()) {
            scale[i] = ((Integer)iter.next()).intValue();
           
            // not so universal if spo is always 12
            scale[i] = scale[i]%spo;
             
            // adjust it so that the strongest note is tonic
            scale[i] = (scale[i]+spo-(strongi%spo))%spo;
            
            if(scale[i] == 0)
                root = i;
                
            i++;
        }
               
        // order it correctly
        tempScale = new int [scale.length];
        for(i = 0; i< scale.length; i++) {
            tempScale[i] = scale[(root+i)%scale.length];
            
        }
        
        return tempScale;
    }
    
    */
    /**
     * extracts the strengths of the pitches, in terms of scale degrees in the
     * scale given
     * @param scale the scale to find the weights for
     * @return array of weights, indexes of which correspond to the indexes of 
     * the scale given
     
    public double [] extractStrengths(int [] scale) {
    	double [] tr = new double [scale.length];
    	for(int i=0; i< tr.length; i++) {
    		tr[i] = 0;
    	}
    	
    	iter = pw.keySet().iterator();
        while(iter.hasNext()) {
        	// for each pitch in the composite
        	Integer pi = ((Integer)iter.next());
        	
        	// check it against each scale degree
        	for(int i=0; i< scale.length; i++ ) {
        		// if it is of the scale degree, add the weight
        		if(pi.intValue()%spo == scale[i])
        			tr[i] = tr[i] + ((Double)pw.get(pi)).doubleValue();
        	}
        }
        
        return tr;
    }*/
    
    public int getRootPitch() {
        return strongi;
    }
    
    public int getKey() {
    	return this.getRootPitchClass();
    }
    
    public int getRootPitchClass() {
        return strongi%spo;
    }
    
    public double getPitchWeight(int pitch) {
        //get the weight
    	Double wei = (Double)(pw.get(pints[pitch]));
    	if(wei == null)
    		return 0;
    	
        //normalise before sending it
        return wei.doubleValue()/durationCount.v();
    }
    
    public void addPhrase(Phrase p) {
    	addPhrase(p, 1.0);
    }
    
    public void addPhrase(Phrase p, double w) {
    	
        for(int i=0; i<p.length(); i++) {
            addNote(p.getNote(i), w);
        }
    }
    
    public void addPart(Part p) {
    	addPart(p, 1.0);
    }
    
    public void addPart(Part p, double w) {
        for(int i=0; i<p.length(); i++) {
            addPhrase(p.getPhrase(i), w);
        }
    }

	public int getSPO() {
		return this.spo;
	}

	/**
	 * finds the cetroid pitch, weighted on the weights(duration)
	 * @return
	 */
	public double getMeanPitch() {
		final double af = 0.0001; // accuracy factor
		int v = 0;
		//Double wei;
		for(int i=0; i<this.pints.length; i++) {
		//	PO.p("getPitch weight = " + this.getPitchWeight(i));
			v += (int)((this.getPitchWeight(i)*i)/af + 0.5);
			//wei = (Double)pw.get(pints[i]);
			//if(wei != null)				
			//	v += (int)((wei.doubleValue()*i)/af +0.5);
		}

		return (v*af);
	}

	/**
	 * compresses the weights of every pitch into pitch-classes (%spo(12))
	 * normalises the resulting array such that all weights sum to 1
	 * @return
	 */
	public double [] getPCWeights() {
		// initialise the variable to store the weights
		AD [] w = new AD [this.spo];
		for(int i=0; i< w.length; i++) {
			w[i] = new AD();
		}
		
		AD norc = new AD(); // normalising constant
		
		// go through all the pitches
    	Iterator iter = pw.keySet().iterator();
    	while(iter.hasNext()) {
    		// for each pitch
    		Integer key = (Integer) iter.next();
    		
    		double toAdd = ((Double)(pw.get(key))).doubleValue();
    	//	PO.p("to add = " + toAdd);
    		norc.add(toAdd);
    	//	PO.p(" accumulating norc " + norc.v());
    		
    		// accumulate the weight of that pitch class
    		w[key.intValue()%this.spo].add(toAdd);
    		
    		//PO.p("adding  = " + ((Double)(pw.get(key))).doubleValue());
    		//PO.p(" is= " + w[key.intValue()%this.spo].v());
    	}
    	
    	
    //	PO.p(" norc = " + norc.v() + " duration count = " + durationCount.v());
    	
    	// convert and return the resulting double array
    	double [] ret = new double [w.length];
    	for(int i=0; i< ret.length; i++) {
    	//	
    		ret[i] = w[i].v()/durationCount.v();
    //		PO.p("w [" + i + "] = " + w[i].v() + " ret = " + ret[i]);
    	}
    	return ret;
	}

	public NoteWeighter getWeighter() {
		return this.weighter;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer("tonal composite : \n");
		Iterator iter = pw.keySet().iterator();
    	AD norc =  new AD();
		
		while(iter.hasNext()) {
    		// for each pitch
    		Integer key = (Integer) iter.next();
    		
    		double toAdd = ((Double)(pw.get(key))).doubleValue();
    		
    		sb.append("key " + key.toString() + " value = " + toAdd + "\n");
    		
    		norc.add(toAdd);
    	
    	}
		
		sb.append("\nduration count = " + this.durationCount.v() + "\n");
		sb.append("added normalis = " + norc.v() +  "  ation constant (should be the same) \n\n");
		return sb.toString();
	}
	
}
