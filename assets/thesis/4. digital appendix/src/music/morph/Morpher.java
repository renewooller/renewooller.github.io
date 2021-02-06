/*
 * Created on 12/01/2005
 *
 * @author Rene Wooller
 */
package music.morph;

import org.w3c.dom.Element;

import jm.music.data.Part;
import jmms.TickEvent;
import lplay.LPlayer;
import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.io.Factorable;

/**
 * @author wooller
 *
 *12/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public abstract class Morpher implements Domable, Factorable{

	protected Morpher next;
	
	/**
	 * 
	 */
	public Morpher() {
		super();
	}
	
	/**give cerae more cuddles and kisses xxx
	 * 
	 * @param from - the part to morph from
	 * 
	 * @param to - the part to morph to
	 * 
	 * @param rootMorph - the part which all the morphers in the chain <br>
	 * prior to this one have already operated on.  Unless subsequent <br>
	 * morphs in the chain have passed a copy, the rootMorphs part [] is<br>
	 * a reference to the rootMorphs part [] used in the MorphMusicGenerator <br>
	 * that contains this morph.  A part [] is used rather than a part <br>
	 * so that in the transition, the data can be stored in separate groups <br>
	 * 
	 * @param amount – how far morph has progressed.  At 0 it should <br>
	 * return <b>from</b> and at 1 it should return <b>to</b>.
	 * 
	 * @return rootMorph - the new transition
	 */

	public abstract Part [] morph(Part from, 
								  Part to, 
								  Part [] rootMorphs, 
								  double amount,
								  double [] params);

    
	public abstract ParameterMap [] getPC();
	
	public abstract String getType();
	
	public String toString() {
		return this.getType();
	}
	
	/**
	 * called on the last frame of the morph, when it is
	 * finishing. 
	 * Don't put anything in here that is crucial for
	 * user-controlled realtime morphing
	 */
	public void finish() {}
	
	public void startInit() {}
    
    public void dsave(Element e) {
        e.setAttribute("type", this.getType());
    }
    
}
