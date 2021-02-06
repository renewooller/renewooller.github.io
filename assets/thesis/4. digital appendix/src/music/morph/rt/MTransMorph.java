/*
 * Created on 14/01/2006
 *
 * @author Rene Wooller
 */
package music.morph.rt;

import java.util.TreeMap;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jmms.TickEvent;
import music.LPart;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.music.PhraseQ;
import ren.tonal.Scales;
import ren.tonal.TonalComposite;
import ren.util.PO;
import ai.MTConverter;
import ai.MTransformat;

public class MTransMorph extends MorpherRT {

	private boolean started = false;
	
	private double ca = 0; // current area (used to work out when to place note)
	private double cv = 0; // current value (actually, the value just before
	private double cd = 0; // current distance since the last note played
	
	private double res = 0;
	private MTransformat [] mts = new MTransformat [2];
	
	private MTConverter mtc = new MTConverter();
	
	private Part [] toRet = new Part [2];
	
	private ParameterMap [] params = new ParameterMap [7];
		
	private TonalComposite tc = new TonalComposite();
	private int [] scale;
	private double [] scWeights;
	
	private int initialMorphLength;
	
	public MTransMorph() {
		super();
		params[0] = (new ParameterMap()).construct(0, 2, 2, "rate mode"); 
		//0 normal, 1 is log, 2 is quantise
		params[1] = (new ParameterMap()).construct(0, 1, 1, "lock to known pitch");
		// 1 is lock to known pitches
		params[2] = (new ParameterMap()).construct(0, 1, 0, "carry remainder");
		// 1 is carry remainder
		params[3] = (new ParameterMap()).construct(0, 4000, -0.2, 0.2, 0.0, "anticipation");
		params[3].setDecimalPlaces(4);
		params[4] = (new ParameterMap()).construct(0, 1, 1, "reset every scope");
		params[5] = (new ParameterMap()).construct(0, 1, 1, "simulate area at each frame");
		
		params[6] = (new ParameterMap()).construct(0, 1, 1, "current value or time since last note");
		
		
	}	

	public Part[] morphRT(LPart[] toFrom, LPart[] mlparts, double mi, TickEvent e,
			PhraseQ hist) {
		
		res = e.getRes();
//		 offset the at by the phase (eg if phase is ahead, we will need to
		// generate the first note at the e.at() slot that = phase
		double phs = inter(MTransformat.PHA, e.at(), false, mi);
		phs = ((int)(phs/0.25))*0.25;
		
		double at = (e.at() - phs + 
				mlparts[0].getScope().getValue())%mlparts[0].getScope().getValue();
					
		// the first one will require an estimation of the current position
		if(this.started == false) {
			this.started = true;
			initAreaValue(mi, e.getRes(), at);
			
		}
		
		// calculating the area for this point at each frame
		if(params[5].getValue() == 1) {
			initAreaValue(mi, e.getRes(), at);
		}
		
		// if the mtransformat hasn't been initialised, initialise it. (shouldn't happen anyway)
		if(mts[0] == null)
			initParts(toFrom[0], toFrom[1], mlparts, this.initialMorphLength);
		
		// empy the note containers from the previous tick
		toRet[0].empty();
		toRet[1].empty();
		
		//TODO make it so that the morph starts on 0
	//	if(e.at()%mlparts[0].getScope().getValue() == 0) {
			
	   //initAreaValue(mi, e.getRes(), mlparts[0].getScope().getValue());
	//		PO.p("initialising area : " + ca);
	//	}
		
			
		
//		 this is a test
//		cv = inter(MTransformat.RAT, at, false, mi);
		
		
		if(at == 0 && params[4].getValue() == 1) {
			//cv = 0;
			ca = cv*cv; // so it will make a note
			cd = cv; //a
	//		PO.p("reseting ... cd = " + cd);
		}
		
	//	PO.p(" at = " + at + " cv = " + cv + " cd = " + cd + " ca = " + ca +
	//			"phs = " + phs + " e.at() " + e.at() + " res = " + e.getRes());
		
		
		//check to see if a note needs to be made
		if(checkMakeNote()) {
			//PO.p("note\n");
			// make it
			Phrase phr = interPhrase(at, mi);
			if(mi < 0.5)
				toRet[0].add(phr);
			else
				toRet[1].add(phr);
		} else {
			// don't make it
		}
		
		//find the rate
		cv = inter(MTransformat.RAT, at, false, mi);
		
		cd += e.getRes(); // update the distance since the last note
		
		// accumulate the area of this slice with previous areas
		ca = ca + cv*e.getRes();
		
	//	PO.p("ret 0 = ", toRet[0], 1);
	//	PO.p("ret 1 = ", toRet[1], 1);
		
		return toRet;
	}
	
	private Phrase interPhrase(double at, double mi) {
		Phrase phr = new Phrase();
		Note n = new Note((int)this.inter(MTransformat.PIT, at, false, mi), 1.0, 
					 (int)(this.inter(MTransformat.VEL, at, false, mi)));
		n.setDuration(this.inter(MTransformat.DUR, at, false, mi));
		
		phr.add(n);
		
		return phr;
	}
	
	

