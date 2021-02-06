/*
 * Created on 16/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;

import org.w3c.dom.Element;

import ren.util.PO;
import ai.MTransformat;

public class Rate extends TransSearch {

	/* the different types of rate change that will be applied */
	public double [] VAL; // length should = DFON
	
	private final int DFON = 7;
	
	private int on;
	
//	private LPart toswi;
//	private MTransformat tt;
	
	
	/**
	 * tested.
	 *
	 */
	public Rate() {
		super();
		super.MIDV = 3;
		
	}
	

	/**
	 * make 7 copies of f.
	 * 
	 * apply 7 different levels of general
	 * rate change to them:
	 * - 4
	 * - 2
	 * - 3/2
	 * - 1.0
	 * - 2/3
	 * -1/2
	 * -1/4
	 * 
	 * determine the distance of each transformed
	 * copy to t, combining avg-rate and pitch 
	 * contour metrics (difoiav and difpic).
	 * 
	 * store the distances in a double []
	 * 
	 * factor the cost params onto the weights.
	 * 
	 * find the smallest
	 * 
	 * return it
	 * 
	 * TODO work out wether slower rate should change scope
	 * 
	 */
	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		//PO.
		// initialise seven copies of f, into op[]
		super.resetOps(f);
	//	PO.p("r1");
		// transform each part in op[] by factors in VAL
		applyRate(f.getScope().getValue());
	//	PO.p("r2");
	//	PO.p("1");
		// use the average rate and the pitch contour metric
		// to judge the distances of co [] to t.
		judgeDistances(t);
		//PO.p("r3");
	}
	
	/**	 transform each part in op[] by factors in VAL */
	private void applyRate(double scope) {
		for(int i=0; i<op.length; i++) {
			
			Mod.rateLoop(op[i].getPart(), VAL[i], scope);
		//	PO.p(" after rate: " + VAL[i] + " \n", op[i].getPart(), 1);
		}
	}
	
	/** 
	 * use the inter-onset contour and the pitch contour metrics
	 *  to judge the distances of co [] to t.
	 */
	private void judgeDistances(LPart t) {
		
		//PO.p("t = " + t.getPart().toString());
		
		
		for(int i=0; i< w.length; i++) {
			this.w[i] = this.differenceFunction(op[i], t);
		//	PO.p("w[i] = " + w[i]);
		}
	}
	
	/** overwrite init costs so that there is an extra
	 * @see costOfIndex(int i)
	 */
	protected void initCostParams(int num) {
		super.initCostParams(8);
	}
	
	
	/** 
	 * the costs will start out favouring whole rate
	 * changes (2, 4, 0.5, 0.25) more.
	 */ 
	protected void setInitialCostValues() {
		co[0].setValue(0.8);
		co[0].setName("four slow");
		
		co[1].setValue(0.95);
		co[1].setName("two slow");
		
		co[2].setValue(0.7);
		co[2].setName("three over two slow");
		
		co[3].setValue(1.0);
		co[3].setName("no change");
		
		co[4].setValue(0.7);
		co[4].setName("two over three fast");
		
		co[5].setValue(0.95);
		co[5].setName("two fast");
		
		co[6].setValue(0.8);
		co[6].setName("four fast");
		
		co[7].setValue(1.0);
		co[7].setName("duration rate");
		
		
		//for(int i=0; i< co.length; i++) {
			//co[i].setName(VAL[i]);
		//}
	}
	
	public void config(int i) {
		super.config(i);
	}
	
	public int opn() {
		return on;
	}

	protected void initOPN() {
		on = DFON;
		
		VAL = new double [] {
				4.0, 2.0, (3.0/2.0), 1.0, (2.0/3.0), 0.5, 0.25
		};
	//	PO.p("initins OPN = " + DFON);
		
	}


	public String getType() {
		return "Rate";
	}


	public double differenceFunction(LPart f, LPart t) {
		if(f.getPart().length() == 0 && t.getPart().length() == 0) {
			return 0.0;
		} else if(f.getPart().length() == 0  ||
				  t.getPart().length() == 0) {
			
			return metric.difempty(f.getPart(), t.getPart());
		}
		
		//PO.p("rate dif");
		//if(t != toswi) {
		//	toswi = t;
	//	PO.p("rate d1");
		MTransformat tt = super.mtconv.conv(t);
		//}
		MTransformat top = super.mtconv.conv(f);
		
		double m1 = super.metric.difioc(top, tt);
		double m2 = super.metric.difpic(top, tt);
		//PO.p("rate e dif");
		return Math.pow(m1*m1 + m2*m2, 0.5); // or /2 TODO hmmm
		
		
	}
	
	

}
