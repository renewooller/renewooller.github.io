import ren.util.PO;
import ai.PitchCompare;
import ai.RhythmCompare;
import ai.StartTimeCompare;

/*
 * Created on 14/02/2006
 *
 * @author Rene Wooller
 */

public class CompareTets {

	public static void main(String [] args) {
		PitchCompare pc = new PitchCompare();
		
		int sp = 40;
		int len = 40;
		double [][] vals = new double [len][len];
		
		for(int i=sp; i<len+sp; i++) {
			for(int j=sp; j<len+sp; j++) {
			//	PO.p("i = " + i + " j = " + j);
				vals[i-sp][j-sp] = pc.compare(i*1.0, j*1.0);
			}
		}
		
	//	PO.p("val = ", vals);
		/*
		StartTimeCompare stc = new StartTimeCompare();
		
		double c = 2.0;
		for(int i=0; i<8; i++) {
			PO.p("start time compare " + i + " : " + c);
			PO.p(" = " + stc.compare(c, i*1.0));
		}*/
		
		RhythmCompare rc = new RhythmCompare();
		for(int i=0; i<20; i++) {
			double ri = i/10.0;
			System.out.print("rhythm " + ri + ": ");
			for(int j =0; j< 20; j++) {
				double rj = j/10.0;
				System.out.print(" " + rc.ratioSim(ri, rj) + ", ");
			}
			System.out.println("\n");
		}
		System.exit(0);
		
	}

}
