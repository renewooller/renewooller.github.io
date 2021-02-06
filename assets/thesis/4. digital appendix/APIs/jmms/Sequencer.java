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

//
//  Sequencer.java
//  
//
//  Created by Rene Wooller on Tue Dec 17 2002.
//  Copyright (c) 2002 LEMu. All rights reserved.
//
package jmms;

import grame.midishare.Midi;
import grame.midishare.MidiAppl;
import grame.midishare.MidiException;

import java.util.Enumeration;
import java.util.Hashtable;

import jm.midi.MidiInputListener;
import jm.midi.event.CChange;
import jm.midi.event.Event;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.data.Tempo;
import ren.env.ValueGraphModel;
import ren.util.PO;

//import jm.midi.event.*;

//import parts.Parameter;
/**
 * this class lets you play a jmusic score using midishare
 */
public class Sequencer extends MidiAppl {

	private int nrefnum;

	private PlayTask playTask;

	private boolean tempoFromScore = false;

	public static int[] msrefnums = new int[10];

	public static int refnumCount = 0;

	private boolean bankStylePrgChg = true;

	private MidiInputListener mil;

	private Tempo tempo; // = new Tempo();
	
	private int ccrem = 0; // the remainder of the CC (so as to keep it
							  // consistent relative to time
	private static int CCRes = 125; // the millisecond resolution of CC

	//midiTempo represents the incoming MIDI clock tempo
	double midiTempo = 200.0;

	private TickTask tickTask;
	
	private int ccseq1, ccseq2 = Integer.MIN_VALUE;
	private boolean ccsw = false;
	
	/*
	 * this opens up the midishare session and connects all the ports (it goes
	 * to port 0)
	 */
	public Sequencer() {
		super();
		
        for(int i=0;i<volumes.length; i++) {
            volumes[i] = 64;
        }
     
        //Midi.Coun
        //   super.refnum = Midi.CountAppls() + 1;
       //    int appls = Midi.CountAppls();
   		//PO.p("appls = " + appls);
   		int appls = 0;
        
		try {
			this.Open("seq " + super.refnum);
		} catch (MidiException e) {
			System.out.println(e);
		}
	
	//	appls = Midi.CountAppls()-1;
	//	PO.p("appls = " + appls);
		
		msrefnums[refnumCount++] = this.refnum;
		
		Midi.Connect(this.refnum, 0, 1);
		Midi.Connect(0, this.refnum, 1);
		PO.p("refnum = " + this.refnum);
		ccseq1 = Midi.NewSeq();
		ccseq2 = Midi.NewSeq();
		
		PO.p(" port 0 state " + Midi.GetPortState(0) + ".. port 1 state " + Midi.GetPortState(1));
	}

	
	public void ApplAlarm(int a) {
		PO.p("appl alarm " + a);
	}
	
	public void setTickTask(TickTask t) {
	    this.tickTask = t;
	}
	
	public void setTempo(Tempo newTempo) {
		this.tempo = newTempo;
	}

	public void close() {
		Midi.Close(this.refnum);
	}

	// int timeFromLast;
	long lastClock = 0;

	long thisClock = 0;

	int timeDif = 0;

	int clockAt = 0;

	private MidiInputListener[] mils = new MidiInputListener[60];
	private MidiNoteListener[] mnol = new MidiNoteListener [60];
	
	private int mnolCount = 0;
	private int milCount = 0;

	public void addMidiInputListener(MidiInputListener mil) {
		this.mils[milCount++] = mil;
	}

	public void addMidiNoteListener(MidiNoteListener m) {
	    this.mnol[mnolCount++] = m;
	}
	
	public void removeMidiInputListener(MidiInputListener mil) {
		for (int i = 0; i < milCount; i++) {
			if (mils[i] == mil) {
				mils[i] = mils[i + 1];
				mil = mils[i];
			}
		}
		milCount--;
	}

