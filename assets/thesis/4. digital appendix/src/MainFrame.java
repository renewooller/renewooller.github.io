/**
 * 
 * Lemu 2 takes place in an amorpheus mass of heaving
 * basslines and wicked beats. realtime control occurs
 * from transiting from one node to another. nodes can
 * be either patterns or manipulations.
 */

// import JSX.ObjIn;
// import JSX.ObjOut;
import gui.musicArea.TableMusicArea;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.OverlayLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jm.music.data.Score;
import jm.util.Write;
import jmms.TickEvent;
import jmms.TickListener;
import jmms.processor.gui.MidiIOView;
import lplay.LPlayer;
import music.MultiMorph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ren.gui.ExampleFileFilter;
import ren.gui.ParameterMap;
import ren.gui.components.LJSlider;
import ren.gui.components.NumTexFieldDouble;
import ren.gui.components.RJFrame;
import ren.io.Domc;
import ren.util.DimUtil;
import ren.util.PO;

// import com.thoughtworks.xstream.XStream;

public class MainFrame extends RJFrame {

    JPanel main;

    TableMusicArea ma;// = new MusicArea();
    
    Lemu2 l2;

    JCheckBoxMenuItem rec;

    JMenuItem mmio, smio, lmio, go, stop;

    protected JMenu actions = new JMenu("actions");

    protected JMenu sysTweak = new JMenu("system tweaks");

    protected JMenu midiIO = new JMenu("MIDI IO");

    protected JMenu view = new JMenu("View");
    
    private MidiIOView miov;

    private JFrame miovJF;

