/*
 * Created on May 24, 2006
 *
 * @author Rene Wooller
 */
package gui.musicArea;

import jmms.Sequencer;
import lplay.LPlayer;
import music.LPart;
import music.MultiTableMorph;
import music.PartMorph;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.io.Domable;
import ren.io.Domc;
import ren.util.RMath;

public class Sepamo implements Domable {

	// different variations
	private PartMorph [] vars = new PartMorph [TableMusicArea.DF_vnum];
	private int cvar = -1; // current variation
	
	// position in the sepanum array
	private int sid = -1;
	
	// the music generator that will generate the music to be played
	private MultiTableMorph mumo;
	
	// the lplayer
	private LPlayer lp;
	
	// position of the component;
	private float x, y;
	private PartMorphComponent pmc; // the component
	
	// the current value of the controller message that will be sent
	//private int ctr = 0;
	
	public Sepamo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Sepamo construct(MultiTableMorph mumo, int id, PartMorphComponent pmc, LPlayer lp) {
		this.mumo = mumo;
		this.sid = id;
		this.lp = lp;
		this.pmc = pmc;
		return this;
	}
	
	/**
	 * only updates the part morph if it is a different variation to the current
	 * @param nv
	 * @param in - the morph channel
	 */
	public void setVariation(int nv, int in) {
	//	PO.p("sepamo " + )
		if(this.cvar != nv && vars[nv] != null) {
		 mumo.setPartMorph(vars[nv], in);
		 this.cvar = nv;
		}
	}
	
	public void loadVariation(PartMorph partMorph, int v) {
		vars[v] = partMorph;
	}
	
	public void update(float x, float y) {
		if(cvar == -1)
			return;
		
		this.x = x;
		this.y = y;
		//PO.p("sid = " + this.sid + " v = " + this.cvar + " x: " + x+ " y: " + y);
		
		//make it snap to the edge by a small factor
		double mi = RMath.snapToEdgesHard(x, 0.1);
	//	PO.p("x = " + x + " mi = " + mi);
		
		// send the MIDI controller data in realtime
		int ctr = (int)(RMath.snapToEdgesHard(y, 0.1)*127.0);
		if(vars[cvar].getFromTo()[0].getPart().getChannel() ==
			vars[cvar].getFromTo()[1].getPart().getChannel()) {
			
			sendCtrls(vars[cvar].getFromTo()[0], ctr);
			
		} else {
			sendCtrls(vars[cvar].getFromTo()[0], ctr);
			sendCtrls(vars[cvar].getFromTo()[1], ctr);
		}
		//	PO.p("y = " + y + " ctr = " + ctr);
		
		vars[cvar].setMI(mi);
		
		if(pmc !=  null) 
			pmc.update();
		
	}
	
	private void sendCtrls(LPart tos, int ctr) {
		Sequencer.sendControllerData(tos.getPart().getChannel()-1, 
				tos.getMIDIControlTypes()[0], ctr, 0);
	}
	
	public double x() {
		return x;
	}
	public double y() {
		return y;
	}

	public String getIDString() {
		return "C" + this.sid + " v" + this.cvar;
	}

	
	public void dload(Element e) {
		sid = Integer.parseInt(e.getAttribute(SID));
		if(e.hasAttribute(CVAR))
			cvar = Integer.parseInt(e.getAttribute(CVAR));
		else 
			cvar = 0;
		
		NodeList pamonoli= e.getElementsByTagName("partMorph");
		
		//clear the existing slots, just in case there are left overs
		for(int i=0; i< vars.length; i++) {
			vars[i] = null;
		}
		
		// fill them all with the new ones at the right places
		for(int i=0; i<pamonoli.getLength(); i++) {
			int vi = Integer.parseInt(
					((Element)(pamonoli.item(i))).getAttribute("varsIndex"));
			
			vars[vi] = (PartMorph)Domc.lo((Element)(pamonoli.item(i)), 
						PartMorph.class, e);
			
		}
		
	}
	public static String SID = "sid", CVAR = "cvar";
	
	
	public void dsave(Element e) {
		// save the position of this sepamo in the array in tableMusicArea
		e.setAttribute(SID, String.valueOf(sid));
		
		e.setAttribute(CVAR, String.valueOf(cvar));
		
		// save each of the variations that exist
		for(int i=0; i< this.vars.length; i++) {
			if(vars[i] != null) {
				Element pe = Domc.sa(vars[i], "partMorph", e);
				pe.setAttribute("varsIndex", String.valueOf(i));
				e.appendChild(pe);
			}
		}
		
		// don't worry about the other attributes, they will be put in
		// when it is constructed - they are effectively transient
		
	}

	public void setVisible(boolean b) {
		this.pmc.setVisible(b);
	}

	public int getSID() {
		return sid;
	}

	public int getVarNum() {
		int vnum = 0;
		for(int i=0; i< vars.length; i++) {
			if(vars[i] != null) {
				vnum++;
			}
		}
		return vnum;
	}

	public PartMorph getCurrentVariation() {
		if(cvar == -1)
			return null;
		
		return this.vars[this.cvar];
	}
	
	public int getCV() {
		return cvar;
	}

	


}
