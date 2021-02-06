/*
 * Created on 20/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import jmms.processor.Actuator;
import jmms.processor.ActuatorCommander;
import jmms.processor.ActuatorContainerManager;
import jmms.processor.MidiProcessor;
import jmms.processor.Process;
import jmms.processor.ProcessFactory;
import ren.gui.LabelledView;
import ren.gui.ParameterMap;
import ren.gui.ViewMaker;
import ren.util.DimUtil;
import ren.util.GB;
import ren.util.Make;
import ren.util.PO;

/**
 * @author wooller
 *
 *20/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MidiProcessorPane extends JPanel implements Serializable {

	private MidiProcessor mproc;
	private ActuatorContainerManager acm;
	
	private JComboBox inputsCB = new JComboBox();
	private JPanel inputParamArea = new JPanel();
	
	private JComboBox procCB;//= new JComboBox();
	private JPanel procParamArea = new JPanel();
	
	private ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == addProc) {
				procSelectPopup = pf.getPopup(addProc, processesAvailable, addProc.getLocationOnScreen().x,
																		   addProc.getLocationOnScreen().y);
				procSelectPopup.show();
			} else if(e.getSource() == remProc) {
				mproc.removeProcess((jmms.processor.Process)procCB.getSelectedItem());
			}
		}
	};
	
	private JButton addProc = Make.button("add", "adds a new process", al);
	private JButton remProc = Make.button("remove", "removes the process from the list", al);
	
	// outputs
	private JList outputs = new JList();	
	private ActuatorFinder af = new ActuatorFinder();
	private JDialog afDialog;
	private MouseListener outputsMouseListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if(outputs.getSelectedValue() != null) {
				af.setActuatorCommander((ActuatorCommander)outputs.getSelectedValue());
				afDialog.setLocation(e.getPoint());
				afDialog.show();
			}
		}
	};
	
	// makeing sliders from ParamMaps
	private ViewMaker viewMaker = new ViewMaker();
	
	//popup 
	private PopupFactory pf = new PopupFactory();
	private JList processesAvailable = new JList();
	private Popup procSelectPopup;
	
	public MidiProcessorPane() {
		super(new GridBagLayout());
	}
	
	public void construct(MidiProcessor proc, ActuatorContainerManager acm) {
		this.mproc = proc;
		this.acm = acm;
		
		
		//setList datas
		this.processesAvailable.setListData(ProcessFactory.getTypes());
		this.procCB = new JComboBox(mproc.getProcessVector());
		
		// the add process popup
		processesAvailable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(processesAvailable.getSelectedValue() != null) {
					mproc.addProcess(ProcessFactory.create(processesAvailable.getSelectedValue().toString()));
					procCB.updateUI();
					procSelectPopup.hide();
				}
			}
		});
		
		// the link output popup
		af.construct(acm, null);//(ActuatorCommander)outputs.getSelectedValue());
		afDialog = new JDialog((Frame)null, "Link midi location to parameters", true);
		afDialog.getContentPane().add(af);
		afDialog.pack();
		
		
		//the actual process combobox
	
		procCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    inputsCB.removeAllItems();
				
				// if it there is nothing in it, don't do anything
				if(procCB.getSelectedItem() == null)
				    return;
				
				Actuator [] accs = ((Process)procCB.getSelectedItem()).getInputs();
				for(int i=0; i<accs.length; i++) {
					inputsCB.addItem(accs[i]);
				}
				
				Object [] op = ((Process)procCB.getSelectedItem()).getOutputs();
				if(op != null)
				    outputs.setListData(op);
				
				procParamArea.removeAll();
				ParameterMap [] params = ((Process)procCB.getSelectedItem()).getVariables();
				if(params != null) {
					for (int i = 0; i < params.length; i++) {
						procParamArea.add((new LabelledView()).construct(params[i], 1));
					}
				}
			}
		});
		
		
		// the input parameter action listener
		this.inputsCB.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        // if it there is nothing in it, don't do anything
				if(inputsCB.getSelectedItem() == null)
				    return;
						
				inputParamArea.removeAll();
				
				ParameterMap pm = ((ParameterMap)inputsCB.getSelectedItem());
							
				inputParamArea.add((new LabelledView()).construct(pm, 1));
				
				inputParamArea.validate();
							
		    }
		});
		
		// the output list
		outputs.addMouseListener(outputsMouseListener);
		
		if(this.getPreferredSize() == null) {
			this.setPreferredSize(DimUtil.scaleScreen(0.4, 0.6));
		}
		
		outputs.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 0.28, 0.8));
		inputsCB.setPreferredSize(new 
				Dimension(outputs.getPreferredSize().width, inputsCB.getLayout().preferredLayoutSize(inputsCB).height));
		procCB.setPreferredSize(inputsCB.getPreferredSize());
		
		inputParamArea.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 0.28, 0.6));
		procParamArea.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 0.28, 0.5));
		
		JPanel procButtonBox = new JPanel(new GridLayout(1, 2));
		procButtonBox.setPreferredSize(procCB.getPreferredSize());
		procButtonBox.add(addProc);
		procButtonBox.add(remProc);
		
		/*Box inputBox = new Box(1);
		inputBox.add(inputsCB);
		inputBox.add(inputParamArea);
		JUtil.entitle(inputBox, "input params");
		inputBox.getBorder().getBorderInsets(inputBox).bottom = 0;
		*/
		
		GB.add(this, 0, 0, new JLabel("input parameters"), 2, 1);
		GB.add(this, 0, 1, inputsCB, 2, 1);
		GB.add(this, 0, 2, inputParamArea, 2, 5);
		
	//	GB.add(this, 0, 0, inputBox, 2, 6);
		
		GB.add(this, 2, 0, new JLabel("processes"), 2, 1);
		GB.add(this, 2, 1, procButtonBox, 2, 1);
		GB.add(this, 2, 2, procCB, 2, 1);
		GB.add(this, 2, 3, procParamArea, 2, 4);
		
		GB.add(this, 4, 0, new JLabel("outputs"), 2, 1);
		GB.add(this, 4, 1, outputs, 2, 6);
		
		
	}
	
	public void update() {
	    /*
	    PO.p("1 size of proc vector = "  + mproc.getProcessVector().size());
	    
	    procCB.removeAllItems();
	    
	    PO.p("2 size of proc vector = "  + mproc.getProcessVector().size());
	    */
	    
		this.procParamArea.removeAll();
	
		inputsCB.removeAllItems();

		this.inputParamArea.removeAll();
						
		this.outputs.setListData(new Vector());
	
		this.setVisible(true);
	}
	
}