	public void removeMidiInputListener(MidiNoteListener m) {
		for (int i = 0; i < mnolCount; i++) {
			if (mnol[i] == m) {
				mnol[i] = mnol[i + 1];
				m = mnol[i];
			}
		}
		milCount--;
	}
	
	// public boolean recievingClock = false;

	public double getMidiTempo() {
		return midiTempo;
	}

	public int getMidiClockAt() {
		return clockAt;
	}

	int recieveTimeDif = 0;

	public boolean isRecievingClock() {
		recieveTimeDif = (int) (Midi.GetTime() - thisClock);
		return recieveTimeDif < 2000; // if it hasn't had any signal for two
		// seconds than it's not recieving
	}

	

	double stn = -1; //start of the note
	int veln = 0; // velocity of note being read
	
	public void ReceiveAlarm(int event) {
		Event midiEvent = null;
		// need to discuss this one to work out how events are going to be
		// passed.
		// remember also to sort out the setMidiListener(MidiListener mil)
		// method.
		switch (Midi.GetType(event)) {

		case Midi.typeCtrlChange: {
			//note controllerType = Midi.GetData0(event), controllerValue =
			// Midi.GetData1(event);
			//    mp.receiveController(Midi.GetChan(event), Midi.GetData0(event),
			// Midi.GetData1(event)));

			//	System.out.println("ctrl type = " + Midi.GetData0(event) + "
			// value = " + Midi.GetData1(event));
			midiEvent = new CChange((short) Midi.GetData0(event), (short) Midi
					.GetData1(event), (short) Midi.GetChan(event), (short) Midi
					.GetDate(event));

			break;
		}
		case Midi.typeKeyOn: {
		    if(tickTask != null) {
		        stn = tickTask.getTick();
		        veln = Midi.GetData1(event);
		    }
		   
	        break;
		}
		case Midi.typeKeyOff: {
		    if(stn != -1 && tickTask != null) { //&& stn != tickTask.getTick()) {
		        
		        pumpMidiNoteInput(stn, tickTask.getTick(), Midi.GetData0(event), veln, Midi.GetChan(event));
		        stn = -1;
		       //     Midi.GetData2(event), Midi.GetChan(event));
		       /* 
		        System.out.println("key off note channel " + Midi.GetChan(event));
		        System.out.println("0 = " + Midi.GetData0(event)); pitch
		        System.out.println("1 = " + Midi.GetData1(event)); velocity
		       */
		    }
		    break;
		}
		case Midi.typeClock: {
			//need to fix this up a lot!
			lastClock = thisClock;
			thisClock = Midi.GetTime();
			timeDif = (int) (thisClock - lastClock);
			timeDif = timeDif * 6;
			if (timeDif != 0)
				midiTempo = (15000 / timeDif);
			clockAt++;
			//	midiEvent = null;
			break;
		}
		} // end of switch block
		
		if (midiEvent != null) {
			for (int i = 0; i < milCount; i++) {
				this.mils[i].newEvent(midiEvent);
			}
		}
		Midi.FreeEv(event); // dispose the event

	//	pcount = pcount++%20;
	//	if(pcount == 0)
	//		PO.p("midi space = " + Midi.FreeSpace());
	}	
	//private int pcount = 0;
	private void pumpMidiNoteInput(double sta, double end, int pitch, int velocity, int chan) {
	    for(int i=0; i<this.mnolCount; i++) {
	        this.mnol[i].noteRecieved(new NoteEvent(pitch, this.tickTask.getRes(), (end-sta), velocity, sta, chan));
	    }
	}
	
