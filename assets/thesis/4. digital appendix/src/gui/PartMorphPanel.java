/*
 * Created on 18/10/2005
 *
 * @author Rene Wooller
 */
package gui;


import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import music.PartMorph;
import music.morph.rt.MorpherRT;
import music.morph.transearch.TraseMorph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ren.gui.ExampleFileFilter;
import ren.gui.LabelledView;
import ren.gui.ParameterMap;
import ren.io.Domc;
import ren.util.GB;
import ren.util.PO;

public class PartMorphPanel extends JPanel {

	private PartMorph pm;

	private JComboBox morphCB = new JComboBox();

	private JComboBox minstCB = new JComboBox();

	protected JPanel paramView = new JPanel();

	private JFrame frame;

	public PartMorphPanel() {
		super(new GridBagLayout());
	}

	public PartMorphPanel construct(PartMorph pmi, JFrame nf) {
		// if(pm == null)
		// System.out.println("the part morph is null in
		// par morph panel");

		// PO.p("setting frame ");
		if (nf == null) {
			try {
				Exception ne = new NullPointerException(
						"null frame fed into construct of PartMorphPanel");
				ne.fillInStackTrace();
				throw ne;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.frame = nf;

		this.pm = pmi;

		initComboBoxes();

		LabelledView cr = new LabelledView().construct(pm.getStrucCO(), true, true,
				"the center of the cross-over", 0), 
				
				// gradient of transition
				gr = new LabelledView().construct(pm
				.getStrucGR(), true, true, "the speed of transition (gradient)", 0), 
				
				// exponential curve of the mi
				ex = new LabelledView().construct(pm.getStrucEX(), true, true, 
						"exponent of the morph index", 0),
				
				// vol
				vearl = new LabelledView().construct(pm.getVolEarl(), true, true,
						"how early the parts come in and out (Volume)", 0), 
				
		      
				vCO = new LabelledView()
				.construct(pm.getVolCO(), true, true, "the point at which the volume crosses over",
						0), 
				vCO2 = new LabelledView()
				.construct(pm.getVolCO2(), true, true, "if using two different channels, vol c-o for second channel",
						0),		
						scCO = new LabelledView().construct(pm.getScopeCO(), true, true,
				"the cross over for scope morph", 0), scGR = new LabelledView().construct(pm
				.getScopeGR(), true, true, "the gradient of scope morph", 0), quCO = new LabelledView()
				.construct(pm.getQuaCO(), true, true, "cross-over of the quantise morph", 0), quGR = new LabelledView()
				.construct(pm.getQuaGR(), true, true, "gradient of the quantise morph", 0), shuCO = new LabelledView()
				.construct(pm.getShuCO(), true, true, "cross-over of the shuffle morph", 0), shuGR = new LabelledView()
				.construct(pm.getShuGR(), true, true, "gradient of the quantise morph", 0),
				
				miqua = new LabelledView().construct(pm.getMIQua(), true, true, 
						"the number of steps that the morph index will be " +
						" quantised to (17 is unquantised", 0),
				rep = new LabelledView().construct(pm.getRep(), true, true, 
								"the number of beats to repeat", 0);
		
				

		JCheckBox solo = new JCheckBox();
		solo.setSelected(this.pm.getSolo());
		solo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pm.setSolo(((JCheckBox)(e.getSource())).isSelected());
			}
		});
		JLabel solab = new JLabel("solo");
		Box sobox = new Box(0);
		sobox.add(solo);
		sobox.add(solab);
		
