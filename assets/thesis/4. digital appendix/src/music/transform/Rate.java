/*
 * Created on 24/04/2005
 *
 * @author Rene Wooller
 */
package music.transform;

import org.w3c.dom.Element;

import jm.music.data.Part;
import jm.music.tools.Mod;
import jm.util.View;
import jmms.TickEvent;
import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 *24/04/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class Rate extends Transformer {

    ParameterMap [] rate = {(new ParameterMap()).construct(1, 13, new double [] {4.0, 3.0, 2.0, 1.66, 1.5, 1.33, 1.0, 0.75, 0.66, 0.5, 0.33, 0.25, 0.125},
            										   1.0,
    												   "rate")};
    
    /**
     * 
     */
    public Rate() {
        super();
      
    }
    
    
    
    transient double len;
    public Part transform(Part stream, Object context, TickEvent e) {
		//obtain the length that we need to keep it at
        len = stream.getEndTime();
        
        Mod.rate(stream, rate[0].getValue(), false);
        
        
        e.setAt(e.at()%
            (super.motherChain.getScopeParam().getValue()*
                    rate[0].getValue()));
        //View.print(stream);
        // if is is too small, repeat it
      //  if(stream.getEndTime() < len)
      //      Mod.repeat(stream, 0, len);
        
        // if it is too big, it will get trimmed automatically
		return super.transform(stream, context, e);
	}

    /* (non-Javadoc)
     * @see music.transform.Transformer#getParamMaps()
     */
    public ParameterMap[] getParamMaps() {
        return rate;
    }

    /* (non-Javadoc)
     * @see music.transform.Transformer#getType()
     */
    public String getType() {       
        return "rate";
    }

    public void dload(Element e) {
        rate[0].setValue(e.getAttribute("rate"));
    }

    public void dsave(Element e) {
        e.setAttribute("rate", rate[0].getValueStr());
    }
}
