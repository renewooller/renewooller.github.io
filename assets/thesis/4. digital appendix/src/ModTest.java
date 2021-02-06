import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import ren.util.PO;

/*
 * Created on 31/10/2005
 *
 * @author Rene Wooller
 */

public class ModTest {

    
    public static void main(String [] args) {
        Part p = new Part();
        Phrase phr1 = new Phrase(0.0);
        phr1.add(new Note(36, 0.5));
        p.add(phr1);
        
        Phrase phr2 = new Phrase(1.0);
        phr2.add(new Note(36, 0.5));
        p.add(phr2);
        
        PO.p(p);
        
        Mod.repeatRT(p, 2.0, 16.0);
        
        PO.p("\n\n");
        
        PO.p(p);
        
        
    }

}
