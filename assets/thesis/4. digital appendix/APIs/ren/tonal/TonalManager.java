/**
 * The TonalManager keeps track of the mode and scale degree of whatever is
 * using it.
 *
 * It is so that the Note itself needn't have multiple representations.
 * instead, when you want to transpose the scale degree, you go
 * tonalManager.transpose(note, -4);
 * 
 * perhaps extra methods could be added to note that support this.
 *
 */
package ren.tonal;

import java.util.Vector;

import jm.music.data.Note;
import music.LPart;

import org.w3c.dom.Element;

import ren.io.Domable;
import ren.util.PO;

public class TonalManager implements Domable {

    public static final int DEFAULT_CHORD = 0; // {1,
                                                // 3, 5,
                                                // 7};

    public static final int DEFAULT_SCALE = 0;

    public static final int DEFAULT_ROOT = 0;

    private Scales scales = Scales.getInstance();

    // the default tuning system is Equal Temperment
    private TuningSystem tuningSystem = new TuningSystem();

    // the current scale/mode/tonally accepable
    // pitchclasses are stored in scale
    private int scaleType;

    // details of chord are yet to be fleshed out
    private int chord;

    // the root of the scale: 0 is C, 1 is C#... 11 is B
    private int root;

    // Note: if you want F Minor, the root is 5 and
    // scale is Aeolian

    private Vector pman = new Vector(32); // parts that are managed
   
   // private int [] tocosc = null; // temporary custom scale
    
    private int stepehot; // the number of passing steps needed for a DEPA
    // representation of this scale
    
    public TonalManager(int scale, int root, int chord) {
        this.setScale(scale);
    	this.setRoot(root);
       
        this.chord = chord;
    }

    public TonalManager(int scale, int root) {
        this(scale, root, DEFAULT_CHORD);
    }

    public TonalManager(int scale) {
        this(scale, DEFAULT_ROOT);
    }

    public TonalManager() {
        this(DEFAULT_SCALE, DEFAULT_ROOT, DEFAULT_CHORD);
    }

    /**
     * root must be from 0 - number of octves in tuning
     * system
     */
    public void setRoot(int nuRoot) {
        if (nuRoot < 0
                || nuRoot >= tuningSystem.getStepsPerOctave()) {
            Error e = new Error(
                    "the root must be from 0 to "
                            + (tuningSystem.getStepsPerOctave() - 1));
            e.fillInStackTrace();
            try {
                throw e;
            } catch (Error er) {
                er.printStackTrace();
            }
        }
        
        this.root = nuRoot;
        
    }

    public void setScale(int nuscale) {
    	setScale(nuscale, true);
    }
    
    public void setScale(int nuscale, boolean upDep) {
    	if(nuscale > scales.size()) {
    		Exception e = new Exception("scale " + nuscale + " hasn't been " +
    				"created");
    		try {e.fillInStackTrace(); throw e;	
    		} catch(Exception ex) {ex.printStackTrace();}
    	}
        this.scaleType = nuscale;
       // this.tocosc = null;
        if(upDep)
        	updateDEPASteps(); // sometimes you want it to change scale but keep depa
    }
    
	public void setScale(int [] cosca) {
		this.scales.addScale(cosca);
		setScale(scales.size());
	}
	
	public int getPassingSteps() {
		return this.stepehot;
	}
	
	/**
	 * doesn't change the state of this tm.
	 * uses the scale passed to claculate the largest step
	 * @param sc
	 * @return
	 */
	public int calculateLargestStep(int [] sc) {
		int lag = Integer.MIN_VALUE; // records the largest gap
		
		for (int i=1; i< sc.length; i++) {
			// find the previous gap, and see if it is the smallest so far
			int preg = (sc[i]-sc[i-1]);
			if(preg > lag)
				lag = preg; // if it is, record it
		}
		
		// check the last secion for the last gap as well
		int lg = (sc[0] + this.getStepsPerOctave() - sc[sc.length-1]); 
		if(lg > lag)
			lag = lg;
		
		return lag;
	}
	
	public int calculateLargestStep(int sc) {
		return calculateLargestStep(scales.getScale(sc));
	}
	
	/**
	 * update the number of passing steps needed in the DEPA representation
	 * to accurately represent all modes of the scale
	 *
	 */
	public void updateDEPASteps() {
		//if(glodepstep > -1)
		//TODO - need a floating point rep
			this.stepehot = this.calculateLargestStep(this.getScale());
		//	PO.p("root = " + this.root + " updating depa steps for scale " + this.getScaleName() + " is " + stepehot);
			
	//	else {
	//		this.stepehot = glodepstep;
	//	}
	}
	
	
	
