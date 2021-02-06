/*
 * Created on 18/10/2005
 *
 * @author Rene Wooller
 */
package gui;

import gui.musicArea.MorphComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import music.MultiMorph;

import org.w3c.dom.Document;

import ren.gui.ExampleFileFilter;
import ren.io.Domc;
import ren.util.GB;
import ren.util.PO;

public class MultiMorphEditor extends BasicMorphEditor {

    private MultiMorph mpm;
    
    private JTabbedPane tabpane = new JTabbedPane();
    
    private JMenu tolemen = new JMenu("tonal lead");
    
    private JRadioButtonMenuItem currejrabum = null;
    
    public MultiMorphEditor() {
        super();
        
    }

    public MultiMorphEditor constructMulti(MorphComponent mc, MultiMorph mpm,
            JFrame containingFrame) {
        super.construct(mc, mpm);
        
        super.frame = containingFrame;
        
        this.mpm = mpm;
        
        initTabPane();
        
        
        
        construct();
        
        this.initToneLeadMenu();
        return this;
    }
    
    private void initTabPane() {
        tabpane.removeAll();
        for(int i=0; i< mpm.size(); i++) {
                  
            tabpane.add("mc " + Integer.toString(i), 
                (new PartMorphPanel().construct(mpm.getPartMorph(i), super.frame)));
        }
    }
    
    public void reinit() {
    	mpm.initScores();	
		mpm.initParts();
	
		// go through and enable all the ones that have parts
		for(int i=0; i< mpm.size(); i++) {
			if(tabpane.getTabCount() <= i) {
				tabpane.add("mc " + Integer.toString(i), 
		                (new PartMorphPanel().construct(mpm.getPartMorph(i), super.frame)));
			}
			tabpane.setEnabledAt(i, true);
			mpm.getPartMorph(i).setEnabled(true);
		}
		
		// go through and  disable any part morphs that haven't got parts
		for(int i=mpm.size(); i< tabpane.getTabCount(); i++) {
			tabpane.setEnabledAt(i, false);

		}
		//if(tabpane.s)
		
		initToneLeadMenu();
		
		repaint();
    }
    
    public void initToneLeadMenu() {
    	tolemen.removeAll();
    	ActionListener al = new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			setTonalLead(e.getActionCommand());
    			if(currejrabum != null) {
    				currejrabum.setSelected(false);
    			}
    			currejrabum = (JRadioButtonMenuItem)e.getSource();
    		}
    	};
    	JRadioButtonMenuItem none = new JRadioButtonMenuItem("none");
    	none.addActionListener(al);
    	
    	tolemen.add(none);
    	for(int i=0; i< this.mpm.size(); i++) {
    		JRadioButtonMenuItem jarab = 
    			new JRadioButtonMenuItem(String.valueOf(i));
    		jarab.addActionListener(al);
    		tolemen.add(jarab);
    	}
    	
    }
    
    public void setTonalLead(String tole) {
    	if(tole.equalsIgnoreCase("none")) {
    		this.mpm.setTonalLead(-1);
    	} else {
    		this.mpm.setTonalLead(Integer.parseInt(tole.substring(0, 1)));
    	}
    	PO.p("set tonal lead to " + mpm.getTonalLead());
    }
    
    public void saveState() {
    	PO.p("in save state");
    	 JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
         ExampleFileFilter filter = new ExampleFileFilter();
         filter.addExtension("mor");
         filter.setDescription("morph configuration files");
         chooser.setFileFilter(filter);
       //  PO.p("before show");
         int returnVal = chooser.showSaveDialog(super.frame);
     //    PO.p("after show " + returnVal + 
     //   		 " appr " + JFileChooser.APPROVE_OPTION + " err " + JFileChooser.ERROR_OPTION);
         if (returnVal == JFileChooser.APPROVE_OPTION) {
             saveState(chooser.getSelectedFile().getAbsolutePath());
         }
    }
    
    private void saveState(String fname) {
    	PO.p("saving " + fname);
        Domc.init();
        if (!fname.endsWith(".mor")) {
            fname = fname.concat(".mor");
        }
        
        Document doc = Domc.makeDoc();
        
        this.mpm.saveState(doc);
        
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
    
    protected void loadState() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("mor");
        filter.setDescription("morph configuration files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(super.frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadState(chooser.getSelectedFile()
                .getAbsolutePath());

        }
    }
    
    protected void loadState(String fname) {
        Domc.init();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    
        try{
            if (!fname.endsWith(".mor")) fname = fname.concat(".mor");
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(fname));
            mpm.loadState(doc);
            this.initTabPane();
            this.initToneLeadMenu();
          //  this.reinit();
            this.repaint();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }
    
    
    private void construct() {
  
        JButton reinit = new JButton("re-initialise");
        reinit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			//	PO.p("mme reinit");
				reinit();
				
				
			}
        	
        });
        super.menuBar.add(reinit);
    	
        JMenu file = new JMenu("file");
        JMenuItem save = new JMenuItem("save");
        save.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		PO.p("saveState()");
        		saveState();
        	}
        });
        file.add(save);
        
        JMenuItem load = new JMenuItem("load");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		loadState();
        	}
        });
        file.add(load);
        super.menuBar.add(file, 0);
        
        tolemen = new JMenu("tonal lead");
        super.menuBar.add(tolemen);

        GB.add(this, 3, 1, tabpane, 6, 6);
        
        
        
    }
}