		JCheckBox tofo = new JCheckBox();
		tofo.setSelected(this.pm.isTonalFollower());
		tofo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pm.setTonalFollower(((JCheckBox)(e.getSource())).isSelected());
			}
		});
		JLabel tolab = new JLabel("follow");
		Box tobox = new Box(0);
		tobox.add(tofo);
		tobox.add(tolab);
		
		JButton save = new JButton("save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		JButton load = new JButton("load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		int vi = 0;
		int wi = 4;
		GB.add(this, 0, vi, sobox, 1, 1);
		GB.add(this, 1, vi, tobox, 1, 1);
		GB.add(this, 2, vi, save, 1,1);
		GB.add(this, 3, vi++, load, 1,1);
		GB.add(this, 0, vi++, morphCB, 2, 1);
		GB.add(this, 0, vi++, minstCB, 4, 1);

		GB.add(this, 0, vi++, rep, wi, 1);
		GB.add(this, 0, vi++, miqua, wi, 1);
		GB.add(this, 0, vi++, cr, wi, 1);
		GB.add(this, 0, vi++, gr, wi, 1);
		GB.add(this, 0, vi++, ex, wi, 1);
		GB.add(this, 0, vi++, vearl, wi, 1);
		GB.add(this, 0, vi++, vCO, wi, 1);
		GB.add(this, 0, vi++, vCO2, wi, 1);
		GB.add(this, 0, vi++, scCO, wi, 1);
		GB.add(this, 0, vi++, scGR, wi, 1);
		GB.add(this, 0, vi++, quCO, wi, 1);
		GB.add(this, 0, vi++, quGR, wi, 1);
		GB.add(this, 0, vi++, shuCO, wi, 1);
		GB.add(this, 0, vi++, shuGR, wi, 1);

		GB.add(this, 3, 0, paramView, wi, vi);

		// morphCB.updateUI();//morphCB.getSelectedIndex();
		updateParamView();

		return this;
	}

	private void initComboBoxes() {
		// PO.p("part morph type in pmpanel initcomboboxes = " +
		// pm.getMorphStrucList().getSelectedItem().toString());
		this.morphCB.setModel(pm.getMorphStrucList());
		this.morphCB.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// System.out.println("paramchanged");
				if (e.getStateChange() == e.SELECTED)
					updateParamView();
			}
		});

		this.minstCB.setModel(pm.getMorphInst());

		// this.msvolCB.setModel(pm.getMorphSVol());
	}

	protected void updateParamView() {

		this.paramView.removeAll();

		if (this.morphCB.getSelectedItem() instanceof TraseMorph) {
			PO.p("selected item instance of traseMorph");

			this.paramView.add((new TraseMorphPanel()).construct(((TraseMorph) (this.morphCB
					.getSelectedItem()))));
		} else {

			ParameterMap[] params = ((MorpherRT) (this.morphCB.getSelectedItem())).getPC();
			if (params == null || params.length == 0) {

				if (frame == null)
					return;

				frame.pack();
				frame.repaint();

				return;
			}

			Box bv = new Box(1);
			Box bh1 = new Box(0);
			Box bh2 = new Box(0);
			bv.add(bh1);
			bv.add(bh2);
			paramView.add(bv);
			
			for (int i = 0; i < params.length; i++) {
				if(i< params.length/2.0)
					bh1.add((new LabelledView()).construct(params[i], true, true, "", 1));
				else {
					bh2.add((new LabelledView()).construct(params[i], true, true, "", 1));
				}
			}
		}
		
		if (frame != null) {
			frame.pack();
			frame.repaint();
		} else {
			System.out.println("implement for no frame in morph PartMorphPanel.updateParamView");
			this.validate();
		}
		// this.repaint();

	}
	
	protected void load() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("pmo");
        filter.setDescription("part morph files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadState(chooser.getSelectedFile()
                .getAbsolutePath());

        }
    }
    
    protected void loadState(String fname) {
        Domc.init();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    
        try{
            if (!fname.endsWith(".pmo")) fname = fname.concat(".pmo");
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(fname));
            Element pme = (Element)doc.getFirstChild();
            
            PartMorph opm = this.pm;
            if(pm.getMultiMorph() == null)
            	PO.p("1strange, isn't it");
            
            this.pm = ((PartMorph)(Domc.lo(pme, doc))).construct(opm.getMultiMorph());
            
            if(pm.getMultiMorph() == null)
            	PO.p("2strange, isn't it");
            
            pm.getMultiMorph().replacePartMorph(opm, pm);
       
            
            this.removeAll();
            this.construct(pm, frame);
            this.validate();
            this.repaint();
            
          //  this.reinit();
           // this.repaint();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

	protected void save() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("pmo");
		filter.setDescription("part morph files");
		chooser.setFileFilter(filter);
		PO.p("before show");
		int returnVal = chooser.showSaveDialog(this);
		PO.p("after show " + returnVal + " appr " + JFileChooser.APPROVE_OPTION + " err "
				+ JFileChooser.ERROR_OPTION);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			save(chooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void save(String fname) {
		Domc.init();
		if (!fname.endsWith(".pmo")) {
			fname = fname.concat(".pmo");
		}

		Document doc = Domc.makeDoc();

		Element el = Domc.sa(pm, "partMorph", doc);
		//el.setAttribute("index", pm.getMultiMorph().get)
		doc.appendChild(el);
		
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fname));

			DOMSource source = new DOMSource(doc);
			StreamResult stream = new StreamResult(out);
			trans.transform(source, stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
