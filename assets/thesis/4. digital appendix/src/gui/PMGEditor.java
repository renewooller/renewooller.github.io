/*
 * Created on 2/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.ConversionException;
import jm.util.Convert;
import jm.util.Write;
import jmms.MidiNoteListener;
import jmms.NoteEvent;
import lplay.AutoPlayer;
import lplay.LPlayer;
import music.LPart;
import music.LScore;
import music.PatternMusicGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ren.gui.ExampleFileFilter;
import ren.gui.LabelledView;
import ren.gui.ParameterMap;
import ren.gui.components.NumTexFieldDouble;
import ren.gui.components.NumberTextField;
import ren.gui.components.PopupPanel;
import ren.gui.components.RJCheckBox;
import ren.gui.components.VGJComboBox;
import ren.gui.components.ValueListener;
import ren.gui.lookFeel.CustomDimensions;
import ren.io.Domc;
import ren.io.Midi;
import ren.tonal.Scales;
import ren.util.DimUtil;
import ren.util.GB;
import ren.util.Gen;
import ren.util.PO;

/**
 * @author wooller - put a partEditor in each lpart and
 *         put these into a JTabbedPane.
 */
public class PMGEditor extends JPanel implements MidiNoteListener {

    private PatternMusicGenerator pmg;

    private VGJComboBox vsca, vkey;
    
 //   private LScore lscore;

    private LPartEditor lpedit;

    // private PopupFactory popupFactory = new
    // PopupFactory();

    private JPanel patternParams;

    private LabelledView tempoView;

    transient LPlayer lplayer;

    private boolean rec = false;

    private RJCheckBox plcb= new RJCheckBox("play", false, 1);
    private RJCheckBox reccb = new RJCheckBox("rec", false, 2);
    private NumberTextField stsc, ensc;
    
    private JButton recb;

    private Box buttons = new Box(1);

    private int bs, ls;

    private transient LPButton currentButton; 

    private JMenuItem miCut, sicut;

    private Popup cutPopup;

    private CutPanel cutPanel;
    private SPosPanel sppanel;
    
    private KeyListener kl = new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
            int mpt = mapKeyCode(e.getKeyCode());
            if(mpt == -1)
                return;
            if(buttons.getComponentCount() > mpt) {
                ((LPButton)buttons.getComponent(mpt)).flipMute();
            }
        }
    };

    public PMGEditor() {
        super();
    }

    public PMGEditor construct(PatternMusicGenerator pmg, LPlayer lplayer) {

        this.setLayout(new GridBagLayout());
        this.sppanel = new SPosPanel(lplayer, pmg.getAutoPlayer());
        this.setPMG(pmg);
        
        this.cutPanel = new CutPanel(pmg.getLScore());
        
        this.lplayer = lplayer;

        // PO.p("ls size = " + lscore.size());
        lpedit = new LPartEditor(pmg.getLScore().getLPart(0));

        lpedit.getNotePanel()
            .setPreferredSize(
                new Dimension((int) (CustomDimensions.screenSize.width / 2.0),
                        (int) (CustomDimensions.screenSize.height / 3.0)));

        lplayer.getSequencer()
            .addMidiNoteListener(this);
        construct();
        lpedit.addBeatTracker(lplayer.getBeatTracker());
        // this.validate();
        return this;
    }

    private void setPMG(PatternMusicGenerator np) {
        
        plcb.setModel(np.getAutoPlayer().getPlayModel());
       
        reccb.setModel(np.getAutoPlayer().getRecModel());
        
        this.sppanel.setAutoPlay(np.getAutoPlayer());
        
        if(this.pmg == null) {
            pmg = np;
            return;
        } else {
            pmg = np;
        }
        
        this.stsc.setValue(np.getAutoPlayer().getStartScope());
        this.ensc.setValue(np.getAutoPlayer().getEndScope());
       
        
        this.patternParams.remove(tempoView);
        tempoView = (new LabelledView()).construct(this.pmg.getLScore()
            .getTempoParam(), true, true,
            "tempo control for entire pattern, in beats per minute", 0);
        tempoView.getModel()
            .setPreferredSize(
                new Dimension(this.lpedit.getPreferredSize().width - 30,
                        CustomDimensions.hslid().height));
        tempoView.setPreferredSize(new Dimension(
                this.lpedit.getPreferredSize().width - 15,
                (int) (CustomDimensions.hslid().height * 1.5)));
        GB.add(patternParams, 0, 0, tempoView, 10, 2, 0, 0, 0, 0, 1.0, 1.0,
            true, true);
        
        if (pmg.getLScore().size() > 0)
            this.setLPart(pmg.getLScore().getLPart(0));

        this.updateButtons();
        this.updateUI();
        this.repaint();
    }
    
    public void setSelectedLPB(LPButton spb) {
        // PO.p("setting");
        // make it so this just changes one variable
        // (selectedIndex) and then
        // calls the updateButtons() method

        if (currentButton != spb) {
            // PO.p("really");
            this.currentButton.setSelected(false);
            this.currentButton = spb;
            this.currentButton.setSelected(true);
            setLPart(spb.getLPart());
        }
    }

    public void setSelectedLPB(int i) {
        setSelectedLPB((LPButton) (this.buttons.getComponent(i)));
    }

    public LPlayer getLPlayer() {
        return this.lplayer;
    }

    public PatternMusicGenerator getPMG() {
        return this.pmg;
    }

    private int getButtonIndex(Component button) {
        if (button == null) {
            // System.out.println("button is null in get
            // button index");
            return buttons.getComponentCount();
        }

        int i;
        for (i = 0; i < buttons.getComponentCount(); i++) {
            if (button == buttons.getComponent(i))
                break;
        }
        return i;
    }