	/**
	 * this method iterates through the process of rendering notes to obtain
	 * the correct starting values to start generating notes.
	 * @param mi
	 * @param res
	 * @param sco
	 */
	private void initAreaValue(double mi, double res, double target) {
		
		double at = 0;
		
		
		this.cv = inter(MTransformat.RAT, at, false, mi);
		this.cd = cv;
		this.ca = cv*cv;
		//PO.p("calculating ca ------------- \n");
		while (at  < target) { // temp added + res
			
			checkMakeNote();
			
			cv = inter(MTransformat.RAT, at, false, mi);
			cd += res;
			
			ca += cv*res;
			
			//PO.p("ca = " + ca); // no double accuracy errors found
			
			// increment at
			at = (((int)(at/res)) + 1)*res;
		}
		
		// now the ca and cv should be set correctly to start generating notes
		PO.p("inited area value cv = " + cv + " cd = " + cd + " ca = " + ca);
	}
	
	
	
	/**
	 * uses the current area (ca) and the current value (cv) to check to see if
	 * a note should be made.
	 * 
	 * ca is updated automatically
	 * @return true if a note should be made
	 * 		   false if it shouldn't be made
	 */
	private boolean checkMakeNote() {
		
		if(params[6].getValueInt() == 0) {
		
			if(cv*cv-params[3].getValue() <= ca) {
				if(params[2].getValue() == 0 || cv == 0) {
					ca = 0;
				} else {
					ca = (ca)%(cv*cv);
				} 
				return true;
			} else 
				return false;
		} else {
		//	PO.p(" cd = " + cd);
		//	PO.p(" ca/cd = " + (ca/cd));
			if(cd -params[3].getValue() >= ca/cd) {
				if(params[2].getValue() == 0 || cv == 0) {
					ca = 0;
				} else {
					ca = (ca)%(cd*cd);
				} 
				cd = 0; // reset the distance
				return true;
			} else 
				return false;
		}
	}
	
	/**
	 * note, this interpolate is for wether or not to linearly interpolate the 
	 * points on the envelope.
	 * the actual morphing aways interpolates linearly between the different
	 * envelopes
	 * @param type
	 * @param at
	 * @param interpolate
	 * @param mi
	 * @return
	 */
	private static double rpow = Math.log(3)/Math.log(2);
	private static double [] pows = new double [] {0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0};
	private double inter(int type, double at, boolean interpolate, double mi) {
		
		// this option enables rate to be interpolated using a squared gradient
		if (type == MTransformat.RAT) {
			if (params[0].getValue() == 1) {
				double a = mts[0].getDimAt(type, at, interpolate);
				double b = mts[1].getDimAt(type, at, interpolate);
				double c = (a - b) * Math.pow((1 - mi), rpow) + b;
				return c;
			} else if( params[0].getValue() == 2) {
				double c = (mts[0].getDimAt(type, at, interpolate)*(1-mi) +
						mts[1].getDimAt(type, at, interpolate)*(mi));
				double mind = Double.MAX_VALUE;
				int ind = -1;
				for(int i=0; i< pows.length; i++) {	
					if(Math.abs(c-pows[i]) < mind) {
						mind = Math.abs(c-pows[i]);
						ind = i;
					}
				}
				return pows[ind];
				
			}
			// if locking pitch
		} else if(type == MTransformat.PIT && params[1].getValue() == 1) {
			int tr = (int)((mts[0].getDimAt(type, at, interpolate)*(1-mi) +
					mts[1].getDimAt(type, at, interpolate)*(mi)) + 0.5);
		//	PO.p("tr = " + tr);
			tr = Scales.getInstance().pitchFromScale(tr, scale, tc.getKey(), 
										 scWeights, tc.getSPO());
		//	PO.p("tra= " + tr);
			return tr;
		}
			
		
		
		return (mts[0].getDimAt(type, at, interpolate)*(1-mi) +
				mts[1].getDimAt(type, at, interpolate)*(mi));
	}
	
	public void startInit() {
		ca = 0;
	//	this.initAreaValue(0.0, mts[0].getQuantise(), mts[0].getScope());
		//this.started = true;
	}
	
	public  void initParts(LPart lfrom, LPart lto, LPart [] lpmorph, int morphLength) {
		mts[0] = mtc.conv(lfrom.getPart(), 
				  lfrom.getQuantise().getValue(), 
				  lfrom.getScope().getValue());

		mts[1] = mtc.conv(lto.getPart(), 
				  lto.getQuantise().getValue(),
				  lto.getScope().getValue());
		
		toRet[0] = lfrom.getPart().copyEmpty();
		toRet[1] = lto.getPart().copyEmpty();
		
		tc.addPart(lfrom.getPart());
		tc.addPart(lto.getPart());
	//	PO.p("lfrom ", lfrom.getPart(), 1);
	//	PO.p("lto   ", lto.getPart(), 1);
	
		TreeMap degs = tc.pitchesToDegrees(tc.getKey());//lfrom.getTonalManager().getRoot());
		scale = tc.extractScale(degs);
		PO.p(tc.toString());
	//	PO.p("scale ", scale);
	//	PO.p("key = " + tc.getKey());
		scWeights = tc.extractWeights(degs);
		this.initialMorphLength = morphLength;
	}
	
	public void finish() {
		started = false;
	}

	public ParameterMap[] getPC() {
		return this.params;
	}

	public String getType() {
		return "music transform morph";
	}

	public void dload(Element e) {
        for (int i = 0; i < this.params.length; i++) {
            String str = e.getAttribute(params[i].getName()
                .replace(' ', '_'));
            if (str.length() > 0) {
                params[i].setValue(str);
            }
        }
    }

    public void dsave(Element e) {
        e.setAttribute("type", this.getType());
        for (int i = 0; i < this.params.length; i++) {
            e.setAttribute(params[i].getName()
                .replace(' ', '_'), params[i].getValueStr());
        }
    }

}
