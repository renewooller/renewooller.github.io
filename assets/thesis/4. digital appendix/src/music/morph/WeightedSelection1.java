/*
 * Created on 12/01/2005
 *
 * @author Rene Wooller
 */
package music.morph;

import org.w3c.dom.Element;

import jm.music.data.Part;
import jm.music.data.Phrase;
import ren.gui.ParameterMap;

/**
 * goes through each phrase and randomly selects a note
 * from one or the other depending on the weighting.
 * 
 * The structure remains intact.  It is not so creative,
 * in that it doesn't derive from any higher-level reps.
 * 
 * 
 * @author wooller
 * 
 * 12/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class WeightedSelection1 extends Morpher {

	/** Creates a new WeightedSelection1 */
	public WeightedSelection1() {
		super();
		toRetPart = new Part [2];
		toRetPart[0] = new Part();
		toRetPart[1] = new Part();
	}

	/**
	 * randomly selects a note from one or the other depending on the <br>
	 * current weighting <br>
	 */
	transient Part [] toRetPart;
	public Part[] morph(Part from, Part to, Part[] rootMorph, double amount, double [] params) {
		// clear the storage parts
		toRetPart[0].empty();
		toRetPart[1].empty();

		//insert notes from the "from", or "start" useing weighted randomness
		Phrase[] fphr = from.getPhraseArray();
		for (int i = 0; i < fphr.length; i++) {
			if (Math.random() > amount) {
			    toRetPart[0].add(fphr[i].copy());
			    if(fphr[i].getStartTime() >= params[0])
			        i = fphr.length;
				
			}
		}
		
		//insert notes from the "to", or "destination" useing weighted randomness
		Phrase[] tphr = to.getPhraseArray();
		for (int i = 0; i < tphr.length; i++) {
			if (Math.random() > (1 - amount)) {
			    toRetPart[1].add(tphr[i].copy());
			    if(tphr[i].getStartTime() >= params[1])
			        i = tphr.length;
			}
		}
		
		//if there is another morpher in the chain, call it, feeding the result
		if (this.next != null) {
			return next.morph(from, to, toRetPart, amount, params);
		} else {
			//otherwise, return the result
			return toRetPart;
		}
	}
	
	/**
	 * @return nothing - there are no parameters for this simple morphing algorithm
	 * @see music.morph.Morpher#getPC()
	 */
	public ParameterMap[] getPC() {
		return new ParameterMap[] {};
	}

	/**
	 * @return the name of this morphing algorithm
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