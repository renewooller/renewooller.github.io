import jm.music.data.Score;
import ren.io.Midi;
import ren.util.PO;

/*
 * Created on Apr 21, 2006
 *
 * @author Rene Wooller
 */

public class MidiReadTest {

	public static void main(String [] args) {
		 
         Score ts = new Score();
         Midi.getInstance().read(ts, 
        		 "D:/Documents and Settings/wooller.QUTAD/My Documents/aceBaseForLemu.mid");
		
		PO.p("ts = " + ts.toString());
         
	}
	
}
