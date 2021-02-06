package lplay;

import java.util.Vector;

import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Rest;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.View;
import jmms.MPlayer;
import jmms.TickEvent;
import jmms.TickListener;
import jmms.processor.Actuator;
import jmms.processor.ActuatorContainer;
import jmms.processor.ActuatorContainerManager;
import jmms.processor.ActuatorEvent;
import jmms.processor.MidiOutputManager;
import jmms.processor.MidiProcessor;
import music.BasicMorphMusicGen;
import music.MusicGenerator;
import music.TempoPC;
import music.singlePart.MorphMusicGenerator;
import ren.gui.seqEdit.BeatTracker;
import ren.music.PhraseQ;
import ren.util.PO;
import ren.util.Playable;
/**
 * This class is responsible for playing music generators.<br>
 * In order to set a music generator as playing, call 
 * {@link #setCurrentMusicGenerator(MusicGenerator) setCurrentMusicGenerator}<br>
 * 
 * @author wooller
 *
 * 22/02/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class LPlayer extends MPlayer implements Playable, Actuator, ActuatorContainer {

	// reference to the currently playing music generator
	private MusicGenerator currentMusicGenerator;
	
	private Score midiScore;
    private double startRecAt = 0;
	boolean recMIDI = false;
	
	private PhraseQ [] chidPhr = new PhraseQ [20];
    
	private Phrase restPhrase;
	private Part restPart;
   
	// midi input stuff
	private MidiProcessor mproc = new MidiProcessor();
    private MidiOutputManager mom = new MidiOutputManager();
    private ActuatorContainerManager aconman = new ActuatorContainerManager();
	
    private BeatTracker btrack;
    
    private Vector tickListeners = new Vector(10);
    
    private boolean VB_VOL = false; // verbose volume
    
	/** Constructs a new LPlayer */
	public LPlayer() {
	   
		this.setTempo(new TempoPC(180));
        
        for(int i=0; i<chidPhr.length; i++)  {
            chidPhr[i] = new PhraseQ(100);
        }
          
		midiScore = new Score("midi");
		//midiScore.initAllParts(3000); // three thousand notephrases per part
	//	historyScore = new Score("history");
		time = System.currentTimeMillis();
		restPhrase = new Phrase();
		restPhrase.add(new Rest(0.25));
		restPart = new Part("rest", 0, 16);
		
		seq.addMidiInputListener(mproc);
		mproc.setSequencer(seq);
		
		//mproc.addProcess(ProcessFactory.create("absolute acceleration"));
	//	mproc.addMidiInputLocation(4, 15);
	//	mproc.addMidiInputLocation(4, 16);
		
	//	mom.createMidiOutputLocation(4, 15, " ");
		
		aconman.registerRoot(mproc);
		aconman.registerRoot(this);
		aconman.registerRoot(mom);
		
        btrack = new BeatTracker();
        btrack.contruct();
        btrack.setLPlayer(this);
        
        
	}
	
	

    public ActuatorContainerManager getACM() {
        return this.aconman;
    }
    public void setACM(
            ActuatorContainerManager aconman) {
        this.aconman = aconman;
    }
    public MidiOutputManager getMOM() {
        return mom;
    }
    public void setMOM(MidiOutputManager mom) {
        this.mom = mom;
    }
    public MidiProcessor getMProc() {
        return mproc;
    }
    public void setMProc(MidiProcessor mproc) {
        this.mproc = mproc;
    }
	public void go() {
	    if(this.playing)
	        return;
        
        
	    super.go();
        
        te = new TickEvent(this, this.tickTask.getTick());
	    
	}
    
    private boolean resetWait = false;
    public void resetTick() {
        this.tickTask.resetTick();
        
        if(this.playing) {
            resetWait = true;
            while(resetWait) {}
        }
    }
    
    
	
	/**
	 * Overwritten from MPlayer, this method is called every tick, and returns <br>
	 * a beat resolution length score that is played through the jmms package <br>
	 * and Midishare. <br>
	 * 
	 * @return Score the score that is to be played, every tick
	 * @see jmms.MPlayer
	 * 
	 */
	long time;
	transient Score s;
	transient TickEvent te;
	public Score nextScore() {
        
		if (currentMusicGenerator != null) {
		    if(te == null)
                te = new TickEvent(this, this.tickTask.getTick()); 
		    
		//    PO.p("tempo in music gen " + 
	//	    		currentMusicGenerator.getLScore().getTempoParam().getValue());
		  
            te.setAt(this.tickTask.getTick());
		    
            updateTickListeners(te);
            
            this.btrack.setBeat(te.at());
            
       //     PO.p("next score ticking + te = " + te.at());
      //      PO.p("currentMusicGenerator type = " + currentMusicGenerator.getClass().getName());
			s = currentMusicGenerator.tickScore(te);
			if(VB_VOL) {
	        	for(int i=0; i< s.length(); i++) {
				PO.p("out s = " + i + " v = " + s.getPart(i).getVolume());
				
				}
	        }
			
			
	//		System.out.println("showing the final result ");
	//		View.print(s);
			if(this.recMIDI)
			    Mod.merge(midiScore, s.copy(), te.at() - startRecAt, false);
			
            
            //determine the number of id channels being used 
            /*
            int nid = 0;
            if(currentMusicGenerator instanceof MorphMusicGenerator) {
                nid = (int)(currentMusicGenerator.getLScore().size()/2.0);
            } else {
                nid = currentMusicGenerator.getLScore().size();
            }
            */
            //add the id channels to the appropriate queue
            int mod = 1;
            if (currentMusicGenerator instanceof MorphMusicGenerator)
                mod = 2;

            for (int i = 0; i < s.size(); i++) {
                if (i % mod == 0) {
                    Part p = s.getPart(i);
                    for (int j = 0; j < p.length(); j++) {
                        Phrase phr = p.getPhrase(j).copy();
                        phr.setStartTime(phr.getStartTime() + te.at());
                        this.chidPhr[p.getIdChannel()].putPhrase(phr);
                    }
                }
            }
            
            resetWait = false;
            
		//	View.print(s);
			
		/*
			if(System.currentTimeMillis() > time + 10000) {
			    time = System.currentTimeMillis();
			    System.out.println("score length = " + midiScore.getEndTime());
			//    View.print(midiScore);
			}
			*/	
			return s;
		} else {
			return new Score("nothing");
		}
	}
	
    public void startRecordingMidi() {
		this.recMIDI = true;
        this.startRecAt = this.tickTask.getTick();
		midiScore = new Score("midi");
	}
	
	public Score stopRecordingMidi() {
		this.recMIDI = false;
        //View.sketch(midiScore);
        View.showMC(midiScore);
		return midiScore;
	}
	
	public Score getHistoryScore() {
	    return this.midiScore;
	}
    
    /**
     * get the history score, specifying 
     * @param num
     * @return
     */
    public Score getHistoryScore(int num) {
        return this.midiScore.copyLast(num);
    }

    public void fillChIDHist(int ch, int n, Vector v) {
        this.chidPhr[ch].fillWithLast(v, n, true);
    }
    
	/**
	 * @return the currently playing MusicGenerator
	 * @see music.MusicGenerator
	 */
	public MusicGenerator getCurrentMusicGenerator() {
		return currentMusicGenerator;
	}

	/**
	 * Set the a new music generator to be playing.
	 * 
	 * @param newCurrentMusicGenerator The new MusicGenerator to play
	 * @see music.singlePart.MorphMusicGenerator#setPlaying(boolean)
	 */
	public void setCurrentMusicGenerator(MusicGenerator newCurrentMusicGenerator) {
		if(newCurrentMusicGenerator == null) {
			this.currentMusicGenerator = null;
			return;
		}
		
		// set the new music generator
		// - this goes first so that linked playing music generators (linked to 
		//   a morphMusicGenerator, for example) can be determined
		newCurrentMusicGenerator.setPlaying(true);

		// if it exists, stop the current music generator from playing
		if (this.currentMusicGenerator != null) {
			if (this.currentMusicGenerator.isPlaying()) {
				this.currentMusicGenerator.setPlaying(false);
			}
		}

		// set the new music generator
		this.currentMusicGenerator = newCurrentMusicGenerator;

		if (this.tempo instanceof TempoPC) {
			//.newCurrentMusicGeneratorif(this.te)
			((TempoPC) this.tempo).setTempoParam(this.currentMusicGenerator
					.getLScore().getTempoParam());
		}

	}

	/**
	 * Adds one beat to another, avoiding double accuracy errors <br>
	 * 
	 * @param a
	 *            beat to be added
	 * @param b
	 *            another beat to be added
	 * @return the sum of both beats
	 * @see #res()
	 */
	public double plus(double a, double b) {
		return (((int) (a / res())) + ((int) (b / res()))) * res();
	}

	/**
	 * Adds one resolution length to the input and returns it. <br>
	 * Avoids java double accuracy errors. <br>
	 * 
	 * @param a
	 *            the beat value to be added to
	 * @return the input value plus one resolution, in beats
	 * @see #res()
	 */
	public double plusRes(double a) {
		return plus(a, res());
	}

	/**
	 * accessor for resolution
	 * 
	 * @return the current resolution in beats
	 */
	public double res() {
		return this.tickTask.getRes();
	}

	/**
	 * returns the resolution in terms of how many times bigger it is to the
	 * <br>
	 * the smallest possible, so that you can keep track of time using an <br>
	 * integer, regardless of the changes in resolution that may occur. <br>
	 * tickTask.SRES is the smallest resolution possible. <br>
	 * 
	 * @return int the number of smallest resolutions in the current resolution
	 *         <br>
	 * @see jmms.TickTask#SRES
	 * @see #tickFromSResCount(int src)
	 */
	public int resTick() {
		return (int) (res() / tickTask.SRES);
	}

	/**
	 * converts resolution from a factorial integer representation to a <br>
	 * beats representation.
	 * 
	 * @param src
	 *            resolution in terms of the number of smallest possible <br>
	 *            resolutions <br>
	 * 
	 * @return resolution in terms of beats
	 * @see jmms.TickTask#SRES
	 * @see #resTick()
	 */
	public double tickFromSResCount(int src) {
		return src * tickTask.SRES;
	}



    /**
     * controls the position of the current morph
     * because the lplayer contains the current morph, 
     * it is the logical point to control it from
     */
    public void commandRecieved(ActuatorEvent e) {
    //	PO.p("morph index command recieved ");
    	
        if(this.currentMusicGenerator instanceof BasicMorphMusicGen) {
      //      System.out.println(" lplayer. morph pos going to : " + e.getNormValue());
            ((BasicMorphMusicGen)currentMusicGenerator).getMorphTracker().setUpdating(false);
            ((BasicMorphMusicGen)currentMusicGenerator).getMorphTracker().setPos(e.getNormValue());
        }
        
    }



    /* (non-Javadoc)
     * @see jmms.processor.ActuatorContainer#getSubActuatorContainers()
     */
    public ActuatorContainer[] getSubActuatorContainers() {
        
        return null;
    }



    /* (non-Javadoc)
     * @see jmms.processor.ActuatorContainer#getActuators()
     */
    private Actuator [] tacarr = {this};
    public Actuator[] getActuators() {
        return tacarr;
    }

    /* (non-Javadoc)
     * @see jmms.processor.ActuatorContainer#getActuatorContainerCount()
     */
    public int getActuatorContainerCount() {
        return 0;
    }



    /* (non-Javadoc)
     * @see jmms.processor.ActuatorContainer#getIndexOfSubContainer(jmms.processor.ActuatorContainer)
     */
    public int getIndexOfSubContainer(ActuatorContainer sub) {
        return 0;
    }



    /* (non-Javadoc)
     * @see ren.lang.Namable#setName(java.lang.String)
     */
    public void setName(String name) {
    }



    /* (non-Javadoc)
     * @see ren.lang.Namable#getName()
     */
    public String getName() {
        return "morph position";
    }
    
    public String toString() {
        return this.getName();
    }



    public BeatTracker getBeatTracker() {
        return btrack;
    }



    public void userModBeat(double d) {
        this.tickTask.userModTick(d);
    }
    
    public void userMod(boolean b) {
        this.tickTask.userModTick(b);        
    }

    public PhraseQ getChIDHistQ(int ch) {
        return this.chidPhr[ch];
    }



    public double getBeatAt() {
        if(this.te != null)
            return this.te.at();
        else
            return -1;
    }

    private void updateTickListeners(TickEvent te) {
        for(int i=0; i< this.tickListeners.size(); i++) {
            ((TickListener)tickListeners.get(i)).tick(te);
        }
    }

    public void addTickListener(TickListener tl) {
        this.tickListeners.add(tl);
    }



    public void setBeatAt(double d) {
        this.tickTask.setTick(d);
    }
    
}