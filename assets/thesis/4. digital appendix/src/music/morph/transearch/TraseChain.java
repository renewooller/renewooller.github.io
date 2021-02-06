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
import ai.AllCompare;
	
public class TraseChain implements Domable {

	// a weight that affects the speed of all the individual transearch
	// algorithms in the chain
	private ParameterMap devFac = 
		(new ParameterMap()).construct(0, 2000, 0, 2.0, 1.0, "global deviation");
	//	a weight that affects the decoy of speed of all the individual transearch
	// algorithms in the chain
	private ParameterMap decFac = 
		(new ParameterMap()).construct(0, 1000, 0, 1.0, 0.5, "global decay");
	
	// factor of how much many frames this trase chain makes
	private ParameterMap frasi = 
		(new ParameterMap()).construct(1, 1000, 30, "limit of frames");
	
	// factor that is multiplied with the middle (no change) cost factor
	private ParameterMap nochafa = 
		(new ParameterMap()).construct(0, 1000, 0, 1.0, 1.0, "global no change");
	
	private ParameterMap glotraspeed = 
		(new ParameterMap()).construct(0, 1000, 0, 2.0, 1.0, "global transform speed");
	
	private ParameterMap nnacfac = 
		(new ParameterMap()).construct(1, 15, 1, "nn acc");
	
	
	private ParameterMap simCutOff =
		(new ParameterMap()).construct(0, 1000, 0, 0.2, 0.1, "similarity cutoff");
	
	//private ParameterMap optiFrasi = 
	//	(new ParameterMap()).construct(1,frasiFac "optimal no");
	
	private Metric met = new Metric();
	private AllCompare ac = new AllCompare();
	
	//private int morphLength;
	
	private boolean VB = false;//true;//true; // verbose print out
//	private boolean VBT = true; // verbose timing information
	
	private TransSearch [] trans = new TransSearch [] {
		 new DivMerge(), new Rate(), new Phase(), new Harmonise(),
		 new PitchStretch(), new Inversions(),//new Transpose(), new ModeLock(),
		 new Octave(), new AddRem()
	};
	
	// how many operations are allowed to be performed each time
	private ParameterMap mutrat = 
		(new ParameterMap()).construct(0, trans.length, 2,"mutation limit");
	
	
	int nodifcount;


	public TraseChain() {
		super();
		
		((AddRem)trans[trans.length-1]).getRepeats().setValue(3.0);
		
	}
	
