/*
 * Created on 9/06/2005
 *
 * @author Rene Wooller
 */
package music.singlePart;


import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;

import jm.music.data.Part;
import jm.music.data.Score;
import jmms.TickEvent;
import lplay.LPlayer;
import music.MusicGenerator;
import music.morph.Morpher;
import music.morph.ParamMorpher;
import music.morph.rt.MorpherRT;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ren.io.Domable;
import ren.io.Domc;
import ren.io.Factorable;
import ren.music.PhraseQ;
import ren.util.PO;

/**
 * The previous morph music generator would only update
 * the morph every 0.5 of a beat because it had to
 * recreate a whole sequence each update.
 * 
 * This new morph music generator will call the morpher
 * every time slice,giving it the morph index and the
 * timeslice
 * 
 * 
 * 
 * @author wooller
 * 
 * 9/06/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MorphMusicGeneratorRT extends
        MorphMusicGenerator implements Serializable, Domable {

   // private Vector histPartVect = new Vector(500);
    private PhraseQ histPhraseQ;    
    
    /**
     *  
     */
    public MorphMusicGeneratorRT() {
        super();
    }

    public MorphMusicGeneratorRT constructMorphRT(
            MusicGenerator m1, MusicGenerator m2) {
        super.constructMorph(m1, m2);
        
        return this;
    }

    /**
     * if a realtime morpher, instead of returning just
     * a slice, return the whole lot, cause the whole
     * lot is just a slice!
     */
    public Score generateTickScore(TickEvent e) {
        //      if it is at the end, play the original
        if (isRTMorph()) {
            if(super.getMorphTracker().getPos() == 0) {    
                return super.getFrom().generateTickScore(e);
            } else if(super.getMorphTracker().getPos() == 1) {
                return super.getTo().generateTickScore(e);
            }
            return this.lscore.getJMScore();
        }else
            return super.generateTickScore(e);
    }

    public Score tickScore(TickEvent e) {
         
        //super.setMRes(((LPlayer)e.getSource()).res()); // not needed now - implicit in TickEvent
        //return the
        return super.tickScore(e);
    }

    /**
     * (morphParams are scope)
     * 
     * it is separate so that it can be overridden
     * easily if necessary
     * 
     * @param i
     *            partIndex
     * @return
     */
    protected Part[] obtainMorphResult(int i,
            Part from, Part to,
            double position, double[] morphParams,
            TickEvent e) {
        
       PO.p("MMGRT obtaining morph result for " + i);
        
        if (isRTMorph()) {
        	PO.p("MMGRT isRTMorph");
            prepHist(from.getIdChannel(), e);
            return null;//morpherRT().morphRT(from, this.histPhraseQ,
                //position, e, null);
        }else
            return super.obtainMorphResult(i, from, to, position, morphParams, e);

    }
    
    /**
     * warning - the history given from the score is
     * uncopied, to minimise object creation.
     * 
     * @param ch
     * @param e
     */
    protected void prepHist(int ch, TickEvent e) {
     //   PO.p("id channel = " + ch);
     //   PO.p(((LPlayer) e.getSource()).getHistoryScore().toString());
       // Score sc = ((LPlayer)e.getSource()).getHistoryScore(20);
  //      PO.p("history score = " + sc.toString());
      //  sc.fillWithChannelID(this.histPartVect, ch);
       // this.histPartVect.clear();
      //  ((LPlayer)e.getSource()).fillChIDHist(ch, 10, histPartVect);
        this.histPhraseQ = ((LPlayer)e.getSource()).getChIDHistQ(ch);
    }

    public MorpherRT morpherRT() {
        return null;//(MorpherRT) (super.morpher());
    }

    public boolean isRTMorph() {
        return morphStrucList.getSelectedItem() instanceof MorpherRT;
    }

    

    public void dload(Element e) {
               
        Element m1e = ((Element)(e.getElementsByTagName("m1").item(0)));
        Element m2e = ((Element)(e.getElementsByTagName("m2").item(0)));
        
        this.constructMorphRT(((MusicGenerator)Domc.lo(m1e, e.getOwnerDocument())),
                              ((MusicGenerator)Domc.lo(m2e, e.getOwnerDocument())));
        
        Element mp = ((Element)(e.getElementsByTagName("morphParam").item(0)));
        
        // load parameter morph type for tempo
        setCBModel(morphTempo, mp, e.getOwnerDocument());
              
        // load the structural morpher type
        setCBModel(morphStrucList, 
            ((Element)(e.getElementsByTagName("morphStruct").item(0))), e.getOwnerDocument());
               
    }
    
    public void dsave(Element e) {
        e.appendChild(Domc.sa(m1, "m1", e.getOwnerDocument()));
        e.appendChild(Domc.sa(m2, "m2", e.getOwnerDocument()));
        e.appendChild(Domc.sa(((ParamMorpher)morphTempo.getSelectedItem()), "morphParam", e.getOwnerDocument()));
        
        
        e.appendChild(Domc.sa(((Morpher)morphStrucList.getSelectedItem()), "morphStruct", e.getOwnerDocument()));
        
        
    }

}