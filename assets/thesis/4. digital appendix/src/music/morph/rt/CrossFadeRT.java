/*
 * Created on 11/10/2005
 *
 * @author Rene Wooller
 */
package music.morph.rt;


import jm.music.data.Part;
import jm.music.tools.Mod;
import jmms.TickEvent;
import music.LPart;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.music.PhraseQ;

public class CrossFadeRT extends MorpherRT {
  
    private ParameterMap [] params = new ParameterMap[] {
            (new ParameterMap()).construct(0, 99, 1.0, 0.01, 1.0, "earliness")
    };
    
    private Part [] toRet = new Part [2];
    
    public CrossFadeRT() {
        super();

    }

    public Part[] morphRT(LPart [] fromTo,
            final LPart[] mlparts, double morphIndex,
            TickEvent e, PhraseQ hist) {
        
        toRet[0] = fromTo[0].getTickPart(e);
        toRet[1] = fromTo[1].getTickPart(e);
      //  Part [] toRet = getMorphSegment(fromTo[0].getPart(), fromTo[1].getPart(), 
     //       e, mlparts[0].getScope().getValue(), mlparts[1].getScope().getValue());
        
        fade(morphIndex, toRet);
        
        return toRet;
    }
    
    public Part [] fade(double mi, Part [] toRet) {
        double f = (1/params[0].getValue());
        for(int i=0; i< toRet[0].size(); i++) {
            Mod.scaleDynamic(toRet[0].getPhrase(i).getNote(0), Math.min((1-mi)*f, 1.0)); 
        }
        for(int i=0; i< toRet[1].size(); i++) {
            Mod.scaleDynamic(toRet[1].getPhrase(i).getNote(0), Math.min(mi*f, 1.0));
        }
        return toRet;
    }

    public ParameterMap[] getPC() {
        return params;
    }

    public String getType() {
        return "fade dynamics";
    }

    public void dload(Element e) {
        params[0].setValue(e.getAttribute("earl"));
    }

    public void dsave(Element e) {
        super.dsave(e);
        e.setAttribute("earl", params[0].getValueStr());
    }


}