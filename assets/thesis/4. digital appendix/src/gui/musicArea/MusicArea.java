package gui.musicArea;

import gui.LScoreTransEditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import lplay.CueManager;
import lplay.LPlayer;
import music.BasicMorphMusicGen;
import music.CueEvent;
import music.CueListener;
import music.MorphFlipper;
import music.MultiMorph;
import music.MusicGenerator;
import music.PatternMusicGenerator;
import music.singlePart.MorphMusicGenerator;
import music.singlePart.MorphMusicGeneratorRT;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.gui.PopupMenuArea;
import ren.gui.components.PlayButton;
import ren.io.Domable;
import ren.io.Domc;
import ren.util.DimUtil;
import ren.util.PO;

/**
 *  
 */

public class MusicArea extends PopupMenuArea implements Domable, CueManager {

	
	protected transient LPlayer lplay;
	
	private LScoreTransEditor tedit;

	private MorphFlipper flipper;
	
	public MusicArea() {
		super();
		lplay = new LPlayer();
		tedit = new LScoreTransEditor();
		flipper = (new MorphFlipper()).construct(lplay, this);
		
		//tc.connect();
	}
	
	public MusicArea construct(Dimension d) {
		super.construct(new String[] { "add pattern"});//, "add morph", "add multi morph"});
		this.setPreferredSize(d);
		
		construct();
		return this;
	}
	
