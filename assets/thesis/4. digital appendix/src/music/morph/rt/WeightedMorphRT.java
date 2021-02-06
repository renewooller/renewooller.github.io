/*
 * Created on 1/07/2005
 *
 * @author Rene Wooller
 */
package music.morph.rt;


import jm.music.data.Part;
import jmms.TickEvent;
import lplay.LPlayer;
import music.LPart;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.music.PhraseQ;
import ren.util.RMath;

/**
 * @author wooller
 *
 *1/07/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class WeightedMorphRT extends MorpherRT{

   
    private Part [] toRet;
   // Part empty;
  
    /**
     * 
     */
    public WeightedMorphRT() {
        super();
     
        toRet = new Part [2];
        toRet[0] = new Part();
        toRet[1] = new Part();
     
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
        return this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
    }

    /**
     * @see music.morph.rt.MorpherRT#morphRT(jm.music.data.Part, jm.music.data.Part, double, jmms.TickEvent, jm.music.data.Part[], double[])
     */
     public Part[] morphRT(LPart []lfromTo, LPart[] mlparts, 
            double morphIndex, TickEvent e, PhraseQ hist) {
            
        int selInt = RMath.rndSelect(morphIndex);
        return getSegment(lfromTo[selInt], selInt, e, mlparts, lfromTo); // was .getPart()
      
    }
    
    public Part [] getSegment(LPart sel, int selected, TickEvent e, 
            final LPart [] mlparts, final LPart [] lfromTo) {
        
        double at = e.at()%mlparts[selected].getScope().getValue();
      
        toRet[selected] = sel.getTickPart(e); //was sel.copyRT(at, ((LPlayer)e.getSource()).plusRes(at)); 
         
        Part empty = new Part();
        lfromTo[1-selected].getPart().copyAttributes(empty);
        toRet[1-selected] = empty;
      //  PO.p("f " + toRet[0].size() + " t " + toRet[1].size());
        return toRet;
    }
   

    public void dload(Element e) {
        
    }

    public void dsave(Element e) {
        super.dsave(e);
    }

}
