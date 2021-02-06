/*
 * Created on 20/10/2005
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
import ren.util.PO;

public class PriorityMorph extends MorpherRT {

    Part [] toRet = new Part [2];
    
    public PriorityMorph() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ParameterMap[] getPC() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getType() {
        return "priority morph";
    }

    public Part[] morphRT(LPart [] fromTo, LPart[] mlparts, double morphIndex, 
                          TickEvent e, PhraseQ hist) {
    	
        toRet = this.getMorphSegment(fromTo[0].getPart(), fromTo[1].getPart(), e, 
                                     fromTo[0].getScope().getValue(), 
                                     fromTo[1].getScope().getValue());
       
       // PO.p("priority morph index = " + morphIndex);
        
        Mod.removePriorityBelow(toRet[0], (int)(morphIndex*100));
        Mod.removePriorityBelow(toRet[1], 100-(int)(morphIndex*100));
     //   PO.p("priority morphing");
        return toRet;
    }

    public void dload(Element e) {
        // TODO Auto-generated method stub
        
    }

}
