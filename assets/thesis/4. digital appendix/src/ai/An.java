/*
 * Created on 13/02/2006
 *
 * @author Rene Wooller
 */
package ai;

import java.util.TreeMap;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.tools.Mod;
import music.LPart;
import ren.tonal.TonalComposite;
import ren.util.AD;
import ren.util.ODouble;
import ren.util.PO;

/**
 * has analysis functions and others
 * @author wooller
 *
 *13/02/2006
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class An {

	/**
	 * returns the average
	 * 
	 */
	public static double avpi(Part p) {
		TonalComposite tc = new TonalComposite();
		tc.addPart(p);
		return tc.getMeanPitch();
	}
	
	
	/**
	 * finds the fundamental pitch (the root pitch that is nearest to the
	 * pitch centroid).
	 * This is the pitch with pitch class of the key, and most central register
	 * @param lp
	 * @return
	 */
	public static int fun(LPart lp) {
		
		if (!lp.isDEPA()) {
			int root = lp.getTonalManager().getRoot(); // root key
			double avpi = An.avpi(lp.getPart()); // pitch centroid
			int spo = lp.getTonalManager().getStepsPerOctave();
			int oct = (int) (avpi / spo);
			
			int fund = 0; // nearest fundamental key pitch to pitch centroid
		
			fund = (oct * spo + root);
		
			return fund;
		} else {
			int root = 0; //root is always 0 in DEPA mode
			double avpi = An.avpi(lp.getPart()); // pitch centroid
		//	PO.p("an.  av pitch = " + avpi);
			int spo = lp.getTonalManager().getPassingSteps()*
						lp.getTonalManager().getScale().length;
			int oct = (int) (avpi / spo);
			
			// nearest fundamental key pitch to pitch centroid
			int fund = (oct * spo + root); 
			return fund;
		}
	}
	
	/**
	 * find the phrase with the specified start time
	 * @param p
	 * @param st
	 * @param dev the acceptable level of deviation (for it to be the same)
	 * @return
	 */
	public static Phrase getSt(Part p, double st, double dev) {
	//	PO.p("st  to get = " + st);		
		
		for(int i=0; i< p.length(); i++) {
	//		PO.p("p.getPhrase(i).getStartTime() = " + p.getPhrase(i).getStartTime());
			if(p.getPhrase(i).getStartTime() > st - AD.DFACC - dev&&
			   p.getPhrase(i).getStartTime() < st + AD.DFACC + dev) {
					return p.getPhrase(i);
			   }
		}
		return null;
	}
	
	/**
	 * if the lp is empty, returns scope + quantise (as if there was a note 
	 * just outside the scope.
	 * 
	 * @param lp
	 * @return
	 */
	public static double avio(LPart lp) {
		Part p = lp.getPart().copyRT(0, lp.getScope().getValue());
		if(p.getSize() == 0)
			return lp.getScope().getValue() + lp.getQuantise().getValue();
		
		if(p.getSize() == 1)
			return lp.getScope().getValue();
				
		Mod.quickSort(p);
		
		//start with the first interval
		double first = p.getPhrase(0).getStartTime();
		double prev = first;
		double next = p.getPhrase(1).getStartTime();
		
		// thwart double accuracy errors
		final double ac = 0.0001;
		int v = (int)((next-prev)/ac + 0.5);
		
		// if there are more than two, iterate through
		for(int i=2; i< p.getSize(); i++) {
			
			prev = next;
			next = p.getPhrase(i).getStartTime();
			
			v += (int)((next-prev)/ac + 0.5);
			
		}
		
		// include the interval betweeen the last one and the first
		prev = next;
		next = first + lp.getScope().getValue();
		v += (int)((next-prev)/ac + 0.5);
		
		// normalise from the number of intervals and return
		return (v*ac)/(p.getSize()*1.0);
	}

	/**
	 * finds the nearest according to onset alone) notephrase to the one 
	 * specified and returns the key that points to it
	 * returns null if the tree is empty
	 * @param t
	 * @param k
	 * @return
	 */
	public static ODouble nnonset(TreeMap t, ODouble k, double scope) {
		// deal with unusual cases
		if(t.containsKey(k))
			return k;
		if(t.isEmpty())
			return null;		
		if(t.size() == 1)
			return (ODouble) t.firstKey();
				
		ODouble pre = (ODouble)((t.headMap(k)).lastKey());
		
		// if it loops around, these are activated to get the correct distance
		double pref = 0;
		double nexf = 0;
		
		// if there is nothing before it, loop around to the end
		if(pre == null)  {
			pre = (ODouble) t.lastKey();
			pref = -scope;
		}
		
		ODouble nex = (ODouble) ((t.tailMap(k)).firstKey());
		// if there is nothing after it, loop to the beginning
		if(nex == null) {
			nex = (ODouble) t.firstKey();
			nexf = scope;
		}
	
		// if pre is nearest neighbour, return it, otherwise, return nex
		if((Math.abs(pre.v+pref - k.v) < Math.abs(nex.v + nexf - k.v)))
			return pre;
		else return nex;
				
	}
	
	/**
	 *  nearest neighbour using all parameters
	 *  goes through all the phrases in t, and finds the most similar
	 *  one to k.  Puts the index (in t) of this phrase into index[0] for 
	 *  reference, and returns the distance that was recorded.
	 *  
	 *  if there isn't anything in t, it returns NaN (there are no nearest
	 *  neighbour).
	 *  
	 * @param t
	 * @param k
	 * @param acom
	 * @param index
	 * @param sco (optional) scope. put <0 if not wanting to use it
	 * @return the distance between 0 and 1 of k and it's nn in t (0 is same)
	 */
	public static double nnall(Part t, Phrase k, AllCompare acom, 
								int [] index, double sco) {
		
		// if there isn't anything in it to be nearest to, return NaN
		if(t.length() == 0)
			return Double.NaN;
		
		double mindis = Double.MAX_VALUE; // the current smallest distance
		// go through, finding the distances from each phrase in t
		for(int i=0; i< t.length(); i++) {
			// current distance is inverted similarity (0 is most similar)
			double currdis = 1- acom.comp(t.getPhrase(i), k, sco);
			
			// if the current distance is smaller (they are similar), record it
			if(currdis < mindis) {
				index[0] = i;
				mindis = currdis;
			}
		}
		
		// return the smallest distance
		return mindis;
				
	}

	/**
	 * Average Pitch Interval from Fundamental
	 * @param p
	 * @see #fun(LPart)
	 * @return the average interval from fundamental
	 */
	public static double avpinfu(LPart p) {
		int fu = An.fun(p);
		int accints = 0;
		for(int i=0; i< p.getPart().length(); i++) {
			accints += Math.abs(p.getPart().getPhrase(i).getNote(0).getPitch()-
								fu);
		}
		return ((accints*1.0)/(p.getPart().size()*1.0));
	}

	/**
	 * go through the part and find the pitch range and the lowest pitch
	 * @param p
	 * @return int [] [0] = range [1] = lowest pitch [2] = highest
	 */
	public static int[] pitchRange(Part p) {
		if(p.size() == 0)
			return null;
		int [] ret = new int [3];
		int lv = Integer.MAX_VALUE;
		int hv = Integer.MIN_VALUE;
		for(int i=0; i< p.length(); i++) {
			ret[0] = p.getPhrase(i).getNote(0).getPitch(); // store the pitch
			if(ret[0] < lv) // if it is smaller than the record
				lv = ret[0]; // record it
			if(ret[0] > hv) //if it is bigger, record it
				hv = ret[0];
		}
	    
		ret[0] = hv-lv; // range
		ret[1] = lv; // low
		ret[2] = hv; // high
				
		return ret;
	}


	public static Phrase getSame(Part part, Phrase phr) {
		for(int i=0; i< part.length(); i++) {
			
			if(part.getPhrase(i).getStartTime() > phr.getStartTime() - 0.0001 &&
			   part.getPhrase(i).getStartTime() < phr.getStartTime() + 0.0001 &&
			   part.getPhrase(i).getNote(0).getPitch() == phr.getNote(0).getPitch()) //&&
		//	   part.getPhrase(i).getNote(0).getDuration() > phr.getNote(0).getDuration() - 0.0001 &&
		//	   part.getPhrase(i).getNote(0).getDuration() < phr.getNote(0).getDuration() + 0.0001 &&
		//	   part.getPhrase(i).getNote(0).getDynamic() == phr.getNote(0).getDynamic()) {
			   {
			
				return part.getPhrase(i);
			}
					
		}
		
		
		return null;
	}
	
	
	/**
	 * takes a part, and records the intervals between notes that have the same
	 * start time.
	 * 
	 * eg 0:60 0:65 1:62 1:59
	 * becomes
	 * {{0, 5}, {3, 0}}
	 * 
	 * @param p
	 * @return
	 */
	public static int [][] harmonicClumps(final Part p, double er) {
		int [][] harda = new int [p.getSize()][];
		
		int cluc = 0; // counts the number of clumpings
		
		for(int i=0; i< p.length();) {
			//PO.p("cluc " + cluc);
			int [] temp = new int [p.length()]; // holds the intervals
			Phrase curr = p.getPhrase(i);
			
			int j=0;
			temp[j++] = 0; 
			int [] loint = new int [] {0, 0}; // value, index of lowest interval
		//	PO.p("curr pitch = " + curr.getNote(0).getPitch());
			while(i+j < p.getSize() && 
					p.getPhrase(i+j).getStartTime() >= curr.getStartTime() - er &&
					p.getPhrase(i+j).getStartTime() <= curr.getStartTime() + er) {//&&
			
				// record the interval
				temp[j] = p.getPhrase(i+j).getNote(0).getPitch() - 
							curr.getNote(0).getPitch();
				
				if(temp[j] < loint[0]) {
					loint[0] = temp[j];
					loint[1] = j;
				}
				
				j++;	
			}
	
			harda[i-cluc] = new int [j];
			for(int k = 0; k < j; k++) {
				//if((i-cluc) < 0)
			//		PO.p("error " + i + " cluc " + cluc);
			//	PO.p("i-cluc" + (i-cluc));
			//	PO.p("was " + harda[i-cluc][k]);
				harda[i-cluc][k] = temp[k] + loint[0]*-1;
			//	PO.p("now " + harda[i-cluc][k]);
				
			}
		
			i = i+j;
		
			cluc += j-1;
		}
		
		return harda;
	}


	public static double avhain(final Part p, double er) {
		int [][] harc = An.harmonicClumps(p, er);
	//	PO.p("part = \n", p, 1);
	//	PO.p("harc = \n", harc);
		return avhain(harc, er);
	}
	
	public static double avhain(int [][] harc, double er) {
		double ain = 0;
		int noc = 0;
		
		for(int i=0; i<harc.length; i++) {
			if(harc[i] == null) // the tail is always null
				break;
			
			//temporary storage of the average interval
			double tain = 0;
			
			for(int j=0; j< harc[i].length; j++) {
				tain += harc[i][j];
			}
			
			// if there is an interval, 
			if(harc[i].length-1 > 0) {
				// take the average of all the intervals in this chunk
				tain = tain/(harc[i].length-1);
			}
			
			// accumulate the average interval
			ain += tain;
			
			noc ++;
		}
		
		
		return ain/noc*1.0;
	}


	/**
	 * the average number of notes in each harmonic clump
	 * @param fh
	 * @return
	 */
	public static double avhasize(int[][] fh) {
		double av = 0;
		int count = 0;
		for(int i=0; i< fh.length; i++) {
			if(fh[i] == null)
				break;
			
			av += fh[i].length;
			
			count++; // becauase fh is likely to have null entries
		}
		
		return av/count;
	}
	
}
