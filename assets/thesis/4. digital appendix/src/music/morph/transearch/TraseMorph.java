/*
 * Created on 2/02/2006
 *
 * @author Rene Wooller
 */
package music.morph.transearch;

import jm.music.data.Part;
import jm.music.tools.Mod;
import jmms.TickEvent;
import music.LPart;
import music.morph.rt.MorpherRT;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.gui.ParameterMap;
import ren.io.Domc;
import ren.music.PhraseQ;
import ren.tonal.TonalManager;
import ren.util.PO;
import ren.util.RMath;


public class TraseMorph extends MorpherRT {

	//TODO : make inversion costs be simple <0 gradiant, 0, >0 gradient
	// so that 0 isn't preffered! (at the moment it is)
	
	// part frame array: holds all the different states of the part over the 
	// whole transition
	private LPart [] pafar;
	
	// holds the scale and key information (SCAKE)
	private TonalManager [] tonar; 
	
	//	 metric used  for calculating the key changes in tonar
	private Metric met;
		
	// A special TraseSearch that is used to find the changes in key and scale
	// throughout the morph
	private KeyMoSearch kemosea = new KeyMoSearch();
	
	// the original parts
	private LPart lfrom, lto;
	
	// copies of the original parts that have been converted to 
	// DESCK (14-slot degrees) representation
	private LPart defrom, deto;
	
	
	
	// the chain that does the processing of the DEPA gestalt
	private TraseChain trach = new TraseChain();
	
	private boolean starting = false;
	
	private boolean VBT = false; // verbose timing information
	
	public TraseMorph() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void startInit() {
		this.starting = true;
		seliFac = 0;//-0.00000001;
	}
	private double seliFac = 0.0;
	public Part[] morphRT(LPart[] toFrom, LPart[] mlparts, double morphIndex, TickEvent e,
			PhraseQ hist) {
	//	PO.p("morphing rt in traseMorphg");
		if(pafar == null)
			PO.p(toFrom[0].getPart().getTitle());
		
		if(starting) {
			if(morphIndex < 0.5) { // going forwards
				seliFac = -0.00000001;
			} else if (morphIndex > 0.5) { // going backwards		
				seliFac = 0.00000001;
			}
			starting = false;
		} //else if
		
		//PO.p("selifac = " + seliFac);
		
		int seli = Math.min((int)((morphIndex- seliFac)*(pafar.length)), pafar.length-1);
		if(seli >= pafar.length)
			PO.p(" bigger! seli = " + seli + " morph index = " + morphIndex + " seliFac = " + seliFac);
		
		
		Part [] tr = new Part [2];
		pafar[seli].getShuffle().setValue(mlparts[0].getShuffle().getValue());
		pafar[seli].getQuantise().setValue(mlparts[0].getQuantise().getValue());
		
		// set the tonal manager
		if (tonar != null) {
			int kseli = (int) ((morphIndex - 0.00000001) * (tonar.length));
			
			//pafar[seli].setTonalManager(this.tonar[kseli]);
			pafar[seli].initToTonalManager(tonar[kseli]); // automatically deals with stepetot changes
		//	pafar[seli].getTonalManager().setRoot(tonar[kseli].getRoot());
		}
		
		// output from LPart automatically converts from DEPA to absolute format
		tr[0] = pafar[seli].getTickPart(e);
					
		// update the current tonal manager that is used for inter-part communication
		mlparts[0].setTonalManager(pafar[seli].getTonalManager());
		
		// leave other part empty
		tr[1] = toFrom[1].getPart().copyEmpty(); // v2
		
	//	PO.p("tr0 = ", tr[0], 1);
	//	PO.p("tr1 = ", tr[1], 1);
		
		//PO.p("tr 0 v = " + tr[0].getVolume() + " tr 1 v = " + tr[1].getVolume());
		
		
		return tr;
	}

	public String getType() {
		return "Transform Heuristic Search";
	}

