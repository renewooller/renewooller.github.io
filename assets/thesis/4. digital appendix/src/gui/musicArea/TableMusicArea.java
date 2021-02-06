/*
 * Created on May 24, 2006
 *
 * @author Rene Wooller
 */
package gui.musicArea;

import java.awt.Component;
import java.awt.Graphics;

import music.MultiMorph;
import music.MultiTableMorph;
import music.MusicGenerator;
import music.PartMorph;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.io.Domc;
import ren.tuio.TuioListener;
import ren.util.PO;


public class TableMusicArea extends CameraMusicArea implements TuioListener, SepamoHolder {
	private static final int Fhivar = 0;

	// the multi-morph-music-generator that will create all the music
	private MultiTableMorph tabmo = (MultiTableMorph)(new MultiTableMorph()).construct(this);
	
	// holds the normal MusicArea components for use in the normal mode
	private Component [] mastore;
	
	// holds the table components
	private Component [] tastore;
	
	// store the music generator before the mode flipped
	private MusicGenerator premugen;
	
	private boolean tableMode = false;
	
	// SElectable PArt MOrphs
	private Sepamo [] sepamarr = new Sepamo [20];
	private int sepanum = 0;
	
	public static int DF_hivar = Integer.MIN_VALUE; ; 
	// keeps track of the highest variation that has been loaded so far
	private int hivar = DF_hivar;
	
	// the default number of variations
	public static int DF_vnum = 10; 
	
	
	//for saving and loading only
	MultiMorph [] mulmoSav = new MultiMorph [10];
	
	public TableMusicArea() {
		super();
		
		super.tc.addTuioListener(this);
		
		//tabmo.initScores();
	}
	
