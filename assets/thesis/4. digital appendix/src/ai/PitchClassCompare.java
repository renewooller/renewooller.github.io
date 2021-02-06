/*
 * Created on May 5, 2006
 *
 * @author Rene Wooller
 */
package ai;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.util.PO;

public class PitchClassCompare extends Compare implements Domable {

	// the prominence of distance on Circle of Fifths
	private ParameterMap cof = (new ParameterMap()).construct(0, 100, 0, 1, 0.5, "cof");
	
	// the prominence of octave-equivalent comparison
	private ParameterMap oct = (new ParameterMap()).construct(0, 100, 0, 1, 0.5, "chroma");
	
	// the prominence of straight linear comparison
	private ParameterMap lin = (new ParameterMap()).construct(0, 100, 0, 1, 0.0, "lin");
	
	// steps per octave
	private int spo = 12;
	
	public PitchClassCompare() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * return the similarity of the two pitch classes, in terms of
	 * - linear distance
	 * - distance in circle of fifths
	 * 
	 * 1 is most similar. 0 is least similar
	 * 
	 */
	public double compare(double a, double b) {
		
		double line = super.abSim(a, b, 127); // get the straight linear
		
		// spread it out a bit 
		line = Math.pow(line, 6.0);
		
		double octe = super.modulusSim(a, b, spo); // octave equiv. similarity
		
		int fif = (int)(2.0*spo/3.0)-1; // get the interval that is 2/3 (fifth)
		a = ((a%spo)*fif)%spo;
		b = ((b%spo)*fif)%spo; // put it into this space
		double coli = super.modulusSim(a, b, spo); // circle similarity
		
		double ret = octe*oct.getValue() + coli*cof.getValue() + 
					 line*lin.getValue();
				
		ret = ret/(oct.getValue()+cof.getValue()+lin.getValue());
		
		return ret;
	}

	public ParameterMap getOctEqu() {
		return oct;
	}
	
	public ParameterMap getLin() {
		return lin;
	}
	
	public ParameterMap getCof() {
		return cof;
	}

	public void setCof(ParameterMap cof) {
		this.cof = cof;
	}

	public int getSpo() {
		return spo;
	}

	public void setSpo(int spo) {
		this.spo = spo;
	}

	
	public void dload(Element e) {
		this.cof.setValue(e.getAttribute(COF));
		spo = Integer.parseInt(e.getAttribute(SPO));
		if(e.hasAttribute(LIN)) { // if it is new
			this.lin.setValue(e.getAttribute(LIN));
			this.oct.setValue(e.getAttribute(OCT));
			
		} else { // if it is old
			this.oct.setValue(1.0);
			this.lin.setValue(1.0-this.cof.getValue());
		}
	}

	private static String COF = "cof";
	private static String SPO = "spo";
	private static String OCT = "oct";
	private static String LIN = "lin";
	
	public void dsave(Element e) {
		e.setAttribute(COF, cof.getValueStr());
		e.setAttribute(SPO, String.valueOf(spo));
		e.setAttribute(OCT, oct.getValueStr());
		e.setAttribute(LIN, lin.getValueStr());
		
	}

	public ParameterMap [] getParams() {
		return new ParameterMap [] {lin, oct, cof};
	}

}