	//panic doesn't seem to work with Reason... Does it work with other synths?
	public void panic() {
		System.out.println("panicing in sequencer");

		int ev;
		//each channel
		for (int i = 0; i < 16; i++) {
           // Midi.typeReset
			ev = Midi.NewEv(Midi.typeCtrlChange); //Midi.AllNoteOff);
			Midi.SetChan(ev, i);
			Midi.SetData0(ev, 123); // 123 should be "AllNotesOff message"
			Midi.SendIm(refnum, ev);
			
            /*
            // each pitch
			for (int j = 0; i < 128; i++) {
				// send a note off message
				int ev2 = Midi.NewEv(Midi.typeKeyOff);
				Midi.SetChan(ev2, i);
				Midi.SetData0(ev2, j);
				Midi.SendIm(refnum, ev2);
				//Midi.FreeEv(ev);
				//   System.out.println(" sending immediately ");
			}*/

            /*
			int ev3 = Midi.NewEv(Midi.typeCtrlChange); //Midi.AllNoteOff);
			Midi.SetChan(ev3, i);
			Midi.SetData0(ev3, 122); // 123 should be "AllNotesOff message"
			Midi.SendIm(refnum, ev3);
            */
		}
	}

	/**
	 * this is the method that plays the score that you give it instantaneously
	 * It ignores the tempo of the score. You need to set that using the
	 * setTempo(double tempo) method
	 * 
	 * @param Score
	 *            s the score that is to be played
	 * 
	 * public void play(int date) { playSeq(seq, date); }
	 */
	boolean useSeq1 = true;

	Sequence sequence;

	Sequence sequence1 = new Sequence();

	Sequence sequence2 = new Sequence();

	public Sequence getMSSeq(Score s) {
		if (s == null) {
			System.out.println("score is null");
			return null;
		}
		
		updateTempo(s);

		// System.out.println("score = " + s.toString());

		if (useSeq1)
			sequence = sequence1;
		else
			sequence = sequence2;

		sequence.reset();

		//if (seq != 0) Midi.FreeSeq(seq); // free the previous Midishare
		// sequence
		//seq = Midi.NewSeq(); // allocates a new one

		for(int i=0; i< s.size(); i++) {
		    partIntoSeq(s.getPart(i), sequence);
		}
		// sequence.print();
		useSeq1 = !useSeq1;
		
		if(tempo == null)
			PO.p("tempo null");
		//else if(tickTask)
		
		ccrem = (inMillis(this.tickTask.getRes(), this.tempo.getPerMinute()) + 
				ccrem)%this.CCRes;
		
	//	PO.p("ccrem = " + ccrem);
		
		return sequence;
	}

	protected void updateTempo(Score s) {
		if (tempoFromScore) {
		//	PO.p("tempo from score");
			tempo.setTempo(s.getTempo());
		} else {
		//	System.out.println("updateTempo, not from score = " +tempo.getPerMinute());
		}
		
		
		
	}

	private int[] instruments = new int[96];

	private int[] volumes = new int[96];
    
	private transient int pchan;

