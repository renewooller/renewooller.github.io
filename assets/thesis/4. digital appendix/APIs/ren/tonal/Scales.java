/*
 * Scales.java
 *
 * Created on 5 May 2003, 15:47
 */

package ren.tonal;

import java.lang.reflect.Field;
import java.util.Vector;

import jm.music.data.Part;
import jm.music.tools.Mod;
import music.LPart;
import ren.util.AD;
import ren.util.Arr;
import ren.util.PO;
import ren.util.VectorMap;
import ai.DefaultNoteWeighter;
import ai.NoteWeighter;

/**
 * 
 * @author Rene Wooller
 */
public class Scales {
	/*
	 * public int [] chromatic = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}; public
	 * int [] ionian = {0, 2, 4, 5, 7, 9, 11}; public int [] dorian = {0, 2, 3,
	 * 5, 7, 9, 10}; public int [] phrygian = {0, 1, 3, 5, 7, 8, 10}; public int []
	 * lydian = {0, 2, 4, 6, 7, 9, 11}; public int [] mixolydian = {0, 2, 4, 5,
	 * 7, 9, 10}; public int [] aeolian = {0, 2, 3, 5, 7, 8, 10}; public int []
	 * lochrian = {0, 1, 3, 5, 6, 8, 10};
	 */

	public static int[] chromatic = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	public static int[] ionian = { 0, 2, 4, 5, 7, 9, 11 };

	public static int[] dorian = { 0, 2, 3, 5, 7, 9, 10 };

	public static int[] phrygian = { 0, 1, 3, 5, 7, 8, 10 };

	public static int[] lydian = { 0, 2, 4, 6, 7, 9, 11 };

	public static int[] mixolydian = { 0, 2, 4, 5, 7, 9, 10};

	public static int[] aeolian = { 0, 2, 3, 5, 7, 8, 10 };

	public static int[] lochrian = { 0, 1, 3, 5, 6, 8, 10 };

	public static int[] harmonic_minor = { 0, 1, 4, 5, 7, 8, 11 };

	public static int[] pentatonic = { 0, 3, 5, 7, 10 };
	
	public static int[] pentatonic_major = { 0, 2, 4, 7, 9};

	public static int[] whole_tone = { 0, 2, 4, 6, 8, 10 };

	public static int[] whole_tone_half_tone = { 0, 2, 3, 5, 6, 8, 9, 11 };

	public static int NUM = 13;
	
	private static Scales inst = new Scales();

	private VectorMap scaleList;
	
	private int numExtra = 0;
	
	public Scales() {

		// setup the public scaleList into the hashmap
		try {
			scaleList = new VectorMap();

			Field[] fields = this.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType().isArray()) {
					int[] sc = (int[]) fields[i].get(this);
					String name = fields[i].getName();
					name = name.replace('_', ' ');
					// name = name.substring(0, name.lastIndexOf(' '));
					this.addScale(name, sc);
					// PO.p(name);
				}
			}
			// PO.p(scaleList.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * used internally
	 * @param name
	 * @param scale
	 */
	public void addScale(String name, int[] scale) {
		scaleList.add(name, scale);
		this.numExtra = Math.max(scaleList.size()-NUM, 0);
	}
	
	public void addScale(int [] scale) {
		addScale(String.valueOf(numExtra), scale);
	}
	
	
	public int[] getScale(String name) {
		return (int[]) scaleList.get(name);
	}

	public int getScaleIndex(String name) {
		return scaleList.find(name);
	}
	
	public String[] getScales() {
		return scaleList.getKeys();
	}

	public int[] getScale(int scaleType) {
		return (int[]) scaleList.get(scaleType);
	}
	
	/**
	 * starting from 0
	 * 
	 * @param in
	 * @return
	 */
	public int [] getCustomScale(int in) {
		
		return getScale(in + NUM);
	}

