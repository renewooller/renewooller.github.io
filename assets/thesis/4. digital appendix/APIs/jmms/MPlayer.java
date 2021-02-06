/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

/*
 * MPlayer.java
 *
 * @author Rene Wooller
 *
 * Created on 22 March 2004, 15:33
 */

package jmms;
import grame.midishare.Midi;

import java.util.Iterator;
import java.util.Vector;

import jm.music.data.Score;
import jm.music.data.Tempo;
import ren.util.PO;

/**
 *	This class is designed to be overwritten, so that a stream of music to be <br>
 *  played can be defined by the code that extends it. <br>
 *  <br>
 *  When overwriting it, you should: <br>
 * 		A. instantiate tempo in the overwritten constructor <br>
 *      B. overwrite nextScore to define the music that will be sent every tick<br>
 * 		C. call {@link #close() close()} before the program exits <br>
 *      D. call {@link #go() go()} and {@link #stop() stop()} to control the
 *      playing <br> of the music. <br>
 *  <br>
 *  It is also important to note that the resolution will automatically change<br>
 *  to be optimal for any given tempo, and so aspects of your program that <br>
 *  use resolution should take this into account. see the
 * {@link jmms.TickTask#updateRes() updateRes} method <br>
 *
 * @author  Rene Wooller
 */
public abstract class MPlayer {
    
	//keeps track of tempo
    protected Tempo tempo;
    
    // deals with playing and recieving MIDI data from Midishare
    protected Sequencer seq = new Sequencer();
    
    // this task is iteratively reschedules itself for execution, one 
    // resolution step ahead, while drawing the data to be played from 
    // MPlayer.nextSequence() which call MPlayer.nextScore()
    protected TickTask tickTask = new TickTask(this); 
    
    protected boolean playing = false;
    
    protected Vector startListeners = new Vector();
    
    
    
    
    /** 
     * Creates a new instance of MPlay <br>
     * Sets the tempo of the Sequencer to be the same as this one.
     * 
     * */
    public MPlayer() {
        seq.setTempo(tempo);
    }
  
    /**
     * This method is called by tickTask every tick, returning the sequence to <br>
     * be played in that tick. <br>
     * 
     * @return Sequence the sequence to be played on this tick
     */
    public Sequence nextSequence() {
        return seq.getMSSeq(this.nextScore());
    }
    
    /**
     * define the music to play by overwriting the nextScore method
     */
    public abstract Score nextScore();
        
    /**
     *  go is used to start the sequencer playing
     */
    public void go() {
//     	send a start message to midi devices
        PlayTask startTask = new PlayTask(Midi.NewEv(Midi.typeStart));
        seq.ScheduleTask(startTask, Midi.GetTime());   
	        
    	// if it is already playing, then don't start another one
    	if(playing)
    		return;

        //start it running
      //  PO.p("starting");
    	seq.setTickTask(tickTask);
        tickTask.start(seq, Midi.GetTime());
        
        
        this.playing = true;
        
        Iterator iter = startListeners.iterator();
        while(iter.hasNext()) {
            ((StartListener)iter.next()).started();
        }
    }

    /**
     * Stops playing, and clears stuck notes
     *
     */
    public void stop() {
    	//stop the tasks
    	tickTask.stop();
    	
    	PlayTask stopTask = new PlayTask(Midi.NewEv(Midi.typeStop));
        seq.ScheduleTask(stopTask, Midi.GetTime());   
	     
    	
    	// clear stuck notes
    	seq.panic();
    	
    	// switch flag
    	this.playing = false;
    }
    
    public void addStartListener(StartListener sl) {
        this.startListeners.add(sl);
    }
    
    
    /**
     * check to see if the player is playing
     */
    public boolean isPlaying() {
    	return this.playing;
    }
    
    /**
     * this should be called when the program is shutting down so that Midishare
     * Exits appropriately
     */
    public void close() {
        seq.close();
    }
    
    /**
     * sets the tempo that is to be referenced when obtaining a tempo value. <br>
     * It links the tempo object to sequencer and tickTask also. <br>
     * 
     * @param tempo the new tempo object
     */
    public void setTempo(Tempo tempo) {
        this.tempo = tempo;
        this.seq.setTempo(tempo);
        if(this.tickTask != null)
        	this.tickTask.setTempo(tempo);
    }
    
    /**
     * @return Tempo the tempo object currently being referenced
     */
    public Tempo getTempo() {
        return this.tempo;
    }
    
    int lms = 100;
    public int getLatency() {
        return lms;
    }
    
    public void setLatency(int nl) {
        System.out.println(nl);
        if(nl < 0)
            nl = 0;
        
        this.lms = nl;
    }
    
    public Sequencer getSequencer() {
        return this.seq;
    }
    
}
