/*
 * ParamNotePanel.java
 *
 * Created on 18 February 2004, 13:08
 */

package ren.gui.seqEdit;

import java.awt.Dimension;

import ren.gui.ParameterMap;
import ren.gui.ParamEvent;
import ren.gui.ParamListener;

/**
 * 
 * @author Rene Wooller
 */
public class ParamNotePanel extends NotePanel implements ParamListener {

	protected ParamNTGC paramConverter;

	private ParameterMap scope, quantise; // used for identifying the source parameter

	/** Creates a new instance of ParamNotePanel */
	public ParamNotePanel() {
	}

	public void construct(NoteToGraphicsConverter n) {
		super.construct(n);
		if (n instanceof ParamNTGC) {
			paramConverter = (ParamNTGC) n;
		}
		paramConverter.getScope().addParamListener(this);
		scope = paramConverter.getScope();
 
        quantise = paramConverter.getQuantiseParam();
        quantise.addParamListener(this);
	}

	public void setScope(ParameterMap nscope) {
		scope.removeParamListener(this);
		this.scope = nscope;
		scope.addParamListener(this);
		paramConverter.setScope(scope);
	}
    
    public void setQuantise(ParameterMap nquant) {
        quantise.removeParamListener(this);
        this.quantise = nquant;
        quantise.addParamListener(this);
        paramConverter.setQuantise(quantise);
    }

	public void paramFired(ParamEvent e) {
		if (e.getSource() == scope) {
			this.notePanel.setPreferredSize(new Dimension(paramConverter
					.getPixelsViewed(),
					this.notePanel.getPreferredSize().height));
			repaintRevalidate();
		} else if(e.getSource() == quantise) {
		    
        }
	}

	public void repaintRevalidate() {
		this.notePanel.revalidate();
		this.notePanel.repaint();
		this.revalidate();
	}

}