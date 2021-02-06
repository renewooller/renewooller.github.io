/*
 * Created on 2/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.data.Part;
import music.LPart;
import ren.tonal.TonalComposite;
import ren.tonal.TonalManager;
import ren.util.AD;
import ren.util.ODouble;
import ren.util.PO;
import ai.AllCompare;
import ai.An;
import ai.MTransformat;
import ai.PitchCompare;

public class Metric {

	/**
	 * for all of these, it is assumed that no notes go over the scope
	 *
	 */
	public Metric() {
		super();
	}

	/**
	 * "No." of "No"tes number of notes difference. Normalised by / by max of
	 * scope/0.25 because of this, if there are polyphonic notes and/or notes
	 * faster than a quarter of a beat, the result may be above 1.0
	 * 
	 * @param src
	 * @param tar
	 * @return
	 */
	public double nono(LPart src, LPart tar) {
		checkError(src, tar);

		// get the difference in number of notes
		Part sc = src.getPart().copyRT(0.0, src.getScope().getValue());

		Part tc = tar.getPart().copyRT(0.0, tar.getScope().getValue());

		double dif = Math.abs(sc.getSize() - tc.getSize());

		// normalise it
		dif = dif / (src.getScope().getValue()/0.25);
		// note: this normalisation makes some assumtions:
		// 1. monophonic music.
		// 2. quantised to 0.25
		return dif;
	}

	/**
	 * relative difference in number of notes:
	 * eg, 1-2 notes returns the same as 5-10 notes
	 * This difference between one note and another becomes a
	 * lot less at high densities, compared to nono, where the 
	 * differences are the same throughout, but very low
	 * @param src
	 * @param tar
	 * @return
	 */
	public double nonor(LPart src, LPart tar) {
		checkError(src, tar);

		// get the difference in number of notes
		Part sc = src.getPart().copyRT(0.0, src.getScope().getValue());

		Part tc = tar.getPart().copyRT(0.0, tar.getScope().getValue());
		double ss = sc.getSize()*1.0;
		double ts = tc.getSize()*1.0;
		
		return 1.0-(Math.min(ss, ts)/Math.max(ss, ts));
		
	}
	
	/**
	 * Difference in PItch FUNdamental
	 * The algorithm used to compare the pitches is passed, so as to make it
	 * more flexible.
	 * 
	 * TonalComposites are passed instead of parts so that they can be reused.
	 * 
	 * @param src
	 * @param tar
	 * @param pc
	 * @return
	 */
	public double difpifun(TonalComposite src, TonalComposite tar, 
						   PitchCompare pc) {
		return 1-pc.compare(src.getRootPitch(), tar.getRootPitch());
				
	}
	
	
	
	/** Difference in average pitch of each input
	 * if  src and tar are empty, there is no difference, ret 0;
	 * if  one is empty and the other isn't, ther is maximum difference, ret 1
	 * otherwise, 
	 * 	this normalises by 50 (in pitch interval) and caps it at 1 beyond this.
	 * @param src
	 * @param tar
	 * @return
	 */
	public double difpiav(LPart src, LPart tar) {
		checkError(src, tar);
		
		if(src.getPart().getSize() == 0 && tar.getPart().getSize() == 0) {
			return 0.0;
		} else if(src.getPart().getSize() == 0 || 
				tar.getPart().getSize() == 0) {
			return 1.0;
		}
		
		double dif = Math.abs(An.avpi(src.getPart())-
							  An.avpi(tar.getPart()));
		return Math.min(dif/50.0, 1.0);
	}
	
	/**
	 * this normalises by the scope, because the biggest difference would be
	 * a piece with as many note onsets as quantise points (qua) and one with
	 * no notes at all (scope + qua), and diff between this is = scope
	 * @param src
	 * @param tar
	 * @return
	 */
	public double difioav(LPart src, LPart tar) {
		checkError(src, tar);
		
		double diff = Math.abs(An.avio(src) - An.avio(tar));
		
		return diff/src.getScope().getValue();
	}
	
	/**
	 * DIFference in Onset-Interval Contour Derives the onset interval contour
	 * for s and t, finds the accumulated difference and normalises by an 
	 * arbitrary maximum possible difference of scope*scope (max area)
	 * 
	 * Having no notes at all is the highest possible inter-onset, so this is
	 * approximated by making it just above the scope. eg, a value of 4.0 could
	 * be scope, so the value of no notes at all will be 4.25.
	 * 
	 * Using this scheme, 4.25 becomes the highest possible io value, while is
	 * 0.25 used as the lowest, making it scope*scope as the normalisation
	 * factor.
	 * 
	 * @param s
	 * @param t
	 * @return the difference in the interval contour. if there are many
	 *         inter-onsets smaller than 0.25, this may exceed 1.0
	 */
	public double difioc(MTransformat src, MTransformat tar) {
		// make sure the scopes are the same
		this.checkError(src.getScope(), tar.getScope());

	//	return Math.sqrt(difc(src, tar, MTransformat.RAT, 
//				 src.getScope()+0.25, false)) / (src.getScope());
		
		return difc(src, tar, MTransformat.RAT, 
						 src.getScope()+0.25, false) / (src.getScope()*
								 						src.getScope());
	}
	
	/**
	 * DIFference in Pitch-Interval Contour Derives the onset interval contour
	 * for s and t, finds the accumulated difference and normalises by an 
	 * arbitrary maximum possible difference of 30*scope which is the difference
	 * in area of, for example, out of phase square waves with an amplitude
	 * of 30.
	 *
	 * @param s
	 * @param t
	 * @return the difference in the interval contour. if there are many
	 *         inter-onsets smaller than 0.25, this may exceed 1.0
	 */
	public double difpic(MTransformat src, MTransformat tar) {
		// make sure the scopes are the same
		this.checkError(src.getScope(), tar.getScope());

		//PO.p("src = " + src.toString());
		//PO.p("tar = " + tar.toString());
		
		double ret = difc(src, tar, MTransformat.PIT, 
								Double.POSITIVE_INFINITY, true) / 
						(src.getScope()*30);
		
		//PO.p("difpic ret = " + ret);
		
		return Math.min(ret, 1.0);
	}
	
	public double difempty(Part s, Part t) {
	//	PO.p("warning: performing dif on a 0 length src or trg ");
		double  d =  (s.length() + t.length())*1.0/1000.0;
		if(d > 0.0)
			return 1.0;
		else
			return 0.0;
	}
	
	/**
	 * nearest neighbour difference, comparing the source to target and the
	 * target to the source.
	 * 
	 * it is necessary to do it both ways, considering if onr part has a single
	 * note, and the other one has many, and the single note will be compared
	 * to just one note in the other one, and could be jusdged exactly the
	 * same, even if it has not as many notes.
	 * 
	 * @param src
	 * @param tar
	 * @return double [] difference in various attributes: start time, pitch,
	 * 	duration, velocity
	 */
	public double difnn(final LPart src, final LPart tar, final AllCompare ac,
			final double acc) {
		checkError(src, tar);
		
		if(src.getPart().length() == 0 || tar.getPart().length() == 0) {
			
			//TODO: this could have serious implications
			return difempty(src.getPart(), tar.getPart());
			//assuming that there is a cap of 1000 notes per part
		}
		
		//PO.p("difnn acc = " + acc);
		
		// find the distance from one to the other
		double dif = difnn1way(src.getPart(), tar.getPart(), ac, 
				src.getScope().getValue(), acc);
		// and the other to the one
		dif = dif + difnn1way(tar.getPart(), src.getPart(), ac,
				src.getScope().getValue(), acc);
		// normalise and return
		return dif/2.0;
	}
	
	public double difnn(final LPart src, final LPart tar, final AllCompare ac) {
		return this.difnn(src, tar, ac, 0.0000001);
	}
	
	/**
	 * go through the source part.  for each phrase find the nearest phrase in
	 * the target part.  find the distance between these and accumulate it. 
	 * normalise it by the number of phrases in source.  This is the accumulated
	 * nearest neighbour distance.
	 * @param sp source part
	 * @param tp target part
	 * @param acomp
	 * @return
	 */
	private double difnn1way(final Part sp, final Part tp, 
							 final AllCompare acomp, final double sco, 
							 final double ac) {
		
		int v = 0;
		
		
		int [] ind = {0}; // holds index of the most similar phrase (not needed)
		
		for(int i=0; i< sp.size(); i++) {
			// find and accumulate the difference
			v = v + (int)(An.nnall(tp, sp.getPhrase(i), acomp, ind, sco)/ac + 0.5);
		}
				
		//normalise by the number of distances that were accumulated
		return (v*ac)/(sp.size()*1.0);
	}
	
	/**
	 * creates tonal composite of the (newly generated) part (p). accumulates
	 * the difference between the weights of the pitch-classes in each composite
	 * normalise by 2 because the weights sum to one
	 * 
	 * When the scales are slightly off, it will be very different (1).
	 * when the scales are the same, but with different weightings (indicating
	 * a relative key), it will be quite similar (near 0).
	 * 
	 * exactly the same will be no difference
	 * 
	 * The steps per octave in tcto need to be default(12)
	 * 
	 * @param p
	 * @param to
	 * @return the difference, normalised to be between 0 and 1.  
	 */
	public double diftc(final Part p, final TonalComposite tcto) {
		// create the tonal composite
		TonalComposite tc = new TonalComposite();
		tc.addPart(p);
		
		// get the weights for all the pitch classes of "from"
		double [] wf = tc.getPCWeights();
		// get the weights for "to"
		double [] wt = tcto.getPCWeights();
		
	//	PO.p("wf = ", wf);
	//	PO.p("wt = ", wt);
		
		// initialise a variable to store the difference without errors
		AD dif = new AD();
		
		for(int i=0; i<tcto.getSPO(); i++) {
		//	PO.p("wf = " + wf[i] + " wt = " + wt[i]);
			dif.add(Math.abs(wf[i]-wt[i]));
		//	PO.p("dif = "  + dif.v());
		}
		//PO.p("final dif = " + dif.v());
		dif.set(dif.v()/2.0); // maximum distance: eg wf = {0, 1} & wt = {1,0}
		
		if(dif.v() > 1.01) {
			
			Exception e = new Exception("error calculating diffrence in " + 
					" tonal composites \n Tonal composite A: " + tc.toString() +
					" \n Tonal composite B: " + tcto.toString());
			try {
				e.fillInStackTrace();
				throw e;
			} catch (Exception ex) {
				ex.printStackTrace();
				PO.p("part that triggered it ", p, 2);
				PO.p("dif > 1 " + dif);
				PO.p("wf = ", wf);
				PO.p("wt = ", wt);
				System.exit(0);
			}
		} else if (dif.v() > 1.0) {
			return 1.0;
		}
			
		return dif.v();
	}
	
	
	/*
	private TreeMap partToNoteTree(LPart lp) {
		TreeMap ret = new TreeMap();
		Part p = lp.getPart().copyRT(0, lp.getScope().getValue());
		for(int i=0; i< p.size(); i++) {
			ret.put((new ODouble()).construct(p.getPhrase(i).getStartTime()),
					p.getPhrase(i).getNote(0));
		}
		return ret;
	}*/
	
	
	/**
	 * 
	 * difference in any type of contour. Note: this is not normalised
	 *
	 * @param src
	 * @param tar
	 * @param type
	 * @param DFNo
	 * @param rel wether or not to normalise each src and tar to have the same
	 * 				mean value. (thus maing it a relative, rather than absolute
	 * 				measure)
	 * @return
	 */
	public double difc(MTransformat src, MTransformat tar, int type,
					   final double DFNo, final boolean rel) {
		
		//if(src.isEmpty() || tar.isEmpty()) 
		//	return 
		// need to work out how to compare a null transformat to a wiggly one
		
		double meansrc = 0;
		double meantar = 0;
		
		if(rel) {
			meansrc = src.computeMean(type);
			meantar = tar.computeMean(type);
		}
		
		// make sure they have the same scope
		this.checkError(src.getScope(), tar.getScope());

		// phase
		final double phs = src.getDimAt(MTransformat.PHA, 0, false);
		final double pht = tar.getDimAt(MTransformat.PHA, 0, false);

		ODouble pres = src.preK(type, 0.000001); // the starting point for source
		ODouble pret = (tar.preK(type, phs - pht + 0.0000001)).copy(); // the starting
		
		//PO.p("1. pres = " + pres.v);
		//PO.p("1  pret = " + pret.v );
		
		// point for target
		pret.v = phs - pht; // set it so that it starts at that point

		
		//PO.p("2  pres = " + pres.v );
		//PO.p("2  pret = " + pret.v );
		
		
		ODouble nes = null;
		ODouble net = null;

		// accutad is the accumulated target distance, used to judge when the
		// end is reached
		double accutad = 0;
		// wah keeps track of Which one is AHead:
		// if the point from src is ahead of the point from tar, wah = -1;
		// if the point from tar is ahead of the point from src, wah = 1;
		// if they are both the same, wah = 0;
		// int wah = 0;

		// the accumulated difference in area
		double dif = 0;

		while (accutad < src.getScope()) { // src and tar scope should ==
			//PO.p("accutad  = "  + accutad);
			
			// find the next points in each
			nes = src.nexK(type, pres.v + 0.00001);
			net = tar.nexK(type, pret.v + 0.00001);
			
			//PO.p("pres = " + pres.v + " nes = " + nes.v);
		//	PO.p("pret = " + pret.v + " net = " + net.v);
			
			// determine which one is longer
			// and find the are difference using the shortest one
			// taking into acount that we might wrap around and neec
			// to add scope
			double sdis = 0; // distance from prev to next in source
			double tdis = 0; // distance from prev to next in target
			
			// need to make it so that the 
			
			if(nes.v <= pres.v) {
				// wrapped around
				sdis = (nes.v - pres.v + src.getScope());
			} else {
				// not wrapped
				sdis = (nes.v - pres.v);
			}
			
			if(net.v <= pret.v) {
				// wrapped around
				tdis = (net.v - pret.v) + tar.getScope();
			} else {
				// not wrapped
				tdis = (net.v - pret.v);
			}
				
		//	if(pret.v < 0)
		//		PO.p("pret <0 = " + pret.v + " net = " + net.v);
			
			// phase can cause problems because it is an imaginary node.
			// so make sure the intervals are realistic:
			if(sdis + accutad > src.getScope())
				sdis = src.getScope()-accutad;
			if(tdis + accutad > src.getScope())
				tdis = src.getScope()-accutad;
			
			double t = 0; // the time interval
			
			// the value difference for this time interval
			double vs = 0;
			double vt = 0;
			if(nes == ODouble.MIN) // if there are no notes at all
				vs = DFNo; //
			else
				vs = src.getDimAt(type, pres) - meansrc;
			
			if(net == ODouble.MIN)
				vt = DFNo;
			else {
				//PO.p("pret =  " + pret.v + " type = " + type);
				vt = tar.getDimAt(type, pret) - meantar;
			}
			double v = Math.abs((vs-vt));
			
	//		PO.p("vs = " + vs + " vt = " + vt);
			
			
	//		PO.p("sdis = " + sdis);
	//		PO.p("tdis = " + tdis);
			// if the distance between points in the target is smaller
			if (sdis > tdis) {
				// the distance used is the smallest one
				t = tdis;	
				// or if it is the other way
			} else if (tdis > sdis) {
				// the distance used is the smallest
				t = sdis;		
			} else if (sdis == tdis) {
				// distance is the same
				t = sdis;		
			}
			
			// iterate one or the other depending on the location of the next 
			if((net.v + phs-pht) == nes.v) {
				// iterate both of them if the next point is the same
				pret = net;
				pres = nes;
			} else if((net.v + phs-pht) < nes.v) {
				pret = net;
			} else if((net.v + phs-pht) > nes.v) {
				pres = nes;
			}
		
			// accumulate the difference in area that was found during this
			// iteration
			dif += t * v;
			// accumulate where we are up to
			accutad += t;
			
	//		PO.p("accutad = " + accutad);
		}

		return dif;
	}

	private void checkError(LPart src, LPart tar) {
		checkError(src.getScope().getValue(), tar.getScope().getValue());
	}

	private void checkError(double s, double t) {
		if (s != t) {
			try {
				Exception e = new Exception("the scope must be the same");
				e.fillInStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Difference in (average) Pitch Interval from Fundamental
	 * So as to save the av pitch interval for t being recalculated, it is 
	 * passed in
	 * 
	 * @param part
	 * @param avpinfut the average pitch interval for t, already calculated
	 */
	public double difpinfu(final LPart part, double avpinfut) {
		double dif = Math.abs(An.avpinfu(part) - avpinfut);
		dif = dif/60.0;
		if(dif > 1.0)
			dif = 1.0;
		dif = Math.pow(dif, 0.125);
		return dif;
	}

	public double difavhain(final Part part, double tah) {
		double fah = An.avhain(part, 0.001);
	//	PO.p("tah " + tah + "fah " + fah);
		return Math.pow(Math.min(1.0, Math.abs(fah-tah)/48.0), 0.125);
	}

	/**
	 * finds the key distance between the two tonal managers
	 * 
	 * @param f from
	 * @param t to
	 * @return
	 */
	public double difKey(TonalManager f, TonalManager t) {
		int [] fs = new int [f.getScale().length];
		for(int i=0; i<fs.length; i++) {
		
			fs[i] = (f.getScale()[i] + f.getRoot())%f.getStepsPerOctave();
			
		}
		
		int [] ts = new int [t.getScale().length];
		for(int i=0; i< ts.length; i++) {
			ts[i] = (t.getScale()[i] + t.getRoot())%t.getStepsPerOctave();
		}
		
		return difScale(fs, ts);
	}

	/**
	 * key distance between two scales, assuming the same root
	 * @param fs
	 * @param ts
	 * @return
	 */
	public double difScale(int [] fs, int [] ts) {
		
		int matches = 0;
		
		for(int i=0; i<fs.length; i++) {
			for(int j=0; j< ts.length; j++) {
				if(fs[i] == ts[j]) {
					// record a match
					matches++;
				}
			}
		}
		
		// normalise by the scale with more degrees so that if there are different
		// scale lengths with the same notes, it will be considered different
		double dma = matches*1.0/(Math.max(fs.length, ts.length))*1.0;
		dma = 1-dma;
		
		if(dma == Double.NaN) {
			PO.p("matches " + matches + " Math.max(fs.length, ts.length) " + Math.max(fs.length, ts.length));
		}
			
		return dma;
	}

	/**
	 * finds the difference in the average clump size
	 * @param f
	 * @param t
	 * @return
	 */
	public double difharclumav(LPart f, LPart t) {
		double fhs = An.avhasize(An.harmonicClumps(f.getPart(), 0.001));
		double ths = An.avhasize(An.harmonicClumps(t.getPart(), 0.001));
	//	PO.p("fhs = " + fhs + ";  ths = " + ths);
		double ret = Math.abs((fhs-ths));
	//	PO.p("ret = " + ret);
		
		int nq = 127;
		if(f.isDEPA()) 
			nq = 127*f.getTonalManager().getPassingSteps();
		
		ret = ret/127;
	//	PO.p("ret/127 = " + ret);
		
		ret = Math.pow(ret, 0.4);
	//	PO.p(" ret pow = " + ret);
		return ret;
	}
	
}
