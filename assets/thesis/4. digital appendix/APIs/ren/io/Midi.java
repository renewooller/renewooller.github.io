/*
 * Created on Apr 21, 2006
 *
 * @author Rene Wooller
 */
package ren.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileReader;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import ren.util.PO;

public class Midi {

	MidiFileReader mifir;// = new MidiFileReader();
	
	private static Midi inst = new Midi();
	
	private boolean verbose = true;
	
	// the resolution in ticks per beat (if PPQ) that will be read in by the file
	private int res = 0;
	
	public Midi() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static Midi getInstance() {
		return inst;
	}
	
	public void read(Score s, String fileName) {
        s.empty();
        
        try{
                System.out.println("--------------------- Reading MIDI File ---------------------");
                InputStream is = new FileInputStream(fileName);
                is = new BufferedInputStream(is, 1024);
                MidiFileFormat mff = MidiSystem.getMidiFileFormat(is);
                if(verbose)	
                	PO.p(" midi file type = " + mff.getType());
                //PO.p(mff.get)
                if(verbose) {
                if(mff.getDivisionType() == Sequence.PPQ) {
                	res = mff.getResolution();      
                	PO.p("div = PPQ. res = " + res);
                } else if(mff.getDivisionType() == Sequence.SMPTE_24 || 
                		mff.getDivisionType() == Sequence.SMPTE_25 ||
                		mff.getDivisionType() == Sequence.SMPTE_30 ||
                		mff.getDivisionType() == Sequence.SMPTE_30DROP) {
                	PO.p("div = smpte");
                }
                }
              
                Sequence seq = MidiSystem.getSequence(is);
                seqToScoreRT(seq, s);
         
        }catch(IOException e){
        	e.printStackTrace();
        } catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
    }
	
	private void seqToScoreRT(Sequence seq, Score sco) {
		sco.empty();
		
		Track [] tra = seq.getTracks();
		
		// initialise parts to hold data for each channel
		Part [] pc = new Part [16]; 
		for(int i=0; i< pc.length; i++) {
			pc[i] = new Part();
			pc[i].setChannel(i);
		}
		long [][][] nons = new long [16][128][2];
		
		// go through the tracks
		for(int i=0; i< tra.length; i++) {
			// reset the nons
			for(int ch = 0; ch < nons.length; ch++) {
				for(int pi = 0; pi < nons[ch].length; pi++) {
					nons[ch][pi][0] = -1;
					nons[ch][pi][1] = -1;
				}
			}
			
			// put the events from the track into the appropriate part
			trackIntoParts(tra[i], pc, nons); 	
		}
		
		// only add parts that have events in them into the score
		for(int i=0; i< pc.length; i++) {
			if(pc[i].length() > 0) {
				sco.add(pc[i]);
			}
		}
		
	}
	
	/**
	 * 
	 * @param t track to get the data from
	 * @param p part to put the data into.  must be of length 16, 
	 * 			one for each midi channel
	 */
	private void trackIntoParts(Track t, Part [] p, long [][][] nons) {
		//  array for holding the noteon channel, pitch (index) vel [0] and start [1]
		
		
		//		 go through the events
		for(int i=0; i< t.size(); i++) {
			// get event
			MidiEvent ev = t.get(i);
			
			// convert it to a short message
			ShortMessage mess;
			if(ev.getMessage() instanceof ShortMessage) {
				mess = (ShortMessage)ev.getMessage();
			
				if(mess.getCommand() == ShortMessage.NOTE_ON && 
				   mess.getData2() > 0) {
					// check for consecutive Note-On messages
					if(nons[mess.getChannel()][mess.getData1()][0] != -1) {
						System.out.println("two note on " + 
						"messages with same pitch " + mess.getData1() + " chan = " + 
						mess.getChannel() + " vel = " + mess.getData2() + 
						" date orig " + nons[mess.getChannel()][mess.getData1()][1]*1.0/96.0 +
						" date new " + ev.getTick()*1.0/96.0);
						
					} else { // otherwise, lodge it
						
						nons[mess.getChannel()][mess.getData1()][0] = mess.getData2(); // velocity
						nons[mess.getChannel()][mess.getData1()][1] = ev.getTick(); // time stamp
						//PO.p("lodged " + )
					}
				} else if(mess.getCommand() == ShortMessage.NOTE_OFF || 
						(mess.getCommand() == ShortMessage.NOTE_ON &&
						 mess.getData2() == 0)) {
					// check to see if this is a note off without the note on
					if(nons[mess.getChannel()][mess.getData1()][0] == -1) {
						System.out.println(" note off message " + 
								mess.getData1() + 
								", without a note on message!");	
						
					} else {// otherwise, store the note
						// set the start time
						Phrase phr = 
							new Phrase(nons[mess.getChannel()][mess.getData1()][1]*1.0/this.res*1.0);
						Note n = new Note();
						n.setPitch(mess.getData1());
						// duration is the time of this note off - the corresponding note on
						n.setDuration((ev.getTick()*1.0/this.res*1.0)-phr.getStartTime());
						n.setDynamic((int)nons[mess.getChannel()][mess.getData1()][0]); // velocity
						phr.add(n);
						p[mess.getChannel()].add(phr);
						
						//clear the space in note on storage
						nons[mess.getChannel()][mess.getData1()][0] = -1; // vel
						nons[mess.getChannel()][mess.getData1()][1] = -1; // time stamp
					}
					
				} else if(mess.getCommand() == ShortMessage.PROGRAM_CHANGE) {
					p[mess.getChannel()].setInstrument(mess.getData1());
				} 
							
			}
		}	
	}
}
