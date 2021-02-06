/*
 * Created on 17/06/2005
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
import ren.io.Domable;
import ren.io.Factorable;
import ren.music.PhraseQ;

/**
 * This interface makes it possible to extend the
 * block style realtime morphers into truer realtime
 * morphers that update on every timeslice.
 * 
 * 
 * @author wooller
 *
 *17/06/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public abstract class MorpherRT implements Domable, Factorable{
    /**
     * morphRT - the method that is called to generate
     * a morph at the particular time specified.
     * 
     * @param toFrom the part morphing from
     * @param mlparts any musical information we might need
     * @param morphIndex where we are in the morph
     * @param e where we are in beats
     * @param hist TODO
     */
    public abstract Part [] morphRT(LPart[] toFrom, final LPart[] mlparts, 
			double morphIndex, TickEvent e, 
			PhraseQ hist);
    
   
    
    /**
     * 
     * @param from
     * @param to
     * @param e
     * @param scof passing -1 will signal to return an empty part
     * @param scot passing -1 will signal to return an empty part
     * @return
     */
    public static Part[] getMorphSegment(Part from, Part to,
            TickEvent e, double scof, double scot) {
        Part [] toRet = new Part [2];
        if(scof != -1) {
        toRet[0] = from.copyRT( e.at() % scof,
            ((LPlayer) e.getSource()).plusRes( e.at() %scof));
        } else {
        	toRet[0] = from.copyEmpty();
        }
        
        if(scot != -1) {
        toRet[1] = to.copyRT( e.at() % scot,
            ((LPlayer) e.getSource()).plusRes( e.at() % scot));
        } else {
        	toRet[1] = to.copyEmpty();
        }
        
        return toRet;
    }
    
    
    
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
    
    /**
     * all sub classes should call this
     */
    public void dsave(Element e) {
        e.setAttribute("type", this.getType());
    }


    /**
     * this is for morphs the require a conversion from one data representation
     * to another, at the start of the morph (eg, MTransform morph). it is 
     * called from the initParts in the part morph which is ultimately
     * called every time the morph is selected.
     * @param lfrom
     * @param lto
     * @param morphLength TODO
     */
	public  void initParts(LPart lfrom, LPart lto, LPart [] lpmorph, int morphLength) {
		
	}
    
    
}
