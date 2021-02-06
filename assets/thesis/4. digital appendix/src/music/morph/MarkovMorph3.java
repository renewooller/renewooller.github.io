/*
 * Created on 4/02/2005
 *
 * @author Rene Wooller
 */
package music.morph;

import org.w3c.dom.Element;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import ren.gui.ParameterMap;
import ren.util.RMath;
import ai.PitchCompare;
import ai.RhythmCompare;
import ai.StartTimeCompare;

/**
 * will include intervalic relationships to predict the next
 * note, and uses a theshold of similarity fitness to control 
 * the markov depth
 * 
 * @author wooller
 * 
 * 4/02/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MarkovMorph3 extends Morpher {                                                 
    
    PitchCompare pcom = new PitchCompare();
    RhythmCompare rcom = new RhythmCompare();
    StartTimeCompare scom = new StartTimeCompare();
    
    ParameterMap [] params;
    
    double scopeSel;
    
	/**
	 *  
	 */
	public MarkovMorph3() {
		super();
		this.next = new WeightedSelection1();
		defineParams();
	}
	
	private void defineParams() {
	    params = new ParameterMap [4+scom.getParams().length];
	    params[0] = (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
	                "pitch weight");
	    params[1] = (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
	                "rhythm weight");
	    params[2] = (new ParameterMap()).construct(0, 100, 0.0, 1.0, 1.0,
	                "start weight");
	    params[3] = (new ParameterMap()).construct(1, 4, 1,
	        		"markov depth");
	    
	    System.arraycopy(scom.getParams(), 0, params, 3, scom.getParams().length);
	    
	}

	/**
	 * 
	 * @see music.morph.Morpher#morph(jm.music.data.Part, jm.music.data.Part,
	 *      jm.music.data.Part[], double)
	 */
	private Part selected, seed;
	private int insel = 0;
	private boolean done = false;
	boolean selectedFrom = true;
	
	// statistical information
	int numNullPreds = 0;
	int numPreds = 0;
	
	public Part[] morph(Part from, Part to, Part[] rootMorphs, double amount, double [] params) {
	    Mod.quickSort(from);
	    Mod.quickSort(to);
	
	   System.out.println("constructing new morph " + amount);
	   numNullPreds = 0;
	   numPreds = 0;
	   
	    //randoml choose the part to make the markov morpher from
	    if(Math.random()>amount) {
		    selected = from;
		    selectedFrom = true;
		    scopeSel = params[0];
	    }else  {
		    selected = to;
		    selectedFrom = false;
		    scopeSel = params[1];
	    }
		seed = rootMorphs[0];
		
		seed.empty();
		
		// always start with a seed of the correct size
		if(seed.getSize() == 0) {
		    seed.add(selected.getPhrase(1)); 
		}
		
		while(seed.size() < (from.size()+to.size())/2) {
		    
		    if(Math.random()>amount) {
			    selected = from;
			    if(!selectedFrom) {
		//	        System.out.println("changing to-from");
			    }
			    scopeSel = params[0];
			    selectedFrom = true;
		    }else {
			    selected = to;
			    if(selectedFrom) 
		//	        System.out.println("changing from-to");
			    scopeSel = params[1];
			    selectedFrom = false;
		    }
    
		    fillPredict(seed, selected);
		    numPreds +=1;
		}
	//	System.out.println(numNullPreds + "   " + numPreds);
		System.out.println("informed predictions ratio = " +
		    (1-((numNullPreds*1.0)/(numPreds*1.0))));
		
		return rootMorphs;
	}
	
	double [] sim;
	Phrase toCompare, toAdd;
	private void fillPredict(Part seed, Part sel) {
	    // compare the most recent note of seed with the
	    // notes in sel to construct a similarity 
	    // matrix.
	    sim = new double [sel.size()];
	    for(int i=0; i<sim.length; i++) {
	        sim[i] = comparePhrases(seed.getPhrase(seed.size()-1), sel.getPhrase(i));
	    }
	    
	    // if the number of exact fits (1) is above a 
	    // certain threshold, increase the order by
        // multiplying the similarity of the previous 
	    // note into it
        
        //...
        
        // if the numer of exact fits is below a certain theshold, use intervals to obtain a better
        // estimation and factor them into sim.  However, when using intervals, the interval itself
        // will need to be the result (that is applied to the current note)
        
        // ...
      
        // predict a phrase using the probability matrix (sim is now acting as such)
	    toAdd = predictPhrase(sel, sim);
	   
	    
	    
	    if(toAdd == null) {
	        numNullPreds += 1;
	    //    System.out.println("null prediction");
	        // produce a random prediction
	        int rs = (int)(Math.random()*(sel.size()-1));
	        toAdd = sel.getPhrase(rs).copy();
	        // make the start time equal to the interval
	        if(rs > 0)
	            toAdd.setStartTime(toAdd.getStartTime()-
	                			   sel.getPhrase(rs-1).getStartTime());
	         
	    }
	    
	    //  toAdd's startTime must be an interval
	    toAdd.setStartTime(toAdd.getStartTime() + 
	        			   seed.getPhrase(seed.size()-1).getStartTime());
	    seed.add(toAdd);
	        
	}

	double cp, cr, cs;
	private double comparePhrases(Phrase a, Phrase b) {
	    cp = pcom.compare(a.getNote(0).getPitch(),
	        			  b.getNote(0).getPitch())*params[0].getValue();
	    cr = rcom.compare(a.getNote(0).getDuration(),
	        			  b.getNote(0).getDuration())*params[1].getValue();
	    cs = scom.compare(a.getStartTime(),
	        			  b.getStartTime())*params[2].getValue();
	  //  System.out.println("p " + cp + "r" +  cr + "s" + cs);
	    
	    return (cp+cr+cs+1.0)/4;
	}
	
	double rand, sum, currProb;
	int selectedPhrase;
	/**
	 * 
	 * @param sel
	 * @param probs
	 * @return null if the there is nothing <br>
     *         Phrase which has start time equal to the
     * 		   interval
	 */
	Phrase toPredict;
	private Phrase predictPhrase(Part sel, double [] probs) {
//	    PO.p("probs", probs);
	    sum = RMath.sum(probs);
	    rand = Math.random()*sum;
	    selectedPhrase = -1;
	    currProb = 0;
	    for(int i=0;i<probs.length; i++) {
	        currProb += probs[i];
	        if(rand < currProb) {
	            selectedPhrase = i;
	            break;
	        }
	        
	    }
	    
	    if(selectedPhrase == -1) {
	        // returns null if the phrases cannot
	        // be predicted
	        return null;
	    } else if(selectedPhrase == sel.size()-1) {
	        toPredict = sel.getPhrase(0).copy();
	        toPredict.setStartTime(scopeSel-
	          sel.getPhrase(sel.size()-1).getStartTime());
	        return toPredict;
	    }
	    
	    toPredict = sel.getPhrase(selectedPhrase+1).copy();
	    
	    // m
	    toPredict.setStartTime(toPredict.getStartTime()-
	        				   sel.getPhrase(
	        				       selectedPhrase).getStartTime());
	    
	    return toPredict;
	    
	}

	/**
	 * @see music.morph.Morpher#getPC()
	 */

	public ParameterMap[] getPC() {
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see music.morph.Morpher#getType()
	 */
	public String getType() {
		return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
	}

    public void dload(Element e) {
        // TODO Auto-generated method stub
        
    }

    public void dsave(Element e) {
        // TODO Auto-generated method stub
        
    }

}