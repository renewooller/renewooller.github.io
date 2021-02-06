import java.util.TreeMap;

import jm.music.data.Part;
import ren.tonal.Scales;
import ren.tonal.TonalComposite;
import ren.util.Gen;
import ren.util.PO;

/*
 * Created on 9/01/2006
 *
 * @author Rene Wooller
 */

public class DPTest {

    public DPTest() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    	
    	Part p = Gen.npart(new double []  {0,   1,   2,  3,  4,  5,  6,  7,  8,  9, 10},//, 2.75, 3.0, 3.5, 3.75},
    					       new int [] {60, 61,  64, 65, 67, 69, 71, 72, 73, 76, 79});//, 60, 61, 62, 63});
    	
    	TonalComposite tc = new TonalComposite();
    	
    	tc.addPart(p);
    	
    	TreeMap degs = tc.pitchesToDegrees();
    	
    	
    	int [] scale = tc.extractScale(degs);
    	double [] weights = tc.extractWeights(degs);
    	
    	for(int i=0; i< scale.length; i++) {
    		PO.p(" scale : " + scale[i] + "  weight :" + weights[i]);
    		
    	}
    	int op = 51;
    	int np = Scales.getInstance().pitchFromScale(op, scale, 0, weights, 12, false);
    	
    	PO.p("old pitch = " + op + " new pitch = " + np);
    	
    	/*
    	//Phrase phr = p.getPhrase(1);
    //	p.removePhrase(phr);
    //	p.add(phr);
        	
    	PO.p(p.toString());
    	MTConverter mtc = new MTConverter();
    	MTransformat mt = mtc.conv(p, 0.25, 4.0);
    	PO.p("  ..... converted = ");
    	PO.p(mt.toString() + "\nrate: ");
    	
    	for(int i=0; i<16; i++) {
    		System.out.print(mt.getDimAt(0, i*0.25, false) + ", ");
    	}
    	PO.p("\n");
    	for(int i=0; i<16; i++) {
    		System.out.print(mt.getDimAt(0, i*0.25, true) + ", ");
    	}*/
    	
    }

}
