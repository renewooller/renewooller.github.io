/*
 * Created on 20/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;

import org.w3c.dom.Element;

import ren.util.PO;
import ai.AllCompare;

public class Phase extends TransSearch {

	private final int DFON = 33;
	private int on;
	/* the different types of phase change that will be applied */
	public double [] VAL;
	
	private AllCompare ac = new AllCompare();
	
	public Phase() {
		super();
		
	}

	/**
	 * transform with all the different 0.25 phase shifts up to 4.0
	 * 
	 * judge distances using the nearest neighbour metric (and the AllCompare
	 * comprator, which can be set)
	 * 
	 * return the closest one, given the costs
	 */
	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
		
		// apply phase transformations
		applyPhase(f.getScope().getValue());
		
		// judge the distances
		judgeDistances(t);
						
	}
	
	/** 
	 * use the nearst neighbout distances to judge co [] to t.
	 */
	private void judgeDistances(LPart t) {
		for(int i=0; i<w.length; i++) {
			w[i] = this.differenceFunction(op[i], t);
		//	PO.p("w [ " + i + "] = " + w[i]);
		}
	}

	private void applyPhase(double scope) {
		for(int i=0; i< VAL.length; i++) {
			Mod.phaseShiftRT(op[i].getPart(), VAL[i], scope);
		//	PO.p("op["+ i + " ] = " + op[i].getPart());
		}
	}
	
	/**
	 * overwrite init costs so that there are only six parameters to deal with
	 * (not 33!)
	 * @see costOfIndex(int i)
	 */
	protected void initCostParams(int num) {
		super.initCostParams(6);
	}
	
	/**
	 * overwritten, so that instead of having a cost parameter for each 33
	 * operations, there are six "meta-parameters": 0, mod 1, mod 5, 0.25&0.75,
	 * gradient fwd, gradient bkwd
	 */
	protected double costOfIndex(int i) {
		double ret  = 0;
		
		// if it is  0-shift
		if(i == 16)
			ret = co[0].getValue();
		//  an integer shift
		else if(i%4 == 0)
			ret = co[1].getValue();
		// an in-between integer shift (ending in 0.5)
		else if(i%2 == 0)
			ret = co[2].getValue();
		// all others (ending in 0.25 or 0.75)
		else
			ret = co[3].getValue();
		
		// apply the distance from 0 costs
		double g = 1.0;
		if(i<16)
			g = co[4].getValue()*((i+1)/17.0); // 0+1/17 || 15+1/17
		else if( i>16)
			g = co[5].getValue()*(1.0 - ((i-16)/17.0)); //17-16/17 || 32-16/17
		
		// add the gradient and the other costs together using euclidean
		// distance, but also scaling (0.25 not 0.5) it so that the costs 
		// aren't so severe
		ret = Math.pow((ret*ret + g*g), 0.25);
		ret = ret/Math.pow(2, 0.25);
		
		return ret;
	}
	
	protected void setInitialCostValues() {

		// 0
		co[0].setName("none");
		co[0].setValue(1.0);
		
		// evrey 1.0
		co[1].setName("ever 1.0");
		co[1].setValue(0.95);
		
		// every 0.5
		co[2].setName("every other 0.5");
		co[2].setValue(0.9);
		
		// every 0.25 or 0.75
		co[3].setName("every other 0.25 or 0.75");
		co[3].setValue(0.85);
		
		// gradient*dist > 0
		co[4].setName("gradient > 0");
		co[4].setValue(0.8);
		
		// gradient*dist<0
		co[5].setName("gradient < 0");
		co[5].setValue(0.8);
		
	}
	
	public void config(int i) {
		super.config(i);
		co[4].setValue(0);
		co[5].setValue(0);
	}
	
	

	public AllCompare getAllCompare() {
		return ac;
	}

	public void setAllCompare(AllCompare ac) {
		this.ac = ac;
	}
	
	public int opn() {
		return on;
	}

	protected void initOPN() {
		this.on = this.DFON;
		
		super.MIDV = 16;
		VAL = new double [on];
		for(int i =0; i< 16; i++) {
			VAL[i] = -(16-i)*0.25;
			VAL[i+17] = (i+1)*0.25;
		}
		VAL[MIDV] = 0;
		
		
	}
	
	public String getType() {
		return "Phase";
	}

	public double differenceFunction(LPart f, LPart t) {
		return super.metric.difnn(f, t, ac);
	}


}