    public MainFrame(Lemu2 l2) {

        super(true);
        this.l2 = l2;
        main = new JPanel(new GridBagLayout());
        this.getContentPane()
            .add(main);

        buildArea();

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	ma.getLPlayer().stop();
            	
                PO.p("saving working version....");
            	saveWorking();
            	PO.p("disablin the tablin");
            	ma.enableTableMode(false);
            	PO.p("slayin the playin");
            	ma.getLPlayer().close();
            	PO.p("exitin the ..mm");
                System.exit(0);
            }
        });

        this.pack();
        this.setVisible(true);

        this.miov = new MidiIOView();
        miov.construct(this.ma.getLPlayer()
            .getMProc(), this.ma.getLPlayer()
            .getMOM(), this.ma.getLPlayer()
            .getACM());

        miovJF = new JFrame();
        miovJF.getContentPane()
            .add(miov);
        miovJF.pack();

        doStuff();
    }

    protected void saveWorking() {
		save(System.getProperty("user.dir") + "\\workingVersion.lem");
	}

	public void doStuff() {
		
		load("workingVersion.lem");
    	
		//load("trat.lem");
    	//load("concMorphwith2.lem");
        // ma.interallyCreateMorph(50, 50, 200, 200);

        // save("lemTest.lem");
        // load("lemTest.lem");

        // save("lemTest.lem");
        // load("test4.lem");
   //     load("cam.lem");
        /*
         * save("lemTest.lem"); load("lemTest.lem");
         */
        // load("markovTest2.lem");
        // load("roomTest2.lem");
    }

    public void buildArea() {
        ma = (TableMusicArea)((new TableMusicArea()).construct(DimUtil.scale(
            DimUtil.getScreenSize(), 0.8, 0.8)));

        main.add(ma);

        // add the actions menu to the menubar, along
        // with the play/stop function
        go = new JMenuItem("go");
        go.addActionListener(this);
        stop = new JMenuItem("stop");
        stop.addActionListener(this);
        JCheckBoxMenuItem gm = new JCheckBoxMenuItem("camera in");
        gm.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ma.enableCameraInput(((JCheckBoxMenuItem)e.getSource()).getState());
        	}
        	
        });
        
        JCheckBoxMenuItem tgm = new JCheckBoxMenuItem("table mode");
        tgm.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ma.enableTableMode(((JCheckBoxMenuItem)e.getSource()).getState());
        	}
        });
        
        JCheckBoxMenuItem fli = new JCheckBoxMenuItem("flip mode");
        fli.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ma.getMorphFlipper().setEnabled(((JCheckBoxMenuItem)e.getSource()).getState());
        	}
        });
        
        actions.add(go);
        actions.add(stop);
        actions.add(gm);
        actions.add(tgm);
        actions.add(fli);
        
        this.menuBar.add(actions);

        // add the clear menu item
        JMenuItem clear = new JMenuItem("new/clear");
        clear.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ma.removeMusicGeneratorComponents();
        	}
        });
        file.add(clear);
        
        // add the record checkbox
        rec = new JCheckBoxMenuItem("record MIDI");
        rec.addActionListener(this);
        file.add(rec);
                        
        addVarLoad();
        
        LJSlider latency = new LJSlider(1, 0, 4000, 100);
        latency.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ma.getLPlayer()
                    .setLatency(
                        ((JSlider) e.getSource()).getValue());
            }
        });
        sysTweak.add(latency);
        this.menuBar.add(sysTweak);

        // make the gui for making midi connections
        mmio = new JMenuItem("make midiIO");
        mmio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                miovJF.setVisible(true);
            }
        });
        midiIO.add(mmio);

        // save the connections
        smio = new JMenuItem("save midiIO");
        smio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveMIO();
            }
        });
        midiIO.add(smio);
        lmio = new JMenuItem("load midiIO");
        lmio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadMIO();
            }
        });
        midiIO.add(lmio);

        this.menuBar.add(midiIO);
        
        JMenuItem tview = new JMenuItem("timer");
        tview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTimer();
            }
        });
        this.view.add(tview);
        this.menuBar.add(view);

    }

    private void addVarLoad() {
    	JMenuItem valo = new JMenuItem("load morph-var");
		valo.setContentAreaFilled(false);
		
		valo.setLayout(new OverlayLayout(valo));
				
		NumTexFieldDouble am = (new NumTexFieldDouble()).
			constructThin((new ParameterMap().
					construct(0, 5, 0, "var")));
		
		valo.add(am);//, BorderLayout.NORTH);
		
		valo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(ma.isTableMode()) {
					MultiMorph mov = loadMultiMorph();
					if(mov != null)
						ma.loadMorphVariations(mov, 
								(int)((NumTexFieldDouble)((JMenuItem)e.
								getSource()).getComponent(0)).getValue());
				}
			
			}
		});
		
		file.add(valo);
		
	}

	protected MultiMorph loadMultiMorph() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mor");
        filter.setDescription("morph configuration files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        String fname = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fname = chooser.getSelectedFile()
                .getAbsolutePath();
        } else 
        	return null;
		
		Domc.init();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    
        try{
            if (!fname.endsWith(".mor")) fname = fname.concat(".mor");
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(fname));
            
            // create empty multi morph
            MultiMorph mm = (new MultiMorph()).constructMultiMorph(null, null);
            // load the essentials
            mm.loadState(doc);
            return mm;
            
        }catch(Exception e) {
            e.printStackTrace();
        }
		
		// TODO Auto-generated method stub
		return null;
	}

	private void displayTimer() {
        TimerFrame tf = new TimerFrame().construct(this.ma.getLPlayer());
        tf.setVisible(true);
    }
    
	public TableMusicArea getTableMusicArea() {
		return this.ma;
	}
	
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        if (e.getSource() == rec) {
            if (rec.getState()) {
                ma.getLPlayer()
                    .startRecordingMidi();
            } else {
                saveScore(ma.getLPlayer()
                    .stopRecordingMidi());
            }
        } else if (e.getSource() == go) {

            ma.getLPlayer()
                .go();
        } else if (e.getSource() == stop) {
            ma.getLPlayer()
                .stop();
        }
    }

    protected void saveMIO() {
        JFileChooser chooser = new JFileChooser();
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mio");
        filter.setDescription("lemu 2 midi io files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

       //    StringBuffer saveXml = new StringBuffer();

            saveMIO(chooser.getSelectedFile()
                .getAbsolutePath());
        }
    }

    protected void saveMIO(String path) {
        try {
            PrintWriter pw = new PrintWriter(
                    new FileWriter(path));

            pw.print(saveMIOString());
            pw.close();

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private String saveMIOString() {
        StringBuffer sb = new StringBuffer(
                this.ma.getLPlayer()
                    .getMProc()
                    .saveString());
        sb.append(this.ma.getLPlayer()
            .getMOM()
            .saveString());
        return sb.toString();
    }

    protected void loadMIO() {
        JFileChooser chooser = new JFileChooser();
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mio");
        filter.setDescription("lemu 2 midi io files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadMIO(chooser.getSelectedFile()
                .getAbsolutePath());

        }
    }

    protected void loadMIO(String path) {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(path));

            String str = br.readLine();

            loadMIOString(str);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadMIOString(String s) {
        this.ma.getLPlayer()
            .getACM()
            .resetUnfinishedPaths();

        this.ma.getLPlayer()
            .getMProc()
            .loadString(s);

        this.ma.getLPlayer()
            .getMOM()
            .loadString(s);

        this.ma.getLPlayer()
            .getACM()
            .loadUninishedPaths();

        miov.update();
    }

    protected void saveScore(Score s) {
   //     Mod.convertFromRealTime(s);

    //    View.notate(s);
    ////   View.show(s);
    //   View.sketch(s);
    //   View.print(s);
        
        JFileChooser chooser = new JFileChooser();
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mid");
        filter.setDescription("MIDI files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Write.midi(s, chooser.getSelectedFile()
                .getAbsolutePath());
        }
        //*/
    }

    /**
     * obtains the file name and location to save at and
     * then calls save(String filename)
     */
    protected void save() {

        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("lem");
        filter.setDescription("lemu 2 files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            save(chooser.getSelectedFile()
                .getAbsolutePath());
        }
    }

    protected void save(String fname) {
        Domc.init();
        if (!fname.endsWith(".lem")) {
            fname = fname.concat(".lem");
        }
        
        Document doc = Domc.makeDoc();
     //   doc.setPrefix(Domc.VERSION);
        Element fich = doc.createElement("firstChild");
        doc.appendChild(fich);
        
        fich.appendChild(Domc.sa(ma, "musicArea", doc));
        
        
        Element mio = doc.createElement("mio");
        mio.setAttribute("data", this.saveMIOString());
        fich.appendChild(mio);
        
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            
            BufferedOutputStream out = new 
            BufferedOutputStream(new FileOutputStream(fname));
        
            DOMSource source = new DOMSource(doc);
            StreamResult stream = new StreamResult(out);
            trans.transform(source, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    protected void load() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("lem");
        filter.setDescription("lemu 2 pattern configuration files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            load(chooser.getSelectedFile()
                .getAbsolutePath());

        }
    }

    
    //* idea, override the dbf and get rid of error in set attribute.
    //    override db so that the 
    
    protected void load(String fname) {
        Domc.init();
      
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setExpandEntityReferences(false);
        
        
       // PO.p(")
        
        //DocumentBuilderFactory.
      //  javax.xml.parsers.
      
        try{
            if (!fname.endsWith(".lem")) fname = fname.concat(".lem");
            DocumentBuilder db = dbf.newDocumentBuilder();
        //    db.
       //     db.getDOMImplementation().g
         //  db.
  //          db.setEntityResolver(null);
            if(!(new File(fname)).exists()) {
            	System.out.println("file " + fname + " doesn't exist");
            	return;
            }
            
            Document doc = db.parse(new File(fname));
            
            Element fich = (Element)doc.getFirstChild();
            
            Element mae = null;
            if(fich.getTagName().equalsIgnoreCase("firstChild")) {
            	
            	 mae = Domc.find(fich, "musicArea");
                
            	
            } else { 
            	 mae = fich;
            	
            }
            
            
            
     //       Domc.setID(mae);
            ma.dload(mae);
            
            main.updateUI();
            
            Element mio = Domc.find(fich, "mio");
            if(mio != null)
            	this.loadMIOString(mio.getAttribute("data"));
            
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
}

class TimerFrame extends JFrame implements TickListener {
       
    private Dimension cosize = new Dimension(80, 40);
    private Font cofont = new Font("Arial", Font.PLAIN, 40);
    private Font lafont = new Font("Arial", Font.BOLD, 40);
    
    private JLabel [] labs = new JLabel[14];
    private JLabel [] cola = new JLabel[labs.length];
    private int [] mods = new int[labs.length];
    
    public TimerFrame() {
        super();
        JPanel lpan = new JPanel(new GridLayout(labs.length, 2));
        for(int i=0; i< labs.length; i++) {
            int ri = i;
            if(ri>= 1)
                ri = i+1;
            mods[i] = (int)Math.pow(2.0, ri);
            
            cola[i] = new JLabel(String.valueOf(mods[i]));
            cola[i].setFont(cofont);
            cola[i].setPreferredSize(cosize);
            lpan.add(cola[i]);
            
            labs[i] = new JLabel("-----");
            labs[i].setFont(lafont);
            labs[i].setPreferredSize(cosize);
            lpan.add(labs[i]);
            
        }
        this.getContentPane().add(lpan);
        this.pack();
    }
    
    public TimerFrame construct(LPlayer lp) {
        lp.addTickListener(this);
        return this;
    }

    public void tick(TickEvent e) {
        labs[0].setText(String.valueOf(e.at()%mods[0]));
        labs[0].repaint();
        for(int i=1; i< labs.length; i++) {
            labs[i].setText(String.valueOf(((int)e.at())%mods[i]));
            if(e.at()%mods[0] == 0)
                labs[i].repaint();
        }
    }
    
}
