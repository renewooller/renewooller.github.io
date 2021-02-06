package music;


import java.awt.Point;

import javax.swing.DefaultComboBoxModel;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jmms.TickEvent;
import lplay.AutoPlayer;
import lplay.LPlayer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.gui.ParameterMap;
import ren.io.Domc;
import ren.util.PO;

/**
 * Pattern music generator stores and plays whole
 * patterns of music
 * 
 * @author wooller
 * 
 * 22/02/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public class PatternMusicGenerator extends MusicGenerator {

    private AutoPlayer autoPlay = new AutoPlayer();

    /**
     * pointID is simultaneously used to identify this
     * pattern music generator and store data on the
     * location of the related graphical component when
     * the program saves its data
     */
    private Point pointID;

    /**
     * metaScope is a way of overiding the local scopes
     * within each part with a global value - it is yet
     * to be implemented
     */
    private ParameterMap metaScope;

    /**
     * this comboBoxModel (essentially a list) is used
     * to store setups of <br>
     * different parts (bands) <br>
     */
    private DefaultComboBoxModel savedScores = new DefaultComboBoxModel();

    /**
     * the frequency (in beats) at which the
     * cueListeners should be fired
     * 
     * @see music.MusicGenerator#fireCues()
     * @see #tickScore(TickEvent)
     */
    private double cueFreq = 4.0;

    /**
     * Constructs a new PatternMusicGenerator<br> -
     * puts a single note into it.<br> - creates a
     * default band setup and puts it into the
     * savedScores Model
     * 
     * @see #savedScores
     */
    public PatternMusicGenerator() {
        super();
    }

    public PatternMusicGenerator constructPattern() {
        super.construct();

        // adds a single note to the pattern
        // automatically
        // if(lscore.size() == 0)
        lscore.add((new LPart()).construct(new Part(new Phrase(new Note(
                (int) (50 + Math.random() * 20), 0.5)))));

        /*
         * //creates a band. The graphical interface
         * that supports the selection // and creation
         * of band setups is yet to be implemented
         * LScore band1 = new LScore();
         * band1.setName("band1"); band1.add((new
         * LPart()).construct(new Part("piano",
         * ProgramChanges.PIANO, 1))); band1.add((new
         * LPart()).construct(new Part("bass",
         * ProgramChanges.BASS, 2))); band1.add(new
         * Part("crystal", ProgramChanges.CRYSTAL, 3));
         * band1.add(new Part("drums", 0, 10));
         * savedScores.addElement(band1);
         */
        return this;
    }

    /**
     * @param p
     *            the new pointID
     * @see #pointID
     */
    public void setPointID(Point p) {
        pointID = p;
    }

    /**
     * @return the current pointID
     * @see #pointID
     */
    public Point getPointID() {
        return pointID;
    }

    /**
     * Tests this pattern music generator for equality
     * with a diferent one. <br>
     * It uses the pointID to do this, so if the
     * graphical Views are in exactly<br>
     * the same location on the screen, it will
     * interperet them as being equal <br>
     * 
     * @param pmg
     *            the external pattern music generator
     *            to compare to
     * @return boolean wether or not the pointID's are
     *         equal
     */
    public boolean equalsPointID(PatternMusicGenerator pmg) {
        if (pmg.getPointID().x == pointID.x && pmg.getPointID().y == pointID.y)
            return true;
        else
            return false;
    }

    /**
     * Sets the metaScope
     * 
     * @param pc
     *            the new metaScope
     * @see #metaScope
     */
    public void setMetaScope(ParameterMap pc) {
        this.metaScope = pc;
    }

    /**
     * @return the svaedScores Model, which holds all of
     *         the band setups <br>
     *         available for this part <br>
     * @see #savedScores
     */
    public DefaultComboBoxModel getSavedScoreModel() {
        return savedScores;
    }

    /**
     * tick score is fired every tick <br>
     * 
     * @param e
     *            the tick event which holds the player
     *            and the current beat
     * @return the resolution length score to be
     *         returned by this pattern
     */
    public Score tickScore(TickEvent e) {//NOTE: the "-res" was commented out because it was tripping up
    //   PO.p("e.at = " + e.at());
    	if (e.at() % cueFreq == cueFreq - e.getRes()) {
       //     PO.p("firing cues");
            this.fireCues();
        } // else

        this.autoPlay.tick(e);

        // PO.p("e.at() is not " + (cueFreq - ((LPlayer)
        // e.getSource()).res()));
        Score s =  super.tickScore(e);
        
       // PO.p("s = " + s.toString());
        return s;
        

    }

    /**
     * @param ls
     *            the new lscore to be set. lscore is
     *            holds the internal <br>
     *            musical data of the pattern <br>
     */
    public void setLScore(LScore ls) {
        this.lscore = ls;
    }

    /**
     * overwritten from MusicGenerator - nothing happens
     * here because the <br>
     * musicGenerator is cued in LPlayer and started
     * playing from there <br>
     */
    public void select() {
    }

    public void dload(Element e) {
        super.construct();

        this.setPointID(new Point(Integer.parseInt(e.getAttribute("px")),
                Integer.parseInt(e.getAttribute("py"))));
        this.setLScore((LScore) Domc.lo(
            (Element) (e.getElementsByTagName("lscore").item(0)), LScore.class,
            e.getOwnerDocument()));

        NodeList apl = e.getElementsByTagName("autoPlay");
        
        if (apl.getLength() > 0) {
            PO.p("loading autoplay");
            this.autoPlay = (AutoPlayer) Domc.lo(
                (Element) (apl.item(0)),
                AutoPlayer.class, e.getOwnerDocument());
        }

    }

    public void dsave(Element e) {

        e.setAttribute("px", String.valueOf(this.getPointID().x));
        e.setAttribute("py", String.valueOf(this.getPointID().y));

        e.appendChild(Domc.sa(this.getLScore(), "lscore", e.getOwnerDocument()));
        e.appendChild(Domc.sa(this.autoPlay, "autoPlay", e.getOwnerDocument()));

    }

    public AutoPlayer getAutoPlayer() {
        return this.autoPlay;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer("PatternMusicGenerator: ");
        sb.append("\n point id = " + this.pointID.toString());
        sb.append("\n lscore = " + this.lscore.print());
        return sb.toString();
    }
    
}