	public void enableTableMode(boolean s) {
		if(s == tableMode)
			return;
		
		tableMode = s;
		
		if(tableMode) {
			mastore = super.getComponents();
			
			super.removeAll();
			
			// add the table components that were positioned previously
			if(tastore != null) 
				for(int i=0; i<tastore.length; i++) {add(tastore[i]);}
			
			// stor the music generator
			this.premugen = super.lplay.getCurrentMusicGenerator();
			
			// set the table morph music generator to be the music generator
			super.lplay.setCurrentMusicGenerator(tabmo);
			
			tc.connect();
			
		} else {
			tastore = super.getComponents();
			
			super.removeAll();
			
			// add the music area components that were positioned previously
			if(mastore != null) 
				for(int i=0; i<mastore.length; i++) {add(mastore[i]);}
			
			//put the previous music generator back
		//	if(this.premugen!= null) 
				super.lplay.setCurrentMusicGenerator(premugen);
						
			if(!super.isCameraIn()) {
				tc.disconnect();
			}
			
		}
		this.repaint();
		
	}
	
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		if(this.tableMode)
			g.drawRect(PartMorphComponent.wic, 
				PartMorphComponent.wic, 
				PartMorphComponent.mx-PartMorphComponent.wic, 
				         PartMorphComponent.my-
				         PartMorphComponent.wic);
	}
	
	public boolean isTableMode() {
		return this.tableMode;
	}
	
	/**
	 * loads the part morphs within the specified multimorph as variations
	 * number v in each Sepamo
	 * @param tl
	 * @param v
	 */
	public void loadMorphVariations(MultiMorph mm, int v) {
		PO.p("mm size = " + mm.getMCSize() + " v = " + v);
	//	mulmoSav[v] = tl;
		loadMorphVariations(mm.getPMarray(), mm.getMCSize(), v);
		
		PO.p("hivar " + hivar);
	}
	
	public void loadMorphVariations(PartMorph [] pmatl, int len, int var) {
		
		PO.p("loading mv 2");
		updateSepamoLength(len);
		
		// load the variation in for each morph channel, and set it as well
		for(int i=0; i< len; i++) {
			if(i == len-1) {
				PO.p("last part morph " + pmatl[i].toString());
			}
			
			sepamarr[i].loadVariation(pmatl[i], var);
			sepamarr[i].setVariation(var, i);
			sepamarr[i].getCurrentVariation().setMute(true);
		}
				
		if(var > hivar) {
			hivar = var;
		}
		
	}
	
	public void updateSepamoLength(int nl) {
		//	 make sure sepamo has the right length
		while(nl > this.sepanum) {
			PartMorphComponent pmc = new PartMorphComponent();
			
			this.sepamarr[sepanum] = (new Sepamo()).construct(tabmo, 
									 sepanum, pmc, this.lplay);
			pmc.construct(sepamarr[sepanum++]);
			
			PO.p("adding a pmc for th " + sepanum + " th sepanum");
			this.add(pmc);
			
		}
		
	}
	

	public void addTuioObj(long session_id, int id) {
		
		int layer = (int)((id*1.0)/(DF_vnum*1.0) + 0.5);
		//this.tabmo.setMuteLayer(layer, true);
	//	if(layer < this.sepanum)
	//		this.sepamarr[layer].setVisible(true);
	//	PO.p("object " + id + " added. layer = " + layer + " sepanum " + sepanum);
		if(sepamarr[layer] == null) {
	//		PO.p("not added");
			return;
		}
		
		this.sepamarr[layer].getCurrentVariation().muteNextBar(false);
	}

	
	public void updateTuioObj(long session_id, int id, float x, float y, float a, 
			float X, float Y, float A, float m, float r) {
		
		//PO.p("components in area = " + this.getComponentCount());
	//	if(x<200)
	//		PO.p(".. id = " + id + " x = " + x + " y = " + y);
		//int vari = id%this.DF_vnum;
		int layer = (int)((id*1.0)/(DF_vnum*1.0) + 0.5);
		int vari = id - layer*DF_vnum;
		
		//PO.p("id = " + id + " sid = " + sid + " cv = " + cv);
		//PO.p(" sp l = " + sepamarr.length);
		// check for errors
		if(vari < 0) {
			PO.p("vari smaller than 0. id = " + id + "  vari = " + vari);
		} else if(vari > hivar && hivar != this.DF_hivar) {
			PO.p("vari bigger than hivar. vari = " + vari + " hivar = " + hivar + " id = " + id);
		} else if(sepamarr[layer] == null) {
			PO.p("sepamar [" + layer + "] is null.  id = " + id);
		} else { //othervise, set the variation and update it
			sepamarr[layer].setVariation(vari, layer);
	//		PO.p("updating...");
			sepamarr[layer].update(x, y);
			
			if(this.sepamarr[layer].getCurrentVariation().isMuteNextBar()) {
				this.sepamarr[layer].getCurrentVariation().muteNextBar(false);
			}
		}
		
	}

	public void removeTuioObj(long session_id, int id) {
		int layer = (int)((id*1.0)/(DF_vnum*1.0) + 0.5);
	//	this.tabmo.setMuteLayer(layer, false);
	//	this.sepamarr[layer].setVisible(false);
	//	PO.p("object " + id + " removed. layer = " + layer);
		if(sepamarr[layer] == null) {
	//		PO.p("not removed");
			return;
		}
		
		this.sepamarr[layer].getCurrentVariation().muteNextBar(true);
		
		/*
		Thread rem = new Thread() {
			public Sepamo sp;
			public void run() {
				
			}
			public void setSp(Sepamo sp) {
				this.sp = sp;
			}
		};
		*/
		//rem.setSp(this.sepamarr[layer]
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public void dsave(Element e) {
		super.dsave(e);
	//	e.appendChild(Domc.sa(tabmo, "tabmo", e));
		
	//	e.setAttribute("tableMode", String.valueOf(tableMode));
		// save number of sepamos
		e.setAttribute("sepanum", String.valueOf(sepanum));
			
		// go through them all
		for(int i=0; i< sepanum; i++) {
			// if it is there, save it
			if(sepamarr[i] != null) {
				e.appendChild(Domc.sa(sepamarr[i], "sepamarr", e));
			}
		}
		
	}
	
	public void dload(Element e) {
		super.dload(e);
		if(!e.hasAttribute("sepanum"))
			return;
		
		
		// clear all the current sepamos, just in case there are left overs
		for(int i=0; i< sepamarr.length; i++) {
			sepamarr[i] = null;
		}
		
		// remove all the gui components too
	//	this.removeAll(); // no need to do this, it is already done
		
		// reset the counter
		sepanum = 0;
		
		NodeList sepnol = e.getElementsByTagName("sepamarr");
		
		// if we aren't in table mode, we'll need space to store the components
		if(!tableMode) {
			tastore = new PartMorphComponent [sepnol.getLength()];
		}
		
		for(int i=0; i< sepnol.getLength(); i++) {
			// get a sepamar from saved. it will have the part morph info
			Sepamo s = (Sepamo)(Domc.lo(((Element)(sepnol.item(i))),
										Sepamo.class, e));
			
			// construct it
			PartMorphComponent pmc = new PartMorphComponent();
			pmc.construct(s);
			if(this.tableMode) {
				// if it is the table mode, just add the component
				this.add(pmc);
			} { // if it isn't, make sure the component will appear when it is
				PO.p(" adding to tastore " + i);
				tastore[i] = pmc;
			}
			
			s.construct(tabmo, s.getSID(), pmc, this.lplay);
			
			if(this.hivar < s.getVarNum()) {
				this.hivar = s.getVarNum();
			}
			
			// put it in at the right spot
			this.sepamarr[s.getSID()] = s;
			
			
			//initialise the variation
			//this.sepamarr[s.getSID()].setVariation(s.getCV(), s.getSID());
			this.tabmo.setPartMorph(s.getCurrentVariation(), s.getSID());
			
			// mute on loading up
			s.getCurrentVariation().setMute(true);
			
		//	this.tabmo.setPartMorph(morph, i)
			
			// increment the record of the number of sepanums
			sepanum++;
		}
		
		// if the are not the same length, something is wrong
		
		if(sepanum != Integer.parseInt(e.getAttribute("sepanum"))) {
			try {
				Exception exx = new Exception("problem with loading separate part morph data : " +e.toString());
				exx.fillInStackTrace();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		PO.p("loaded sepamos " + sepanum);
		for(int i=0; i< sepanum; i++) {
			if(sepamarr[i] == null)
				PO.p("sepamo [" + i + "] = null");
			else {
				PO.p("sepamo [" + i + "] = not null. cvar = " + this.sepamarr[i].getCV());
				PO.p(" mute? " + this.sepamarr[i].getCurrentVariation().getMute());
			}
			
		}
		
		
		//construct it.
		//add it to the sepamarr at the appropriate spot
		
		
		
		
		/*  // this make it out of sync with the menu checkbox
		if(e.hasAttribute("tableMode")) {
			this.enableTableMode(
					Boolean.valueOf(e.getAttribute("tableMode")).
					booleanValue());
			//super;
		}*/ 
		
		// not needed - it's just a player
	//	Element ta = (Element)e.getElementsByTagName("tabmo").item(0);
		//if(ta != null)
		//	this.tabmo = (MultiTableMorph)Domc.lo(ta, MultiTableMorph.class, e);
		
		
	}
	
	public void removeMusicGeneratorComponents() {
		super.removeMusicGeneratorComponents();
		this.clear();
	}
	
	public void clear() {
		for(int i=0; i<sepanum; i++) {
			sepamarr[i] = null;
		}
		
		this.sepanum = 0;
		
		
	}

	public Sepamo getSepamo(int i) {
		return this.sepamarr[i];
	}

	public int getSepNum() {
		
		return this.sepanum;
	
	}
	
}