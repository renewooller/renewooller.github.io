import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.View;
import ren.util.PO;

/*
 * Created on 9/11/2005
 *
 * @author Rene Wooller
 */

public class ShowTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Score s = new Score("test");
        Part p1 = new Part("part1");
        Part p2 = new Part("part2");
        Part p3 = new Part("part3");
        
        for(int i=0; i<50; i++) {
            Phrase phr = new Phrase(i*1.0);
            phr.addNote(30+(int)(Math.random()*10), 1.0);
            p1.add(phr);
            phr = new Phrase(i*1.0 + 10);
            phr.addNote(50+(int)(Math.random()*10), 1.0);
            p2.add(phr);
            phr = new Phrase(i*1.0 + 20);
            phr.addNote(70+(int)(Math.random()*10), 1.0);
            p3.add(phr);
        }
        s.add(p1);
        s.add(p2);
        s.add(p3);
        
        
        PO.p("showing...");
      //  Mod.convertToRealTime(s, cutPoint)
        View.show(s);
        View.print(s);
    }

}
