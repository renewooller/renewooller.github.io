/*
 * Created on 3/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package music.transform;


import java.io.IOException;
import java.io.ObjectInputStream;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jmms.TickEvent;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.io.Domc;
import ren.util.PO;

/**
 * @author wooller
 * 
 * TODO To change the template for this generated type
 * comment go to Window - Preferences - Java - Code
 * Style - Code Templates
 */
public class Scope extends Transformer {

    private ParameterMap sv = (new ParameterMap()).construct(0, 13,
        new double[] { 2.0, 3.0, 4.0, 6.0, 8.0, 12.0, 16.0, 24.0, 32.0, 42.0, 48.0, 64.0, 96.0, 128},
        8.0, "scope");

    Phrase rest = new Phrase(new Rest(1.0));

    public Scope() {
        super();
       
    }

    public Scope construct(ParameterMap sv) {
        this.sv = sv;

        return this;
    }

    /**
     * goes hrough all of the subsequent transforms,
     * only if subsequent transforms follow the same
     * stream protocol
     * 
     */
    public Part transform(Part stream, Object context, TickEvent e) {
    	// modeulate the position we are at
        e.setAt(e.at() % sv.getValue());

        
        double et = stream.getEndTime();
        // trimm all the  bits beyond scope off it
        if (et > sv.getValue()) {
            stream = stream.copyRT(0.0, sv.getValue());
            // copy(0.0, sv.getValue(), true,
            // false, true, false);
        } else {
            stream = stream.copy();
        }
        // adds a rest so that other functions like
        // rate can find out what the scope is
        // **** this is covered by rate itself now
        /*
        if (et < sv.getValue()) {
            if (rest == null) {
                rest = new Phrase(new Rest(1.0));
            }
            rest.getNote(0)
                .setLength(sv.getValue());
            stream.add(rest);
            rest.setStartTime(0.0);
        }*/

        return super.transform(stream, context, e);
    }

    public ParameterMap[] getParamMaps() {
        return new ParameterMap[] { sv };
    }

    public String getType() {
        return "scope";
    }

    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {

        // this is required to go first by Java
        ois.defaultReadObject();

        this.rest = new Phrase(new Rest(1.0));

    }

    public void dload(Element e) {
        super.dload(e);
        sv.setValue(e.getAttribute("sv"));
        
       // PO.p("sv = " + e.getAttribute("sv"));
        
        Element ne = ((Element) (e.getElementsByTagName("next").item(0)));
        
        if(ne == null)
            return;
        try {
            next = (Transformer)Domc.lo(ne, 
                Class.forName(ne.getAttribute("class")),
                e.getOwnerDocument());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dsave(Element e) {
        super.dsave(e);
        e.setAttribute("sv", String.valueOf(sv.getValue()));
     
    }

}