	/**
	 * get the temporary custom scale
	 * @param num TODO
	 * @return
	 */
	public int [] getCustomScale(int num) {
		return scales.getCustomScale(num);
	}
	

    public void transpose(Note note, int amount) {
        int [] scale = scales.getScale(scaleType);
                
        // check to see if the note is tonal
        if (!isTonal(note)) {
            try {
                throw new Exception(
                        "note hasn't been set into tonality");
            } catch (Exception e) {
                e.fillInStackTrace();
                e.printStackTrace();
            }
        }

        // extract current octave and degree
        int oct = extractOctave(note);
        int deg = extractDegree(note);

        // System.out.println("oct = " + oct);
        // System.out.println("deg = " + deg);

        // shift it
        deg += amount;

        // make it within the bounds
        while (deg < 0) {
            deg += scale.length;
            oct--;
        }
        while (deg >= scale.length) {
            deg -= scale.length;
            oct++;
        }

        // don't do anything if it is out of range
        if (tuningSystem.getStepsPerOctave() * oct
                + scale[deg] > 127
                || tuningSystem.getStepsPerOctave()
                        * oct + scale[deg] < 0)
            return;

        note.setPitch(tuningSystem.getStepsPerOctave()
                * oct + scale[deg]);

    }

    private int extractDegree(Note n) {
        return extractDegree(n.getPitch());
    }

    private int extractDegree(int p) {
        int [] scale = scales.getScale(scaleType);
        
        // scale degree
        int deg = (p - root)
                % tuningSystem.getStepsPerOctave();
        for (int i = 0; i < scale.length; i++) {
            if (scale[i] == deg) {
                return i;
            }
        }

        Exception e = new Exception(
                "pitch isn't in the expected tonality");
        e.fillInStackTrace();
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Integer.MIN_VALUE;
    }

    private int extractOctave(Note n) {
        // octave
        return (int) ((n.getPitch() - root)
                / tuningSystem.getStepsPerOctave() + 0.5);
    }

    private int extractOctave(int p) {
        return (int) ((p - root)
                / tuningSystem.getStepsPerOctave() + 0.5);
    }

    /*
     * public int getAbsDegree(int pitch) { }
     */

    public boolean isTonal(Note n) {
        
        for (int j = 0; j < (scales.getScale(scaleType)).length; j++) {
            if ((n.getPitch() - root)
                    % tuningSystem.getStepsPerOctave() == (scales.getScale(scaleType))[j])
                return true;
        }
        return false;
    }

    public boolean isTonal(int p) {
        for (int j = 0; j < (scales.getScale(scaleType)).length; j++) {
            if ((p - root)
                    % tuningSystem.getStepsPerOctave() == (scales.getScale(scaleType))[j])
                return true;
        }
        return false;
    }

    public boolean isDegree(int p, int d) {
        if (extractDegree(p) == d)
            return true;
        else
            return false;
    }

    public boolean isPitchClass(int p, int pc) {
        if ((p)% getStepsPerOctave() == ((pc+root)%getStepsPerOctave())) {
            return true;
        } else
            return false;
    }

    public int getStepsPerOctave() {
        return this.tuningSystem.getStepsPerOctave();
    }

    /**
     * gets the scale degree, with octave as part of it.
     * 0 + root is the first element of the scale degree
     */
    private int extractAbsDegree(int p) {
        return extractDegree(p)
                + (extractOctave(p) * scales.getScale(scaleType).length);
    }

    private int getPitchFromAbsDegree(int ad) {
        return scales.getScale(scaleType)[ad % scales.getScale(scaleType).length] + root
                + tuningSystem.getStepsPerOctave()
                * ((int) (ad / scales.getScale(scaleType).length));
    }

    /**
     * returns all the pitches that fit into the current
     * tonality
     */
    public int[] getTonalPitches(int bot, int top) {
        int[] toRet = new int[(int) (this.scales.getScale(scaleType).length * ((top - bot) / tuningSystem.getStepsPerOctave()))];

        // make sure the bottom pitch is a tonal pitch
        while (!isTonal(bot))
            bot++;

        int abd = extractAbsDegree(bot);
        for (int i = 0; i < toRet.length; i++) {
            toRet[i] = getPitchFromAbsDegree(abd++);
            // System.out.println("tonal pitch = " +
            // toRet[i]);

        }

        return toRet;

    }

    // use a tonalSetter object so that you can create
    // different algorithms for
    // setting the tonality outide of this class
    // public void set(Note n, TonalSetter ts) {
    // System.out.println("unimplemented");
    // }

