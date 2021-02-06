/*
 * Created on 1/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.tools.Mod;
import music.LPart;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.gui.ParameterMap;
import ren.io.Domable;
import ren.io.Domc;
import ren.util.PO;
import ren.util.RMath;
import ai.MTConverter;

/**
 * if needed, the general proceedure could be optimised: instead of making
 * copies, could performa the different operations on one copy, recroding the
 * distance and updoing the operation after each one
 * 
 * each one
 * 
 * @author wooller
 * 
 * 27/02/2006
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public abstract class TransSearch implements Domable {

	public int MIDV; // in subclasses, this must be set to the middle (no change) transformation
	
	protected ParameterMap dev;// = (new ParameterMap()).construct(0, , mapMin, mapMax, value, name);
	protected ParameterMap devDec;
	protected boolean bypass = false;

	protected ParameterMap traspee; // speed of transformation
	protected ParameterMap trackConsistence; // ratio between tracking perfectly (0)
	// and being consistent/targeting pivot states (1)
		
	protected Metric metric = new Metric();

	protected MTConverter mtconv = new MTConverter();

	/* the weights */
	protected double[] w;

	// stores various data about the desicion
	private double [][] wi;
	
	
	/* the options (resulting from the transformations */
	protected LPart[] op;

	/* the cost factors for each operation */
	protected ParameterMap [] co;

	protected ParameterMap [] otherParams;
	
	private int rsel = 0; // the index of the selected one
	
	private boolean VB = false;
	
	public TransSearch() {
		super();
		bypass = false;
		this.initParams();
	}
	
	/**
	 * the number of ops. and ws.  In some cases this is not necessarily the
	 * length of the array, so this method is a way to the that information
	 * fro mthe child classes where this is the case.
	 * 
	 * Other chilren should just put a single integer
	 * @return
	 */
	public abstract int opn();
	
	/**
	 * weights must have been initialised with distances between op[] and t.
	 * 
	 * goes through all the weights and factors them with withs the costs for
	 * each operation co[]. during this, it keeps track of the smallest result
	 * 
	 * returns the lpart in op[] with the smallest result.
	 * @param steps 
	 * @param f 
	 * @param t 
	 * @param glopar : deviation, decay, midle weight, transform speed
	 * @return
	 */
	protected LPart findClosest(int steps, LPart f, LPart t, double [] glopar) 
			throws InterruptedException {
		
	
	//	PO.p("finding closeest ", w);
		
		if(Thread.interrupted())
			throw new InterruptedException();
		
		// check for major problems
		if (w.length != op.length) {
			try {
				Exception e = new Exception("weights and " + "options must me the same length");
				e.fillInStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	//PO.p("finding closest.... ");
		// prepare data for a quicksort <key, value> where weight is key, 
		// index in op is value
		wi = new double[opn()][5];
	//	PO.p("opn() " + opn());
		for (int i = 0; i < opn() ; i++) {
	//		PO.p(" cost of index " + i + " is " + costOfIndex(i) + " w [" + i + "] = " + w[i]);
		//	PO.p("1in");
			if(i == this.MIDV) {
		//		PO.p("2in");
				wi[i][0] = (1-(1-w[i])*costOfIndex(i)*glopar[2]);
			}else {
	//			PO.p("3in");
				wi[i][0] = (1-(1-w[i])*costOfIndex(i));
			}
	//		PO.p("4in");
			wi[i][1] = i;
		}
	//	PO.p("after");
		// perform the quicksort, aranging indexes from lowest weight to 
		// highest
		double midw = wi[MIDV][0];
		
		quickSort(wi, 0, wi.length - 1, 0);
		//PO.p("after quicksort");
		// if the selected one is the same at the MIDV, swap it with the MIDV
		if(wi[0][0] == midw) {
			int fwi = (int)wi[0][1];
			for(int i=0; i< wi.length; i++) {
				if(wi[i][1] == MIDV) {
					wi[i][1] = fwi;
					wi[0][1] = MIDV;
					break;
				}
			}
		}
		
		int sel = 0;
		// if deviation is being used
		if (this.dev.getValue() > 0) {
			PO.p("dev > 0 = " + dev.getValue());
			// the start deviation, factored by the global deviation, minus
			// decay
			sel = (int) ((this.dev.getValue() * glopar[0]) - (steps - 1)
					* (this.devDec.getValue() * glopar[1]));

			// bound it
			sel = Math.max(Math.min(sel, wi.length - 1), 0);

			if (sel >= wi.length) {
				PO.p(this.getType());
				PO.p("out of wi, which = ", wi);
			}
			
		} else {
			// target difference, scaled between the least different and the 
			// current difference (midw)
			
			// get the target 
			double targdif = (midw-wi[0][0])*
				(1-Math.min(
					this.traspee.getValue()*glopar[3], 1.0))+wi[0][0];
			
		//	PO.p("\n\nsteps = " + steps + " midw = " + midw + " wi[0][0] = " + 
		//			wi[0][0] + " traspee = " + traspee.getValue() * glopar[3] + " td 1 = " + targdif);
			
			// to make each step consistently different use (s-(s-m*steps)
			targdif = (midw - (midw-targdif)*(steps+1));
			
		//	PO.p(" td2 = " + targdif);
			
			targdif = RMath.boundHard(targdif, wi[0][0], midw);
		//	PO.p(" td3 = " + targdif);
			double ditadi = -1; // to record the difference	
			
			//PO.p("pre sel ");
			sel = selectMorph(wi, midw, targdif, t, f);
		//	PO.p("post sel ");
		
		}
		
		rsel = (int) wi[sel][1];
		
		//for(int i=0; i< op.length; i++) {
	//		PO.p("op " + i+ " = ", op[i].getPart(), 1);
	//	}

		//PO.p(" wi = ", wi);
		
	//	PO.p("                                   " + this.getClass().getSimpleName());
	//	PO.p("speed deg = " + sel + " \nselected change = " + rsel + " mid = " + MIDV);
	//			+ " \nselected change = " + minindex + " mid = " + MIDV);
	//	PO.p("transeasrched part = ", op[rsel].getPart(), 1);
		
	//	PO.p(" finding closest " + this.getType() + " rsel " + rsel + " - " + 
	//			" midv " + MIDV + " = " + (rsel-MIDV));
		
	//	PO.p("result = ", op[rsel].getPart(), 1);
		
		// just to show it better, sort using the combined rating
		quickSort(wi, 0, wi.length-1, 4);
		
		if(this.getClass().getName().equalsIgnoreCase("keymosearch")) {
		System.out.print("wi = "  + "\n {");
        for(int i=0; i<wi.length; i++) {
            System.out.print(" " + i +"[");
            for(int j = 0; j< wi[i].length; j++) {
                System.out.print("," + wi[i][j] + " ");
            }
            System.out.print("],\n");
            
            PO.p("op root = " + op[(int)wi[i][1]].getTonalManager().getRoot() +
            	  " scale = " +  op[(int)wi[i][1]].getTonalManager().getScaleName());
            
        }
        System.out.print("}");
		} else if(this.getType().equalsIgnoreCase("add remove")){
	//		PO.p("wi = ", wi);
			/*
		//	PO.p("wi = ", wi);
			for(int i=0; i< this.opn(); i++) {
				if(op[i] == null) {
					PO.p(" op[i] null, this is " + this.getType() + " i = " + i);
				}
				PO.p("op " + i + " = ", op[i].getPart(), 1);
			}//*/
		}
		
		if(VB) {
		PO.p("\n\ntransearch " + this.getType());
		PO.p(" selected = " + rsel + " .  mid = " + this.MIDV + "  result = ");
		PO.p("   ", op[rsel].getPart(), 2);
		}
		return op[rsel];

	}

	/**
	 *  wi [][x] will be have the following values:
	 *   0 = dif(m, t); 1 = index; 2 = miss_fac; 3 = consistency; 4 = sel_func
	 *   
	 *   miss factor is the amount of either side of the target,
	 *   normalised by the maximum possible max(targ - 0, diff(s, t)-targ).
	 *   
	 *   consistency is the measure of the difference of the functional 
	 *   difference from source dif(m, s)/dif(s,t) (mdev) and the target 
	 *   1 - (dif(m, t)/dif(s,t)) (mhone). If it is smooth, there will be little 
	 *   difference between these.  the result is already normalized.  eg if
	 *   mdev = 0 (hasn't changed at all); and mhone = 0 (is far away from the 
	 *   target) it is consistent.  Where as, if mdev = 1 (has totally changed
	 *   from the source) and if mhone = 0 (is still far away from the target)
	 *   it is very inconsisent.  if mdev = 0 and mhone = 1 it hasn't deviated from
	 *   s but has become similar to target
	 *   
	 * @param wi
	 * @param midw
	 * @param targdif
	 * @param t 
	 * @param f 
	 * @return
	 */
	private int selectMorph(double[][] wi, double midw, double targdif, 
							LPart t, LPart f) {

		//PO.p("targ diff = " + targdif + " midw = " + midw);
		
		// find the position which is closest to the target, but also as 
		// consistent as possible
		double [] min = new double [2];
		min[0] = Double.MAX_VALUE;
		min[1] = -1; // index
		
		targdif = situationNowhereCheck(wi, targdif, midw);
		
		// 0 = dif(m, t); 1 = index; 2 = miss_fac; 3 = consistency; 4 = sel_func
		for(int i=0; i< wi.length; i++) {
		//	PO.p("select morph i = " + i);
			//add the miss factor
			wi[i][2] = miss(wi[i][0], targdif, midw, wi[0][0]);
		//	PO.p("post miss");
			//add the distance from the source
			wi[i][3] = differenceFunction(op[(int)wi[i][1]], f);
		//	PO.p("op " + i + "\n", op[(int)wi[i][1]].getPart(), 1);		
		//	PO.p("from " + i + "\n", f.getPart(), 1);		
		//	PO.p("diff = " + wi[i][3]);
			// put them together into a function
			wi[i][4] = wi[i][2]*(1-this.trackConsistence.getValue()) +
					   wi[i][3]*(this.trackConsistence.getValue());
			
			// if it is smallest so far, record it
			if(wi[i][4] < min[0]) {
				min[0] = wi[i][4];
				min[1] = i;
			}
		}
		return (int)min[1];
	}
	
	/**
	 * 
	 * to be able to check when the situation is that the "no-change" 
	 * transformation is the lowest, and still above 0. so that other options
	 * can be specified. 
	 * @param wi2 - MUST be sorted from lowest to highest
	 * @param targdif
	 * @param midw
	 * @return
	 * @see AddRem
	 */
	protected double situationNowhereCheck(double[][] wi2, double targdif, double midw) {
		return targdif;
	}

	/**
	 * 
	 * 
	 * @param mds
	 * @param mdt
	 * @return  0.5 < 1 is inconsistent; 0.5 is maximally consistent 0 < 0.5 is
	 * 			pivot-like
	 */
	private double cons(double mds, double mdt) {
		double co = (mds)+(mdt);
		
		co = co/2.0;
		
		// maybe add a squasher here
		
		// hang on! this is just the average difference or m 
		// between source an target/
		// 0 would mean that it is similar to source and target.
		// 1 would mean that it is different to source and target.
		// 0.5 would mean that one could be very different and the other could
		// be very similar; it says nothing about consistency!
		
		return co;
	}
	
	/**
	 * finds how far this index is off the target and normalises it by the 
	 * largest possible distance.
	 * @param m dif(m, t)
	 * @param t target difference
	 * @param f dif(f, t)
	 * @param lop the lowest possible difference in all the different ops
	 * @return
	 */
	private double miss(double m, double t, double f, double lop) {
		// find how much it misses by
		double miss = Math.abs(m-t);
		
		//demonimator is the larest distance from either side
		double deno = (Math.max(t-lop, f-t));
		if(deno == 0 && miss == 0) {
			
		} else {
			// normalise it
			miss = miss/deno;
		}
		
		// constrain it
		// if the denominator is 0,  needs to be capped at one
		if(miss > 1.0) {
			miss = 1.0;
	//		PO.p(" t " + t + " - lop " + lop + " = " + (t-lop) +
	//			"; f " + f+ " - t " + t + " = " + (f-t) + " p miss = " + miss*(Math.max(t-lop, f-t)) +
	//			" n miss =  "+ miss);
		}
		
		return miss;
	}
	

	/**
	 * indicates wether the most recently processed operatio ninduced a change 
	 * in the data
	 * @return
	 */
	public boolean isChanged() {
		if(MIDV == rsel)
			return false;
		else
			return true;
				
	}
	
	/** this can be overridden */
	protected double costOfIndex(int i) {
		return co[i].getValue();
	}

	/**
	 * Used at the beginning of each find to reset params
	 * 
	 * creates copies of f and puts them into op[].
	 * 
	 * each of these can then be modified.
	 * 
	 * calls initCosts also
	 */
	protected void resetOps(LPart f) {
		Mod.quickSort(f.getPart());
		//PO.p(" opn = " + opn() + " op legth = " + op.length);
		if(op.length < opn()) {
			op = new LPart [opn()+10];
			w = new double [opn()+10];
		}
		
		for (int i = 0; i < opn(); i++) {
		//	PO.p("reseting ops");
			
			op[i] = f.copy();
			w[i] = Double.NaN;
		}
		// would be needed if not Add/Remove
		//initParams()

	}
	
	/**
	 * called upon initialisation of this object.
	 * 
	 * Transearch algorithms that need to change the number of options dynamic-
	 * ally, should implement a separate method to do this. (so that the values
	 * aren't also reset)
	 */
	protected void initParams() {
		initOPN();
		
		op = new LPart[this.opn()];
		//PO.p(" initin params. opn = " + opn() + " op legth = " + op.length);
		
		w = new double[opn()];
		
		this.dev = (new ParameterMap()).construct(0, opn(), 0, opn(), 0.0, 
																"deviation");
		
		this.devDec = 
			(new ParameterMap()).construct(0, 100, 0, 1.0, 0.5, "deviation decay");
		
		this.traspee = 
			(new ParameterMap()).construct(0, 100, 0, 1.0, 0.5, "transform spd");
		
		this.trackConsistence = 
			(new ParameterMap()).construct(0, 100, 0, 1.0, 0.5, "tra v cons");
		
		initCostParams(opn()); // the cost parameters are often of different 
		//length to op and w
		
		setInitialCostValues();
	}
	
	protected abstract void initOPN();
	
	protected void initCostParams(int num) {
	//	PO.p("initinc cost params for " + this.getClass().getName() + " = " + num);
		co = new ParameterMap[num];
		for (int i = 0; i < num; i++) {
			co[i] = new ParameterMap();
			co[i].construct(0, 100, 0, 1, 1.0);
		}
	}

	/**
	 * for the child TransSearch to set individual values for the cost
	 */
	protected abstract void setInitialCostValues();

	
	/**
	 * enable the cost parameters to be used in a GUI
	 * 
	 * @return
	 */
	public ParameterMap[] getCostParams() {
		return this.co;
	}
	
	public ParameterMap [] getOtherParams() {
		return this.otherParams;
	}

	/**
	 * transearch algorithms should implement this method so that the particular
	 * difference function can be used when finding the target op
	 * 
	 * @param f
	 * @param t
	 * @return
	 */
	public abstract double differenceFunction(LPart f, LPart t);
	
	public LPart find(final LPart f, final LPart t, int steps, double [] glopar) throws InterruptedException {
		//double [] ng = new double [] {0, 0, 1.0};
	//	if(this.VB)
	//		PO.p("find... bypass = " + bypass);
		//PO.p(" f = ", f.getPart(), 1);
		if(bypass == true) { // || (f.getPart().getSize() == 0) {
			this.rsel = MIDV;
			return f.copy();
		} else 
			this.findb(f, t, steps);
		
		
		return this.findClosest(steps, f, t, glopar);
	}
	
	/**
	 * each sub-class should override this method to carry out the following:
	 * 
	 * set up the weights w according to distances of the various 
	 * transformations to the target according to a metric that is relevant
	 * to the transformation
	 * 
	 * @param f
	 * @param t
	 * @param steps
	 * @throws InterruptedException
	 */
	protected abstract void findb(final LPart f, final LPart t, int steps) throws InterruptedException;

	
	public void checkError(LPart f, LPart t) {
		if (f.size() == 0 || t.size() == 0)
			return;
		try {
			if (f.getPart().getPhrase(f.size() - 1).getStartTime() >= f.getScope().getValue()
					|| t.getPart().getPhrase(t.size() - 1).getStartTime() >= t.getScope()
							.getValue()) {
				
				PO.p("f =  ", f.getPart(), 1);
				PO.p("t = ", t.getPart(), 1);
				Exception e = new Exception("note phrases within f and t must"
						+ " be within bounds of scope,  f " + f.getScope().getValue() +
						"  t " + t.getScope().getValue());
				
				e.fillInStackTrace();
				throw e;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	/**
	 * useful method, not always used
	 * @param ex
	 * @return
	 */
	protected double[] initVAL(int ex) {
		double[] val = new double[ex * 2 + 1];
		for (int i = 0; i < val.length; i++) {
			val[i] = i - ex;
		}
		return val;
	}


	private static void quickSort(double[][] wi, int lowIndex, int highIndex, int sobi) {
		int lowToHighIndex, highToLowIndex, pivotIndex;

		double pivotValue, lowToHighValue, highToLowValue;
		double [] parking;
		
		int newLowIndex, newHighIndex, compareResult;

		lowToHighIndex = lowIndex;
		highToLowIndex = highIndex;

		/**
		 * Choose a pivot, remember it's value No special action for the pivot
		 * element itself. It will be treated just like any other element.
		 */
		pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
		pivotValue = wi[pivotIndex][sobi];

		/**
		 * Split the Vector in two parts.
		 * 
		 * The lower part will be lowIndex - newHighIndex, containing elements <=
		 * pivot Value
		 * 
		 * The higher part will be newLowIndex - highIndex, containting elements >=
		 * pivot Value
		 */
		newLowIndex = highIndex + 1;
		newHighIndex = lowIndex - 1;
		// loop until low meets high (until partition complete)
		while ((newHighIndex + 1) < newLowIndex) { 
			// loop from low to high to find a candidate
			
			// for swapping
			lowToHighValue = wi[lowToHighIndex][sobi];
			while (lowToHighIndex < newLowIndex && lowToHighValue < pivotValue) {
				newHighIndex = lowToHighIndex; // add element to lower part
				lowToHighIndex++;
				lowToHighValue = wi[lowToHighIndex][sobi];
			}

			// loop from high to low, find other candidate for swapping
			highToLowValue = wi[highToLowIndex][sobi];
			while (newHighIndex <= highToLowIndex
					&& highToLowValue > pivotValue) {
				newLowIndex = highToLowIndex; // add element to higher part
				highToLowIndex--;
				highToLowValue = wi[highToLowIndex][sobi];
			}

			// swap if needed
			if (lowToHighIndex == highToLowIndex) {
				// one last element may go in either part
			
				newHighIndex = lowToHighIndex; // move element arbitrary to lower part
			
			} else if (lowToHighIndex < highToLowIndex) { //not last element yet

				if (lowToHighValue >= highToLowValue) {
					// low >= high, swap, even if equal
					parking = wi[lowToHighIndex];
					wi[lowToHighIndex] = wi[highToLowIndex];
					wi[highToLowIndex] = parking;
					
					newLowIndex = highToLowIndex;
					newHighIndex = lowToHighIndex;

					lowToHighIndex++;
					highToLowIndex--;
				}
			}
		}

		// Continue recursion for parts that have more
		// than one element
		if (lowIndex < newHighIndex) {
			quickSort(wi, lowIndex, newHighIndex, sobi); // sort
			// lower
			// subpart
		}
		if (newLowIndex < highIndex) {
			quickSort(wi, newLowIndex, highIndex, sobi); // sort
			// higher
			// subpart
		}

	}
	
	public abstract String getType();
	
	public void dload(Element e) {
		if(e.hasAttribute("dev"))
			this.dev.setValue(e.getAttribute("dev"));
		if(e.hasAttribute("devDec"))
			this.devDec.setValue(e.getAttribute("devDec"));
		if(e.hasAttribute("traspee"))
			this.traspee.setValue(e.getAttribute("traspee"));
		if(e.hasAttribute(BYP))
			this.bypass = Boolean.valueOf(e.getAttribute(BYP)).booleanValue();
		if(e.hasAttribute(TRACON))
			this.trackConsistence.setValue(e.getAttribute(TRACON));
		
		NodeList nl = e.getElementsByTagName("cost");
	//	PO.p("nl.length = " + nl.getLength());
		for(int i=0; i< nl.getLength(); i++) {
	//		PO.p(" element name " + ((Element)nl.item(i)).getAttribute(ParameterMap.PARAM_NAME));
			for(int j = 0; j<co.length; j++) {
		//		PO.p(" j name = " + co[j].getName());
				
				if(((Element)nl.item(i)).getAttribute(ParameterMap.PARAM_NAME).equalsIgnoreCase(
					co[j].getName())) {
					co[j].setClosestValue(
						Double.parseDouble(
								((Element)nl.item(i)).
								getAttribute(ParameterMap.VALUE)));
				}
			}
		}
		/*
		for (int i = 0; i < this.co.length; i++) {
            String str = e.getAttribute(co[i].getName()
                .replace(' ', '_'));
            if (str.length() > 0) {
                co[i].setValue(str);
            }
        }*/
    }
	
	private static final String BYP = "bypass";
	private static final String TRACON = "tracon";
	
	
    public void dsave(Element e) {
    	//String ts = new String();
    	if(this.getType() == null) {
    		PO.p("getType() method needs to be properly filled in for the " + 
    				"subclass of transearch " + this.getClass().getName());
    		return;
    	}
    	
        e.setAttribute("type", this.getType());
        
        e.setAttribute("dev", dev.getValueStr());
        e.setAttribute("devDec", devDec.getValueStr());
        
        e.setAttribute("traspee", traspee.getValueStr());
     //   PO.p("setting bypass = " + String.valueOf(bypass));
        
        e.setAttribute("bypass", String.valueOf(bypass));
        
        e.setAttribute(TRACON, this.trackConsistence.getValueStr());
               
        
		for (int i = 0; i < this.co.length; i++) {
			e.appendChild(Domc.sa(co[i], "cost", e.getOwnerDocument()));
		}
		
    }

	public ParameterMap getDev() {
		return dev;
	}

	public ParameterMap getDevDec() {
		return devDec;
	}
	
	public ParameterMap getTransformSpeed() {
		return this.traspee;
	}

	public boolean getBypass() {
		return this.bypass;
	}
	
	public ParameterMap getTrackVsConsistence() {
		return this.trackConsistence;
	}

	public void setBypass(boolean b) {
	//	PO.p("setting bypass to " + b);
		this.bypass = b;
	}
	
	public void printW() {
		PO.p("wi = \n", wi);
	}
	
	
	
	public void printSWO() {
		PO.p("---- " + this.getType() + " -------\n");
		PO.p(" sel = " + rsel + " midv = " + this.MIDV);
		PO.p("op = \n", op);
		PO.p(" wi = \n", wi);
	}

	// set it to non-biased settings
	public void config(int i) {
		for(int j=0; j< co.length; j++) {
			co[j].setValue(1.0);
		}
		
	}
	
	
}
