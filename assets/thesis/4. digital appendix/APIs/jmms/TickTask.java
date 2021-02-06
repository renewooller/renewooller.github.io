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

package jmms;

import grame.midishare.Midi;
import grame.midishare.MidiAppl;
import grame.midishare.MidiTask;
import jm.music.data.Tempo;
import ren.util.PO;

/**
 * TickTask is an extension of the MidiTask and is used to play the events from<br>
 * a Sequencer
 * 
 * @author wooller
 *
 *22/02/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class TickTask extends MidiTask {

	/**
	 * the player that generated this tickTask
	 */
	private MPlayer mp;

	/**
	 * the play task that is used to play sequences
	 */
	private PlayTask pt;

	/**
	 * the tempo, which controls the speed
	 */
	private Tempo tempo;

	/**
	 * a flag used in stopping
	 */
	private boolean stop = false;

	/**
	 * the resolution of the cycle.  This changes depending on the tempo
	 * @see #updateRes()
	 */
	private  double res = 0.25;

	//used for sending MIDI clock data - has not been re-implemented to suit
	// the new system of dynamic resolution
    private PlayTask timingTask;

    private int clocksPerRes;
    private double bt24th = 1.0/24.0;

    private int umrcount;

	/**
	 * seq is the sequence that is to be played
	 */
	private Sequence seq;

	/**
	 * the smallest possible resolution, in beats
	 */
	public static double SRES = 0.0625;

	/**
	 * an integer representation of where the tick is up to, ie it keeps track<br>
	 * of the beat, by counting how many SRES length periods have elapsed. <br>
	 * This technique is used to avoid double accuracy error
	 * @see #Execute(MidiAppl, int)
	 * @see #getTick()
	 */
	private transient int resCount = 0;

	/**
	 * this is used to record the timing offset that occurs when dynamically <br>
	 * changing the resolution <br>
	 */
	private transient double offset = 0;

	/**
	 * records a change in resolution.
	 * @see #setResolution(double)
	 */
	private transient int setResOn = -1; 

	// a space for temporarily holding tempo
	private transient double ttempo;
	
	/**
	 * creates a new tick task
	 * @param mp the player
	 */
	public TickTask(MPlayer mp) {
		this.mp = mp;
		this.tempo = mp.getTempo();
	}

	
	/**
	 * stops the tick task from playing
	 */
	public void stop() {
		this.stop = true;
	}

	/**
	 * starts the tick task
	 * @param appl the Midishare Midiappl, such as {@link jmms.Sequencer Sequencer}
	 * @param date the time at which it stated
	 */
	public void start(MidiAppl appl, int date) {
        this.stop = false;
        
        
		Execute(appl, date);
	}

	/**
	 * @return the current resolution, in beats
	 */
	public double getRes() {
		return res;
	}

	/**
	 * Overwritten from Midishare <br>
	 * call this to execute the current Midishare event <br>
	 * 
	 * @param MidiAppl
	 *            appl the MidiAppl that is to be used to send the event
	 * @param int
	 *            date the actual date that it should be executed
	 */
	
	public void Execute(MidiAppl appl, int date) {
		// temporary tempo
		ttempo = tempo.getPerMinute();
		
		//send MIDI Clock messages
		//trigger make changes in tickscore
		if (!stop) {
			/*
			 * There sould be 24 clocks to every beat
			 */
		    // number of 24th ticks in the resolution
		    clocksPerRes = (int)(this.getRes()/bt24th); 
		    for (int i = 0; i < clocksPerRes; i++) {
		        this.timingTask = new PlayTask(Midi.NewEv(Midi.typeClock));
		        appl.ScheduleTask(timingTask, date + inMillis(i * bt24th, ttempo)); 
		    }
						 
		} else {
			if (seq != null)
				seq.reset();

			return;
		}
		
		updateRes(); //res is set, if synchronised

		seq = mp.nextSequence(); // seq is of res length
		
		// the first event of the sequence if obtained, which links to the other
		// events in the sequence
		int firstEv = 0;
		firstEv = seq.nextEvent();
		
		// if seq not empty, set it playing
		if (firstEv != 0) { 
			pt = new PlayTask(firstEv);
			PlayTask.setAdvance(mp.getLatency());
			pt.setSequence(seq);	
            
          //  PO.p("firstEv date = " + Midi.GetDate(firstEv));
          
			appl.ScheduleTask(pt, date + Midi.GetDate(firstEv));		
		}

		// if the player is not stopping, reschedule this tickTask
		if (!stop) {
		    offset = 0;
		 //   System.out.println("offset = " + offset);
			appl.ScheduleTask(this, date + inMillis(res+offset, ttempo));
		}
		
		//update rescount which is an integer representation of where the tick
		// is up to
		resCount += (int) (this.res / this.SRES);
		if(resCount == Integer.MAX_VALUE) {
			resCount = resCount%Integer.MAX_VALUE;
		}
	}

	/**
	 * @return return the current tick ie the amount of time that has elapsed<br>
	 * in beats <br>
	 */
	public double getTick() {
		return resCount * this.SRES;
	}
	
    public void resetTick() {
        this.resCount = 0;
    }
    
	/**
	 * updates the resolution to be optimal given the current tempo. <br>
	 */
	public void updateRes() {
		//offset is reset to 0
		if(offset != 0) {
			offset = 0;
		}
		// determine the best resolution
		ttempo = tempo.getPerMinute();
		if (ttempo < 50) {
			this.setResolution(this.SRES);
		} else if (ttempo < 110) {
			this.setResolution(0.125);
		} else if (ttempo < 230) {
			this.setResolution(0.25);
		} else if (ttempo < 470) {
			this.setResolution(0.5);
		}
		
	}

	/** sets the resolution dynamically, taking into account the differences<br>
	 * in them that will cause a glitch in timing <br>
	 * 
	 *  note: offset is being set back to zero (above) because this problem somehow dissappeared
	 * 
	 * @param newRes the new resolution to be set
	 */
	private void setResolution(double newRes) {
	   // Sytem.out.println("setResOn" )
	    
		// if a change has been initiated
		if (setResOn > -1) { 
			if (resCount % setResOn == 0) { // change if synchronised
				offset = offset +((int)(res/SRES) - (int)(newRes/SRES))*SRES;
				res = newRes;
				setResOn = -1;
			}
			return;
		} 

		// if there is no change at all, return
		if (newRes == res) {
			return;
		}
		
		//otherwise initiate change
		setResOn = (int) (Math.max(newRes, res) / this.SRES);
	}

	/**
	 * converts from beats into milliseconds
	 * 
	 * @param toConvert the value in beats to convert
	 * @param t the tempo
	 * @return the number of milliseconds represented by the beat value, given<br>
	 * the tempo.<br>
	 */
	private int inMillis(double toConvert, double t) {
		//System.out.println("tempo " + t);
		double toRet = (toConvert * 60000 / t);//tempo.getPerMinute()));
		return (int) (toRet + 0.5); // round this mutha off!
	}

	/**
	 * @param t the new tempo
	 */
	public void setTempo(Tempo t) {
		this.tempo = t;
	}


    public void userModTick(double d) {
        
        this.resCount = (int) (umrcount + (d/this.SRES));
    }


    public void userModTick(boolean b) {
        if(b)
            umrcount = resCount;
        else
            umrcount = -1;
    }


    public void setTick(double d) {
        this.resCount = (int)(d/this.SRES);
    }

}