	/**
	 * lpmorph is unused in thie implementation
	 */
	public void initParts(LPart lfrom, LPart lto, LPart[] lpmorph, int morphLength) {
		this.lfrom = lfrom.copy();
		this.lto = lto.copy();
			
		// convert into desck (degree, scale, key) representatation
		this.defrom = lfrom.copy().convertToDEPA();
		this.deto = lto.copy().convertToDEPA();
		
		//PO.p("initin in the trase morph";
		
		//apears to be redundant
	//	this.trach.setMorphLength(morphLength);
		
	
		
		
		
		/* .. auto setting of parameters.  it will need to use nndif instead of avnn
		double avn = ((lfrom.getPart().getSize() + 
					  lto.getPart().getSize())*1.0)/2.0;
		
		this.trach.getFrameLimit().setClosestValue(30.0*(16.0/avn));
		((AddRem)this.trach.getTraseType(AddRem.class)).getRepeats().setClosestValue(4.0*(avn/16.0));
		*/
	}
	
	
	public void initTrase() {
		//PO.p(" here");
		
		if(defrom == null || (defrom.getPart().length() == 0 && deto.getPart().length() == 0))
			return;
		//PO.p(" there   ere");
		LPart lfc = defrom.copy();
		LPart ltc = deto.copy();
		double sc1 = defrom.getScope().getValue();
		double sc2 = deto.getScope().getValue();
		Mod.cropFaster(lfc.getPart(), 0, sc1, false);
		Mod.cropFaster(ltc.getPart(), 0, sc2, false);
		double nsc = -1;
		
		// if the higher scope can be made from perfect divisions of the lower
		if((Math.max(sc1, sc2)/Math.min(sc1, sc2))%1.0 == 0) {
			//set the new scope to be the highest one
			nsc = Math.max(sc1, sc2);
		} else {
			// if they are polyscopic ;) (like polyrhythms), use the cycle that
			// is the lowest multiple
			nsc = RMath.getMultiple((int)sc1, (int)sc2);

		}
		
		lfc.getScope().setClosestValue(nsc);
		ltc.getScope().setClosestValue(nsc);
		
	//	PO.p("from before = ", lfc.getPart(), 1);
		Mod.repeatRT(lfc.getPart(), defrom.getScope().getValue(), nsc);
		Mod.repeatRT(ltc.getPart(), deto.getScope().getValue(), nsc);
		
	//	PO.p("lfrom.getScpre = " + lfrom.getScope().getValue() + " nsc = " + nsc);
	//	PO.p("from after = ", lfc.getPart(), 1);
		
	//	lfc.convertToDEPA();
	//	ltc.convertToDEPA();
	//	PO.p("lfc)
		
	//	PO.p("to = ", ltc.getPart(), 1);
		
		double t1 = System.currentTimeMillis();
		
		
		LPart [] tarr = this.trach.transformComplete(lfc, ltc);
		
		
		
		if(tarr != null )
			pafar = tarr;
		else 
			PO.p("null trase generated");
		
		if(VBT) {
			t1 = System.currentTimeMillis()-t1;
			//PO.p(" for \n");
			//PO.p(" src    ", lfc.getPart(), 1);
			//PO.p(" target ", ltc.getPart(), 1);
			
			PO.p("\nFor src.length of " + lfc.getPart().length() +
					", + targ.lenth of " + ltc.getPart().length() + ", = " + 
					(lfc.getPart().length() + ltc.getPart().length()));
			PO.p(" frame limit = " + this.trach.getFrameLimit().getValue() + 
					" actual frames = " + (pafar.length-2));
			PO.p("TraSe morph generated in " + t1/1000.0 + " seconds \n");
			
		}
		
		if(this.tonar == null) {
			this.initKeyTrase();
		}
		
	}
	
	public void initKeyTrase() {
		TonalManager [] tetoma = new TonalManager [200];
		int i=0;
		tetoma [i++] = this.defrom.getTonalManager().copy();
		
		TonalManager toto = this.deto.getTonalManager().copy();
		
		//if(tetoma[i-1].calculateLargestStep(tetoma[i-1].getScaleType()) !=
		//   toto.calculateLargestStep(toto.getScaleType())) {
			//TonalManager.setGlobDepStep(3);
		//}
			
		
		LPart tef= new LPart();
		LPart ptef = new LPart(); // used for compating to the previous 
		tef.setTonalManager(tetoma[0]);
		
		boolean failed = false;
		
		double dif = this.kemosea.differenceFunction(tef, deto);// met.difKey(tetoma[0], toto);
		int stepAt = 0;
		while (dif > 0) {
			if(i > (tetoma.length-1)) {
				break;
			}
			
			tetoma [i] = findKey(tetoma[i-1], toto, stepAt++);
			
			tef.setTonalManager(tetoma[i]);
			dif = kemosea.differenceFunction(tef, deto);//met.difKey(tetoma[i], toto);
				
			ptef.setTonalManager(tetoma[i-1]);
			if(kemosea.differenceFunction(tef, ptef) == 0) {
				this.kemosea.printW();
				PO.p("tetoma " + i + " no difference from previous - " + 
						"Key search FAILED; reduce track vs consistency \n" + 
						" or reduce the influence of key-difference (not a proper distance)");
				failed = true;
				break;
			}
			
			PO.p("\n\n step " + i + " : ");
			kemosea.printW();
			
			i++;
		}
	//	PO.p("from = " + this.defrom.getTonalManager().toString());
	//	PO.p("tp = "+ this.deto.getTonalManager().toString());
		
		// store the results
		if(failed) {
			this.tonar = new TonalManager [1];
		} else {
			this.tonar = new TonalManager [i];
		}
		
		for(int j = 0; j<i; j++) {
			//if(VB)
			//PO.p(" frame " + j + " : " + tetoma[j].toString());
			
			if(failed) {
				
			} else { // only put it in if it didn't fail;
				this.tonar[j] = tetoma[j];
			}
		}
		if(failed)
			PO.p(" FAILED ");
		
		
		
	}
	