	public void delete(MGComponent mgc) {
		
		this.remove(mgc);
		this.repaint(mgc.bounds());
	}
	
	
	public void initLoad(LPlayer lplay) {
		this.lplay = lplay;
		Component [] comps = this.getComponents();
		for(int i=0; i< comps.length; i++) {
			if(comps[i] instanceof PlayButton) {
				((PlayButton)comps[i]).setPlayer(lplay);
			} else if(comps[i] instanceof LScoreTransEditor) {
				this.remove(comps[i]);
				tedit = new LScoreTransEditor();
				tedit.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 0.33334, 1.0));				
				tedit.fullSize();			
				this.add(tedit, 0);
			}
		}
		//this.pb.setPlayer(lplay);
		if(this.getMouseListeners().length == 0) {
			super.init();
		}
		
	}
	
	private transient PlayButton pb;
	
	private void construct() {
	/*
		pb = (new PlayButton()).construct(lplay);
		pb.setBackground(this.getBackground());
		pb.setBounds(0, 0, 40, 40);
		pb.setLocation(0, 0);
		this.add(pb);
		*/
		// It might be good to put in a Master control area that has global things like stop and
		// start in it.
		//MasterControlArea mca = new MasterControlArea(lplay, this.getPreferredSize());
		//		this.add(mca);

		tedit.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 0.33334, 1.0));
		//tedit.setLocation((int)(this.getPreferredSize().width*0.66), 0);
		//tedit.setBounds((int)(this.getPreferredSize().width*0.66), 0,
		//		(int)(this.getPreferredSize().width*0.66),
		//		this.getPreferredSize().height);
		tedit.fullSize();
		
		this.add(tedit);
		
	}

	protected void popupCommand(String s, int x, int y) {
		if (s.equals("add pattern")) {
			this.add((new PatternComponent()).construct(x, y, this));
			repaint();
		} else if (s.equals("add morph")) {
//			createMorph(x, y);
		} else {
			try {
				Exception e = new Exception("uncaught popup command"
						+ "in musicArea");
				e.fillInStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
/*
	public void interallyCreateMorph(int x1, int y1, int x2, int y2) {
		PatternComponent a, b;
		a = new PatternComponent();
		this.add(a.construct(x1, y1, this));
		
		b = new PatternComponent();
		this.add(b.construct(x2, y2, this));
		
		this.add((new MorphComponent()).construct(a, b,
				this.lplay, this), 0);
		this.repaint();
	}
	*/
	private PopupFactory pf = new PopupFactory();

	private Popup popup;

	private JLabel mplabel = new JLabel("select the component to morph from");

	protected static boolean mcreate = false;
	private static String mctype = null;
    
	private static MGComponent[] toMorph = new MGComponent[2];

	public void createMorph(int x, int y, String type) {
		mcreate = true;
        mctype = type;
		popup = pf.getPopup(this, mplabel, x, y);
		popup.show();
        toMorph[0] = null;
        toMorph[1] = null;
	}

	private MusicGenerator nextCue;

	private transient CueListener cueListener = new CueListener () {
		public void execute(CueEvent e) {		
     //       PO.p("setting music generator " + nextCue.toString());
			lplay.setCurrentMusicGenerator(nextCue);
            //remove the cue listener from the one that is cueing
			e.getMusicGenerator().setCueListener(null);
            
		}
	};
	
	public void mgCue(MusicGenerator mg) {
		//initialise it
		if(mg instanceof BasicMorphMusicGen) {
			((BasicMorphMusicGen)mg).startInit();
		}
		
        if(lplay.getCurrentMusicGenerator() == mg)
            return;
        
        // PO.p("mgCue  " + mg.toString());
		// no need to cue if it is the first one
		if(lplay.getCurrentMusicGenerator() == null){
        //    PO.p("current musicGenerator is null");
			lplay.setCurrentMusicGenerator(mg);
			return;
		}
        
        //initialise the cuelistener in in the current music generator
		lplay.getCurrentMusicGenerator().setCueListener(this.cueListener);
       
		nextCue = mg;
        
   //     PO.p("current musicGenerator :" + lplay.getCurrentMusicGenerator().toString());
   //     PO.p("cueListener within it  :" + lplay.getCurrentMusicGenerator().getCueListener().toString());
   //     PO.p("next cue               :" + nextCue.toString());
        
	}
	
	/**
	 * called from the morph musicgenerator at the moment
	 */
	public void trackerEndCue(MusicGenerator current, MusicGenerator next) {
		this.lplay.setCurrentMusicGenerator(next);
	}

	public void removeCue() {
		if(this.nextCue != null) {
			this.nextCue.setCueListener(null);
			this.nextCue = null;
		}
	}

	/**
	 * replaced by the removeCue method
	 * this method is used when the mg is to be played immediately
	 * @param mgIn
	 *
	public void mgPlay(MusicGenerator mgIn) {	
		
		lplay.setCurrentMusicGenerator(mgIn);
		if(nextCue != null) {
			nextCue.setCueListener(null);
			nextCue = null;
		}	
	}*/

	public void mgcClicked(MGComponent mgc) {
		mgcClicked(mgc, null);
	}
	
	public void mgcClicked(MGComponent mgc, MouseEvent e) {
    //    PO.p("mg clicked");
		if (mcreate) { // if morphing is being created
			if (toMorph[0] == null) {
				toMorph[0] = mgc;
				mplabel.setText("select the component to morph to");
				popup.show();
             //   PO.p("1");
			} else if (toMorph[1] == null) {
				if (toMorph[0] == mgc) {
					// morphing to itself - nothing
             //       PO.p("morphing to itself is not possible");
				} else {
					toMorph[1] = mgc;
					popup.hide();
                    
                  //  PO.p("adding a morph");
					mcreate = false;
                    if(this.mctype.equals("multi")) {
                       
                        this.add((new MorphComponent()).construct(toMorph[0], toMorph[1],
                            this.lplay, this, 
                            (new MultiMorph()).
                            constructMultiMorph(toMorph[0].getMusicGenerator(), 
                                toMorph[1].getMusicGenerator())), 0);
                  //      PO.p("added a multi morphg");
                    }else  {
                        this.add((new MorphComponent()).construct(toMorph[0], toMorph[1],
                            this.lplay, this), 0);
                 //       PO.p("just added a normal morpher");
                    }
					this.repaint();
					toMorph[0] = null;
					toMorph[1] = null;
				}
			}
			return;
		} else {
		//	if(e == null) {
		//		System.out.println("mouseEvent is null, even when a morph isn't" +
		//				"being created.. in music area");
		//	}
			/*set the score for the transformer chain
			String st = "n";
			if(mgc == null)
				st = "mgc";
			else if(mgc.getMusicGenerator() == null) 
				st = "mgc.mg";
			else if(mgc.getMusicGenerator().getLScore() == null) 
				st = "lscore";
			else if(this.tedit == null)
				st = "tedit";
			System.out.println(st);
			*/
			
			clickNoCreate(mgc, e);
		
		}
	}
	
	public void clickNoCreate(MGComponent mgc, MouseEvent e) {
		this.tedit.setLScore(mgc.getMusicGenerator().getLScore());
		
		if(e == null) {
			this.mgCue(mgc.getMusicGenerator());
			return;
		}
		
		if (!e.isControlDown() && e.getButton() != e.BUTTON3) {
			if (!this.lplay.isPlaying())
				this.lplay.go();

			this.mgCue(mgc.getMusicGenerator());
		}
	}
	
/*
	public void mgcClicked(MGComponent mgc) {
		mgcClicked(mgc, null);
	}
	
	public void mgcClicked(MGComponent mgc, MouseEvent e) {
		
	}
	
	public void createMorph(int x, int y) {}
	
	public void mgPlay(MusicGenerator mg) {}
	*/
	
	public LPlayer getLPlayer() {
		return this.lplay;
	}
	
	/**
	 *  the tedit is not null all through this.
	 * @param ma
	 *
	public void loadMusicArea(MusicArea ma) {
		
		this.removeMusicGeneratorComponents();
		Component [] toAdd = ma.getMusicGeneratorComponents();
		for(int i=0; i<toAdd.length; i++) {
			MGComponent mc = null;
			//if(toAdd[i] instanceof MGComponent){
				mc = (MGComponent)toAdd[i];
				mc.initUI();
			//} else if (toAdd[i] instanceof MorphComponent) {
			//	mc = ((MorphComponent)toAdd[i]).getRealMC();
			//	mc.initUI();
			//} 
			
			mc.setMusicArea(this);
			this.add(toAdd[i]);
		}
		
		
		
		this.updateUI();
	//	System.out.println("finished loading music area");
		//repaint();
		Component [] comps = this.getComponents();
		
	}*/
	
	public void addMusicGenerators(Object [] pmgso, Object [] mmgso,
                                   MultiMorph [] mular) {//MorphMusicGenerator [] mmgs, PatternMusicGenerator [] pmgs) {
		/*
	    if(pmgso == null) {
		    pmgso = mmgso;
		    mmgso = new Object [0];
		}*/
		    
	    MorphMusicGenerator [] mmgs = new MorphMusicGenerator [mmgso.length];
	    for(int i=0; i< mmgso.length; i++) {
	        if(mmgso[i] instanceof MorphMusicGenerator)
	            mmgs[i] = (MorphMusicGenerator)mmgso[i];
	    }
	    
	    PatternMusicGenerator [] pmgs = new PatternMusicGenerator [pmgso.length];
	    for(int i=0; i< pmgso.length; i++) {
	        if(pmgso[i] instanceof PatternMusicGenerator) 
	            pmgs[i] = (PatternMusicGenerator)pmgso[i];
	    }
	  		
		// make pattern components
		PatternComponent [] pcomps = new PatternComponent [pmgs.length];
		for(int i=0; i< pcomps.length; i++ ) {
		    pmgs[i].construct();
			pcomps[i] = (new PatternComponent()).construct(this, pmgs[i]);
			this.add(pcomps[i]);
		}
        
        MorphComponent [] mcomps = new MorphComponent [mmgs.length + mular.length];
		
		// make morph components with the pattern components
		for(int i=0; i< mmgs.length; i++) {
			// search through the pattern components and find the ones that are relevant to the morph
			toMorph = findToMorphs(mmgs[i], pmgs, pcomps);
			mcomps[i] = (new MorphComponent());
            this.add(mcomps[i]);
            mcomps[i].construct(toMorph[0], toMorph[1], this.lplay, this, mmgs[i]);	  
		}
      
		// make multi morph components 
        for(int i=0; i< mular.length; i++) {
            // search through the pattern components and find the ones that are relevant to the morph
            toMorph = findToMorphs(mular[i], pmgs, pcomps);
            mcomps[i+mmgs.length] = (new MorphComponent());
            mcomps[i+mmgs.length].construct(toMorph[0], toMorph[1], this.lplay, this, mular[i]);   
            this.add(mcomps[i+mmgs.length]);
        }
        
				
	}
    
    private PatternComponent [] findToMorphs(BasicMorphMusicGen bmmg, 
                                                  PatternMusicGenerator [] pmgs,
                                                  PatternComponent [] pcomps) {
        PatternComponent [] toMorph = new PatternComponent [2]; // 0 = from; 1 = to
        PatternMusicGenerator [] toFrom = bmmg.getToFromPMG(); // what it needs to be
        for(int j=0; j< pmgs.length; j++) {
            if(pmgs[j].equalsPointID(toFrom[0]))
                toMorph[0] = pcomps[j];
            else if(pmgs[j].equalsPointID(toFrom[1]))
                toMorph[1] = pcomps[j];             
        }
        return toMorph;
    }
	
	/**
	 * @return sd[0] is an array of pattern music generators, 
	 * sd[1] is an array of morphMusicGenerators
	 * sd[2] is multimorphs
	 */
	public Object [] getMusicGeneratorsToSave() {
		Object [] sd = new Object [3]; 
        // 0 morph music generator 1 patternMusicGenerators 2 multiMorphs
		
		Component [] comps = this.getComponents();
		int morphg = 0;
		int pattg = 0;
        int multig = 0;
		//count them
		for(int i=0; i<comps.length; i++) {
			if(comps[i] instanceof MorphComponent) {
                if(((MorphComponent)comps[i]).getMusicGenerator() instanceof MultiMorph)
                    multig++;
                else
                    morphg++;
                
            }else if(comps[i] instanceof PatternComponent)
				pattg++;
		}
		
		MorphMusicGenerator [] mmgs = new MorphMusicGenerator [morphg];
		int mmgc = 0;
		PatternMusicGenerator [] pmgs = new PatternMusicGenerator[pattg];
		int pmgc = 0;
        MultiMorph [] mults = new MultiMorph[multig];
        int mugc = 0;
        
		
		for(int i=0; i<comps.length; i++) {
			if(comps[i] instanceof MorphComponent) {
				MusicGenerator tmugen = ((MorphComponent)comps[i]).getMusicGenerator();
                if(tmugen instanceof MultiMorph)
                    mults[mugc++] = (MultiMorph)tmugen;
                else if(tmugen instanceof MorphMusicGenerator)
                    mmgs[mmgc++] = (MorphMusicGenerator)tmugen;
			}else if(comps[i] instanceof PatternComponent) {
				((PatternComponent)comps[i]).updatePointID();
				pmgs[pmgc++] = ((PatternComponent)comps[i]).getPatternMusicGenerator();
				
				//points[pmgc++] = comps[i].getLocation();
			}
		}
		
		sd[0] = pmgs;
        sd[1] = mmgs;
        sd[2] = mults;
        //sd[2] = points;
		
		return sd;
		
	}
		
	public void removeMusicGeneratorComponents() {
		Component [] comps = this.getComponents();
		for(int i=0; i<comps.length; i++) {
			if(comps[i] instanceof MGComponent || comps[i] instanceof MorphComponent) {
				this.remove(comps[i]);
          //      PO.p("removing");
			}
		}
	}
	
	/**
	 * goes through all the components in the music area 
	 * @return returns the ones that are musicgenerators or morphcomponents
	 */
	public MGComponent [] getMusicGeneratorComponents() {
		Component [] comps = this.getComponents();
		Component [] mgComps = new Component [comps.length];
		
		int mgCount = 0;
		for(int i=0; i<comps.length; i++) {
			if(comps[i] instanceof MGComponent || comps[i] instanceof MorphComponent) 
				mgComps[mgCount++] = comps[i];
		}
		
		MGComponent [] toRet = new MGComponent [mgCount];
		for(int i=0; i< toRet.length; i++) {
			toRet [i] = (MGComponent)mgComps[i];
		}
		
		return toRet;
	}
	
	public MorphComponent [] getMultiMorphComps() {
		Component [] c = this.getComponents();
		
		int n =0 ;
		// find number of multi morphs
		for(int i = 0; i<c.length; i++) {
			if(c[i] instanceof MorphComponent) {
				n++;
			}
		}
		
		MorphComponent [] toRet = new MorphComponent [n];
		n = 0;
		for(int i = 0; i<c.length; i++) {
			if(c[i] instanceof MorphComponent) {
				toRet[n++] = ((MorphComponent)c[i]);
			}
		}
		
		return toRet;
	}

	public MusicGenerator [] getMusicGenerators() {
		MGComponent [] mgcArr = this.getMusicGeneratorComponents();
		MusicGenerator [] mgArr = new MusicGenerator [mgcArr.length];
		for(int i=0; i<mgcArr.length; i++) {
			mgArr[i] = mgcArr[i].getMusicGenerator();
		}
		return mgArr;
	}

	public MorphFlipper getMorphFlipper() {
		return this.flipper;
	}
	
    public void dload(Element e) {
        NodeList nl = e.getChildNodes();
        Object[] parr = new Object [Integer.parseInt(e.getAttribute(this.PATT_LEN))];
        Object[] marr = new Object [Integer.parseInt(e.getAttribute(this.MOR_LEN))];
        MultiMorph [] mular = new MultiMorph [Integer.parseInt(e.getAttribute(this.MULTI_LEN))];
        
        int pi =0;
        for(int i=0; i<nl.getLength(); i++) {
            if(nl.item(i).getNodeName().startsWith(PATTG)) {
                parr[pi++] = Domc.lo((Element)nl.item(i), PatternMusicGenerator.class, e.getOwnerDocument());         
            }
        }
        
        int mi = 0;
        for(int i=0; i<nl.getLength(); i++) {
            if(nl.item(i).getNodeName().startsWith(MORG)) {
                marr[mi++] = Domc.lo((Element)nl.item(i), MorphMusicGeneratorRT.class, e.getOwnerDocument());
            }
        }
        
        int mui = 0;
        for(int i=0; i<nl.getLength(); i++) {
            if(nl.item(i).getNodeName().startsWith(this.MULTI)) {
                mular[mui++] = (MultiMorph)Domc.lo((Element)nl.item(i), MultiMorph.class, e.getOwnerDocument());
            }
        }
        
        this.removeMusicGeneratorComponents();
        this.addMusicGenerators(parr, marr, mular);
    }

    private static String PATTG = "pattg";
    private static String PATT_LEN = "plen";
   
    private static String MORG = "morphg";
    private static String MOR_LEN = "mlen";
    
    private static String MULTI = "multimo";
    private static String MULTI_LEN = "multilen";
    
    
    public void dsave(Element e) {
       
        Object [] toSave = this.getMusicGeneratorsToSave();
        // 0 is an array of pattern generators
        for(int i=0; i< ((Object[])toSave[0]).length; i++) {
            e.appendChild(Domc.sa(
                ((Domable)(((Object [])toSave[0])[i])),
                this.PATTG+String.valueOf(i),
                e.getOwnerDocument()));
            
        }
        e.setAttribute(this.PATT_LEN, Integer.toString(((Object[])toSave[0]).length));
                
        //1 is an array of morph music generators
        for(int i=0; i< ((Object[])toSave[1]).length; i++) {
            e.appendChild(Domc.sa(
                ((Domable)(((Object [])toSave[1])[i])),
                this.MORG+String.valueOf(i),
                e.getOwnerDocument()));
        }
      
        e.setAttribute(this.MOR_LEN, Integer.toString(((Object[])toSave[1]).length));
        
        // 2 is an array of multi morph generators
        for(int i=0; i< ((Object[])toSave[2]).length; i++) {
            e.appendChild(Domc.sa(
                ((Domable)(((Object [])toSave[2])[i])),
                this.MULTI+String.valueOf(i),
                e.getOwnerDocument()));
        }
      
        e.setAttribute(this.MULTI_LEN, Integer.toString(((Object[])toSave[2]).length));
        
        
        
    }
	
	
	
	/*
	Vector mgcomps = new Vector(80);
	
	public Component add(Component c) {
		if(c instanceof MGComponent)
			mgcomps.add(c);
		
		return super.add(c);
	}
	
	public void remove(Component c) {
		if(c instanceof MGComponent) 
	}*/
	
	/*
	private void writeObject(ObjectOutputStream oos) throws Exception{
	
		oos.defaultWriteObject();
	
	//	oos.writeObject(this.getMusicGeneratorComponents());
		
		
		
	}
	
	public void readObject(ObjectInputStream ois) throws Exception{
	
		ois.defaultReadObject();
	/*	
		System.out.println("removing musigenerators");
		this.removeMusicGeneratorComponents();
		
		System.out.println("adding mgs");
		MGComponent [] mgRead = (MGComponent[])ois.readObject();
		for(int i=0; i<mgRead.length; i++) {
			this.add(mgRead[i]);
		}*
	}*/
	
	
}

/*
/// this doesn't do anything at the moment
class MasterControlArea extends JPanel implements ActionListener {

	JButton hideShow = new JButton("hide");
	
	public MasterControlArea(LPlayer lplay, Dimension ma) {
		this.setLayout(new GridBagLayout());
		
		hideShow.setPreferredSize(CustomDimensions.butd());
		hideShow.addActionListener(this);
		// unimplemented - tempo should be different for each part
	//	GB.add(this, 0, 0, lplay.getTempoPC().getValueGenerator(), 10, 1);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "hide") {
			hideShow.setText("show");
			hide();
		} else if(e.getActionCommand() == "show") {
			hideShow.setText("hide");
			show();
		}
		
	}
	
	public void hide() {
	}
	
	public void show() {
		
	}
	
}*/



