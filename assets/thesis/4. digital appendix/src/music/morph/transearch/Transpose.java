/*
 * Created on 24/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;

import org.w3c.dom.Element;

import ren.tonal.TonalComposite;
import ren.util.PO;

public class Transpose extends TransSearch {

	private double [] VAL;
	private final int EX = 11; // the extent
	private final int DFON = EX*2 +1;
	private int on;
	
	private LPart toswi;
	private TonalComposite tt;
	
	/**
	 * Note: transposing affects the root pitch (in the tonal manager).  This
	 * has an important effect on the way mode-lock transsearch will work
	 *
	 */
	public Transpose() {
		super();
		
	}

	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
				
		//apply the different transpositions and find the distances according
		// to the tonal composite method
		for(int i=0; i< op.length; i++) {
			Mod.transposeRT(op[i].getPart(), (int)VAL[i]);
			op[i].getTonalManager().shiftRoot((int)VAL[i]); // for future calculations
			// find the difference in tonal composite
			w[i] = this.differenceFunction(op[i], t);
		}
				
	}
	
	protected void initCostParams(int num) {
		super.initCostParams(num + 4);
	}

	int TSM, TBI, ECU, NOF;
	protected void setInitialCostValues() {
		
		TSM = co.length-4;
		co[TSM].setValue(0.82); 
		co[TSM].setName("targ < 0");
		
		TBI = co.length-3; 
		co[TBI].setValue(0.82);
		co[TBI].setName("targ > 0");
		
		ECU = co.length-2;
		co[ECU].setValue(0.5);
		co[ECU].setName("exp curve");
		
		NOF = co.length -1;
		co[NOF].setValue(0.5);
		co[NOF].setName("non-fifth");
		
		
	//	PO.p("co length = " + co.length); 
		
		for(int i=0; i<11;i++) {
			co[i].setName("down " + (11-i));
		}
		
		co[11].setName("no shift");
		
		for(int i=12; i< 23; i ++) {
			co[i].setName("up " + (i-11));
		//	PO.p(" up " + i);
		}
		
	}
	
	protected double costOfIndex(int i) {
		if(i>VAL.length)
			return Double.NaN;
		double tr;
		
		if(i<EX)
			tr =  (1.0-
									 ((1.0-co[TSM].getValue())*
											 ((EX-i)/(EX*1.0))));
		// if i=0, and co = 75, we have (12-0)/12 = 1.0, * 1-0.75=0.25, and then
		// 1-0.25, which is a factor of 0.75 on the original cost.
		// as i > 0, (12-i)/12 gets smaller, making 1.0-cost smaller, making
		// 1-the result larger, which means there will be less of a factor as 
		// we get closer to the i=12 index which is 0 change
		else if(i>EX)
			tr = (1.0-
					 ((1.0-co[TBI].getValue())*
							 ((i-EX)/(EX*1.0))));
		// if i == 13, there is a 1/12th effect of the gradient (1-(1-(1/12)g)
		// i=14, 1/6th effect etc, until
		// i=24 1 whole effect of the gradient (1-(1-g)) = g
		else // middle
			return co[i].getValue();
		
		double exp = Math.pow((co[ECU].getValue()+0.5), 10);
		//PO.p("fac = " + fac + " exp = " + exp);
		tr = Math.pow(tr, exp);
		
		if( tr < 0.0000001)
			tr = 0;
		
		if(i != MIDV-7 && i != MIDV-5 && i != MIDV+5 &&  i != MIDV+7) {
			tr = tr*co[NOF].getValue();
		} 
		
		return tr*co[i].getValue();
		
	}
	
	
	public int opn() {
		return on;
	}

	protected void initOPN() {
		on = DFON;
		
		super.MIDV = EX;
		VAL = super.initVAL(EX);
		
	}

	public String getType() {
		return "Transpose";
	}

	public double differenceFunction(LPart f, LPart t) {
		if(this.toswi != t) {
			this.toswi = t;
			TonalComposite tt = new TonalComposite();
			tt.addPart(t.getPart());
		}
		
		return super.metric.diftc(f.getPart(), tt);
	}

}
