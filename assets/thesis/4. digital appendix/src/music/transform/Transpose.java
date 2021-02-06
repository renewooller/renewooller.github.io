/*
 * Created on 4/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package music.transform;

import org.w3c.dom.Element;

import jm.music.data.Part;
import jm.music.tools.Mod;
import jmms.TickEvent;
import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Transpose extends Transformer {
	
    ParameterMap pitch = (new ParameterMap()).construct(-12, 12, 0, getType());
    
	public Transpose() {
		super();
        paramMaps[0] = pitch;
	}
	
	/* (non-Javadoc)
	 * @see music.transform.Transformer#transform(jm.music.data.Part, jm.music.data.Part, double)
	 */
	public Part transform(Part stream, Object context, TickEvent e) {
		Mod.transpose(stream, pitch.getValueInt());
		if(next == null)
		    return stream;
		else
		    return next.transform(stream, context, e);
	}

	/* (non-Javadoc)
	 * @see music.transform.Transformer#getPC()
	 */
	public ParameterMap[] getParamMaps() {
		return paramMaps;
	}

	public String getType() {
		return "transpose";
	}

    public void dload(Element e) {
        pitch.setValue(e.getAttribute("pitch"));
    }

    public void dsave(Element e) {
        e.setAttribute("pitch", pitch.getValueStr());
    }
	
	
}
