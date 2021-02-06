import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import ren.util.PO;

/*
 * Created on 20/10/2005
 *
 * @author Rene Wooller
 */

public class PriorityTest {

    public static void main(String [] args) {
        new PriorityTest();
    }
    
    public PriorityTest() {
        
        Part p = new Part();
        for(int i=40; i<60; i++) {
            Phrase phr = new Phrase();
            phr.setPriority(i);
            p.add(phr);
        }
        
        PO.p(p.toString());
        
        Mod.removePriorityBelow(p, 50);
        
        PO.p(p.toString());
        
    }

}
