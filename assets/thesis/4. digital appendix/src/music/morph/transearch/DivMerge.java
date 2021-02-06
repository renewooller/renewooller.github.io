/*
 * Created on 15/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.data.Part;
import jm.music.data.Phrase;
import music.LPart;
import ren.gui.ParameterMap;
import ren.util.AD;
import ren.util.PO;
import ai.AllCompare;

public class DivMerge extends TransSearch {

	private AllCompare ac = new AllCompare();
	private static final int DFON = 6;
	int on = DFON;
	
	//overlap factor
	private ParameterMap lapfac;
	
	public DivMerge() {
		super();
		// TODO Auto-generated constructor stub
		otherParams = new ParameterMap [] {(new ParameterMap()).construct(0, 100, 0, 1.0, 0, "overlap")};
		lapfac = otherParams[0];
	}

	/**
	 * There are 6 copies that will be evaluate using the number of 
	 * notes metric:
	 * 
	 * 1)
	 * Make a copy of f.
	 * go through from first to last:
	 * 	where a note overlaps with the next note:
	 * 		merge notes:
	 * 			set that duration of this note to be: nex.st - curr.st + nex.d
	 * 			remove the next note
	 * 2)
	 * Make another copy.
	 * go through from last to first:
	 * 	where a the next note's duration overlaps with curr note's starttime,
	 *  	merge them, and set the pitch to be that of curr note.
	 *  	remove the curr note
	 * 3) plain copy
	 * 4)
	 * go through and store the longest duration (ldur)
	 * copy
	 * split this note in half
	 * 5) copy, 
	 * split note in 25:75
	 * 6) 75:25
	 * 
	 * Compare all the copies of f to t using the "number of notes" metric
	 * Metric.nono.  store all of these into an array with weightings for each
	 * operation (including no operation).
	 * 
	 * modify the weightings by a factor of the costs given in the parameters.
	 * 
	 * find the smallest weighting
	 * 
	 * return the corresponding lpart (with the smallest weighting)
	 * 
	 * @param f
	 * @param t
	 */
	public void findb(LPart f, LPart t, int steps) throws InterruptedException {
		super.checkError(f, t); 
		
		
		// initialise the copies
		super.resetOps(f);
		
		//	FILL OUT COPIES
		applyDivMerge(f.getScope().getValue());
		
	//	PO.p("parts = ",op);
		
		//  fill the distance measures
		judgeDistances(t);
		
		// find the closest
	//	return super.findClosest(steps);
		
	}

	/**
	 * fill out the different copies
	 * @param scope
	 */
	private void applyDivMerge(double scope) {

		// copy 1
		mergeFwd(op[0].getPart());
		
		// copy 2
		mergeBack(op[1].getPart());
		
		//copy 3 is no change
		
		// find the note with with longest duration
		int lodin = findLDur(op[2].getPart());
		super.MIDV = 2;
		
		// copy 4
		split(op[3].getPart(), lodin, 0.5, scope);
		
		// copy 5
		split(op[4].getPart(), lodin, 0.25, scope);
	
		// copy 6
		split(op[5].getPart(), lodin, 0.75, scope);
	
	}
	
	/**
	 * judge distance, Was simply according to the number of notes, but that 
	 * doesn't work well in complex examples
	 * @param t
	 */
	private void judgeDistances(LPart t) {
		
		for(int i=0; i< w.length; i++) {
			w[i] = this.differenceFunction(op[i], t);
		//	PO.p("judge ids = " + w[i]);
		}
	}
	
	
	/**
	 * find the point at which to add the new note
	 * 
	 * make a copy of the note and change duration of both notes
	 * 
	 * add new note
	 * 
	 * 
	 * @param p
	 * @param lodin
	 * @param sf split factor
	 */
	private void split(Part p, int lodin, double sf, double scope) {
		// a quantise factor
		double qu = 0.125;
		
		// there isn't a note or if it is too short to consider splitting
		if(lodin < 0 || p.getPhrase(lodin).getNote(0).getDuration()*sf < qu)
			return;
		
		// store duration of the next note
		double nepdur = p.getPhrase(lodin).getNote(0).getDuration()*(1-sf);
		
		// shorten the duration of the first note
		p.getPhrase(lodin).getNote(0).setDuration(
				((int)(((int)(p.getPhrase(lodin).getNote(0).getDuration()/
						AD.DFACC + 0.5))*sf))*AD.DFACC);
		
		//find the point at which to add the new note
		double ip = ((int)(p.getPhrase(lodin).getNote(0).getDuration()/AD.DFACC + 0.5) + 
					(int)(p.getPhrase(lodin).getStartTime()/AD.DFACC + 0.5))*AD.DFACC;
		
		//quantise the insertion point
		
		ip = ((int)(ip/qu))*qu;
		
		//PO.p("ip = " + ip);
		// if it is beyond the next loop, don't add it.
		if(ip >= scope*2.0) {
			return;
		}
		
		//otherwise, make the insertion point be within the loop
		ip = ip%scope;
		
		// make a new notephrase
		Phrase nep = p.getPhrase(lodin).copy();
		
		// set it to be the right duration
		nep.getNote(0).setDuration(nepdur);
		
		// implement the insetion point
		nep.setStartTime(ip);
		
		//PO.p("lodin = " + p.getPhrase(lodin).toString());
		//PO.p("  nep = " + nep.toString());
		
		// add it
		p.add(nep);
		
	}
	
	private int findLDur(final Part p) {
		double lod = Double.MIN_VALUE;
		int lodin = -1;
		
		for(int i=0; i< p.length(); i++) {
			if(p.getPhrase(i).getNote(0).getDuration() > lod) {
				lod = p.getPhrase(i).getNote(0).getDuration();
				lodin = i;
			}
		}
		return lodin;
	}
	
	/**
	 * go through from first to last:
	 * 	where a note overlaps with the next note:
	 * 		merge notes:
	 * 			set that duration of this note to be: nex.st - curr.st + nex.d
	 * 			remove the next note
	 * @param p
	 */
	private void mergeFwd(Part p) {
		if(p.length() <= 1)
			return;
		
		Phrase curr;;// = p.getPhrase(0);
		Phrase next;
		for(int i=1; i< p.length(); i++) {
			// the current note
			curr = p.getPhrase(i-1);
			//  the next note
			next = p.getPhrase(i);
			// see if the duration of the current note exceeds the inter-onset
			if(curr.getStartTime() + curr.getNote(0).getDuration() + lapfac.getValue()>=
			   next.getStartTime()) {
				// if it does, merge the two notephrases
				merge(curr, next, true);
				
				//	 and remove the latter one
				p.removePhrase(i);
			}
		}
	}
	
	/**
	 * go through from last to first:
	 * 	where a the next note's duration overlaps with curr note's starttime,
	 *  	merge them, and set the pitch to be that of curr note.
	 *  	remove the curr note
	 * @param p
	 */
	private void mergeBack(Part p) {
		if(p.length() <= 1)
			return;
		
		Phrase curr;// = p.getPhrase(p.length());
		Phrase next;
		
		for(int i=p.length()-2; i > -1; i--) {
			
			// the current note
			curr = p.getPhrase(i +1);
			//  the next note
			next = p.getPhrase(i);
			// see if the duration of the current note exceeds the inter-onset
			if(next.getStartTime() + next.getNote(0).getDuration() >=
			   curr.getStartTime()) {
				// if it does, merge the two notephrases
				merge(next, curr, false);
				
				//	 and remove the latter one
				p.removePhrase(i+1);
			}
			// because the ones that it removes will always be after
			// the i, we don't need to worry about the length changing
		}
	}

	private void merge(Phrase a, Phrase b, boolean fwd) {
		// check for error
		if(a.getStartTime() > b.getStartTime()) {
			try { Exception e = new Exception(" b must be >= a)");
				e.fillInStackTrace();
			} catch(Exception e) {e.printStackTrace();}
		}

		// set the duration of the foremost note
		a.getNote(0).setDuration(
					(b.getStartTime()-a.getStartTime()) +
					b.getNote(0).getDuration());
		 
	
		// set the pitch and dynamic of the foremost note, if backwards
		if(!fwd) {
			a.getNote(0).setPitch(b.getNote(0).getPitch());
			a.getNote(0).setDynamic(b.getNote(0).getDynamic());
		}
		
	}
	
	
	
	protected void setInitialCostValues() {
		co[0].setValue(0.95); // fwd
		co[0].setName("forward");
		
		co[1].setValue(0.95); // bkwd
		co[1].setName("backward");
		
		co[2].setValue(1.0); // plain
		co[2].setName("plain");
		
		co[3].setValue(0.85); // split in half
		co[3].setName("split half");
		
		co[4].setValue(0.8); // split 25:75
		co[4].setName("split quarter");
		
		co[5].setValue(0.8); //split 75:25
		co[5].setName("split three quarters");
	}
	
	public void config(int i) {
		if(i == 0) {
		co[0].setValue(1.0); // fwd
		co[1].setValue(1.0); // bkwd
		co[2].setValue(1.0); // plain
		co[3].setValue(1.0); // split in half	
		co[4].setValue(1.0); // split 25:7	
		co[5].setValue(1.0); //split 75:25	
		} else if (i==1) {
			co[0].setValue(1.0); // fwd
			co[1].setValue(1.0); // bkwd
			co[2].setValue(1.0); // plain
			co[3].setValue(0.8); // split in half	
			co[4].setValue(0.8); // split 25:7	
			co[5].setValue(0.8); //split 75:25	
		}
	}
	
	public int opn() {
		
		//PO.p("op num = " + OP_NUM);
		return on;
	}

	protected void initOPN() {
		this.on = this.DFON;
	}

	public String getType() {
		return "Divide and Merge";
	}

	public double differenceFunction(LPart f, LPart t) {
		return super.metric.difnn(f, t, ac);
		//return super.metric.difioav(f, t);
		//return super.metric.nono(f, t);
	}
	
	
	
	
}