	//channel in part must be in between 1 and 16
	private void partIntoSeq(Part p, Sequence seq) {
		
		// if it has nothing in it, don't change the program or nothing
		
		// get the channel (-1 because Midishare counts from 0)
		pchan = p.getChannel() - 1;
		if (pchan < 0 ) {
			try {
				Exception e = new Exception(
						"part channel mus be between > 0 ");
				e.fillInStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ccenvIntoSeq(pchan, p.getCCEnvs(), seq);
		
		if(p.getSize() == 0) {
			return;
		}
		
		
		if (instruments[pchan] != p.getInstrument()) {
			//send program change only if the sequencer is operating in non
			// realTime.
			// if it is real time it is to many program changes
			//System.out.println(" sending ");
			// if(!realTime) {
			if (bankStylePrgChg) {
				sendBankPrgChng(p.getInstrument(), pchan, refnumCount - 1);

			} else {
				sendPrgChng(p.getInstrument(), pchan);
			}
			//update instrument value
			instruments[pchan] = p.getInstrument();
			// }
		}

        // stops sending volume uncecesarily
        if(volumes[pchan] != p.getVolume()) {
          //  System.out.println("sending volume " + p.getVolume());
            sendVol(pchan, p.getVolume());
            volumes[pchan] = p.getVolume();
        }
        
     //   controlChangesIntoSeq(p.getCCStates(), pchan, seq);
        
		for(int i=0; i< p.size(); i++) {
		    phraseIntoSeq(p.getPhrase(i), pchan, seq);
		}
	}

	private void ccenvIntoSeq(int chan, Hashtable envs, Sequence seq) {
		int catf = 0;
	//	PO.p("catf*this.CCRes+this.ccrem = " + catf*this.CCRes+this.ccrem);
	//	PO.p("this.tickTask.getRes()" + this.tickTask.getRes());
		while(catf*this.CCRes+this.ccrem <= inMillis(this.tickTask.getRes(), this.tempo.getPerMinute())) {
			int currDate = catf*this.CCRes+this.ccrem;
			Enumeration en = envs.keys();
			while(en.hasMoreElements()) {
				Integer ctype = (Integer)en.nextElement();
				int cce = Midi.NewEv(Midi.typeCtrlChange);
			//	Midi.SetChan(cce, chan%16);
		//		Midi.SetPort(cce, (int)(chan*1.0/16.0));
				this.setPortChan(cce, chan);
				
				Midi.SetData0(cce, ctype.intValue()); // set the cc type
				Midi.SetData1(cce, (int)(((ValueGraphModel) 
			    		   envs.get(ctype)).getValAt(currDate)+ 0.5) ); // value
		//		PO.p("value = " + Midi.GetData1(cce));
				Midi.SetDate(cce, currDate); // set the date of the event
				seq.addMidishareEvent(cce);
			//	if(ctype.intValue() == 7 && Midi.GetData1(cce) == 0) {
			//		PO.p("volume 0 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//		PO.p("current date = " + currDate);
			//	}
			}
		//	PO.p(" curr date = " + currDate);
			catf++;
		}
	}

	private void phraseIntoSeq(Phrase phr, int channel, Sequence seq) {
		try {
			if (phr == null)
				new NullPointerException();
		} catch (NullPointerException e) {
			e.toString();
			return;
		}

		int dateOfEvent = 0;
		int ev = 0;

		
		double counter = 0.0;
		double startTime = phr.getStartTime();
        
   //     PO.p("sequencer: phrase start time = " + startTime);
        
		//  System.out.println("trempo = " + tempo);
		for(int i=0; i< phr.size(); i++) {
			dateOfEvent = inMillis((counter + startTime), tempo.getPerMinute());
			//   System.out.println(" doe " + dateOfEvent);
			Note n = (Note) phr.getNote(i);

			if (n.getPitch() > jm.JMC.REST + 60) {
				double idur = n.getDuration();
				//  System.out.println(" idur " + idur);

				int milDur = inMillis(idur, tempo.getPerMinute());
				//      System.out.println(" millDur " + milDur);
				// duration conversion working properly.

				ev = makeMSNote(n.getPitch(), n.getDynamic(), inMillis(n
						.getDuration(), tempo.getPerMinute()), dateOfEvent,
						channel);

				//      System.out.println("date = " + Midi.GetDate(ev));
				//    System.out.println("ev length " + Midi.GetData2(ev));
				seq.addMidishareEvent(ev);
			}
			counter += n.getRhythmValue();
		}
	}

	public void sendPrgChng(int instrument, int channel) {
		if (channel < 1 || channel > 15) {
			try {
				Exception e = new Exception(
						"part channel mus be between 1 and 16, or 0-15 if sending directly");
				e.fillInStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//  System.out.println(" sending program change ");
		//System.out.println("insturment = " + instrument);
		int event = Midi.NewEv(Midi.typeProgChange); // ask for a new note event
		if (event != 0) { // if the allocation was succesfull

			setPortChan(channel, event);
			
			
			//                  Midi.SetPort(event,destinationPort); // set the destination port
			Midi.SetField(event, 0, instrument);

			Midi.SetDate(event, 0);
		}
		Midi.SendIm(refnum, event);
	}

	private static void setPortChan(int chan, int event) {
		Midi.SetChan(event, (chan)%16); // set the Midi channel
		Midi.SetPort(event, (int)((chan)*1.0/16.0)); // set the port
	//	PO.p("in chan = " + chan + "  chan " + Midi.GetChan(event) + " port = " + Midi.GetPort(event));
	}
	
	/**
	 * static version is a bank style program change
	 */
	public void sendBankPrgChng(int instrument, int channel, int refnumIndex) {
		//  System.out.println("Sending insturment = " + instrument);
		Integer bank = null;
		int prgChng = instrument;
		//    Integer lsb = null;
		int event = Midi.NewEv(Midi.typeProgChange); // ask for a new note event
		if (event != 0) { // if the allocation was succesfull

		//	Midi.SetChan(event, channel); // set the Midi channel
			setPortChan(channel, event);
			//                  Midi.SetPort(event,destinationPort); // set the destination port
			//check to see if lsb is being used
			if (instrument > 999999) {
				prgChng = instrument % 1000;
				int lsb = (int) (instrument / 1000);
				lsb = lsb % 1000;
				int msb = instrument / 1000000;
				//     System.out.println("msb: " + msb + " lsb: " + lsb + "
				// prgChng: " + prgChng);
				sendControllerData(channel, 0, msb, msrefnums[refnumIndex]);
				sendControllerData(channel, 32, lsb, msrefnums[refnumIndex]);

			} else if (instrument > 99) {
				bank = new Integer((100 + ((int) (instrument / 100))));
				// System.out.println(" bank = " + bank.intValue());
				prgChng = instrument % 100;
			}
			Midi.SetField(event, 0, prgChng);

			Midi.SetDate(event, 0);
		}
		if (bank != null) {
			/// System.out.println(" bank not null " + bank.intValue() + "
			// instrument = " + instrument);
			int bevent = Midi.NewEv(Midi.typeProgChange);
		//	Midi.SetChan(bevent, channel);
			setPortChan(channel, bevent);
			
			Midi.SetField(bevent, 0, bank.intValue());
			Midi.SetDate(bevent, 0);
			Midi.SendIm(msrefnums[refnumIndex], bevent);
			Midi.SetDate(event, 2000);
		}
		//   System.out.println(" sending immediately " + instrument + " " +
		// channel);
		Midi.SendIm(msrefnums[refnumIndex], event);
	}

    
	public void sendVol(int channel, int value) {
        
        
        int event = Midi.NewEv(Midi.typeCtrlChange); // ask for a new note event
		if (event != 0) { // if the allocation was succesfull

			setPortChan(channel, event);
		//	Midi.SetChan(event, channel); // set the Midi channel
			//                  Midi.SetPort(event,destinationPort); // set the destination port
			Midi.SetField(event, 0, 7);
			Midi.SetField(event, 1, value);
			Midi.SetDate(event, 0);
		}
		Midi.SendIm(refnum, event);
		// Midi.FreeEv(event);
	}

	public static void sendVol(int channel, int value, int refNumIndex) {
		int event = Midi.NewEv(Midi.typeCtrlChange); // ask for a new note event
		if (event != 0) { // if the allocation was succesfull

			setPortChan(channel, event);
			//Midi.SetChan(event, channel); // set the Midi channel
			//                  Midi.SetPort(event,destinationPort); // set the destination port
			Midi.SetField(event, 0, 7);
			Midi.SetField(event, 1, value);
			Midi.SetDate(event, 0);
		}
		Midi.SendIm(msrefnums[refNumIndex], event);
	}

	
	/**
	 * sonds MIDI controller messages
	 * 
	 * @param channel
	 * @param type
	 * @param value
	 * @param refNumIndex
	 */
	private static int[] evToDispose = new int[300];
	static {
		for (int i = 0; i < evToDispose.length; i++) {
			evToDispose[i] = -1;
		}
	}

	static int freeCount = 0;
	public static void sendControllerData(int channel, int type, int value,
			int refNumIndex) {
		//System.out.println("channel " + channel + " type " + type + " value "
		// + value);
		if (value < 0 || value > 127) {
			try {
				Exception e = new Exception("ctrl value must be from 0-127");
				e.fillInStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//System.out.println("type" + type + "value" + value);
		int event = Midi.NewEv(Midi.typeCtrlChange);

		if (event != 0) { // if the allocation was succesfull

			//Midi.SetChan(event, channel); // set the Midi channel
			setPortChan(channel, event);
		//	Midi.SetPort(event, 0);
			Midi.SetField(event, 0, type);
			Midi.SetField(event, 1, value);
			//Midi.SetDate(event, 0);
			Midi.SendIm(msrefnums[refNumIndex], event);
		}
		
		freeCount++;
		freeCount = freeCount%400;
		//if(freeCount == 0)
	//		System.out.println("midishare space = " + Midi.FreeSpace());
		/*
		 * evCount++; evCount = evCount%400; if(evCount == 0)
		 * System.out.println(Midi.FreeSpace());
		 */
	}
	
	public static int makeCtrlData(int channel, int type, int value, int date) {
		int event = Midi.NewEv(Midi.typeCtrlChange);

		if (event != 0) { // if the allocation was succesfull

			//Midi.SetChan(event, channel); // set the Midi channel
			//Midi.SetPort(event, 0);
			setPortChan(channel, event);
			Midi.SetField(event, 0, type);
			Midi.SetField(event, 1, value);
			Midi.SetDate(event, date);
			return event;
		}
		return -1;
	}
	

	//int countr = 0;

	/**
	 * toggles the mode of wether to get the tempo from the score, or to rely
	 * soley on it being set. true means that the tempo will come from the score
	 *  
	 */
	public void setTempoFromScore(boolean b) {
		tempoFromScore = b;
	}

	/**
	 * converts from beats to milliseconds.  The tempo should be in BPM.
	 * @param toConvert
	 * @param tempo
	 * @return
	 */
	private int inMillis(double toConvert, double tempo) {
		double toRet = ((toConvert * 60000 / tempo));
		return (int) (toRet + 0.5); // round this mutha off!
	}

	int count = 0;

	private int makeMSNote(int pitch, int dynamic, int duration, int date,
			int channel) {

		int event = Midi.NewEv(Midi.typeNote); // ask for a new note event
		if (event != 0) { // if the allocation was succesfull
			p("setting parameters");
			setPortChan(channel, event);
			
			Midi.SetField(event, 0, pitch); // set the pitch field
			Midi.SetField(event, 1, dynamic); // set the velocity field
			//    System.out.println(" setting Duration " + duration);
			Midi.SetField(event, 2, duration); // set the duration field
			//  System.out.println(count + " getting Duration " +
			// Midi.GetField(event, 2));
			Midi.SetDate(event, date); // will try and make date relative to the
			// short sequence
		}
		return event;
	}

	public void setMidiInput(MidiInputListener mil) {
		this.mil = mil;
	}

	private void p(String toPrint) {

		//       System.out.println(toPrint);
	}

	/**
	 * 
	 * @param channel
	 * @param type
	 * @param val
	 * @param grad
	 * @param refnumindex
	 */
	
	public void sendCCtrlData(int channel, int type, int val, double grad, int refnumindex) {
		int tseq = -1;
		ccsw = !ccsw;
		if(ccsw) {
			tseq = ccseq1;
		} else {
			tseq = ccseq2;
		}
		Midi.ClearSeq(tseq);
				
		int ti = this.inMillis(this.tickTask.getRes(), this.tempo.getPerMinute());
		
		int nv = val;
		PO.p("grad = " + grad);
		for( int d = 0; d<ti; d += this.CCRes) {
			
			nv = val + (int)(((d/1000.0)*grad) + 0.5);
			PO.p("date = " + d + " new value " + nv);
			Midi.AddSeq(this.makeCtrlData(channel, type, val, d), tseq);
		}
		
	//	Midi.SendIm(this.nrefnum, tseq);
		
	}
}