/*
 * Created on 17/10/2005
 *
 * @author Rene Wooller
 */
package music;


import gui.musicArea.MorphEndListener;
import gui.musicArea.MusicArea;

import javax.swing.DefaultComboBoxModel;

import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jmms.TickEvent;
import lplay.CueManager;
import lplay.LPlayer;
import music.morph.MorphFactory;
import music.morph.ParamMorpher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ren.gui.ParameterMap;
import ren.io.Domc;
import ren.io.Factorable;
import ren.util.PO;

/**
 * A music generator that morphs from one music
 * generator to another
 * 
 * @author wooller
 * 
 * 14/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public abstract class BasicMorphMusicGen extends MusicGenerator {

    /** the start musicgenerator to morph from */
    protected MusicGenerator m1;

    /** the finish musicgenerator to morph to */
    protected MusicGenerator m2;

    protected DefaultComboBoxModel morphTempo;

    // if it is true, the start initiation sequence has been fired
    private boolean startIn = false;
    
    /**
     * tracks the location of the morph
     * 
     * @see music.MorphTracker#getPos()
     */
    protected MorphTracker mtracker;

    /** holds the length of the morph in beats */
    protected ParameterMap morphLength = (new ParameterMap()).construct(1, 1024,
    		1, 1024, 32, "morph length");
    	
    	/*(new ParameterMap()).construct(1, 16,
        new double[] { 2.0, 4.0, 6.0, 8.0, 12.0, 16.0, 24.0, 26.0, 32.0, 42.0, 48.0, 64.0, 
    		96.0, 128.0, 192, 256.0, 512.0, 1024.0}, 16.0, "morph length");*/

    /**
     * this listener is triggered when the morph
     * finishes
     */
    private MorphEndListener morphEndListener;

    /**
     * temporarily holds the result of the morph for one
     * part. [0] is derived <br>
     * from m1(start) and [1] is derived from
     * m2(destination)
     */
    protected Part[] morphResult = new Part[2];

    /**
     * the music generator that is cued for starting
     * when this morph is complete
     */
    protected MusicGenerator cuedMG;

    protected transient Score startScore, endScore;

    protected Part emptyPart = new Part();

    protected double[] morphParams = new double[2];

	protected CueManager cueman;
    
    /**
     * creates a new BasicMorphMusicGen <br>
     * to initialise to and from and other variables,
     * use
     * {@link #construct(MusicGenerator, MusicGenerator) construct(m1, m2)}
     * <br>
     */
    public BasicMorphMusicGen() {
        super();
        
    }

    /**
     * initialses m1 and m2 and the morph tracker
     * 
     * @param m1
     *            the music generator to morph from
     * @param m2
     *            the music generator to morph to
     * @return this BasicMorphMusicGen, with the new
     *         values
     */
    public BasicMorphMusicGen constructMorph(MusicGenerator m1,
            MusicGenerator m2) {
        super.construct();

//      initialise the Models of the morph algorithms
        initMorphAlgorithmModels();

//      create mtracker
        mtracker = (new MorphTracker()).construct(morphLength, 0.5);
        this.mtracker.initForwards();
        
        if(m1 == null)
        	return this;
        
        

        // set references to start and destination music
        // generators
        this.m1 = m1;
        this.m2 = m2;

       // lscore = new LScore();

        // initialise the lscore with the correct number
        // of parts
        initScores();

        // add dependants
        m1.addDependant(this);
        m2.addDependant(this);

        
        return this;
    }

    /**
     * initialises the Models from which various
     * morphing algorithms can be <br>
     * selected <br>
     */
    protected void initMorphAlgorithmModels() {
        morphTempo = new DefaultComboBoxModel(MorphFactory.createAllParam());
    }

    /**
     * initialises the lscore to have the correct number
     * of parts. using
     * 
     * Also
     * 
     * {@link music.LScore#mergeLScores(LScore, LScore) 
     */
    public void initScores() {
        // make lscore the have the most number of parts
        // from the morph subjects
    //	PO.p("initing the scores");

        lscore.mergeLScores(m1.getLScore(), m2.getLScore());

        // make sure the parts are as long as they need
        // to be.
        // start parts
        startScore = m1.getLScore()
            .getJMScore()
            .copy();
        for (int i = 0; i < startScore.length(); i++) {
            Mod.cropFaster(startScore.getPart(i), 0, m1.getLScore()
                .getLPart(i).getScope().getValue());
        }

        // end parts
        endScore = m2.getLScore()
            .getJMScore()
            .copy();
        for (int i = 0; i < endScore.length(); i++) {
            Mod.cropFaster(endScore.getPart(i), 0, m2.getLScore()
                .getLPart(i).getScope()
                .getValue());
        }

      //  PO.p("lscore = " + this.lscore.toString());
        
    }

    protected void updateGlobalParameterMorph() {
    	if(m1 == null)
    		return;
        // morph the tempo according to the
        // currently selected algorithm
        morphTempo().morph(m1.getLScore()
            .getTempoParam(), m2.getLScore()
            .getTempoParam(), this.lscore.getTempoParam(),
            this.mtracker.getPos());
    }

    /**
     * updates thie lscore if m1(start or m2(finish)
     * have changed
     */
    public void updateTo(MusicGenerator mg) {
        this.initScores();
    }

    /**
     * for when this has been selected: initialises the
     * lscore again, as <br>
     * changes may have occured in m1(start) and/or
     * m2(finish)
     */
    public void select() {
        initScores();
    }

    /**
     * @param the
     *            tickEvent that holds the current tick
     *            at and the player
     * @return the resolution length score
     * @see music.MusicGenerator#tickScore(TickEvent)
     */
    public Score tickScore(TickEvent e) {

    	if(checkTrackerForEnd()) {
    		// this tells the musicArea to play the cued
            // music generator
            this.setPlaying(false);

    		super.tickScore(e);
    	}

        updateMorph(e);
        updateTracker(e);
        // gets the music and calls the visuals as well
        return super.tickScore(e);
    }

    protected boolean checkTrackerForEnd() {
    	if(m1 == null)
    		return false;
    	
    	// check to see if we have finished
        if ((mtracker.getBeatPos() + mtracker.getUpdateRes()) == morphLength.getValue()
                && this.mtracker.isUpdating()) {
           
            // tickscore updates the graphics and get
            // the lscore for this tick
            return true;
        }
    	return false;
    }
    
    
    protected void updateTracker(TickEvent e) {
    	if(this.m1 != null) {
    	//	PO.p("updating tracker ");
    		this.mtracker.update(e);
    	}
    }

    /*
     * check to see if the lscore has got enought parts
     * in it
     */
    protected void updateMorph(TickEvent e) {
    	if(m1 != null)
    		this.updateGlobalParameterMorph();
    }

    /**
     * @param mel
     *            the new morph end listener which will
     *            be triggered when the <br>
     *            morph finishes <br>
     */
    public void setMorphEndListener(MorphEndListener mel) {
        this.morphEndListener = mel;
    }

    /**
     * Only usable if the start and destination morphs
     * are <br>
     * PatternMusicGenerators
     * 
     * @return the start and destination pattern music
     *         generators together. <br>
     *         [0] is the start (from) [1] is the finish
     *         (to)
     */
    public PatternMusicGenerator[] getToFromPMG() {
        PatternMusicGenerator[] toRet = new PatternMusicGenerator[2];
        if (m1 instanceof PatternMusicGenerator)
            toRet[0] = (PatternMusicGenerator) m1;
        if (m2 instanceof PatternMusicGenerator)
            toRet[1] = (PatternMusicGenerator) m2;

        return toRet;
    }

    /** convenience method for the morph tempo algorithm */
    protected ParamMorpher morphTempo() {
        return (ParamMorpher) this.morphTempo.getSelectedItem();
    }

    public void setMorphTempo(DefaultComboBoxModel nmt) {
        this.morphTempo = nmt;
    }

    public DefaultComboBoxModel getMorphTempo() {
        return morphTempo;
    }

    /**
     * @return the music generator that the morph is
     *         going from (start)
     */
    public MusicGenerator getFrom() {
        return m1;
    }

    /**
     * @return the music generator that the morph is
     *         going to (destination
     */
    public MusicGenerator getTo() {
        return m2;
    }

    /**
     * @param from
     *            the new music generator to morph from
     *            (start)
     */
    public void setFrom(MusicGenerator from) {
        m1 = from;
    }

    /**
     * @param to
     *            the new music generator to morph to
     *            (destination
     */
    public void setTo(MusicGenerator to) {
        m2 = to;
    }

    /**
     * 
     * /**
     * 
     * @return the musicGenerator that is cued for
     *         playing when the morph <br>
     *         finishes
     */
    public MusicGenerator getCuedMG() {
        return this.cuedMG;
    }

    /**
     * @return the morphTracker, which keeps track of
     *         how far the morph has <br>
     *         progressed
     */
    public MorphTracker getMorphTracker() {
        return this.mtracker;
    }

    /**
     * @return the model that stores the current morph
     *         length
     */
    public ParameterMap getMorphLength() {
        return this.morphLength;
    }

    public void startInit() {
    	startIn = true;
    }

    public void finishInit() {
    	startIn = false;
    }

    /**
     * setplaying is used to correctly set the state of
     * this <br>
     * BasicMorphMusicGen and dependant listeners both
     * before and after <br>
     * playing. <br>
     * 
     * @param boolean
     *            pl wether it is being set to play
     *            (true) or not (false) <br>
     */
    public void setPlaying(boolean pl) {

        // when finishing
        if (pl == false) {
            // just used for printing useful information on the screen
            this.finishInit();

            this.lscore.empty();

            // changes the playing variable to false;
            super.setPlaying(false);
            
            if(this.morphEndListener != null) {
            	// hides the ball
            	this.morphEndListener.morphEnded();
            } 
            /*
            // if it is ending because it has reached the end (not cause it was flipped)
            if(this.mtracker.getPos() == 0 || 
               this.mtracker.getPos() == 1) {
            	this.cueman.getLPlayer().setCurrentMusicGenerator(getCuedMG());
            	// the cued player will be set playing now, so we remove it
            	cueman.removeCue();
            }*/
            
            return;
        }
        if(!startIn) {
        	this.startInit();
        }
        // this.morpher().mres = MRES;
        // when starting

        // sets the playing variable to true (pl must be
        // true)
        super.setPlaying(pl);

        // sets the appropriate value for the cued
        // musicGenerator, tracker
        // and directional flag depending on which music
        // generator we are
        // transitioning from
        if(this.m1 == null) {
        	PO.p("bmmg m1 null");
        	return;
        }
        
        PO.p(" m1 x = " + ((PatternMusicGenerator)m1).getPointID().x +
        	 " ; playing state = " + m1.isPlaying());
        PO.p(" m2 x = " + ((PatternMusicGenerator)m2).getPointID().x +
           	 " ; playing state = " + m2.isPlaying());
        
        if (this.m2.isPlaying()) {
        	PO.p("bmmg init back " + m2.getClass().getName());
        	
            this.cuedMG = m1;
            this.mtracker.initBackwards();

        } else if(this.m1.isPlaying()){
        	PO.p("bmmg init fwd");
        	this.cuedMG = m2;
            this.mtracker.initForwards();
        } else {
        	PO.p("bmmg -- cue set from tracker direction");
        	if(!this.mtracker.isBackwards()) {
        		this.cuedMG = m2;
        	} else {
        		this.cuedMG = m1;
        	}
        }

        // initialise counters for the morph algorithm
        // this.allCount = 0;

        // update graphical components (make the ball
        // visible)
        if(morphEndListener != null)
        	this.morphEndListener.morphStarted();

    }

    protected static void setCBModel(DefaultComboBoxModel dcbm, Element type, Document d) {
        // PO.p("type = " + type.getAttribute("type"));
        boolean found = false;
        for (int i = 0; i < dcbm.getSize(); i++) {
            // PO.p(((Factorable)dcbm.getElementAt(i)).getType());
            if (((Factorable) dcbm.getElementAt(i)).getType()
                .equalsIgnoreCase(type.getAttribute("type"))) {
                Object o = Domc.lo(type, dcbm.getElementAt(i)
                    .getClass(), d);

                // PO.p("removing element");
                dcbm.removeElementAt(i);
                // PO.p("inserting");
                dcbm.insertElementAt(o, i);
                // PO.p("object type " + o.toString());
                dcbm.setSelectedItem(dcbm.getElementAt(i));
                found = true;
            }
        }
        if(!found) {
            System.out.println(" the type " + type.getAttribute("type") + 
                " was not found ");
        }
    }

	public void setCueManager(CueManager ncm) {
		this.cueman = ncm;
	}

}