    // tonal note constructors
    public Note newNote(int deg, int oct,
            double duration, int velocity) {
        return new Note(
                tuningSystem.getStepsPerOctave() * oct
                        + scales.getScale(scaleType)[deg], duration,
                velocity);
    }

    public Note newNote(int deg, int oct, double dur) {
        return new Note(
                tuningSystem.getStepsPerOctave() * oct
                        + scales.getScale(scaleType)[deg], dur);
    }

    public Note newNote(int deg, int oct) {
        return new Note(
                tuningSystem.getStepsPerOctave() * oct
                        + scales.getScale(scaleType)[deg], 1.0);
    }

    public Note newNote(int deg) {
        return newNote(deg, 5);
    }

    /**
     * root of the scale is from 0 to 11
     * @return
     */
    public int getRoot() {
        return this.root;
    }

    public int getScaleType() {
        return this.scaleType;
    }
    
    public int [] getScale() {
    	return this.scales.getScale(getScaleType());
    }

    public Scales getScales() {
    	return this.scales;
    }
    
	public void addLPart(LPart part) {
		this.pman.add(part);
	}

	public void removeLPart(LPart part) {
		
		this.pman.remove(part);
	}

	/**
	 * doesn't copy the parts that are managed, otherwise it gets too sticky
	 * @return
	 */
	public TonalManager copy() {
		TonalManager c = new TonalManager(this.scaleType, this.root, this.chord);
		return c;
	}

	/**
	 * adds to the value of the root key. (and modulus is applied)
	 * @param i
	 */
	public void shiftRoot(int i) {
		root +=i;
		root = ((root%this.getStepsPerOctave())+this.getStepsPerOctave())%
				this.getStepsPerOctave();
	}
	
	/** NOT NEEDED since using DEPA representation
	 * locks the notes in the part p to the current scale.
	 * 
	 * @param p
	 * @param tc unique scale to use, if tonal composites are being used(tocosc != null)
	 *
	public void lockMorphToCurrentScale(Part p, TonalComposite tc, 
									    LPart f, LPart mo, LPart t) {	
		
		scales.lockMorphToScale(p, getScaleType(), tc, getStepsPerOctave(),
				f, mo.getTonalManager(), t);
				
	}*/

	public void dload(Element e) {
		this.setRoot(Integer.parseInt(e.getAttribute("root")));
		this.setScale(Integer.parseInt(e.getAttribute("scaleType")));
		//e.setAttribute("spo", String.valueOf(this.getStepsPerOctave())); later
	}

	public void dsave(Element e) {
		
		e.setAttribute("root", String.valueOf(this.getRoot()));
		e.setAttribute("scaleType", String.valueOf(this.getScaleType()));
		//this.get
	}

	/**
	 *  converts absolute pitch representation 
	 * to one where every even number is a tonal degree and
	 * every odd number is a passing note.
	 * 
	 *  assumes that the pitch is shifted to match the root.  eg, 
	 * if it is in c# (root = 1), the 1st degree in 4th octave would be 61.
	 * 60 would refer to the 7th (if in ionian).  59 would refer to the passing
	 * note betwee the 6th and 7th.
	 *
	 * @param p
	 * @return a pitch representation where each octave is made from
	 *  0-2*(scale length) and root hasn't been applied to it yet.  
	 *  eg, if 70 is middle. and root is D, the pitch will need to be
	 *  shifted up 2 points (c# =1, d = 2) and reduced to 12 tones before it
	 *  can be rendered as a MIDI pitch 
	 * 
	 */
	public int getDEPA(int p) {
		// get scale
		int [] ts = this.getScale();
		
		if(ts.length == this.getStepsPerOctave()) {
		//	PO.p("ts.length = " + ts.length);
		//	for(int i=0; i< ts.length; i++) {
		//		PO.p("ts " + i + " = " + ts[i]);
		//	}
			
			return p;
		}
		else if(ts.length > this.getStepsPerOctave()) {
			Exception ex = new Exception("impossible scale length");
			ex.fillInStackTrace();
			try {throw ex;
			} catch(Exception e) {e.printStackTrace();}
		}
		
		
		// the octave we are in (considers root also)
		int oct = this.extractOctave(p); 
		
		// convert to pitch class
		p = (p-root)%this.getStepsPerOctave();
		
	//	PO.p("pitch class" + p);
		
		// convert to degree and passing note
		p = extractDEPA(p, ts);
	//	PO.p("extract depa " + p);
		
		// add the octaves again, this time relative to scale length
		p += oct*(ts.length*this.stepehot);
		
		return p;	
	}
	