	public static Scales getInstance() {
		// PO.p("gettin instance");
		if (inst == null) {
			// PO.p("inst null");
			inst = new Scales();
			return inst;
		}
		// PO.p("not null");
		return inst;
	}

	
	public void lockMorphToScale(Part p, int scale, TonalComposite unbo, 
			double mi, LPart f, TonalManager mo, LPart t) {
				
		TonalComposite tt = new TonalComposite();
		tt.addPart(t.getPart());
		TonalComposite tf = new TonalComposite();
		tf.addPart(f.getPart());
		
		// if the predefined scale is to be used
		if(unbo == null) {
			lockToScale(p, this.getScale(scale), 
				mo.getRoot(), 
				mo.getStepsPerOctave(), 0, 
				DefaultNoteWeighter.di, false, 
				new double [][] {tf.getPCWeights(), tt.getPCWeights()}, 
				new double [] {(1-mi), mi});
		} else { // if the scale from a different f and t is being passed
			lockToScale(p, unbo, 
					mo.getRoot(), 
					mo.getStepsPerOctave(), 0, 
					false);
		}
						
	}
	
	public void lockToScale(Part p, String scale, int key, 
			int spo, int upDwnRnd, NoteWeighter weighter, 
			boolean favour, double [][] contextPCweights, 
			double [] contextWeights) {
		this.lockToScale( p, this.getScale(scale),  key, 
				 spo,  upDwnRnd,  weighter, 
				 favour, contextPCweights, 
				 contextWeights);
	}

	public void lockToScale(Part p, TonalComposite tc, int key, 
			int spo, int upDwnRnd, boolean favour) {
		
		lockToScale(p, tc.extractScale(tc.pitchesToDegrees(key)), 
				key, spo, upDwnRnd, tc.getWeighter(), favour, 
				new double [][] {tc.getPCWeights()}, new double [] {1.0});
		
	}
	

	/**
	 * 
	 * @param p
	 * @param scale
	 * @param key
	 * @param spo
	 * @param upDwnRnd (1 - round up, -1 round down, 0 random
	 * @param weighter algorithm used to judge the emphasis of the note
	 * @param favour true: favour repetition of pitches, false :favour variety
	 * @param contextPCweights weights of pitch classes previously computed
	 * 			from relevant contexts [context(0-n)][pc(0-spo)]
	 * @param contextWeights the weights of how much to favour each context
	 * 			[context(0-n)]
	 * 
	 *
	 */
	public void lockToScale(Part p, int[] scale, int key, 
			int spo, int upDwnRnd, NoteWeighter weighter, 
			boolean favour, double [][] contextPCweights, 
			double [] contextWeights) {
		//		make arguments usable if they are null
		if(contextPCweights == null || contextWeights == null) {
			contextPCweights = new double [0][0];
			contextWeights = new double [0];
		}
		
		// go though and find the ones that are not going to change, or the ones
		// that only have a 1-option change and count their occurence.  
		// Record the ones that will change and their options
		
		AD [] nocha = Arr.AD(scale.length); // weights of *degrees* that won't change
		
		int [][] cha = new int [p.length()][4]; // changers and options
		// [0] = distance, [1] = lower degree, [2] = upper degree,  
		// [3] = index in p
		int nucha = 0; // number record to change (last index of cha)
		
		// go through
		for(int i=0; i<p.size(); i++) {
			// get the pitch-class offset by the key
			int pdeg = (p.getPhrase(i).getNote(0).getPitch() + spo - key) % spo;
			
			// find information about the closest index(es) in the scale
			//[0] the distance, [1] the closest, [2] (if existing) the other 
			// closest index (in scale)
			int [] clos = this.findClosest(scale, pdeg, spo);
			
			// if the distance is 0, don't change the original, but record
			// the instance of this scale degree
			if(clos[0] == 0) {
				nocha[clos[1]].add(weighter.weigh(p.getPhrase(i)));
			} else if(clos[2] == -1) { // if there is only one closest
				// shift the pitch to be it
				p.getPhrase(i).getNote(0).setPitch(
						p.getPhrase(i).getNote(0).getPitch() + clos[0]);
				
				// record the instance of this scale degree
				nocha[clos[1]].add(weighter.weigh(p.getPhrase(i)));
			} else { // if the distance isn't zero, and if there are two options
				// record this in cha
				cha[nucha][0]= clos[0]; // record the distance (+- of lower);
				cha[nucha][1]= clos[1];//record the lowerDegree
				cha[nucha][2]= clos[2];//record the upperDegree
				cha[nucha++][3] = i; // record the index of the note
			}
		}
		
		this.implementLockChanges(scale, nucha, cha, nocha, contextPCweights, 
				contextWeights, upDwnRnd, p, favour);
		
	}
	
