package music.singlePart;

import javax.swing.DefaultComboBoxModel;

import jm.music.data.Part;
import jmms.TickEvent;
import music.BasicMorphMusicGen;
import music.LPart;
import music.MusicGenerator;
import music.morph.MorphFactory;
import music.morph.Morpher;
import music.morph.ParamMorpher;

import org.w3c.dom.Element;

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
public class MorphMusicGenerator extends
        BasicMorphMusicGen{

    /**
     * holds the current morphers
     */
    protected DefaultComboBoxModel morphStrucList, morphInst, 
        morphSVol, morphDVol1, morphDVol2;

   
    /** temporarily holds the parts to be morphed */
    private Part to, from;
    
    // this is only used in this class, which itself is deprecated
    private transient Part[] histPart = new Part[2];
 
    /**
     * creates a new MorphMusicGenerator <br>
     * to initialise to and from and other variables,
     * use
     * {@link #construct(MusicGenerator, MusicGenerator) construct(m1, m2)}
     * <br>
     */
    public MorphMusicGenerator() {
        super();
    }
    
    /**
     * initialises the Models from which various
     * morphing algorithms can be <br>
     * selected <br>
     */
    public void initMorphAlgorithmModels() {
        super.initMorphAlgorithmModels(); // does tempo
        
        Morpher[] m = MorphFactory.createAllStruc();

        for (int i = 0; i < m.length; i++) {
    //        System.out.println(m[i].getType());
        }

        morphStrucList = new DefaultComboBoxModel(m);
      
        morphInst = new DefaultComboBoxModel(new String [] {"together", "alternate"});
        morphSVol = new DefaultComboBoxModel(new String [] {"cross", "early", "constant"});
       //     MorphFactory.createAllParam());
        
        //not useing these other ones at the moment
        
        morphDVol1 =  new DefaultComboBoxModel(
            MorphFactory.createAllParam());
        morphDVol2 =  new DefaultComboBoxModel(
            MorphFactory.createAllParam());
        
    }
  
    // TODO
    private Morpher morphVol() {
        return (Morpher) this.morphSVol.getSelectedItem();
    }

   
    /**
     * @return the model of the current and avaliable
     *         structural morph algorithms
     */
    public DefaultComboBoxModel getMorphStrucList() {
        return morphStrucList;
    }

    /**
     * @param the
     *            new model of the parameter morph
     *            algorithms
     */
    public void setMorphStrucList(
            DefaultComboBoxModel morphStrucList) {
        this.morphStrucList = morphStrucList;
    }

   
    public DefaultComboBoxModel getMorphInst() {
        return this.morphInst;
    }
    
    public DefaultComboBoxModel getMorphSVol() {
        return this.morphSVol;
    }
    
    /**
     * for when this has been selected: initialises the
     * lscore again, as <br>
     * changes may have occured in m1(start) and/or
     * m2(finish)
     */
    public void select() {
        super.select(); //init();
    }

    /*
     * check to see if the lscore has got enought parts
     * in it
     */
    protected void updateMorph(TickEvent e) {
        super.updateMorph(e);
        
        // go through each part
        int len = Math.max(startScore.size(),
            endScore.size());
      
        // //ok/has correct length (when pre-made)
        for (int i = 0; i < len; i++) {

            // initialise values for from and to etc
            initMorphParts(i);
  
            if(from.getIdChannel() != to.getIdChannel()) {
                System.out.println("must have same ID channel in updateMorph");
                PO.p("from chan = " + from.getChannel() + " id = " + from.getIdChannel());
                PO.p("to chan = " + to.getChannel() + " id = " + to.getIdChannel());
            }       
            
            // perform the morph and put it into the morph result 
            initContextMorphParams(this.lscore.getLPart(i*2), 
                                   this.lscore.getLPart(i*2 +1));
          
            morphResult = obtainMorphResult(i, from,
                to, this.mtracker.getPos(),
                morphParams, e);
            
            if(morphResult == null) {
            	PO.p("morphResult null");
            }
            
            // if we are playing the melody with both instruments, add them together
            if(from.getChannel() != to.getChannel() && 
                    this.morphInst.getSelectedItem().equals("together")) {
           
                Part mrc1 = morphResult[0].copy();
                Part mrc2 = morphResult[1].copy();
                for(int j=0; j< mrc2.size(); j++) {
                    morphResult[0].addPhrase(mrc2.getPhrase(j));
                }
                for(int j=0; j< mrc1.size(); j++) {
                    morphResult[1].addPhrase(mrc1.getPhrase(j));
                }
            } 
            // do volume
//          morph volume
            morphVolume(morphResult[0], morphResult[1], 
                    from.getVolume(), to.getVolume());
                     
            // put the result into the lscore (which holds the music data that
            // is played every tick) of this music generator
            this.lscore.getLPart(2 * i)
                .setPart(morphResult[0].copy());
            this.lscore.getLPart(2 * i + 1)
                .setPart(morphResult[1].copy());

        }

    }

    private void morphVolume(Part into1, Part into2, int vol1, int vol2) {
        double mtp = this.mtracker.getPos();
        // same channel
        if(into1.getChannel() == into2.getChannel()) {
            if(this.morphSVol.getSelectedItem().equals("cross") ||
                    this.morphSVol.getSelectedItem().equals("early")) {
                into1.setVolume((int)(vol1*(1-mtp)) +
                                (int)(vol2*(mtp)));
            } else if(this.morphSVol.getSelectedItem().equals("constant")) {
                into1.setVolume((int)((vol1+vol2)/2));
            }
            into2.setVolume(into1.getVolume());
        } else {
            // different channels
            if(this.morphSVol.getSelectedItem().equals("cross")) {
                into1.setVolume((int)(vol1*(1-mtp)));
                into2.setVolume((int)(vol2*(mtp)));
            } else if(this.morphSVol.getSelectedItem().equals("early")) {
                if(mtp < 0.5) {
                    into1.setVolume(vol1);
                    into2.setVolume((int)(vol2*(mtp*2)));
                } else {
                    into1.setVolume((int)(vol1*((1-mtp)*2)));
                    into2.setVolume(vol2);
                }
            }else if(this.morphSVol.getSelectedItem().equals("constant")) {
                into1.setVolume((int)((vol1+vol2)/2));
                into2.setVolume(into1.getVolume());
            } else if(this.morphSVol.getSelectedItem().equals("exp")) {
                // make it exponential as well
            }
        }
        
    }

    protected void initMorphParts(int i) {
        // if it has not gone through all the start
        // parts
        if (startScore.size() > i)
            from = startScore.getPart(i);

        // if it has not gone through all the end
        // parts
        if (endScore.size() > i)
            to = endScore.getPart(i);

        // if the end part hasn't enought parts to
        // morph to
        if (endScore.size() <= i) {
            // it will morph from an empty
            to = emptyPart;
            to.setChannel(from.getChannel());
            to.setInstrument(from.getInstrument());
            to.setIdChannel(from.getIdChannel());
            // the morph part parameters will =
            // from's parameters
            /*
            this.updatePartParameterMorph(
                startScore.getLPart(i),
                startScore.getLPart(i),
                this.lscore.getLPart(2 * i),
                this.lscore.getLPart(2 * i + 1));*/

        } else if (startScore.size() <= i) {
            // it will morph to an empty
            from = emptyPart;
            from.setChannel(to.getChannel());
            from.setInstrument(to.getInstrument());
            from.setIdChannel(to.getIdChannel());
            // the morph part parameters will = to's
            // parameters
            /*
            updatePartParameterMorph(
                endScore.getLPart(i),
                endScore.getLPart(i),
                this.lscore.getLPart(2 * i),
                this.lscore.getLPart(2 * i + 1));*/
        } else { // if it is normal
            // the morph part parameters will be
            // some kind of merger
            /*
            updatePartParameterMorph(
                startScore.getLPart(i),
                endScore.getLPart(i),
                this.lscore.getLPart(2 * i),
                this.lscore.getLPart(2 * i + 1));*/
        }
    }
    

    public void startInit() {
        this.morpher().startInit();
    }
    
    public void finishInit() {
        this.morpher().finish();
    }
    
    /**
     * (morphParams are scope)
     * 
     * it is separate so that it can be overridden
     * easily if necessary
     * 
     * e isn't needed in this one
     * 
     * @param i
     *            partIndex
     * @return
     */
    protected Part[] obtainMorphResult(int i, Part from, Part to,
            double position, double[] morphParams,
            TickEvent e) {
  
        return morpher().morph(from, to, this.histPart,
            position, morphParams);

    }

    /**
     * 
     * @return the currently selected morphing algorithm
     */
    public Morpher morpher() {
        return (Morpher) this.morphStrucList.getSelectedItem();
    }

    /**  
     * the morph params are parameters that are
    particularly important to the structural morph. 
    scope is sent
    so that the process can be optimised. 0 is
    scope of from, 1 is scope of to.
     * */
    private double [] initContextMorphParams(LPart into1, LPart into2) {
        morphParams[0] = into1.getTransformChain()
            .getScopeParam()
            .getValue();
        morphParams[1] = into2.getTransformChain()
            .getScopeParam()
            .getValue();
        return morphParams;
    }
   

    public void dload(Element e) {
        // TODO Auto-generated method stub
        
    }

    public void dsave(Element e) {
        // TODO Auto-generated method stub
        
    }

    
}