//    private void setLScore() {
        
        // System.out.println("setting the score " +
        // toSet.size()
        // + " tempo = " +
        // toSet.getTempoParam().getValue());

        // tempo : note: this is strange that the
        // dimensions have to be smaller the second time
        // that LV is created
       
 //   }

    /**
     * Sets the LPart into the part editor used when a
     * different part is selected
     * 
     * @param lpart
     */
    private void setLPart(LPart lpart) {
        // System.out.println("setting the part " +
        // lpart.getPart().getTitle() + " into the part
        // editor");

        lpedit.setLPart(lpart);
        // this.doLayout();
        this.setVisible(false);
        this.setVisible(true);
    }

    private void updateButtons() {
        int bs = buttons.getComponentCount();
        int ls = pmg.getLScore().size();
    //     PO.p("ls = " + ls + "bs = " + bs);
        while (bs > ls) {
            buttons.remove(--bs);
        }
        for (int i = 0; i < ls; i++) {
            pmg.getLScore().getLPart(i)
            .getPart()
            .setIdChannel(i);
                      
            if (i < bs) { // if a button already
                            // exists, just set the part
                ((LPButton) buttons.getComponent(i)).setLPart(pmg.getLScore().getLPart(i));
                
            } else { // add one, if it doesn't exist
                // PO.p("adding button");

                LPart templp = pmg.getLScore().getLPart(i);

                currentButton = partButton(templp);
                buttons.add(currentButton);

                // currentButton.setPreferredSize(new
                // Dimension(120, 20));
                // currentButton.insets().left = 0;
                // currentButton.insets().right = 0;
                bs++;
            }
            
            
        }
        // this.doLayout();
      //  buttons.repaint();
        buttons.setVisible(false);
       buttons.setVisible(true);

    }

    private void construct() {
        if (pmg.getLScore().size() > 0) {
            setLPart(pmg.getLScore().getLPart(0));
        }
        updateButtons();

        // the save/load
        JMenuBar jmb = new JMenuBar();
        jmb.setAlignmentX(Component.LEFT_ALIGNMENT);
        JMenu jmFile = new JMenu("file");
        JMenuItem miSave = new JMenuItem("save pattern setup to file");
        JMenuItem miLoad = new JMenuItem("load pattern setup file");
        JMenuItem miImport = new JMenuItem("import MIDI file");
        JMenuItem miExport = new JMenuItem("export MIDI file");

        jmFile.add(miSave);
        jmFile.add(miLoad);
        jmFile.add(miImport);
        jmFile.add(miExport);

        jmb.add(jmFile);
        this.add(jmb);

        // actions
        // save
        miSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });
        // load
        miLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });
        // import
        miImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importFile();
            }
        });
        // export
        miExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportFile();
            }
        });

        // preview this pattern
        JMenuItem preview = new JMenuItem("preview");
        preview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lplayer.setCurrentMusicGenerator(pmg);
                if (!lplayer.isPlaying())
                    lplayer.go();
            }
        });

        miCut = new JMenuItem("crop all parts");
        miCut.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                crop(e);
            }
        });
        
        sicut = new JMenuItem("crop current part");
        sicut.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                cropSingle(e);
            }
        });


        JMenuItem mergeChan = new JMenuItem("merge same channels");
        mergeChan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mergeSameChan();
            }
        });

        JMenuItem splitChan = new JMenuItem("split into multiple channels");
        splitChan.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               splitSelectedPart();
           }
        });
        
        
        JMenuItem clearAuto = new JMenuItem("clear autoplay");
        clearAuto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pmg.getAutoPlayer().clearRecorded();
            }
        });
        
        JMenuItem setPos = new JMenuItem("set pos");
        setPos.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setPos(e);
            }
        });
        
        JMenu actions = new JMenu("actions");
        actions.add(preview);
        actions.add(miCut);
        actions.add(sicut);
        actions.add(mergeChan);
        actions.add(splitChan);
        actions.add(clearAuto);
        actions.add(setPos);
        jmb.add(actions);

        constructPitchFunctions(jmb);
        
        constructTemporalFunctions(jmb);
        
        this.constructGenerativeFunctions(jmb);
        
        recb = new JButton("rec");
        recb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rec = !rec;
                if (rec)
                    recb.setText("stop rec");
                else
                    recb.setText("record");
            }
        });
        
        int v = 0;
        GB.add(this, 0, v++, recb, 1, 2);

        // add remove button box
        JButton addButton = new JButton("add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                add();
            }
        });
        JButton remButton = new JButton("remove");
        remButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                remove();
            }
        });
        JButton upb = new JButton("up");
        upb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shiftUp();
            }
        });
        JButton dnb = new JButton("dn");
        dnb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shiftDown();
            }
        });

        JPanel addRem = new JPanel();
        addRem.setLayout(new GridBagLayout());
        // addRem.add(addButton);
        // addRem.add(remButton);
        GB.add(addRem, 0, 0, addButton, 1, 1);
        GB.add(addRem, 0, 1, remButton, 1, 1);
        GB.add(addRem, 1, 0, upb, 1, 1);
        GB.add(addRem, 1, 1, dnb, 1, 1);

        addRem.setBorder(BorderFactory.createEtchedBorder());
        GB.add(this, 0, v++, addRem, 1, 2); v++;

        //TODO add a global key change
        initGlobalTonalViews();
        Box tonb = new Box(0);
        tonb.add(vkey);
        tonb.add(vsca);
        
        GB.add(this, 0, v++, tonb, 2, 1);
        
        JPanel apan = new JPanel(new GridBagLayout());

        JButton init = new JButton("init");
        init.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initAutoplay();
            }
        });
        stsc = new NumberTextField(0, 9999, pmg.getAutoPlayer().getStartScope());
        stsc.setColumns(4);
        stsc.addValueListener(new ValueListener() {
            public void valueGeneratorUpdate(int nv) {
                pmg.getAutoPlayer().setStartScope(nv);
            }
        });
        ensc = new NumberTextField(0, 9999, pmg.getAutoPlayer().getEndScope());
        ensc.setColumns(4);
        ensc.addValueListener(new ValueListener() {
            public void valueGeneratorUpdate(int nv) {
                pmg.getAutoPlayer().setEndScope(nv);
            }
        });
        
        GB.add(apan, 0, 0, plcb, 1, 1);
        GB.add(apan, 1, 0, reccb, 1, 1);
        GB.add(apan, 2, 0, init, 1, 1);
        GB.add(apan, 3, 0, stsc, 1, 1);
        GB.add(apan, 4, 0, ensc, 1, 1);
                
        v++;
        GB.add(this, 0, v++, apan, 1, 1);
        
        // parts button box
        buttons.setBorder(BorderFactory.createEtchedBorder());
        buttons.setPreferredSize(new Dimension(130,
                this.lpedit.getPreferredSize().height));
        DimUtil.insets(buttons, 0, 0, 0, 0);
        GB.add(this, 0, v++, buttons, 1, 10);

        // parameters
        patternParams = new JPanel(new GridBagLayout());
        patternParams.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED),
            "all parts"));

        // tempo
        tempoView = (new LabelledView()).construct(this.pmg.getLScore()
            .getTempoParam(), true, true,
            "tempo control for entire pattern, in beats per minute", 0);
        tempoView.getModel()
            .setPreferredSize(
                new Dimension(this.lpedit.getPreferredSize().width - 16,
                        CustomDimensions.hslid().height));
        tempoView.setPreferredSize(new Dimension(
                this.lpedit.getPreferredSize().width - 8,
                (int) (CustomDimensions.hslid().height * 1.5)));

        GB.add(patternParams, 0, 0, tempoView, 10, 2, 0, 0, 0, 0, 1.0, 1.0,
            true, true);

        GB.add(this, 1, 0, patternParams, 10, 2);

        // the part editor
        lpedit.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED),
            "current part"));
        GB.add(this, 1, 2, lpedit, 10, 16);
        // edit.getNotePanel().setCompo
    }

    private void initGlobalTonalViews() {
    	vkey = new VGJComboBox(new String [] {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"});
        vkey.setName("key");
        
        LabelledView lvk = (new LabelledView()).construct(vkey, "set the key signature of the part", 0);
        
        vkey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                pmg.getLScore().setKey(vkey.getSelectedIndex());
                repaint();
            }
        });
               
        vsca = new VGJComboBox(Scales.getInstance().getScales());
        vsca.setName("scale");
       
        LabelledView lvs = (new LabelledView()).construct(vsca, "set the scale/tonality of this part", 0);
        
        vsca.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pmg.getLScore().setScale(vsca.getSelectedIndex());
                repaint();
            }
        });  
    }
    
	private void constructPitchFunctions(JMenuBar jmb) {
		JMenu pifunc = new JMenu("pitch");
        
		JMenuItem octDown = new JMenuItem("oct down");
        octDown.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		oct(-1);
        	}
        });
        pifunc.add(octDown);
        
        JMenuItem octup = new JMenuItem("oct up");
        octup.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		oct(1);
        	}
        });
        pifunc.add(octup);
      //  jmb.add(pifunc);
        
        JMenuItem sixUp = new JMenuItem("six up");
        sixUp.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		shift(6);
        	}
        });
        pifunc.add(sixUp);
        
        
        jmb.add(pifunc);
        
        
       
	}
	 
	private void constructTemporalFunctions(JMenuBar jmb) {
		JMenu tf = new JMenu("time");
		JMenuItem phs = new JMenuItem("phase shift");
		phs.setContentAreaFilled(false);
		
		phs.setLayout(new OverlayLayout(phs));
				
		NumTexFieldDouble am = (new NumTexFieldDouble()).
			constructThin((new ParameterMap().
					construct(0, 128,-8.0, 8.0, 0, "phase")));
		
		phs.add(am);//, BorderLayout.NORTH);
		
		phs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mod.phaseShiftRT(pmg.getLScore(),
						((NumTexFieldDouble)((JMenuItem)e.
						getSource()).getComponent(0)).getValue());
			
			}
		});
		
		tf.add(phs);
		jmb.add(tf);
	}

	private void constructGenerativeFunctions(JMenuBar jmb) {
		JMenu gf = new JMenu("gen");
		JMenuItem ran = new JMenuItem("random notes");
		ran.setContentAreaFilled(false);
		
		ran.setLayout(new OverlayLayout(ran));
		
		NumTexFieldDouble am = (new NumTexFieldDouble()).
		constructThin((new ParameterMap().
				construct(0, 5000, 0, "gen n")));
		
		ran.add(am);
		
		ran.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gen((int)((NumTexFieldDouble)((JMenuItem)e.
						getSource()).getComponent(0)).getValue());
			}
		});
		
		gf.add(ran);
		
		
		JMenuItem adr = new JMenuItem("add ran notes");
		adr.setContentAreaFilled(false);
		
		adr.setLayout(new OverlayLayout(adr));
		
		NumTexFieldDouble am2 = (new NumTexFieldDouble()).
		constructThin((new ParameterMap().
				construct(0, 5000, 0, "add n")));
		
		adr.add(am2);
		
		adr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addGen((int)((NumTexFieldDouble)((JMenuItem)e.
						getSource()).getComponent(0)).getValue());
			}
		});
		
		gf.add(adr);
		
		jmb.add(gf);
	
		
	}
	
	protected void gen(int num) {
		Gen.npartMono(this.pmg.getLScore().getPart(this.getButtonIndex(this.currentButton)),
				num, 0, 
				this.pmg.getLScore().getLPart(
						this.getButtonIndex(this.currentButton)).getScope().getValue(),
						60, 65, 0.25);
		
		this.setLPart(this.pmg.getLScore().getLPart(
				this.getButtonIndex(this.currentButton)));
	}
	
	protected void addGen(int num) {
		Gen.addnpart(this.pmg.getLScore().getPart(this.getButtonIndex(this.currentButton)),
				num, 0, 
				this.pmg.getLScore().getLPart(
						this.getButtonIndex(this.currentButton)).getScope().getValue(),
						40, 80);
		this.setLPart(this.pmg.getLScore().getLPart(
				this.getButtonIndex(this.currentButton)));
	}
	

	protected void oct(int i) {
		Mod.octRT(this.pmg.getLScore().getPart(this.getButtonIndex(this.currentButton)), i);
	}
	
	protected void shift(int i) {
		Mod.transposeRT(
				this.pmg.getLScore().getPart(
						this.getButtonIndex(this.currentButton)), 
						i);
		
	}

	private void initAutoplay() {
        PO.p("pmg: init autoplay 1");
        pmg.getAutoPlayer().initForStartPlay();
        PO.p("reseting tick in autoplay 2");
        lplayer.resetTick();
        
        PO.p("done autoplay 3");
     //   PO.p("initing autoplay recmodel = " + 
    //        pmg.getAutoPlayer().getRecModel().isSelected() +
       //     " play model = " + pmg.getAutoPlayer().getPlayModel().isSelected());
        if(pmg.getAutoPlayer().getRecModel().isSelected()) {
            for(int i=0; i< pmg.getLScore().size(); i++) {
                //PO.p("recording initial for "+ i);
                pmg.getAutoPlayer().record(pmg.getLScore().getLPart(i), 
                    LPart.MUTE, pmg.getLScore().getLPart(i).isMute());
            }
        }
        
        
    }
    
    private void crop(MouseEvent e) {
        if (PopupFactory.getSharedInstance() == null) {
            PopupFactory.setSharedInstance(new PopupFactory());
        }

        Point p = e.getPoint();
        cutPopup = PopupFactory.getSharedInstance()
            .getPopup(this, this.cutPanel, p.x, p.y + 50);

        cutPanel.setPopup(cutPopup);
        cutPanel.setPart(-1);
        cutPopup.show();

    }
    
    protected void cropSingle(MouseEvent e) {
    	
    	if (PopupFactory.getSharedInstance() == null) {
            PopupFactory.setSharedInstance(new PopupFactory());
        }

        Point p = e.getPoint();
        cutPopup = PopupFactory.getSharedInstance()
            .getPopup(this, this.cutPanel, p.x, p.y + 50);

        cutPanel.setPopup(cutPopup);
        cutPanel.setPart(this.getButtonIndex(this.currentButton));
        cutPopup.show();
	}
    
    private void setPos(MouseEvent e) {
        if (PopupFactory.getSharedInstance() == null) {
            PopupFactory.setSharedInstance(new PopupFactory());
        }

        Point p = e.getPoint();
        cutPopup = PopupFactory.getSharedInstance()
            .getPopup(this, this.sppanel, p.x, p.y + 50);

        //this.pmg.getAutoPlayer().updateToPos()
        sppanel.setPopup(cutPopup);
        cutPopup.show();
    }

    protected void shiftDown() {
        // counter intuitively, the pmg.getLScore() counts the
        // other way
        int bi = this.getButtonIndex(this.currentButton);
        if (pmg.getLScore().shiftUp(bi)) {
            this.setSelectedLPB(bi + 1);
            this.updateButtons();
        }

    }

    protected void shiftUp() {
        int bi = this.getButtonIndex(this.currentButton);
        if (pmg.getLScore().shiftDown(bi)) {
            this.setSelectedLPB(bi - 1);
            this.updateButtons();
        }
    }

    // methods called by actionlisteners
    private void add() {
        LPart tlp = (new LPart()).construct(new Part("part one", 1,
                1 + pmg.getLScore().size(), new Phrase(new Note(60, 1.0))));
        this.pmg.getLScore().add(tlp, this.getButtonIndex(currentButton) + 1);
        this.setLPart(tlp);

        this.updateButtons();
        this.pmg.updateDependants();

    }

    private void remove() {
        this.pmg.getLScore().remove(this.getButtonIndex(currentButton));
        this.updateButtons();
        this.pmg.updateDependants();
    }

    private void mergeSameChan() {
        this.pmg.getLScore().mergeSameChan();
        this.updateButtons();
    }
    
    private void splitSelectedPart() {
        this.pmg.getLScore().splitLPart(this.getButtonIndex(currentButton));
        this.updateButtons();
    }
    

    private int mapKeyCode(int in) {
        if(in == 192) {
            return 0;
        } else if(in > 48 && in < 58) {
            return in - 48;
        } else if(in == 48)
            return 10;
        else if(in == 81 ) {
            return 11;
        } else if(in == 87)
            return 12;
        else if(in == 69)
            return 13;
        else if(in == 82)
            return 14;
        else if(in == 84)
            return 15;
        else if(in == 89)
            return 16;
        else 
            
            PO.p(" mkc = "  + in);
            
            return -1;
        
    }
    
    private void saveToFile() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("pat");
        filter.setDescription("lemu 2 pattern configuration files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            try {
                String fname = chooser.getSelectedFile()
                    .getAbsolutePath();
                // fname.
                if (!fname.endsWith(".pat")) {
                    fname = fname.concat(".pat");
                }

                Domc.init();
                
                Document doc = Domc.makeDoc();
                doc.appendChild(Domc.sa(pmg, "pmg", doc));
             //   doc.setTextContent(Domc.VERSION);
                
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer trans = tf.newTransformer();

                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(fname));

                DOMSource source = new DOMSource(doc);
                StreamResult stream = new StreamResult(out);
                trans.transform(source, stream);

                /*
                 * BufferedWriter writer = new
                 * BufferedWriter(new
                 * FileWriter(fname)); String str =
                 * Convert.scoreToXMLString(this.pmg.getLScore().getJMScore());
                 * writer.write(str); writer.close();
                 */
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFile() {
        JFileChooser chooser =  new JFileChooser(System.getProperty("user.dir"));
        
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("pat");
        filter.setDescription("lemu 2 pattern configuration files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            try {
                String fname = chooser.getSelectedFile()
                    .getAbsolutePath();

                if (!fname.endsWith("pat"))
                    fname = fname + ".pat";

                BufferedReader reader = new BufferedReader(
                        new FileReader(fname));

                String line = reader.readLine();
                reader.close();
                if(line.endsWith("Score>"))
                    oldLoad(line);
                    
                else {
                    DocumentBuilderFactory dbf = 
                        DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    
                    Document doc = db.parse(new File(fname));
                    Element pmge = (Element)doc.getFirstChild();
              //      Domc.setID(pmge);
                    this.pmg.dload(pmge);
                    
                    // TODO
                    
      //              PO.p("printing the pmg just after loading"  + pmg.toString());
                    
                    this.setPMG(pmg);
                    
                }
                
                

                this.updateButtons();

                /*
                 * idea: store a model in param
                 * component and set the value generator
                 * to be transient
                 */

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void oldLoad(String line) throws ConversionException {
        
        Score score = Convert.xmlStringToScore(line);

        LScore ls = new LScore();
        ls.add(score);

        this.pmg.setLScore(ls);
        this.setPMG(pmg);
        // this.setLScore();

        
    }
    
    private void exportFile() {
        JFileChooser chooser = new JFileChooser();
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mid");
        filter.setDescription("MIDI files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            try {
                String fname = chooser.getSelectedFile()
                    .getAbsolutePath();
                // fname.
                if (!fname.endsWith(".mid")) {
                    fname = fname.concat(".mid");
                }

                // BufferedWriter writer = new
                // BufferedWriter(new
                // FileWriter(fname));
                // System.out.println(this.pmg.getLScore().getJMScore().toString());
                // fasle means the scope isn't being ignored
                Write.midi(this.pmg.getLScore().getJMScore(false), fname);

                // writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void importFile() {
        JFileChooser chooser = new JFileChooser();
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mid");
        filter.setDescription("midi files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            try {
                String fname = chooser.getSelectedFile()
                    .getAbsolutePath();
                System.out.println(fname);
                BufferedReader reader = new BufferedReader(
                        new FileReader(fname));
                /*
                 * StringBuffer sb = new StringBuffer();
                 * int ch = reader.read(); while (ch !=
                 * -1) { sb.append((char)ch); ch =
                 * reader.read(); }
                 * 
                 * 
                 * System.out.println(sb.toString());
                 */
                Score ts = new Score();
                Midi.getInstance().read(ts, fname);
              //  Read.midi(ts, fname); // in.toString();
                // System.out.println(ts.toString());

                // PO.p("read in = " + ts.toString());

                Mod.convertToRealTime(ts, 16.0);

                Mod.quickSort(ts);

                // Mod.channels1to16(ts);

                System.out.println(ts.toString());

                pmg.getLScore().setJMScore(ts);

                reader.close();

                /*
                 * idea: store a model in param
                 * component and set the value generator
                 * to be transient
                 */

            } catch (Exception e) {
                e.printStackTrace();
            }
            updateButtons();
            // if(buttons.getComponentCount() > 0 &&
            // buttons.getComponent(0) != null)
            // ((JButton)buttons.getComponent(0)).doClick();
        }
    }

    // action listener for renaming the parts
    /*
     * private ActionListener pma = new ActionListener() {
     * public void actionPerformed(ActionEvent e) { if
     * (e.getActionCommand().equals("rename")) {
     * JTextField tf = new JTextField();
     * tf.setText(currentButton.getText());
     * tf.selectAll(); tf.addActionListener(new
     * ActionListener() { public void
     * actionPerformed(ActionEvent e) {
     * lscore.getPart(getButtonIndex(currentButton)).setTitle(
     * ((JTextField) e.getSource()).getText());
     * 
     * currentButton.setText(((JTextField)
     * e.getSource()) .getText()); pop.hide(); } }); Box
     * b = new Box(0); b.add(new JLabel("set part title
     * to: ")); b.add(tf); pop =
     * popupFactory.getPopup(currentButton, b, (int)
     * currentButton.getLocationOnScreen().getX(), (int)
     * currentButton.getLocationOnScreen().getY());
     * pop.show(); tf.requestFocus(); // pop.hide(); } } };
     */

    // button that is having the popup menu on it
    /**
     * al is the actionlistener for updating the display
     * after switching between parts
     */
    private LPButton partButton(LPart p) { // ActionListener
                                            // al) {
        LPButton b = new LPButton(p, kl);
        b.addPGME(this);
        return b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jmms.MidiNoteListener#noteRecieved(jm.music.data.Phrase)
     */
    public void noteRecieved(NoteEvent e) {
        // PO.p("recieveing note");
        if (rec) {
            pmg.getLScore().record(e);
            this.repaint();
        }
    }
}

class CutPanel extends JPanel {
    private NumberTextField stcut = new NumberTextField(0, 1024, 0);

    private NumberTextField encut = new NumberTextField(0, 1024, 0);

    private JButton cutb = new JButton("crop");

    private LScore toCut;

    private Popup cutp;

    private int parti = -1;
    
    
    public CutPanel(LScore toCut) {
        super();
        this.toCut = toCut;
        cutb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (stcut.getValue() == encut.getValue()
                        || stcut.getValue() > encut.getValue())
                    return;
                crop();

            }
        });
        this.add(stcut);
        this.add(encut);
        this.add(cutb);
    }

    public void setPart(int pi) {
    	this.parti = pi;
    }
    
    public void setPopup(Popup cutPopup) {
        this.cutp = cutPopup;
    }

    private void crop() {
    	
    	if(parti != -1) {
    		Mod.crop(toCut.getPart(parti), stcut.getValue(), encut.getValue());
    	} else {        
    		for (int i = 0; i < toCut.size(); i++) {
				Mod.crop(toCut.getPart(i), stcut.getValue(), encut.getValue());
			}
    	}
        cutp.hide();
    }

}

class SPosPanel extends PopupPanel {
    
    private LPlayer lp;
    private AutoPlayer ap;
    
    private NumberTextField pos = new NumberTextField(0, 1024, 0);

    private JButton set = new JButton("set");

    
    public SPosPanel(LPlayer lp, AutoPlayer ap) {
        this.lp = lp;
        this.ap = ap;
     
        set.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setPos();
            }
        });
        this.add(pos);
        this.add(set);
    }
    
    public void setAutoPlay(AutoPlayer ap) {
        PO.p("size = " + this.ap.size());
        this.ap = ap;
    }

    private void setPos() {
        lp.setBeatAt(pos.getValue()*1.0);
        ap.updateToPos(pos.getValue()*1.0);
        popup.hide();
    }
    
    
}


