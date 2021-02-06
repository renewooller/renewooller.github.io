/*
 * Created on 23/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;

import org.w3c.dom.Element;

public class Octave extends TransSearch {

	private double [] VAL;// = new double [] {-3, -2, -1, 0, 1, 2, 3};
	private final int EX = 3;
	private final int DFON = EX*2+1;
	private int on;
	
	public Octave() {
		super();
		
	}

	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
		
		// make all the different octaves
		for(int i=0; i<op.length; i++) {
			if(f.isDEPA()) {
				Mod.transposeRT(op[i].getPart(), 
						(i-3)*f.getTonalManager().getDEPsPerOctave());
			} else {
				Mod.transposeRT(op[i].getPart(), 
						(i-3)*f.getTonalManager().getStepsPerOctave());
			}
		}
		
		// judge the distances 
		for(int i=0; i< w.length; i++) {
			w[i] = this.differenceFunction(op[i], t);
		}
		
	}

	/**
	 * the cost on the ends will be 0.55
	 */
	protected void setInitialCostValues() {
		double fac = 0.55; // this setting is made so that an octave shift of
		//  3 is only just selected
		for(int i=0; i< co.length; i++) {
			co[i].setName(Integer.toString((int)VAL[i]));
			co[i].setValue(1.0 - Math.abs((i-EX)/(EX*1.0))*(1-fac));
		}
	}
	
	public void config(int i) {
		super.config(i);
	}
	
	public int opn() {
		return on;
	}

	protected void initOPN() {
		this.on = this.DFON;
		
		super.MIDV = EX;
		VAL = super.initVAL(EX);
				
	}

	public String getType() {
		return "Octave";
	}

	public double differenceFunction(LPart f, LPart t) {
		return super.metric.difpiav(f, t);
	}

}
