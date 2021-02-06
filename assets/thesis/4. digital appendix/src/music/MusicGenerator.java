package music;


import java.util.Enumeration;
import java.util.Vector;

import jm.music.data.Score;
import jmms.TickEvent;
import jmms.TickListener;
import ren.io.Domable;
import ren.util.PO;

/**
 * MusicGenerator is designed to be extended, so that different approaches to <br>
 * Music generation can be easily defined and played using
 *  {@link lplay.LPlayer LPlayer} <br>
 * 
 * @author wooller
 *
 * 22/02/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public abstract class MusicGenerator implements Domable {

	/**
	 * cue listener is used for when external objects need to be triggered in 
	 * some way by the music generator.
	 */
	protected transient CueListener cueListener;
	
	/**
	 * lscore holds that musical data that is to be played.  To created dynamic
	 * music, you will need to update lscore constantly.
	 */
	protected LScore lscore = new LScore();
	
	/** a flag that represents wether this musicGenerator is playing or not */
	private boolean playing = false;
	
	/** 
	 * for an external object that need to update on every tick, it is <br>
	 * referenced as a tick listener. It is transient to avoid serializing the<br>
	 * View during serialization of this Model <br>
	 */
	protected transient TickListener tickListener;
	
	/**
	 * dmgs (Dependant Music Generators) is used in updating music generators <br>
	 * and ther graphical components that depend on this one <br>
	 * @see #addDependant(MusicGenerator)
	 * @see #removeDependant(MusicGenerator)
	 */
	private transient Vector dmgs;
	
	/** Create a new MusicGenerator
	 * Initalises lscore, the internal representation of musical data played <br>
	 * by the music generator <br>
	 */
	public MusicGenerator() {
		
	}
	
	public MusicGenerator construct() {
	    if(lscore == null)
	        lscore = new LScore();
	    if(dmgs == null)
	        dmgs = new Vector(10);
	    return this;
	}
	
	/** tickScore is called frequently (every tick) by the player.  It updates <br>
	 * the dependant tickListener and returns the score to be played for the <br>
	 * current tick <br>
	 * 
	 * @param e tick event that contains the current beat that the player is up to.
	 * @return a resolution length score that is extracted from lscore, the <br>
	 * internal representation of musical data played by the music generator <br>
	 */
	public Score tickScore(TickEvent e) {
        if(tickListener != null)
            tickListener.tick(e);
        
		return generateTickScore(e);
	}

    /**
     * this will need to overridden to be getJMScore if doing it in realtime
     * @param e
     * @return
     */
	public Score generateTickScore(TickEvent e) {
      //  PO.p("haven't overridden old generateTickScore");
	    return lscore.getTickScore(e);
	}
	
	/**
	 * change the status of wether this musicGenerator is playing or not <br>
	 * @param playing the new status - false is not playing, true is playing
	 */
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	/**
	 * @return boolean true if this musicGenerator is playing, false if it isn't 
	 */
	public boolean isPlaying() {
		return playing;
	}
		
	/**
	 * the select method should be triggered by any Views that hold the <br>
	 * MusicGenerator Model.  
	 *
	 */
	public abstract void select();
	
	/**
	 * @return the current lscore, the internal representation of musical data
	 */
	public LScore getLScore() {
		return lscore;
	}
	
	/**
	 * triggers the cuelistener
	 * @see #cueListener
	 */
	public void fireCues() {
		if(cueListener == null) 
			return;
		CueEvent c = new CueEvent();
		c.setMusicGenerator(this);
   //     PO.p("executing the cue listener");
		cueListener.execute(c);
	}
	
	/**
	 * @return the current cueListener
	 * @see #cueListener
	 */
	public CueListener getCueListener() {
		return cueListener;
	}
	
	/**
	 * @param cueListener the new cueListener
	 * @see #cueListener
	 */
	public void setCueListener(CueListener cueListener) {
		this.cueListener = cueListener;
	}
	
	/**
	 * Get the TickListener.
	 * @return the TickListener value.
	 */
	public TickListener getTickListener() {
		return tickListener;
	}

	/**
	 * Set the TickListener.
	 * @param newTickListener
	 *            The new TickListener.
	 */
	public void setTickListener(TickListener newTickListener) {
		this.tickListener = newTickListener;
	}

	/**
	 * @param dmg adds a dependant music generator to this musical generator so<br>
	 * it is updated when relevant changes occur. <br>
	 * For example, a patternMusicGenerator will have whichever morph music <br>
	 * generators are linked to it as dependants <br>
	 * @see #updateTo(MusicGenerator)
	 * @see #removeDependant(MusicGenerator)
	 * @see #dmgs
	 */
	public void addDependant(MusicGenerator dmg) {
		dmgs.add(dmg);
	}
	
	/**
	 * @param dmg remove a dependant music generator from this music generator's
	 * set of dependant music generators <br>
	 * @see #updateTo(MusicGenerator)
	 * @see #addDependant(MusicGenerator)
	 * @see #dmgs
	 */
	public void removeDependant(MusicGenerator dmg) {
		dmgs.remove(dmg);
	}
	
	/**
	 * updates the musical generators that are dependant on this one. <br>
	 * @see #updateTo(MusicGenerator)
	 */
	public void updateDependants() {
		Enumeration enumr = dmgs.elements();
		while(enumr.hasMoreElements()) {
			((MusicGenerator)enumr.nextElement()).updateTo(this);
		}
	}
	
	/**
	 * to be overwritten for music generators that need to be updated when <br>
	 * other music generators change.
	 * @param update the musicGenerator that changed
	 * @see #updateDependants()
	 */
	public void updateTo(MusicGenerator update) {
	}
	
}