	/**
	 * converts pitch class representation from 0-12
	 * to 0-(scale length)*2 where every even number is a tonal degree and
	 * every odd number is a passing note.
	 * 
	 * @param p must be in pitch class format, eg 0-12 (or 0-SPO).  
	 * root information is not considered 
	 * 
	 * @return
	 */
	private int extractDEPA(int p, int [] ts) {
//		PO.p("p ==== " + p);
		
		// search through the degress of the scale to see if it belongs
		for(int i=0; i< ts.length; i++) {
			// if it does, it is tonal; return the the degree*2
			if(p == ts[i]) {
	//			PO.p("does" + i*this.stepehot);
				return i*this.stepehot;
				
				//if it doesn't belong but is smaller than the current search, 
				//it is a passing note; return the previous degree*2 plus the
				//position of the passing note
			} else if(p < ts[i]) {
	//			PO.p("doesn't. prev = " + ((i-1)*this.stepehot + " rem  = "  + (p-ts[i-1])));
				return ((i-1)*this.stepehot + (p-ts[i-1]));
				// special case must be made for the last gap
			} else if(i == ts.length-1 && p<ts[0]+this.getStepsPerOctave()) {
				return (i*this.stepehot) + (p-ts[i]);
			}
		}
		
		
		return Integer.MIN_VALUE;
	}

	/**
	 * converts from DEPA to absolute
	 * @param p
	 * @return
	 */
	public int getABS(int p) {
	//	PO.p("depa p coming in " + p);
//		 get scale
		int [] ts = this.getScale();
		
		if(ts.length == this.getStepsPerOctave()) {
			return p;
		}
		else if(ts.length > this.getStepsPerOctave()) {
			Exception ex = new Exception("impossible scale length");
			ex.fillInStackTrace();
			try {throw ex;
			} catch(Exception e) {e.printStackTrace();}
		}
		
		int depsPerOctave = (this.stepehot*ts.length);
		
		// the octave we are in (considers root also)
		int oct = (p)/depsPerOctave;
	//	PO.p("octave we are in " + oct);
		// convert to degree class
		p = (p)%depsPerOctave;
		
		
		// convert back to absolute pitch
		//PO.p("p " + p + "  " + this.stepehot + " back to degree index " + 
		//		(int)(p*1.0/this.stepehot*1.0) );
		
		p = ts[(int)(p*1.0/this.stepehot*1.0)] + p%this.stepehot;
	
		
	//	int proo = root;
	///\	if(root > this.getStepsPerOctave()/2.0) {
	//		proo = root-this.getStepsPerOctave();
	//	}
	//	PO.p("root = " + root);
		// add the octaves again, this time relative to absolute steps per octave
		p += oct*(this.getStepsPerOctave()) + root;
	//	PO.p("abs p out " + p);
		return p;
	}

	public int getScaleIndex(String ns) {
		return this.scales.getScaleIndex(ns);
	}

	public int getDEPsPerOctave() {
		return this.stepehot*this.getScale().length;
	}

	public String getScaleName() {
		if(this.scaleType >= this.scales.NUM) {
			return "custom " + (this.scales.NUM - this.scaleType);
		} else
			return this.scales.getScales()[this.scaleType];
		
	}
	
	public String toString() {
		return " root = " + this.getRoot() + " scale = "  + this.getScaleName();
	}

	/**
	 * in DEPA mode, when changing from one scale into another, sometimes it 
	 * needs translating, the difference in length of scale and steps in the
	 * DEPA need to be taken into account.
	 * 
	 * ATM, Irreversible dammage is wrout by changing into a scale with a 
	 * different length.  This is a fundamental descision kind of problem though
	 * Translating to scale with different numbers of steps is fine
	 * 
	 * @param p the pitch to be converted
	 * @param sefro the step of scale coming from
	 * @param lefro the length of the scale coming from
	 * @param ns the type of new scale
	 * @return
	 */
	public int convertDEPA(int p, int stefro, int lefro, int steto, int leto) {
		double [] np = new double [4];
		
		// convert to scale degree representation
		np[0] = p*1.0/stefro*1.0;
		
		// store the remainder.
		np[1] = np[0]%1.0;
		
		// make it an integer scale degree
		np[0] = ((int)np[0])*1.0;
		
		// scale it for the new scale length eg
		// 7 becomes 5,
		np[2] = (int)((np[0]/lefro*1.0)*leto + 0.5);
		
		//  then add the remainder again, 
		// so it's 5.5 and multiply by new step-length:
		// 5.5 becomes 16.5, and then, round it off so it become 17
		np[3] = (int)((np[2]+np[1])*steto + 0.5);
		
		return (int)np[3];
	}
	
}
