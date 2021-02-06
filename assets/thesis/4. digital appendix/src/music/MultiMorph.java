/*
 * Created on 17/10/2005
 *
 * @author Rene Wooller
 */
package music;

import jm.music.data.Part;
import jm.music.data.Score;
import jmms.TickEvent;
import music.morph.ParamMorpher;
import music.morph.rt.MTransMorph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.io.Domc;
import ren.tonal.TonalManager;
import ren.util.PO;

public class MultiMorph extends BasicMorphMusicGen {

    private PartMorph[] pms = new PartMorph[20];

    private int npms = 0;

    private int tole = -1; // the tonal leader

	private boolean flip = true;
    
	private MorphFlipper flipper;
	
	private double stofset = -1;
	
	private boolean morphFirst = false;
	
	private boolean VB_VOL = false;  // verbose volume 
	
    public MultiMorph() {
        super();
      
    }
    
    public MultiMorph constructMultiMorph(MusicGenerator m1, MusicGenerator m2) {
        super.constructMorph(m1, m2);

        // initialise the partMorphs
        if(m1 != null)
        	initParts();

        return this;
    }

    public PartMorph getPartMorph(int i) {
        if (i < this.npms && i >= 0)
            return this.pms[i];
        else {

            PO.p(" i = " + i + " \n" + toString());

            try {
                Exception e = new NullPointerException();
                e.fillInStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    
    public PartMorph [] getPMarray() {
    	return this.pms;
    }
    

    public void initParts() {
    	
        npms = (int) (lscore.size() / 2);

       // PO.p(lscore.getLPart(0).getPart().toString());
        
        for (int i = 0; i < npms; i++) {
            
            if (this.pms[i] == null) {
                this.pms[i] = (new PartMorph()).construct(this);
            }
            
            //this.lscore.setLPart(m1.getLScore().getLPart(i).copyEmpty(), i*2);
           // this.lscore.setLPart(m2.getLScore().getLPart(i).copyEmpty(), i*2+1);
            
            
            this.pms[i].initParts(m1.getLScore(), m2.getLScore(), 
                                  this.lscore, i, this.morphLength.getValueInt());

        }
    }

    /**
     * updates thie lscore if m1(start or m2(finish)
     * have changed
     */
    public void updateTo(MusicGenerator mg) {
        this.initScores();
        this.initParts();
    }

    public void select() {
        this.initScores();
        this.initParts();
    }

    public Score tickScore(TickEvent e) {
    	if(stofset == -1) {
    		stofset = e.at();
    	}
    	
        if(tickListener != null)
        	tickListener.tick(e);
        // check to see if we have finished
    //   PO.p("npms = " + this.npms);

        updateMorph(e);
        
        updateTracker(e);
        // gets the music and calls the visuals as well
        
        
        
        Score rs =  generateTickScore(e);
        
        if(VB_VOL) {
        	for(int i=0; i< rs.length(); i++) {
			PO.p("rs = " + i + " v = " + rs.getPart(i).getVolume());
			
			}
        }
        
     //   PO.p("\n\n" + rs.toString());
        if(super.checkTrackerForEnd()) {
        	// this tells the musicArea to play the cued
            // music generator
            super.cueman.trackerEndCue(this, super.cuedMG);
             
        }
        
        checkFlip(e);
        
        return rs;
    
    }

    protected void checkFlip(TickEvent e) {
    	if(this.flip) {
    		if(this.flipper!= null) {
    			this.flipper.checkToFlip(this, e, stofset);
    		}
    	}
    }
    
    public Score generateTickScore(TickEvent e) {
     //  PO.p("pos " + this.mtracker.getPos());
    //    PO.p("generate tick score " + e.at() + "\n" +this.lscore.getJMScore().toString());
        if(super.m1 == null) {
        	return this.lscore.getJMScore();
        }
    	
        Score tr = null;
    //    PO.p("morphFirst  = " + morphFirst);
        
        if ((!morphFirst)&&
        		super.getMorphTracker()
            .getPos() == 0) {
    //    	PO.p("NOT");
            tr =  super.getFrom()
                .generateTickScore(e);
        } else if ((!morphFirst) &&
        		super.getMorphTracker()
            .getPos() == 1) {
   //     	PO.p("NOT");
            tr = super.getTo()
                .generateTickScore(e);
        } else {
        	
    //    	PO.p("mmmmmm oooorphing");
            tr = this.lscore.getJMScore();
        }
        //PO.p("at " +e.at());
        
    //    PO.p("returning " + tr.toString());
        return tr;
    }
    
    protected void updateMorph(TickEvent e) {
    	this.updateGlobalParameterMorph();
        
        boolean [] plapa = new boolean [this.npms]; // if true, play this part
        // check to see if one of them is solo
        boolean onesolo = false;
        // assume one of them is soloing
        for(int i = 0; i< this.npms; i ++) {
        	// if one of them is soloing
        	if(this.pms[i].getSolo()) {
        		onesolo = true;
        		plapa[i] = true; // play this one
        	} else {
        		plapa[i] = false;
        	}   	
        }
        
        Part[] lcp = new Part[2];
  //     PO.p("number of pms = " + this.npms);
        for (int i=0; i < this.npms; i++) {
        	if((onesolo && plapa[i]) || !onesolo) {
        		// handle cases where the partmorph has been set to null
        		if(pms[i] == null) {
        			lcp = null;
        		} else {
     //   			PO.p("pms " + i + " tracker " + this.mtracker.getPos());
        			lcp = this.pms[i].getTickMorph(this.mtracker.getPos(), e);
        		}
        		if(lcp != null) {
        			this.lscore.getLPart(i * 2)
                		.setPart(lcp[0].copy());
        			this.lscore.getLPart(i * 2 + 1)
                		.setPart(lcp[1].copy());
        			
        			if(VB_VOL) {
        			PO.p("update morph.i = " + i); 
        			PO.p("v1 = " + this.lscore.getLPart(i*2).getPart().getVolume());
        			PO.p("v2 = " + this.lscore.getLPart(i*2+1).getPart().getVolume());
        			}
        			
        		} else {
        			this.lscore.getLPart(i * 2).getPart().empty();
        			this.lscore.getLPart(i * 2 + 1).getPart().empty();
        		}
        	}
        }
     //   PO.p("lscore = "+ lscore.print());
    }

    public void finishInit() {
    	super.finishInit();
        for (int i = 0; i < this.npms; i++)
            this.pms[i].finishInit();
    }

    public void startInit() {
    	super.startInit();
    	this.stofset= -1;
    	
    	morphFirst = false; // default is to playback when on 0 and 1
        for (int i = 0; i < this.npms; i++) {
        	
            this.pms[i].startInit();
            
            // if one of the morphing algorithms is an interpolation, it will need
            // to use morph first, to stop the jittering
            if(pms[i].getMorphStrucList().getSelectedItem() 
            		instanceof MTransMorph) {
            	this.morphFirst = true;
            }
        }
       
    }
   
    private static String M1 = "m1";

    private static String M2 = "m2";

    private static String M_TEMPO = "morphTempo";

    private static String MORLEN = "morphLength";
    
    private static String PMS = "pms";
    
    private static String TOLE = "tole";

    private static String LP = "lplay";
    private static String CM = "cueman";
    
    public void dload(Element e) {
	
    	// if m1 and m2 have been saved
    	if(e.getElementsByTagName(M1).getLength() > 0) {
    	
    		Element m1e = ((Element) (e.getElementsByTagName(M1).item(0)));
    		Element m2e = ((Element) (e.getElementsByTagName(M2).item(0)));
       
    		this.constructMultiMorph(((MusicGenerator) Domc.lo(m1e,
        			e.getOwnerDocument())), 
        			((MusicGenerator) Domc.lo(m2e,
        					e.getOwnerDocument())));
    	} else {
    		// otherwise construct it empty
    		this.constructMultiMorph(null, null);
    	}
      //  Element cme = ((Element) (e.getElementsByTagName(CM).item(0)));
        
        
        dloadEssential(e);
        	        
    }
    
    public void dloadEssential(Element e) {
    	PO.p("multimorph loading essential");
    	
    	Element mp = ((Element) (e.getElementsByTagName(M_TEMPO).item(0)));

        // load parameter morph type for tempo
        setCBModel(morphTempo, mp, e.getOwnerDocument());

        super.morphLength.setValue(e.getAttribute(MORLEN));
        
        if(e.hasAttribute(TOLE))
        	this.tole = Integer.parseInt(e.getAttribute(TOLE));
        
        NodeList nl = e.getChildNodes();
        this.npms = 0;
        
        PO.p("nl.getLength");
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i)
                .getNodeName()
                .startsWith(PMS)) {
                pms[npms] = (PartMorph) Domc.lo(((Element) nl.item(i)),
                    PartMorph.class, e.getOwnerDocument());
                
                PO.p("constructing pms " + i);
                
                pms[npms++].construct(this);
                // pms[npms++].construct(this);
            }
        }
       
    }
    
