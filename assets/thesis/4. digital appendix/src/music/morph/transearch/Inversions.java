/*
 * Created on 21/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;
import ren.util.PO;
import ai.AllCompare;
import ai.MTransformat;

public class Inversions extends TransSearch {

	private AllCompare ac = new AllCompare();
	
	private static final int DFON = 13;
	
	protected int on;
	
	private double [] VAL;
	
	//private LPart toswi = null; // switch, to detect if to has changed
	//private MTransformat tt = null;
	//TODO test this transformer
	
	
	/**
	 * inversions like chord inversions. ie, taking the lowest pitch and
	 * putting it up the top or visa-versa
	 *
	 */
	public Inversions() {
		super();
		
	}
	
	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
		
		if(f.isDEPA()) 
			applyInversions(f.getTonalManager().getDEPsPerOctave());
		else 
			applyInversions(f.getTonalManager().getStepsPerOctave());
		
		judgeDistances(t);
		
	//	return super.findClosest(steps, nochaf);
	}

	private void applyInversions(int spo) {
		for(int i=0; i< op.length; i++) {
			Mod.invertRT(op[i].getPart(), VAL[i], spo, 0, 127);
		}
	}
	
	
	/**
	 * find the distances of each transformed result from the target t
	 * using the pitch contour metric
	 * @param t
	 */
	private void judgeDistances(LPart t) {
		
		for(int i=0; i< op.length; i++) {
		//	MTransformat opt = super.mtconv.conv(op[i]);
			w[i] = this.differenceFunction(op[i], t);
		}
		
	}
	
	
	 /** overwrite init costs to include gradient as well
	 * @see costOfIndex(int i)
	 */
	protected void initCostParams(int num) {
		super.initCostParams(num+3);
	}
	
	protected void setInitialCostValues() {
		for(int i=0; i< VAL.length; i++) {
			co[i].setName(VAL[i]);
		}
		
		// let all the first 25 costs be 1.0 (no cost)
		// but make the gradients (the last to , be less
		co[co.length-3].setValue(1); 
		co[co.length-3].setName("grad < zero");
		
		co[co.length-2].setValue(1);
		co[co.length-2].setName("grad > zero");
		
		co[co.length-1].setValue(0.3);
		co[co.length-1].setName("exp curve");
		
//		 0.82 is the result of tuning it so that when using the complete
		// chromatic scale, an inversion of -6 semi-tones (the max) will be
		//selected over no inversion (just)
	}
	
	public void config(int i) {
		co[co.length-3].setValue(0); 
		co[co.length-2].setValue(0); 
		co[co.length-1].setValue(0.5); 
	}
	
	protected double costOfIndex(int i) {
		if(i>12)
			return Double.NaN;
		
		// val from -1 to +1
		double val = (i-super.MIDV*1.0)/super.MIDV*1.0;
		//PO.p("val = " + val);
		if(i<super.MIDV) {
			val = co[co.length-3].getValue()*val;
		//	PO.p(" < val = " + val);
		} else if(i>super.MIDV) {
			val = co[co.length-2].getValue()*val*-1;		
		//	PO.p(" > val = " + val);
		} else
			return co[i].getValue();
		
		val += 1.0;
		
		double exp = Math.pow((co[co.length -1].getValue()+0.5), 20);
		//PO.p("fac = " + fac + " exp = " + exp);
		val = Math.pow(val, exp);
		
		if( val < 0.0000001)
			val = 0;
		
		return co[i].getValue()*val;
		
	}
	
	public int opn() {
		return on;
	}

	protected void initOPN() {
		this.on = this.DFON;
		super.MIDV = 6;
		VAL = new double [(int)(super.MIDV*2+1)];
		double step = 1.0/12.0;
		for(int i=0; i< VAL.length; i++) {
			VAL[i] = step*(i-super.MIDV);
		}
	}

	public String getType() {
		return "inversions";
	}

	/**
	 * this needs to be tested
	 */
	public double differenceFunction(LPart f, LPart t) {
		if(f.getPart().length() == 0  ||
				  t.getPart().length() == 0) {
			
			return metric.difempty(f.getPart(), t.getPart());
		}
		
		//if(this.toswi != t) {
		//	this.toswi = t;
		//PO.p("f = ", f.getPart(), 2);
		//PO.p("t = ", t.getPart(), 2);
		return super.metric.difpic(super.mtconv.conv(f), super.mtconv.conv(t));
	//	return super.metric.difnn(f, t, ac);
	}
	
	

	

}
