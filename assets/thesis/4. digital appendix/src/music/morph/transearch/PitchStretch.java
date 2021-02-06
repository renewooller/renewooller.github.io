/*
 * Created on 21/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;
import ren.util.PO;
import ai.An;

public class PitchStretch extends TransSearch {

	public double [] VAL;
	private static final int DFON = 15;
	private int on;
	
//	private LPart toswi;
//	private double avpinfut;
	
	
	//TODO test it
	
	
	/**
	 * note: pitch stretch requires the TonalManager of any lpart used to
	 * be consistent with the Part within the LPart
	 *
	 */
	public PitchStretch() {
		super();
	}

	/**
	 * initialise 25 copies (op)
	 */
	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		
		super.resetOps(f);
		
		// An.fun finds the fundamental pitch that is nearest to the average
		// the root key pitch is specified by the tonal manager
		applyStretch(An.fun(f), f.getTonalManager().getStepsPerOctave(), f);
		
		judgeDistances(t);
	}

	
	private void judgeDistances(LPart t) {
		
		for(int i=0; i< op.length; i++) {
			w[i] = this.differenceFunction(op[i], t);
			//PO.p("original w = " + w[i] );
		}
	}

	private void applyStretch(int fund, int spo, final LPart f) {
		
		for(int i=0; i< VAL.length; i++) {
			if(!f.isDEPA()) {
				Mod.expandFunIntRT(op[i].getPart(), fund, VAL[i], spo, 0, 127);
			} else {
				Mod.expandFunDEPAintRT(op[i], fund, VAL[i], spo, 0, 
						f.getTonalManager().getDEPsPerOctave()*10); // ten octaves
			}
			
		}
	}
	
	
	protected void initCostParams(int num) {
		super.initCostParams(4);
	}
	
	protected void setInitialCostValues() {
		co[0].setValue(1.0);
		co[0].setName("gradient <1");
		
		co[1].setValue(1.0);
		co[1].setName("0");
		
		co[2].setValue(1.0);
		co[2].setName("gradient >1");
		
		co[3].setValue(0.5);
		co[3].setName("exponential curve");
		
		/*
		final double st2 = 1.0/(co.length*2.0);
		for(int i=0; i< co.length; i++) {
			co[i].setName(VAL[i]);
			co[i].setValue(1-i*st2);
			
		}*/
	}
	
	public void config(int i) {
		co[0].setValue(0.0);
		co[2].setValue(0.0);
		co[3].setValue(0.5);		
	}
	
	protected double costOfIndex(int i) {
		
		double val = (i*1.0-MIDV)/(MIDV*1.0);
				
		if(i<MIDV) {
			//PO.p("co[0].getValue()  = " + co[0].getValue() + " val = " + val);
					
			val = (val*co[0].getValue());
		} else if(i == MIDV)
			return co[1].getValue();
		else if(i > MIDV)
			val = (val*co[2].getValue())*-1;
		else
			return Double.NaN;
		
		val = val + 1.0;
		
		double fac = Math.pow((co[3].getValue()+0.5), 20);
		
		val = Math.pow(val, fac);
		
		if( val < 0.0000001)
			val = 0;
		
		
		return val;
	}

	public int opn() {
		return on;
	}

	protected void initOPN() {
		on = this.DFON;
		super.MIDV = 7;
		VAL = new double [on];
		final double st = 1.0/7.0;
		for(int i=0; i<VAL.length; i++) {
			VAL[i] = st*i;
		}
		
		
	}
	
	public String getType() {
		return "Pitch Stretch";
	}

	public double differenceFunction(LPart f, LPart t) {
		if(f.getPart().length() == 0  ||
				  t.getPart().length() == 0) {
			
			return metric.difempty(f.getPart(), t.getPart());
		}
		//if(this.toswi != t) {
		//	toswi = t;
		//	avpinfut = ;
		//}
	//	PO.p(" pitch stretch dif func. fun for f = " + An.fun(f) + " for t = " + An.fun(t));
	//	PO.p(" An.avpinfu(t) = " + An.avpinfu(t) + "  An.avpinfu(f) = " + An.avpinfu(f));
		return super.metric.difpinfu(f, An.avpinfu(t));
		
	}


}