	private void implementLockChanges(int [] scale, int nucha, int [][] cha, 
			AD [] nocha, double [][]contextPCweights, double [] contextWeights,
			int upDwnRnd, Part p, boolean favour) {
		
		//PO.p("nocha = ",nocha);
		
		//	go through all the ones that need to change
		for(int i=0; i< nucha; i++) {
			
			// stor the weight of the upper degree and the lower degree
			double [] ul = new double [2]; //[0] lower [1] upper
			// add the recently recorded weight of lower degree to the 
			//weight of pitch class in context
			if(cha[i][0] < 0) { // if the lower (first recorded) degree is 
								// lower in pc (normal)
				ul[0] = nocha[cha[i][1]].v() + 
					this.weightPCSum(contextPCweights, contextWeights, 
							         scale[cha[i][1]]);
				// same for higher degree
				ul[1] = nocha[cha[i][2]].v() + 
					this.weightPCSum(contextPCweights, 
			         contextWeights, scale[cha[i][2]]);
			} else { // if the lower (first recorded) degree is actually higher
					// in pc (wrapped around, eg, 7 will have 0 as higher)
				ul[1] = nocha[cha[i][1]].v() + 
					this.weightPCSum(contextPCweights, contextWeights, 
						         scale[cha[i][1]]);
				// lower degree is the second recorded (higher pc, but lower shift)
				ul[0] = nocha[cha[i][2]].v() + 
					this.weightPCSum(contextPCweights, 
							contextWeights, scale[cha[i][2]]);
			}
			
			//if the weights are both the same
			if(ul[0] == ul[1]) {
				if(upDwnRnd == 0) {
					 // if we are relying on random chance to decide
						if(Math.random() < 0.5) {
							// don't change direction
							Mod.transposeRT(p.getPhrase(cha[i][3]), cha[i][0]);
						} else { // change direction
							Mod.transposeRT(p.getPhrase(cha[i][3]), -cha[i][0]);
						}
				} else if(upDwnRnd == -1) { // we are shifting down
					Mod.transposeRT(p.getPhrase(cha[i][3]), -Math.abs(cha[i][0]));
				} else if(upDwnRnd == 1) { // we are shifting up
					Mod.transposeRT(p.getPhrase(cha[i][3]), Math.abs(cha[i][0]));
				} else {
					try {
						Exception e = new Exception("up down rnd mus tbe -1 0 or 1");
						e.fillInStackTrace();
						throw e;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// if the lower option has more emphasis than the higher,
				// and we are favouring more, or if we aren't favouring & its
				// lower
			} else if (ul[0] > ul[1] && favour ||
					   ul[0] < ul[1] && !favour) { 
				// shift down
				Mod.transposeRT(p.getPhrase(cha[i][3]), -Math.abs(cha[i][0]));
			} else if (ul[0] < ul[1] && favour ||
					   ul[0] > ul[1] && !favour) {
				//shift up
				Mod.transposeRT(p.getPhrase(cha[i][3]), Math.abs(cha[i][0]));
			}
		}
	}
	
	/**
	 * 
	 * @param cpc context sources with pitch class weightings
	 * @param w weights for each context source. should add to zero
	 * @param pc the pitch class in question
	 * @return weighted importance of pitch class
	 */
	private double weightPCSum(double [][] cpc, double [] w, int pc) {
		AD ad = new AD();
		for(int i=0; i< cpc.length; i++) {
			ad.add(cpc[i][pc]*w[i]);
		}
		return ad.v();
	}
		
	public int pitchFromScale(int pi, int[] scale, int key, 
			  double[] weights, int spo) {
		return this.pitchFromScale(pi, scale, key, weights, spo, true);
	}
	
	/**
	 * 
	 * @param scale 
	 * @param npi  pitch class, offset by key
	 * @param spo  
	 * @return [0] the distance of the closest index, [1] the closest, [2] (if
	 * existing) the other closest index (in scale)
	 */
	public int [] findClosest(int [] scale, int npi, int spo) {
		int [] ret = Arr.in(3, -1);
		int[] minIndices = new int[2];
		int pos = 0;

		int mv = Integer.MAX_VALUE; // min value

		for (int i = 0; i < scale.length; i++) {
			// find the distance (wrapped around)
			int d = Integer.MAX_VALUE; // the distance
			if (npi >= scale[i]) {
				if(npi - scale[i] < (scale[i] + spo - npi)) {
					d = scale[i]-npi; // negative value (shift down)
				} else {
					d = scale[i] + spo - npi; // positive value (shift up)
				}		
			}else if (scale[i] > npi) {
				if(scale[i] - npi < (npi + spo - scale[i])) {
					d = scale[i]-npi; // positive value (shift up)
				} else {
					d = scale[i] - npi - spo; // negative value (shift down)
				}
			}

			// record the smallest distance (mv)
			if (Math.abs(d) < Math.abs(mv)) {
				mv = d;
				// / reset the recording of min indices that are the same
				// because
				// they are all trumped by this one
				pos = 0;
				minIndices[pos++] = i;

			} else if (Math.abs(d) == Math.abs(mv)) { // but if it is the same, record the new one
				minIndices[pos++] = i;
			}
		}
		ret[0] = mv;
		
		ret[1] = minIndices[0];
		
		if(pos == 2) {
			ret[2] = minIndices[1];
		}
		
		return ret;
	}
	
	/**
	 * 
	 * Take a pitch and return the closest pitch to it in the given scale
	 * 
	 * Complexity is scale length (n)
	 * 
	 * @param pi
	 * @param scale
	 * @param key -
	 *            the key to which the scale applies (eg 0 of scale is key
	 *            pitch)
	 * @param weights
	 * @param spo steps per octave
	 * @param favourHeavy wether or not to favour the heavy weights or the 
	 * 					  lighter weights
	 * @return
	 */
	public int pitchFromScale(int pi, int[] scale, int key, 
							  double[] weights, int spo, boolean favourHeavy) {
		// make the pitch into the pitch class, and adjust for different keys
		int oct = (int) (pi / spo);

		int npi = (pi + spo - key) % spo;

		int[] minIndices = new int[scale.length];
		int pos = 0;

		double mv = Double.MAX_VALUE; // min value

		for (int i = 0; i < scale.length; i++) {
			double d = Double.MAX_VALUE; // the distance
			if (npi >= scale[i])
				d = Math.min(Math.abs(npi - scale[i]), Math.abs(scale[i] + spo - npi));
			else if (scale[i] > npi)
				d = Math.min(Math.abs(scale[i] - npi), Math.abs(npi + spo - scale[i]));

			// record the smallest distance (mv)
			if (d < mv) {
				mv = d;
				// / reset the recording of min indices that are the same
				// because
				// they are all trumped by this one
				pos = 0;
				minIndices[pos++] = i;

			} else if (d == mv) { // but if it is the same, record the new one
				minIndices[pos++] = i;
			}
		}

		double val;
		if(favourHeavy) {
			// interestingly, the smallest double is actually the smallest +
			val = -Double.MAX_VALUE;
			
		}else {
			val = Double.MAX_VALUE;
		}
		int index = -1;
				
		// a loop to favour the indices that are more heavily weighted (or not
		// depending on favourHeavy switch
		
		for (int i = 0; i < pos; i++) {
			
			if(favourHeavy) { // record largest (heaviest)
			
				if (val < weights[minIndices[i]]) {
			
					val = weights[minIndices[i]];
					index = minIndices[i];
			
				} else if (val == weights[minIndices[i]]) { // round up
				
					//if (minIndices[i] == 0) { // avoid
						index = minIndices[i];
					//} // maybe make this random?
				}
			} else { // record smallest
				if (val > weights[minIndices[i]]) {
					val = weights[minIndices[i]];
					index = minIndices[i];
				} else if (val == weights[minIndices[i]]) {// round down
					//if (minIndices[i] != 0) {
					//	index = minIndices[i];
					//}
				} // note: rounding down so that 11 jumps to 10 not 12 again
			}
		}
		
		// when it it at one end of the scale it is liable to drop or gain an
		// octave... this makes sure that the result is as similar as the
		// original
		int tret = (scale[index] + key);
		if (Math.abs((tret + oct * spo) - pi) > Math.abs((tret + (oct + 1) * spo) - pi)) {
			tret = (tret + (oct + 1) * spo);
		} else if (Math.abs((tret + oct * spo) - pi) > Math.abs((tret + (oct - 1) * spo) - pi)) {
			tret = (tret + (oct - 1) * spo);
		} else {
			tret = (tret + (oct) * spo);
		}

		return tret;
	}

	public Vector createAllScales(int npitches) {
		Vector all = new Vector(1000);
		int[] sc = new int[npitches];
		// set all the variables to -1
		for (int i = 0; i < sc.length; i++) {
			sc[i] = -1;
		}
		
		// start generating scales recursively
		casrecur(sc, 0, all, 0);
		
		return all;
	}

	/**
	 *  for use with createAllScales method as a recursive function
	 * @param sc
	 * @param at
	 * @param all
	 * @param total
	 * @return
	 */
	private int[] casrecur(final int[] sc, final int at, Vector all, final int total) {
		PO.p("sc = ", sc);
		PO.p("at = " + at + " total = " + total);
		PO.p(" ");
		
		// if there are ever more than 12 intervals, stop
		if (total > 12)
			return null;
		
		// if we've filled up all the variables, add the scale to the vector
		if (at == sc.length) {
			// but not if it doesn't actually have 12 intervals
			if(total != 12)
				return null;
			
			int[] scc = new int[sc.length];
			System.arraycopy(sc, 0, scc, 0, sc.length);
			all.add(scc);
			return sc;
		}
		
		// make a copy that we can write to
		int [] s = new int [sc.length];
		System.arraycopy(sc, 0, s, 0, sc.length);

		//seed from 1, only if constraints are satisfied:
		// if any of the positions on either side are != 1 AND
		// there are not two consecutive ones on either side
		if( ((sc[smd(at-1, sc.length)] != 1)||(sc[smd(at+1, sc.length)] != 1)) &&
			((sc[smd(at-1, sc.length)] != 1)||(sc[smd(at-2, sc.length)] != 1))&&
			((sc[smd(at+1, sc.length)] != 1)||(sc[smd(at+2, sc.length)] != 1))) {
			s[at] = 1;
			casrecur(s, at+1, all, total+s[at]);
		}

		s[at] = 2; // seed from 2, no constraints
		casrecur(s, at + 1, all, total + s[at]); 
		/*
		// seed from three, only if the constraints are satisfied:
		// none of the neighours can be a three
		if (sc[smd(at - 1, sc.length)] != 3 &&
			sc[smd(at + 1, sc.length)] != 3) {
			
			s[at] = 3;
			casrecur(s, at + 1, all, total + s[at]);
		}*/
		return sc;
	}

	// scale modulus
	public int smd(int at, int len) {
		if (at < 0)
			return (at % len + len) % len;
		else
			return at % len;
	}

	/**
	 * if the pitch goes off the scale, puts it back, but with the right pitch
	 * class
	 * @param pitch
	 * @param spo
	 * @return
	 */
	public static int boundMIDIPitch(int pitch, int spo) {
		return boundPitch(pitch, spo, 0, 127);
	}
	
	/**
	 * if the pitch goes off the scale, puts it back, but with the right pitch
	 * class
	 * @param pitch
	 * @param spo
	 * @return
	 */
	public static int boundPitch(int pitch, int spo, int low, int high) {
		if(pitch < low)
			pitch = pitch%spo + spo + low;
		else if(pitch > high)
			pitch = ((int)(high*1.0/spo*1.0))*spo + pitch%spo;
		return pitch;
	}

	public int size() {
		return this.scaleList.size();
	}

	
	
}
