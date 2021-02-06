/*
 * Created on 24/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import music.LPart;

import org.w3c.dom.Element;

import ren.tonal.Scales;
import ren.tonal.TonalComposite;
import ren.util.PO;

public class ModeLock extends TransSearch {

	private Scales scales;
	private String [] snames;
	private TonalComposite cosca; // used to derive the context scale
	private static final int DFON = Scales.NUM + 1; // scales plus scale from original pitches
	private int on;
	
	private LPart toswi;
	private TonalComposite tt;
	
	public ModeLock() {
		super();
		
		
	}

	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
		
		// think about passing this in in TransSearch.find (the rate of change)
		double transRate = 0.5;
		
		
		TonalComposite tf = new TonalComposite();
		tf.addPart(f.getPart());
		
		int [] coscale = cosca.extractScale(cosca.pitchesToDegrees(t.getTonalManager().getRoot()));
		//PO.p("before lock", op[0].getPart(), 1);
		scales.lockToScale(op[0].getPart(), 
				coscale, 
				t.getTonalManager().getRoot(), 
				t.getTonalManager().getStepsPerOctave(), 
				0, cosca.getWeighter(), false, 
				new double [][] {cosca.getPCWeights()}, new double [] {1.0});
		
		op[0].getTonalManager().setScale(coscale);
		//PO.p("after lock", op[0].getPart(), 1);
		w[0] = super.metric.diftc(op[0].getPart(), tt);
		
		// the second one is no change (lock to chromatic
		w[1] = super.metric.diftc(op[1].getPart(), tt);
		
		for(int i=2; i< op.length; i++) {
		//	PO.p("modelock " + i);
			
			if(co[co.length-1].getValue() == 0.0) { // if non-chromatic factor is on
				w[i] = 1.0;
				
			}
			
		//	System.out.println("scale = " + this.snames[i]);
			
			// the tonal composite used to help lock notes away from used
			// pitches (favour = false)
			
			scales.lockToScale(op[i].getPart(), snames[i-1], 
					f.getTonalManager().getRoot(), 
					f.getTonalManager().getStepsPerOctave(), 0, 
					tt.getWeighter(), false, 
					new double [][] {tf.getPCWeights(), tt.getPCWeights()}, 
					new double [] {(1-transRate), transRate});
					
			
			//find distances
			w[i] = super.metric.diftc(op[i].getPart(), tt);
			//PO.p("op ", op[i].getPart(), 0);
		}
		
		//PO.p("finished mode lock");
	}

	protected void initCostParams(int num) {
		super.initCostParams(num + 1); // scales plus non-chromatic factor
	}
	
	protected void setInitialCostValues() {
		int i = 0;
		
		co[i].setName("pitches used");
		co[i++].setValue(0.6);
		
		
		//public static int[] chromatic = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.8); // chromatic is a cheap shot, but necessary
		
		//public static int[] ionian = { 0, 2, 4, 5, 7, 9, 11 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.9); // 
	
		//public static int[] dorian = { 0, 2, 3, 5, 7, 9, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.8); // 
		
		//public static int[] phrygian = { 0, 1, 3, 5, 7, 8, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.9); // 
		
		//public static int[] lydian = { 0, 2, 4, 6, 7, 9, 11 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.7); // 
		
		//public static int[] mixolydian = { 0, 2, 4, 5, 7, 9, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.8); // 
		
		//public static int[] aeolian = { 0, 2, 3, 5, 7, 8, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.9); // 
		
		//public static int[] lochrian = { 0, 1, 3, 5, 6, 8, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.7); // 
		
		//public static int[] pentatonic = { 0, 3, 5, 7, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.8); // 
		
		//public static int[] harmonic_minor = { 0, 1, 4, 5, 7, 8, 11 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.8); // 
		
		//public static int[] whole_tone = { 0, 2, 4, 6, 8, 10 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.5); // 
		
		//public static int[] whole_tone_half_tone = { 0, 2, 3, 5, 6, 8, 9, 11 };
		co[i].setName(snames[i-1]);
		co[i++].setValue(0.5); // 
	
		
		co[co.length-1].setName("non-chromatic factor");
		co[co.length-1].setValue(1.0);
		
	}
	
	protected double costOfIndex(int i) {
		if(i == 0 || i == 1)
			return co[i].getValue();
		
		// factor by the non-chromatic factor
		return co[i].getValue()*co[co.length-1].getValue();
		
		
	}
	
	public String [] getScaleNames() {
		return this.snames;
	}


	public int opn() {
		return on;
	}
	
	protected void initOPN( ) {
		super.MIDV = 1; // chromatic
		scales = new Scales();
		snames = scales.getScales();
		this.on = DFON;
	}

	public String getType() {
		return "mode lock";
	}

	public void setContextScale(LPart lfc, LPart ltc) {
		cosca = new TonalComposite();
		cosca.addPart(lfc.getPart());
		cosca.addPart(ltc.getPart());
		
	}

	public double differenceFunction(LPart f, LPart t) {
		if(this.toswi != t) {
			toswi = t;
			tt = new TonalComposite();
			tt.addPart(t.getPart());
		}
		
		// TODO Auto-generated method stub
		return 0;
	}
	


}
