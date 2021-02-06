/*
 * Created on May 3, 2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.data.Part;
import music.LPart;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.io.Domc;
import ren.tonal.TonalManager;
import ai.PitchClassCompare;

public class KeyMoSearch extends TransSearch {

	private double [] deglop;
	private LPart def, det;// = new LPart();

	private int on;
	
	private PitchClassCompare pcc = new PitchClassCompare();
	
	// parameter controlling the influence of key-distance 
	private ParameterMap keyd = (new ParameterMap()).construct(0, 100, 0, 1,
								  1.0, "key dis");
	
	private ParameterMap rootd = (new ParameterMap()).construct(0, 100, 0, 1,
			  1.0, "root dis");
	
	private ParameterMap scaled = (new ParameterMap()).construct(0, 100, 0, 1,
			  1.0, "scale dis");
	
	
	private ParameterMap sameLength = (new ParameterMap()).construct(0, 1, 0, 1,
			  1.0, "same scale length");
	
	/**
	 * 
	 * this is a special type of transearch that looks exclusively at the key
	 * and mode combinations
	 *
	 */
	public KeyMoSearch() {
		super();
		
	}

	public int opn() {
		//PO.p("on in opn = " + on);
		return on;
	}

	protected void initOPN() {
		
		def = (new LPart()).construct(new Part());
		det = (new LPart()).construct(new Part());
		deglop = new double [] {0, 0, 1, 1};
		// all the possible operations are the number of different 
		// keys and scales
		this.on = def.getTonalManager().getStepsPerOctave()*10; // (1-10 only)
				//  def.getTonalManager().getScales().size();
		//PO.p("on = " + on);
		
		this.MIDV = 0;
		
	}

	protected void setInitialCostValues() {
		// TODO add a cost for linear and fifths key shifts as well as favouring 
		// different modes
		//for(in)
	//	super.initCostParams(on);
	}
	
	protected void initCostParams(int np) {
		super.initCostParams(0);
	}
	
	/*
	protected double costOfIndex(int i) {
		return 1.0;
	}*/

	/*
	protected double costOfIndex(int i) {
		return 1.0;
	}*/
	
	protected void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.resetOps(f);
		
	//	PO.p("findb starting " + steps);
		
		// go through all the key shifts
		//int rooshi= 0;
		int spo = f.getTonalManager().getStepsPerOctave();
		int hs = (int)(spo/2 + 0.5);
		int modes = 10; // just use modes from 1-10 (ignore chromatic and 
		// wt/ht scales)
		// NOTE: a special parameter controls the dissimilarity rating of scales that are
		// of a different length.
		
		for(int i=0; i<spo; i++) {
		//	PO.p("findb i " + i);
			//rooshi = (i+hs)%spo - hs;
			//PO.p(" i =  " + i);
			// go through all the mode shifts for this key shift
			for(int j=0; j < modes; j++) {
		//		PO.p("   findb j " + j);
			//	PO.p(" j =  " + j);
				
				int curat = i*(modes) + j;
				// shift the root
				op[curat].getTonalManager().shiftRoot(i);
				
				// convert from 1-10 to 0-9
				int newMode = (op[curat].getTonalManager().getScaleType()-1);
			
				// increment and wrap (9+1 goes to 0)
				newMode = (newMode+j)%modes;
				
				// convert from 0-9 to 1-10;
				newMode = newMode+1;
								
				// change the mode
				op[curat].getTonalManager().setScale(newMode);	
				
				// record the difference
				this.w[curat] = differenceFunction(op[curat], t);
				
			}	
		}	
		//PO.p(" end of findb w = ", w);
	}

	public double differenceFunction(LPart f, LPart t) {
		if(sameLength.getValue() == 1 && 
				f.getTonalManager().getScale().length != 
					t.getTonalManager().getScale().length) {
			return 1.0;
		}
		
		// difference in the occurence of pitches only
		double pid = metric.difKey(f.getTonalManager(), t.getTonalManager());
		
		// difference in root key, using the circle of fifths
		double rod = (1-this.pcc.compare(f.getTonalManager().getRoot(),
									  t.getTonalManager().getRoot()));
				
		// difference in occurence of pitches in scaleType only
		double scad = metric.difScale(f.getTonalManager().getScale(),
									  t.getTonalManager().getScale());
		
		double norm = (this.keyd.getValue()+ 
					  this.rootd.getValue() +
					  this.scaled.getValue());
				
		double ret = pid*this.keyd.getValue() + 
					 rod*this.rootd.getValue() +
					 scad*this.scaled.getValue();
		
		ret = ret/norm;
		
		return ret;
	}

	public String getType() {
		return "Key_Morph_Search";
	}

	public LPart find(TonalManager from, TonalManager to, int steps, double [] glopar) {
		if (glopar == null)
			glopar = this.deglop;
		
		this.def.setTonalManager(from);
		this.det.setTonalManager(to);

		try {
			return super.find(def, det, steps, glopar);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
		
	}
		
	protected double costOfIndex(int i) {
		return 1.0;
	}

	
	public void dload(Element e) {
		super.dload(e);
		if(e.hasAttribute(KEYD))
			keyd.setValue(e.getAttribute(KEYD));
		if(e.hasAttribute(ROOTD))
			rootd.setValue(e.getAttribute(ROOTD));
		if(e.hasAttribute(SCALED))
			scaled.setValue(e.getAttribute(SCALED));
		
		Element pce = Domc.find(e, PCC);
		if(pcc != null)
			pcc = (PitchClassCompare)Domc.lo(pce, e.getOwnerDocument());
		
	}

	private static String KEYD = "keyd";
	private static String ROOTD = "rootd";
	private static String SCALED = "scaled";
	private static String PCC = "pcc";
	
	public void dsave(Element e) {
		super.dsave(e);
		
		e.setAttribute(KEYD, keyd.getValueStr());
		e.setAttribute(ROOTD, rootd.getValueStr());
		e.setAttribute(SCALED, scaled.getValueStr());
		
		e.appendChild(Domc.sa(pcc, PCC, e));
		
	}

	/**
	 * The pitchClass compare is used for comparing the roots of keys
	 * @return
	 */
	public PitchClassCompare getPitchClassCompare() {
		return this.pcc;
	}

	public ParameterMap getKeyd() {
		return keyd;
	}

	public ParameterMap getRootd() {
		return rootd;
	}

	public ParameterMap getScaled() {
		return scaled;
	}
	
	public ParameterMap getSameLength() {
		return this.sameLength;
	}

}
