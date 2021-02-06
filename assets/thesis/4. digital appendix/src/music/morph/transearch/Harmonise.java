/*
 * Created on Apr 3, 2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.data.Part;
import jm.music.data.Phrase;
import music.LPart;
import ren.gui.ParameterMap;
import ren.util.PO;
import ai.An;

public class Harmonise extends TransSearch {

	private ParameterMap clumpFac =
		(new ParameterMap()).construct(1, 8, 0, 1.0, 0.125, "clumping factor");
	
	
	//private int in
	
	//private double avhainTo = Double.MAX_VALUE;
	
	public Harmonise() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int opn() {
		return 13;
	}

	/** use default */
	protected void initOPN() {
		super.MIDV = 6;
	}

	/** use default */
	protected void setInitialCostValues() {
		co[0].setName("de oct");
		co[1].setName("de 7th");
		co[2].setName("de 6th");
		co[3].setName("de 5th");
		co[4].setName("de 4th");
		co[5].setName("de 3rd");
		co[6].setName("nothing");
		co[7].setName("ha 3rd");
		co[8].setName("ha 4th");
		co[9].setName("ha 5th");
		co[10].setName("ha 6th");
		co[11].setName("ha 7th");
		co[12].setName("ha oct");
	//	super.initCostParams(opn());
	}
	
	public void config(int i) {
		super.config(i);
	}

	protected void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
		
		// find which notes are harmonised, and what the intervals are
		int [][] harda = An.harmonicClumps(f.getPart(), 0.001);
		
	//	double avhain = An.avhain(t.getPart(), 0.001);
	//	PO.p(avhain);
	//	PO.p("harda length = " + harda.length);
		
	//	PO.p(" harda = ", harda);
	//	PO.p("going to deharm");
		// deharmonise octaves
		int a = 0;
		
		// assume depa
		//0 
		deharmonise(op[a], 7*2, harda); // was 12
		w[a] = this.differenceFunction(op[a++], t);
		
		//.PO.
		// 1
		deharmonise(op[a], 6*2, harda);
		w[a] = this.differenceFunction(op[a++], t);
		
		// 2
		deharmonise(op[a], 5*2, harda);
		w[a] = this.differenceFunction(op[a++], t);
		
		//3
		deharmonise(op[a], 4*2, harda);
		w[a] = this.differenceFunction(op[a++], t);
		
		// 4
		deharmonise(op[a], 3*2, harda);
		w[a] = this.differenceFunction(op[a++], t);
		
		// 5
		deharmonise(op[a], 2*2, harda);
		w[a] = this.differenceFunction(op[a++], t);
		
	    // 6
		w[a] = this.differenceFunction(op[a++], t);
	//	PO.p(" a = " + a + " and th epart is ", op[a].getPart(), 1);
				
		
		//if(w[])
// unlike deharmonise, use the DEPA format
		//if (f.isDEPA()) {
			harmonise(op[a], 2*2, harda); //3
			w[a] = this.differenceFunction(op[a++], t);

			harmonise(op[a], 3*2, harda); 
			w[a] = this.differenceFunction(op[a++], t);
			
			harmonise(op[a], 4*2, harda); 
			w[a] = this.differenceFunction(op[a++], t);
		//	PO.p(" wa = " + w[a-1]);
			
			harmonise(op[a], 5*2, harda); 
			w[a] = this.differenceFunction(op[a++], t);
			
			harmonise(op[a], 6*2, harda); 
			w[a] = this.differenceFunction(op[a++], t);
			
			harmonise(op[a], 7*2, harda); 
			w[a] = this.differenceFunction(op[a++], t);
		//	PO.p(" wa = " + w[a-1]);
	//	} else {
	//		harmonise(op[a], 4, harda);
	//		w[a] = this.differenceFunction(op[a++], t);

	//		harmonise(op[a], 7, harda);
	//		w[a] = this.differenceFunction(op[a++], t);

	//		harmonise(op[a], 12, harda);
	//		w[a] = this.differenceFunction(op[a++], t);
	//	}
	//	PO.p("w = ", w);
		
	//	for(int i=0; i< op.length; i++) {
	//		PO.p("op in harmonise =  " + i, op[i].getPart(), 1);
	//	}
		
	////	PO.p("op 2 = ", op[2].getPart(), 1);
	}
	
	private void harmonise(LPart p, int v, final int [] [] harda) {
		int utoco = 0;// where up to count
		for(int i=0; i< harda.length; i++) {
			if(harda[i] == null) {
				break;
			}
			
			if(harda[i].length == 1) { // if it isn't already harmonised
				Phrase phr = p.getPart().getPhrase(utoco).copy();
				// if it is in DEPA format but not in 7tatonic scale
				if(p.isDEPA() && p.getTonalManager().getScale().length != 7) {
			//		phr.getNote(0).setPitch(phr.getNote(0).getPitch() + 
			//				p.getTonalManager().getDEPA(v));
					try {
						Exception ex = new Exception("can't deal with non-7 scales in harmonise yet");
						ex.fillInStackTrace();
						throw ex;
					}catch (Exception e) {
						e.printStackTrace();
					}
				} else {// otherwise, add the harmonic value
					// if in depa, v will be in depa too
					phr.getNote(0).setPitch(phr.getNote(0).getPitch() + v);
				}
				p.getPart().getPhraseList().add(utoco, phr);
				utoco++;
			}
			
			utoco += harda[i].length;
			
		}
		
	}

	private void deharmonise(LPart p, int v, final int[][] harda) {
		int lofset = 0; 
		int pos = 0;
		for(int i=0; i<harda.length-lofset; i++) {
			if(harda[i] == null)
				break;
			
			for(int j = 0; j<harda[i].length; j++) {
				
				if(p.isDEPA()) {
					
					if(harda[i][j] == v) {
						//if(v == 7) {
						//	PO.p("pos = " + pos);
						//	PO.p("v=7, posth" + p.getPart().getPhrase(pos-lofset).toString());
						//}
						
						p.getPart().removePhrase(pos-lofset);
						lofset++;
					}
				} 
				pos++;
			}
		}
	
	}
	
	public String getType() {
		
		return "harmonise";
	}

	/**
	 * stores the average harmonic interval for t, to speed things up, so
	 * if using a different to, this.avhainTo will need to be reset to 
	 * Double.MAXVALUE
	 * 
	 */
	public double differenceFunction(LPart f, LPart t) {
		
		if(f.getPart().length() == 0  || t.getPart().length() == 0) {	
			return metric.difempty(f.getPart(), t.getPart());
		}
		
		//if(this.avhainTo == Double.MAX_VALUE) {
		//this.avhainTo = ;
		//}
		
	//	PO.p("f = ", f.getPart(), 1);
	//	PO.p("avahin = " + An.avhain(f.getPart(), 0.001));
		double difavhain = super.metric.difavhain(f.getPart(), An.avhain(t.getPart(), 0.001));
		
		double picd = super.metric.difharclumav(f, t);
		double w = 0.8;
		//PO.p("difavhain = " + difavhain + " picd = " + picd);
		return (w*difavhain) + ((1-w)*picd);
	}

}
