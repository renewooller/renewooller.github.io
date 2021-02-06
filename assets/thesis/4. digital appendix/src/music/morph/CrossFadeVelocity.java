/*
 * Created on 4/02/2005
 *
 * @author Rene Wooller
 */
package music.morph;

import org.w3c.dom.Element;

import jm.music.data.Part;
import jm.music.tools.Mod;
import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 *4/02/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class CrossFadeVelocity extends Morpher {

	/**
	 * 
	 */
	public CrossFadeVelocity() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see music.morph.Morpher#morph(jm.music.data.Part, jm.music.data.Part, jm.music.data.Part[], double)
	 */
	public Part[] morph(Part from, Part to, Part[] rootMorphs, double amount, double [] params) {
		
		rootMorphs[0].setPhraseList(from.copy().getPhraseList());
		rootMorphs[1].setPhraseList(to.copy().getPhraseList());
		
		Mod.scaleDynamic(rootMorphs[0], (1-amount));
		Mod.scaleDynamic(rootMorphs[1], amount);
		
		return rootMorphs;
	}

	/* (non-Javadoc)
	 * @see music.morph.Morpher#getPC()
	 */
	public ParameterMap[] getPC() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see music.morph.Morpher#getType()
	 */
	public String getType() {
		return "cross-fade";
	}

    public void dload(Element e) {
        
    }

    public void dsave(Element e) {
        e.setAttribute("type", getType());
    }

}