	private TonalManager findKey(TonalManager from, TonalManager to, int steps) {
		return this.kemosea.find(from, to, steps, null).getTonalManager();
	}
	
	/**
	 * returns the length of the array of transitions, or 0 if the array in null
	 * @return
	 */
	public int getLength() {
		if(pafar != null)
			return pafar.length;
		else
			return 0;
		
	}
	
	public int getKeyLength() {
		if(tonar != null) {
			return tonar.length;
		} else {
			return 0;
		}
	}

	public TraseChain getTraseChain() {
		return this.trach;
	}

	public ParameterMap[] getPC() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isReady() {
		if(lfrom == null)
		return false;
		else return true;
	}

	public KeyMoSearch getKeyMoSearch() {
		return kemosea;
	}

	public void dload(Element e) {
			
			this.trach = (TraseChain)(Domc.lo(Domc.find(e, "trach"),		       
									          e.getOwnerDocument()));
			
			
			Element kem = Domc.find(e, KEMO);
			if(kem != null) 
				this.kemosea = ((KeyMoSearch)Domc.lo(kem, e.getOwnerDocument()));
			
			//PO.p(trach.toString());
			
			Element efro = Domc.find(e, "lfrom");
			if(efro != null) {
				
				this.lfrom = ((LPart)(Domc.lo(efro, 
					  e.getOwnerDocument())));
				this.lto = ((LPart)(Domc.lo(Domc.find(e, "lto"), 
					  e.getOwnerDocument())));
			}
			
			// load the pafar
			if(e.getAttribute(this.NUM_P).length() > 0) {
				this.pafar = 
					new LPart [Integer.parseInt(e.getAttribute(this.NUM_P))];
			
				PO.p("loaded pafar " + this.pafar.length);
				
				NodeList nl = e.getElementsByTagName(LP);
				
				for(int i=0; i< nl.getLength(); i++) {
					Element el = (Element)nl.item(i);
					for(int j = 0; j< pafar.length; j++) {
						if(Integer.parseInt(el.getAttribute(this.IND)) == i) {
							pafar[i] = (LPart)Domc.lo(el, e.getOwnerDocument());
						}
					}
					
				}
				
			}
			
	//		 load the tonar
			if(e.getAttribute(this.KNUM_P).length() > 0) {
				this.tonar = 
					new TonalManager [Integer.parseInt(e.getAttribute(this.KNUM_P))];
			
				PO.p("loaded tonar " + this.tonar.length);
				
				NodeList nl = e.getElementsByTagName(KTM);
				
				for(int i=0; i< nl.getLength(); i++) {
					Element el = (Element)nl.item(i);
					for(int j = 0; j< tonar.length; j++) {
						if(Integer.parseInt(el.getAttribute(this.KIND)) == i) {
							tonar[i] = (TonalManager)Domc.lo(el, e.getOwnerDocument());
						}
					}
					
				}
				
			}
			
		}

	private static String NUM_P = "num_of_steps";

	private static String LP = "an_lpart_in_pafar";

	private static String IND = "index_in_pafar";

	private static String KNUM_P = "num_of_K_steps";

	private static String KTM = "an_tonal_manager_in_tonar";

	private static String KIND = "index_in_tonar";

	private static String KEMO = "kemosea";

	public void dsave(Element e) {
		super.dsave(e);
		e.appendChild(Domc.sa(trach, "trach", e.getOwnerDocument()));
		
		e.appendChild(Domc.sa(lfrom, "lfrom", e.getOwnerDocument()));
		e.appendChild(Domc.sa(lto, "lto", e.getOwnerDocument()));
		
		
		if (pafar != null && pafar.length != 0) {
			e.setAttribute(this.NUM_P, String.valueOf(this.pafar.length));
			for (int i = 0; i < this.pafar.length; i++) {
				Element pa = Domc.sa(pafar[i], LP, e);
				pa.setAttribute(IND, String.valueOf(i));
				e.appendChild(pa);
			}
		}
		
		
		e.appendChild(Domc.sa(kemosea, KEMO, e.getOwnerDocument()));
		
		
		if (tonar != null && tonar.length != 0) {
			e.setAttribute(this.KNUM_P, String.valueOf(this.tonar.length));
			for (int i = 0; i < this.tonar.length; i++) {
				Element ta = Domc.sa(tonar[i], KTM, e);
				ta.setAttribute(KIND, String.valueOf(i));
				e.appendChild(ta);
			}
		}
		
		
		
		
			
	}

}