    public void loadState(Document doc) {
    	Element e = (Element)doc.getFirstChild();
    	dloadEssential(e);
    }

    public void dsave(Element e) {
    	if(this.m1 != null)	
        e.appendChild(Domc.sa(m1, this.M1, e.getOwnerDocument()));
        if(this.m2 != null)
    	e.appendChild(Domc.sa(m2, this.M2, e.getOwnerDocument()));
        dsaveEssential(e);
        if(this.m1 != null) { // why do I init them after??!
        this.initScores();
        this.initParts();
        }
    }

    public void dsaveEssential(Element e) {
		e.appendChild(Domc.sa(((ParamMorpher) morphTempo.getSelectedItem()), this.M_TEMPO, e
				.getOwnerDocument()));
		e.setAttribute(MORLEN, super.morphLength.getValueStr());

		e.setAttribute(TOLE, String.valueOf(tole));
		
		for (int i = 0; i < this.npms; i++) {
			e.appendChild(Domc.sa(pms[i], this.PMS + Integer.toString(i), e.getOwnerDocument()));
		}

	}
    
    public void saveState(Document doc) {
    	Element e = doc.createElement("state");
		doc.appendChild(e);
		dsaveEssential(e);
    }
    
    public int size() {
        return this.npms;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.npms; i++) {
            if (this.pms[i] != null)
                sb.append("i = " + i + " - not null\n");
            else
                sb.append("i = " + i + " - null\n");
        }
        return sb.toString();
    }

    /**
     * replaces the orignal part morph opm with the new one, pm.
     * 
     * it also makes sure that when the part is replaced, the to and from are
     * updated within it, by calling initParts
     * @param opm
     * @param pm
     */
	public void replacePartMorph(PartMorph opm, PartMorph pm) {
		for(int i=0; i< this.npms; i++) {
			if(this.pms[i] == opm)
				this.pms[i] = pm;
		}
		this.initParts();
	}
	
	/**
	 * sets the part morph at the specified index.
	 * 
	 * makes mo attempt to update the parts
	 * 
	 * @param morph
	 * @param i
	 */
	public void setPartMorph(PartMorph morph, int i) {
		if (morph != null) {
			// initialise the carrier of attributes
		//	PO.p("mm set part morph. from null = " + (morph.getFrom() == null));
			
			this.lscore.setLPart(morph.getFrom().copyEmpty(), i * 2);
			this.lscore.setLPart(morph.getTo().copyEmpty(), i * 2 + 1);
			
			if(i+1 > this.npms)
				npms = i+1;
		}
		
		// set the part morph
		this.pms[i] = morph;
				
	}
	
	public int getTonalLead() {
		return this.tole;
	}
	
	public void setTonalLead(int ntl) {
		this.tole = ntl;
	}

	public TonalManager getTonalLeadObj() {
		return this.pms[this.getTonalLead()].getCurrentTonalManager();
		
	}

	public int getMCSize() {
		return this.npms;
	}

	public double getLongestScope() {
		double lon = -1;
		for(int i=0; i< this.npms; i++) {
			if(this.pms[i].getMorphScope().getValue() > lon)
				lon = this.pms[i].getMorphScope().getValue();
		}
		
		return lon;
	}
		
	public boolean isFlip() {
		return flip;
	}
	
	public void setFlip(boolean nf) {
		this.flip = nf;
	}
	
	public void setFlipper(MorphFlipper nef) {
		this.flipper = nef;
	}
	
}
