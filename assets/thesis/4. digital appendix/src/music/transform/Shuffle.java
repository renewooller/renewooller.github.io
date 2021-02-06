/*
 * Created on 20/10/2005
 *
 * @author Rene Wooller
 */
package music.transform;

import jm.music.data.Part;
import jm.music.tools.Mod;
import jmms.TickEvent;
import lplay.LPlayer;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.util.PO;

public class Shuffle extends Transformer {

    ParameterMap [] sh = new ParameterMap [] {
            (new ParameterMap()).construct(0, 1000, 0.0, 0.5, 0.0,"shuffle")
    };
    
  //  private PhraseLL swingFuture = new PhraseLL();
    
    public Shuffle() {
        super();
        sh[0].setDecimalPlaces(3);
        // TODO Auto-generated constructor stub
    }

    public Part transform(Part stream, Object ob, TickEvent e) {
        
      //  PO.p("\n\n\nbefore = " + stream.toString());
        
        swing(stream, 
            sh[0].getValue(), 
            super.motherChain.getQuantiseParam().getValue(),
           ((LPlayer)e.getSource()).res(),
            e.at());
        //*/
        
   //     PO.p("after = " + stream.toString());
        
        return super.transform(stream, ob, e);
    }
  
    private void swing(Part stream, double amt, double qres, double res,
            double beatAt) {
        /*
        if (swingFuture.length() > 0) {
            swingFuture.resetIter();
            Phrase phr = null;
            while (phr != swingFuture.en() && swingFuture.length() > 0) {
                phr = swingFuture.iter();
                
                if (phr.getStartTime() < beatAt)
                    swingFuture.remove(phr);
                else if (phr.getStartTime() < beatAt + res) {
                    phr.setStartTime(beatAt % res);
                    stream.add(phr);
                    swingFuture.remove(phr);
                }
            }
        }//*/

        if (qres < res) {
            Mod.swing(stream, amt, qres);
            return;
        }

        for (int i = 0; i < stream.size(); i++) {
            double st = stream.getPhrase(i)
                .getStartTime() + beatAt;
            
           // PO.p("old st = " + (st-beatAt));
            
            if (st % (qres * 2.0) == qres) {
                st = st + qres * amt;
             //   PO.p("new st = " + (st-beatAt));
                stream.getPhrase(i).setStartTime(st-beatAt);
                
                 //   swingFuture.add(stream.getPhrase(i));
                 //   stream.removePhrase(i);
                
            }
        }
        
    }
    
    public ParameterMap[] getParamMaps() {
        return sh;
    }

    public String getType() {
       return "shuffle";
    }

    public void dload(Element e) {
        super.dload(e);
        sh[0].setValue(Double.parseDouble(e.getAttribute("sh")));
    }

    public void dsave(Element e) {
        e.setAttribute("sh", sh[0].getValueStr());
        super.dsave(e);
    }
    
}