	int recurCount = 0;
	public LPart[] transformComplete(LPart lfc, final LPart ltc) {
		recurCount = 0;
		
		// set modelock up with the information it needs
		for(int i=0; i< trans.length; i++) {
			if(trans[i] instanceof ModeLock) {
				((ModeLock)trans[i]).setContextScale(lfc, ltc);
			}
		}
		
		
	//	PO.p("\n\n transforming all \n");
		LPart [] tra = new LPart [frasi.getValueInt()+1];
		int tranum = 0;
		
		tra[tranum++] = lfc.copy();
			
	//	PO.p("firt = ", tra[tranum-1].getPart(), 1);
	//	PO.p("tra length =  " + tra.length);
		if(VB) {
		PO.p("original part ", lfc.getPart(), 1);
		PO.p("target part = ", ltc.getPart(), 1);
		}
		
		double difnn = met.difnn(tra[tranum-1], ltc, ac);
		if(nochafa.getValue() < 1) {
			difnn += 0.00001;
		}
		if(VB)
		PO.p("difnn = " + difnn + " tranum  = " + tranum + " tra.length = " + 
				tra.length + " sim cutoff = "+ this.simCutOff.getValue());
	//	double predif = -1;
	//	LPart prepart = null;
		while(difnn > this.simCutOff.getValue() && 
				tranum < tra.length 
				) {
			
			if(VB)
			PO.p("\ntransform step " + tranum + "\n");
			
		//	PO.p("tranum = " + tranum + " compare to part = ", ltc.getPart(), 1);
			
			try {
			//	PO.p("transformComplete, finding.  tranum = " + tranum);
		//		PO.p("predif = " + predif + " difon = " + difnn);
	
				tempnopoli = false;//1-this.trans[trans.length-1].getCostParams()[AddRem.POL].getValueInt();
				this.nodifcount = 0;
				tra[tranum] = find(tra[tranum-1], ltc, tranum);//, (predif-difnn), prepart);
				
			//	PO.p("continuing");
				
				if(tra[tranum] == null) {
					NullPointerException e = new NullPointerException ("tra is null");
					e.fillInStackTrace();
					throw e;
				}
			} catch(NullPointerException ne) {
				ne.printStackTrace();
			} catch (InterruptedException e) {
			
				PO.p("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!/ninterupted/n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				return null;
			}
			
		//	PO.p("2 tranum = " + tranum + " compare to part = ", ltc.getPart(), 1);
			Mod.quickSort(tra[tranum].getPart());
			Mod.removeSameRT(tra[tranum].getPart());
			if(VB) {
			PO.p("part " + tranum + " = ", tra[tranum].getPart(), 1);
			PO.p("target   = ", ltc.getPart(), 1);
			}
		//	predif = difnn;
		//	prepart = tra[tranum].copy();
			difnn = met.difnn(tra[tranum], ltc, ac, Math.pow(0.1,nnacfac.getValue()));
			
			// if they are the same, don't iterate
		//	if(predif < difnn + 0.000000000000001 &&
		//	   predif > difnn - 0.000000000000001) {
				
		//	} else {
				tranum++;
		//	}
			
			//PO.p("difnn = " + difnn + " tranum " + (tranum-1));
			
	//		PO.p("3 tranum = " + tranum + " compare to part = ", ltc.getPart(), 1);
			
			
			
			if(difnn == 0) {
			//	PO.p("no dif part = ", tra[tranum-1].getPart(), 1);
		//		PO.p("target part = ", ltc.getPart(), 1);
			//	PO.p("checking again " +
			//			met.difnn(tra[tranum-1], ltc, ac, 
			//					Math.pow(0.1,nnacfac.getValue())));
				
				
			}
			
		}
		if(VB)
		PO.p("target part = ", ltc.getPart(), 1);
		
		LPart [] ret = new LPart [tranum+1];
		for(int i=0; i< (ret.length-1); i++) {
			ret[i] = tra[i];
		}
		ret[ret.length-1] = ltc.copy();
	//	PO.p(recurCount + ", recurcount");
		return ret;
	}
	
	/**
	 * 
	 * @param f
	 * @param t
	 * @param steps
	 * @param difon the difference between the nearness of the previous step and the one before it
	 * @return
	 * @throws InterruptedException
	 */
	//int cauco = 0;
	private boolean tempnopoli = false;
	public LPart find(LPart f, final LPart t, int steps) throws InterruptedException {
		
		LPart fc = f.copy();
		int chaco = 0; // count the number of times a change is made
		if(VB) {
		PO.p("\n");
		PO.p("at step : " + steps);
		PO.p("part  ", fc.getPart(), 1);
		}
		//if((int)(1.000000000000001 - difon) == 1.0) {
			
	//	} else {
	//		cauco = 0;
	//	}
		double ngts = this.glotraspeed.getValue();//*((50.0-nodifcount*1.0)/50.0);
		int i = 0;
		if(tempnopoli) {
			i = trans.length-1;
		}
		for(; i<trans.length; i++) {
			if(VB)
			PO.p("transforming chain " + i + " "  + trans[i].getType());
			
			
			Mod.cropFaster(fc.getPart(), 0.0, f.getScope().getValue());
			
			
			
		//	if(i == trans.length-1) {
		//		PO.p("poli = " + trans[i].getCostParams()[AddRem.POL].getValueStr());
		//	}
			
			fc = trans[i].find(fc, t, steps, new double [] {devFac.getValue(),
					decFac.getValue(), 
					this.nochafa.getValue(), ngts});
			
			//PO.p("tc. glotraspeed = " + this.glotraspeed.getValue()*(nodifcount*1.0/10.0 +1.0));
			if(trans[i].isChanged()) {
				chaco++;
			}
					
			// if this round was switched to opposite poli mode, switch it back
			if(tempnopoli)  {
			//	trans[trans.length-1].getCostParams()[AddRem.POL].setValue(
		//				1-trans[trans.length-1].getCostParams()[AddRem.POL].getValue());
				tempnopoli = false;
			}
			
			if(chaco > this.mutrat.getValueInt()) {
				break;
			}
				
		}	
		
		if(VB)
		PO.p("tc.  chaco = " + chaco);
		
		if(met.difnn(fc, f, ac) == 0) { // this could be changed to simply equals
			nodifcount++; // gets reset before each frame find
			recurCount++; // this one gets reset at each transformComplete
			//System.out.print("    rcnt" + recurCount + " ");
			
			if(nodifcount >2) {
			PO.p("no difference to last one! at step " + steps + ", caught in loop " + nodifcount);//increasing the deviation"); //;
			PO.p("last ", f.getPart(), 1);
			PO.p("this ", fc.getPart(), 1);
			PO.p("target", t.getPart(), 1);
			PO.p("ngts = " + ngts);
			trans[trans.length-1].printSWO();
			}
	//		cauco++; // redundant, not that deviation isn't really being used
			
		//	PO.p("printing oct and add/rem choice ");
		//	trans[trans.length-2].printSWO();
		//	trans[trans.length-1].printSWO();
			
			//// temporarily switch it to opposite mode
				tempnopoli = true; // signal that it has been switched, so it can switch back
			//	trans[trans.length-1].getCostParams()[AddRem.POL].setValue(
			//		1-trans[trans.length-1].getCostParams()[AddRem.POL].getValueInt());
			//	PO.p("skipping all except for add/remove on next round");
			//}
			
			if(nodifcount > 60) {
				PO.p(" printing the choices made \n");
				for(int j=0; j< this.trans.length; j++) {
					trans[j].printSWO();
					
				}
				PO.p(" stopping the needless creation of similar states ");
				throw new InterruptedException();
			}
			
			
	//		PO.p("tc. recoursing to recursion " + nodifcount);
			return this.find(f, t, steps);
		}
		
		return fc;
	}
	
	public int getRecurCount() {
		return this.recurCount;
	}
	
	public void dload(Element e) {
		devFac.setValue(e.getAttribute(GDEV));
		decFac.setValue(e.getAttribute(GDEC));
		if(e.getAttribute(MUTR).length() > 0) // backwards compatable
			mutrat.setClosestValue(Double.parseDouble(e.getAttribute(MUTR))); // forwards compatable
		
		if(e.getAttribute(FRASI).length() > 0) // backwards compatable
			this.frasi.setClosestValue(Double.parseDouble(e.getAttribute(FRASI))); // forwards compatable
		
		//PO.p("frasi loadded" + frasi.getValue());
		
		if(e.getAttribute(NOCHA).length() > 0) // backwards compatable
			this.nochafa.setClosestValue(Double.parseDouble(e.getAttribute(NOCHA))); // forwards compatable
		
		
		if(e.hasAttribute(NNAC)) // bc and concise
			this.nnacfac.setValue(e.getAttribute(NNAC));
		
		if(e.hasAttribute(GLOTRASP))
			this.glotraspeed.setValue(e.getAttribute(GLOTRASP));
		
		if(e.hasAttribute(CUTOFF))
			this.simCutOff.setValue(e.getAttribute(CUTOFF));
		
		trans = new TransSearch [Integer.parseInt(e.getAttribute(NUMT))];
		NodeList tsearr = e.getElementsByTagName(TSEA);
	//	PO.p("loading the trase chain, length  " + tsearr.getLength() + " numt = " + trans.length);
		
		for(int i=0; i< tsearr.getLength(); i++) {
	//		PO.p(" at " + i);
			
			Element tse = (Element)tsearr.item(i);
			trans[i] = (TransSearch)(Domc.lo(tse, e.getOwnerDocument()));
		}
		
		
		
	}

	private static String GDEV = "gdeviation";
	private static String GDEC = "gdecay";
	private static String MUTR = "mutation_rate";
	private static String NUMT = "num_of_trans";
	private static String TSEA = "a_transSearch";
	private static String FRASI = "frame_limit";
	private static String NOCHA = "stasis_cost_factor";
	private static String NNAC = "nn_acc_factor";
	private static String GLOTRASP = "global_transform_speed";
	private static String CUTOFF = "similarity_cutoff";
	
	public void dsave(Element e) {
		e.setAttribute(GDEV, devFac.getValueStr());
		e.setAttribute(GDEC, devFac.getValueStr());
		e.setAttribute(NUMT, String.valueOf(trans.length));
		e.setAttribute(MUTR, this.mutrat.getValueStr());
		e.setAttribute(FRASI, this.frasi.getValueStr());
		e.setAttribute(NOCHA, this.nochafa.getValueStr());
		e.setAttribute(NNAC, this.nnacfac.getValueStr());
		e.setAttribute(GLOTRASP, this.glotraspeed.getValueStr());
		e.setAttribute(CUTOFF, this.simCutOff.getValueStr());
		
		for(int i=0; i< this.trans.length; i++) {
			e.appendChild(Domc.sa(trans[i], TSEA, e.getOwnerDocument()));
		}
		
	}

	public ParameterMap getDecFac() {
		return decFac;
	}

	public ParameterMap getDevFac() {
		return devFac;
	}

	public ParameterMap getMutationLimit() {
		return this.mutrat;
	}
	
	public ParameterMap getNoChangeFactor() {
		return this.nochafa;
	}

	public ParameterMap getGlobalTransformSpeed() {
		return this.glotraspeed;
	}
	
	public ParameterMap getFrameLimit() {
		return this.frasi;
	}
	
	public TransSearch [] getChain() {
		return this.trans;
	}
	
	

	public String toString() {
		StringBuffer sb = new StringBuffer(" Trase Chain = \n");
		for(int i=0; i< this.trans.length; i++) {
			sb.append(trans[i].getType() + "\n");
		}
		return sb.toString();
	}

	//public void setMorphLength(int morphLength) {
	//	this.morphLength = morphLength;
	//}

	public ParameterMap getNNacc() {
		return this.nnacfac;
		
	}

	public ParameterMap getSimCutOff() {
		return this.simCutOff;
	}
	
	public TransSearch getTraseType(Class cl) {
		
		for(int i=0; i< this.trans.length; i++) {
			if(trans[i].getClass().getName().equalsIgnoreCase(cl.getName()) ) {
				return trans[i];
			}
		}
		return null;
	}

	
	
}
