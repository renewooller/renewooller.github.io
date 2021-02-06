/*
 * Created on 20/10/2005
 *
 * @author Rene Wooller
 */
package music.transform;

import jm.music.data.Part;
import jm.music.tools.Mod;
import jmms.TickEvent;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.util.PO;

public class Quantise extends Transformer {

    private ParameterMap qv = (new ParameterMap()).construct(0,
        3, new double[] {0.0, 0.125, 0.25, 0.5}, 0.25, "quantise");//0.0625, 1.0/12.0, 0.125,  0.25, 1.0/3.0, 0.5}, 
    
    public Quantise() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ParameterMap[] getParamMaps() {
        return new ParameterMap [] {qv};
    }

    public String getType() {
        return "quantise";
    }

    /**
     * quantise is a cutter.
     */
    public Part transform(Part stream, Object context, TickEvent e) {
        //if(stream.getChannel() == 10)
    //    PO.p("at = " + e.at());
        
       //     PO.p(" before quantise= " + stream.toString());
        
        if(e.getRes() < qv.getValue()) {
    //        PO.p("e.getres = " + e.getRes() + "  qv = " + qv.getValue());
            
            // if it is not on the quantised beat
            if(e.at()%qv.getValue() != 0) {
                stream.empty(); // don't play it
           //     PO.p("emtpying " + stream.toString());
                
                return super.transform(stream, context, e);
            }    
           
        } 
        
        // the segment we want to quantise
        double seg = Math.max(qv.getValue(), e.getRes());
               
        Mod.cropFaster(stream, e.at(), e.at()+seg, true);
       
        Mod.quantise(stream, qv.getValue(), true, true, false);
      //  PO.p("alfter Quantise = " + stream.toString());
        
        
        return super.transform(stream, context, e);
    }
    
    public void dload(Element e) {
        super.dload(e);
        qv.setValue(Double.parseDouble(e.getAttribute("qv")));
    }

    public void dsave(Element e) {
        super.dsave(e);
        e.setAttribute("qv", Double.toString(qv.getValue()));
    }

}
