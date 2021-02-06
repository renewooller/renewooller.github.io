/*
 * Created on Jun 1, 2006
 *
 * @author Rene Wooller
 */
package music;

import gui.musicArea.MorphComponent;
import gui.musicArea.MusicArea;
import jmms.TickEvent;
import lplay.LPlayer;
import ren.util.PO;

public class MorphFlipper {

	private int at = -1; // the current morph music ganerator we are at
	private boolean enab = false; // wether morph flipping is enabled
	private MultiMorph [] mumos; // holds the multi morph music generators 
	// in order of height on screen
	private double losc; // the longest scope in all the partMorphs of the 
	// current multiMorph
	
	// may need a parameterMap for flipscope, but it may be more
	// convincing to for it to be automatically the scope of the morph
	
	private LPlayer lp; // on a flip, the lplayer sets the next mmg playing
	private MusicArea ma; // used to get the music generators
	
	public MorphFlipper() {
		super();
	}
	
	public MorphFlipper construct(LPlayer lp, MusicArea ma) {
		this.lp = lp;
		this.ma = ma;
		return this;
	}

	/**
	 * slow sort through the  morph components in the music area and orders them
	 * into mumos according to the height on screen.  eg highest (y = 0) is 
	 * index 0 and lowest (y = screenHeight) is index length-1 
	 *
	 */
	public void initSort() {
		MorphComponent [] mugis = ma.getMultiMorphComps();
		// sort them according to the highest
		
		mumos = new MultiMorph [mugis.length];
		PO.p("mf. initSort length = " + mumos.length);
		
		for(int i=0; i< mumos.length; i++) {
			// fin the smallest in what is left
			int [] lind = {Integer.MAX_VALUE, -1}; // lowest value and index of
			for(int j=0; j<mugis.length; j++) {
				if(mugis[j] != null) { // if it hasn't been removed
					// if it is smaller
					if(mugis[j].getY() < lind[0]) {
						// set it
						lind[0] = mugis[j].getY();
						
						// set index
						lind[1] = j;
					}
				}
			}
			PO.p("lind " + i + " = " + lind[0]);
			// set this index in the mumos to be the lowest found this round
			mumos[i] = (MultiMorph)mugis[lind[1]].getMMG();
			// set the mugi to null so that it won't be considered for next round
			mugis[lind[1]] = null;
			
			// set the flipper to be this while we are at it
			mumos[i].setFlipper(this);
		}
		
		
		
	}
	
	
	/**
	 * changes lplayer currentMusicGenerator and this.at
	 * @param m
	 */
	public void flip() {
		
		// find the next at that is flippable (as in, able to be flipped to)
		int pat = at; // store previous at value
		at = (at+1)%mumos.length; // increment
		while(!mumos[at].isFlip()) { // if it isn't flippable increment more
			at = (at+1)%mumos.length;
			
			if( at == pat) // if none of them are flippable, stay at the current
				break;
		}
		
		// flip the current music generator to be the newly found next-in-line
		initForPlay(pat, at);
		this.lp.setCurrentMusicGenerator(mumos[at]);
		
	}
	
	private void initForPlay(int f, int t) {
		//mumos[at].initParts(); // might need to optimise this better
		
		// make the morph index the same
		mumos[t].getMorphTracker().setPos(mumos[f].getMorphTracker().getPos());
		
		// make the direction the same
		mumos[t].getMorphTracker().setBackwards(
				mumos[f].getMorphTracker().isBackwards());
		
		// update the record of the longest scope of current player
		this.losc = mumos[t].getLongestScope();
	}
	
	/**
	 *  check to see if this is the time at which to flip it.
	 *  if it is, flip
	 * @param m
	 * @param e
	 * @return
	 */
	public void checkToFlip(MultiMorph m, TickEvent e, double stofset) {
		if(!enab) // if it isn't enabled, just return
			return;
		
		// make sure the morpher that is calling this check is the current one
		if(mumos[at] != m) {
			for(int i=0; i< mumos.length; i++) {
				if(mumos[i] == m) {
					at = i;
					initForPlay(at, at);
					break;
				}
			}
		}
		
		
		// if the next tick is going to fall on the start of the longest scope
		if((e.at()-stofset+e.getRes())%losc == 0) {
			PO.p("checktoflip.  losc = " + losc + " e.at() = " + e.at());
			// flip it
			flip();
		}
	}
	
	/**
	 * enables the morph flipping functionality and  initialises the sorted
	 * mumo contaner [] if enableing
	 * 
	 * @param nen
	 */
	public void setEnabled(boolean nen) {
		if(nen) {
			initSort();
		}
		this.enab = nen;
		this.at = 0;
		initForPlay(0, 0); // initialises the longest scope for flipping play
		// but it is up to the user to clip on a morpher to actually start the
		// cycle of flipping
	}
	
}
