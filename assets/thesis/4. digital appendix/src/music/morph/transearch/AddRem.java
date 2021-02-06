/*
 * Created on 27/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.data.Phrase;
import jm.music.tools.Mod;
import music.LPart;
import ren.gui.ParameterMap;
import ren.util.PO;
import ai.AllCompare;
import ai.An;


public class AddRem extends TransSearch {

	private final int DFON = 1024; // this will mean that the arrays are inililised
	// with 400 as the maximum number of note that can be dealt with
	
	private int on;
	private AllCompare ac = new AllCompare();
	private final int REP = 3; // index of the parameter for repeating in co[]
	public static final int POL = 4; // index of parameter for poliphipny
	private final int INTER = 5;
	private final int NMET = 6;
	//private final int PRES = 7; // preserve pattern
	// the number of additional repeats that this add/rem performs
	
	private boolean VB = false;
	
	public AddRem() {
		super();
	
	}

	public LPart find(final LPart f, final LPart t, int steps, double [] glopar) throws InterruptedException {
		
		
		//PO.p("find... bypass = " + bypass[0]);
		if(super.bypass == true || f.getPart().getSize() == 0)
			return f.copy();
		if(VB)
		PO.p(" distance coming into add rem = " + 
				this.differenceFunction(f, t));//metric.difnn(f, t, ac));
		
	//	PO.p("ad rem find. steps = " + steps);
		int reps = co[REP].getValueInt();
	//	PO.p("reps = " + reps);
		LPart toRet = f;
		for(int i=0; i< reps; i++) {
		
//			PO.p("repeating " + i + toRet.getPart().toString());
		//	PO.p(" repeating " + i, toRet.getPart(), 1);
			this.findb(toRet, t, steps);
			toRet = this.findClosest(steps, f, t, glopar);
			Mod.quickSort(toRet.getPart());
		}
		return toRet;
		
	}
	
	protected void findb(final LPart f, final LPart t, int steps) throws InterruptedException {
	//	PO.p("number of note coming into Add/Rem = " + f.getPart().length() + 
	//			", num in targ = " + t.getPart().length() + 
	//			" total = " + (f.getPart().length() + t.getPart().length()));
		
		
		//PO.p("f fundamental = " + f.getTonalManager().getRoot());
		
		//int difroo = f.getTonalManager().getRoot()-
		//			 t.getTonalManager().getRoot();
		
		int mid = (int)(f.getTonalManager().getStepsPerOctave()*1.0/2.0 + 0.5);
		
		
	//	if(difroo > mid) {
	//		difroo = f.getTonalManager().getStepsPerOctave()-difroo;
	//	} else if(difroo < -mid) {
	//		difroo = f.getTonalManager().getStepsPerOctave()+difroo;
	//	}
		
		
		int insiz = f.getPart().size();
		
		// init the variations, one for each note in source and target
		resetOPNUM(insiz, t.getPart().size());
		super.resetOps(f);
		
		
		
		// each one will have a different note removed
		for(int i=0; i<insiz; i++) {
			if(Thread.interrupted())
				throw new InterruptedException();
			
			op[i].getPart().removePhrase(i);
			
			// record weight difference
			w[i] = this.differenceFunction(op[i], t);
	//		PO.p("w[ " + i + "] = " + w[i]);
		}
		
		// or nothing changed
		w[MIDV] = this.differenceFunction(op[MIDV], t);
//		PO.p("w[ " + f.getPart().size() + "] = " + w[f.getPart().size()]);
		
	//	PO.p("before ", f.getPart(), 1);
		// or notes added
		for(int i=insiz+1; i <on; i++) {
			if(Thread.interrupted())
				throw new InterruptedException();
			
			// check to see if there is a phrase already on here
			Phrase phr = null;
			if(co[POL].getValueInt() == 1) { // poliphony
				phr = An.getSame(op[i].getPart(), 
						t.getPart().getPhrase(i-(insiz+1)));
				
				// 
				
			} else if(co[POL].getValueInt() == 0) { // no poliphony
				phr = An.getSt(op[i].getPart(), 
						t.getPart().getPhrase(i-(insiz+1)).getStartTime(), 
						0.00001);
			}
			
			// if there isn't a note on this bit already
			if(phr == null) { // add one
				
				Phrase toadd = t.getPart().getPhrase(i-(insiz+1)).copy();
				op[i].getPart().addPhrase(toadd);
				
				
				if(VB)
				PO.p("not a note here, adding " + toadd.getStartTime() + 
						"  " + toadd.getNote(0).getPitch());
			} else { // other wise,
				// check to see if there is another
				
				
				phr.getNote(0).setDuration(phr.getNote(0).getDuration()*(1.0-co[INTER].getValue()) +
						t.getPart().getPhrase(i-(insiz+1))
						.getNote(0).getDuration()*co[INTER].getValue());
				
				phr.getNote(0).setDynamic((int)(phr.getNote(0).getDynamic()*(1.0-co[INTER].getValue()) +
						t.getPart().getPhrase(i-(insiz+1))
						.getNote(0).getDynamic()*co[INTER].getValue()));
				if(VB)	
					PO.p("is a note here. before " + i + " pi change " + phr.getNote(0).getPitch());
				
				phr.getNote(0).setPitch(
						t.getPart().getPhrase(i-(insiz+1))
						.getNote(0).getPitch());
				if(VB)
					PO.p("after " + i + " pi change " + phr.getNote(0).getPitch());
				
				//phr.add(t.getPart().getPhrase(i-(insiz+1))
				//		.getNote(0).copy());
				
				
				phr.setStartTime(
						t.getPart().getPhrase(i-(insiz+1)).getStartTime());
		//		PO.p(" pitch = " + t.getPart().getPhrase(i-(f.getPart().size()+1))
			//	.getNote(0).getPitch());
			//	PO.p("is a note here, so modify phr = " + phr.toString());
			}
			
			
			
	//		PO.p("added = ", op[i].getPart(), 1);
			
			// record weight difference
			w[i] =super.metric.difnn(op[i], t,ac)*(1-co[this.NMET].getValue()) +
				   super.metric.nono(op[i], t)*co[this.NMET].getValue();
			//, 0.125);//Math.pow(super.metric.difnn(op[i], t,ac), 0.125);
			
	//		PO.p("w[ " + i + "] = " + w[i]);
			
		}
		
		//PO.p("after ", f.getPart(), 1);
		if(super.dev.getValue() > this.on) {
			dev.setValue(on);
		}
	//	PO.p("repfind");
	
	//	PO.p("add/rem, distances =", w);
		
	}
	
	/**
	 * situation nowhere.
	 * gotta get myself right, out of hyeah
	 * 
	 * wi must be sotreted from lowest to highest
	 */
	protected double situationNowhereCheck(double[][] wi, 
										   double targdif, 
										   double midw) {
		
		// if it isn't a situation nowhere, or if we have arrived at the end
		// and are wanting to go to the end,
		//don't change anything
		if(targdif < midw || (midw == 0 && targdif == midw))
			return targdif;
		
		// otherwise, swap wit hthe next smallest
		
		// the first and second smallest
		double [] minn = {Integer.MAX_VALUE, Integer.MAX_VALUE};
		int [] inn = {-1, -1}; // indexes to that ^
		
		// this only works because it is sorted from lowest to highest
		for(int i=wi.length-1; i>=0;  i--) {
			//if a minimum is found
			if(wi[i][0] < minn[0]) {
				// shift it back one
				minn[1] = minn[0];
				inn[1] = inn[0];
				
				// record new minimum
				minn[0] = wi[i][0];
				inn[0] = (int)(wi[i][1] + 0.5);	
			}
		}
		
		// if it has been set return it
		if(minn[1] < Integer.MAX_VALUE)
			return minn[1];
		
		//otherwise
		System.out.println("! ! !problem finding next smallest note in situation nowhere check");
		PO.p("wi in situ nowhere, ", wi);
		
		return minn[0];
		
	}
	private void resetOPNUM(int fn, int tn) {
		
		on = fn + tn + 1;
		super.MIDV = fn;

	//	PO.p("reseting opnum ;;;;;;;;;;;;;;;;;;;;;;;; fn = " + fn + " tn = " 
	//			+ tn + " new on = " + on);
	}
	
	protected void initCostParams(int num) {
		super.initCostParams(7);
		
	}
	
	protected void setInitialCostValues() {
		
		co[0].setValue(1.0);// remove
		co[0].setName("remove");
		
		co[1].setValue(1.0);// nothing
		co[1].setName("nothing");
		
		co[2].setValue(1.0);// add
		co[2].setName("add");
		
		// the number of times to add/remove a note
		co[REP].construct(0, 10, 0, 10, 2, "cycles");
		co[POL].construct(0, 1, 1, "poliphony");
		co[INTER].construct(0, 100, 0, 1, 0.25, "interpolation");
		co[this.NMET].construct(0, 100, 0, 1.0, 0.0, "num note in metric");
	//	co[this.PRES].construct(0, 1, 1, "preserve gestalt");
	}
	
	public void config(int i) {
		setInitialCostValues();
		co[INTER].setValue(1.0);
	}

	protected double costOfIndex(int i) {
		
		if(i < this.MIDV) {
			return co[0].getValue();
		} else if( i == this.MIDV) {
			return co[1].getValue();
		} else {
			return co[2].getValue();
		} 
	}
	
	public ParameterMap getRepeats() {
		return this.co[REP];
	}

	public int opn() {
		return on;
	}

	protected void initOPN() {
		this.on = this.DFON;
	}

	public String getType() {
		return "add remove";
	}

	public double differenceFunction(LPart f, LPart t) {
		if(f.getPart().length() == 0  ||
				  t.getPart().length() == 0) {
			
			return metric.difempty(f.getPart(), t.getPart());
		}
		
		return super.metric.difnn(f, t, ac);
	}

}
