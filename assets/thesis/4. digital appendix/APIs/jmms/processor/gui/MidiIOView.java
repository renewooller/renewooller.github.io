/*
 * Created on 19/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor.gui;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import jm.midi.MidiInputListener;
import jm.midi.event.CChange;
import jm.midi.event.Event;
import jmms.processor.ActuatorCommander;
import jmms.processor.ActuatorContainerManager;
import jmms.processor.MidiInputLocation;
import jmms.processor.MidiLocation;
import jmms.processor.MidiOutputLocation;
import jmms.processor.MidiOutputManager;
import jmms.processor.MidiProcessor;
import ren.util.DimUtil;
import ren.util.Make;
import ren.util.PO;

/**
 * @author wooller
 *
 *19/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class MidiIOView extends JPanel implements Serializable {
	
	private JPanel thisPanel = this;
	
	//models
	private MidiProcessor mproc;
	private ActuatorContainerManager acm;
	private MidiOutputManager mom;
	
	// overall TODO
	private JTabbedPane jtp = new JTabbedPane();
	private JPanel midiIO = new JPanel(new GridBagLayout());
//	private JPanel midiProcPane = new JPanel(new GridBagLayout());
	
	private MidiProcessorPane mprocPane = new MidiProcessorPane();
	
//	Midi IO - input
	private JList mio_in = new JList();
	
	//Midi IO - Output
	private JList mio_out = new JList();
	
	//popup
	private MidiLocationSetter setter = new MidiLocationSetter();
	private ActuatorFinder actFinder = new ActuatorFinder();
	private JDialog setterDialog, actFindDialog, addOutputDialog;
	
	
	private ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == mio_in_add) {
				mproc.createMidiInputLocation();
				mio_in.updateUI();//validate();//repaint();
				
			} else if(e.getSource() == mio_in_rem) {
				if(mio_in.getSelectedValue() != null) {
					mproc.removeMidiInputLocation((MidiInputLocation)mio_in.getSelectedValue());
				}
				mio_in.updateUI();
			} else if(e.getSource() == mio_in_set) {
				//set location for setterDialog to current value
				setter.setMidiLocation((MidiLocation)mio_in.getSelectedValue());
				
				setterDialog.setLocationRelativeTo(mio_in_set);//.getLocation().x, mio_in_set.getLocation().y);
				//locSetPopup.hide();
				setterDialog.show();
				
			}  else if(e.getSource() == mio_in_link) {
				actFinder.setActuatorCommander((ActuatorCommander)mio_in.getSelectedValue());
				actFindDialog.setLocationRelativeTo(mio_in_link);
				actFindDialog.show();
				
			} else if(e.getSource() == mio_out_add) {
				mom.createMidiOutputLocation();
				mio_out.updateUI();
			} else if(e.getSource() == mio_out_rem) {
				if(mom.remove((MidiOutputLocation)(mio_out.getSelectedValue()))) {
					mio_out.updateUI();
				}
			} else if(e.getSource() == mio_out_set) {
				setter.setMidiLocation((MidiLocation)mio_out.getSelectedValue());
				setterDialog.setLocationRelativeTo(mio_out_set);
				setterDialog.show();
			} 
		}
	};
	
	// mio  buttons TODO
	private JPanel mio_in_buttons = new JPanel(new GridLayout(4, 1));
	private JButton mio_in_add = Make.button("add input", "creates a new MIDI input location", al);
	private JButton mio_in_rem = Make.button("remove", "removes the selected MIDI input location(s)", al);
	private JButton mio_in_set = Make.button("set input", "to edit the MIDI input manually", al);
//	private JButton mio_in_learn = Make.button("learn input", "detect the MIDI input and set it automatically", al);
	private JButton mio_in_link = Make.button("link", "select a parameter to respond to this input", al);
	//private JButton mio_in_remLink = Make.button("remove link", "remove a parameter that responds to this input", al);
	
	private JPanel mio_out_buttons = new JPanel(new GridLayout(3, 1));
	private JButton mio_out_add = Make.button("add output", "creates a new MIDI output location", al);
	private JButton mio_out_rem = Make.button("remove", "removes the selected MIDI output location(s)", al);
	private JButton mio_out_set = Make.button("set output", "to edit the MIDI output manually", al);
	//prlivate JButton mio_out_learn = Make.button("learn output", "detect the MIDI input and set it automatically", al);
	
	/**
	 * 
	 */
	public MidiIOView() {
		//construct();
	}
	
	public void construct(MidiProcessor mproc, MidiOutputManager mom, ActuatorContainerManager acm) {
		this.mproc = mproc;
		this.mom = mom;
		// acm below
		
		this.mio_in.setListData(mproc.getMidiInputLocations());
		this.mio_out.setListData(mom.getMidiOutputLocation());
		
		if(acm == null) {
			acm = new ActuatorContainerManager();
			acm.registerRoot(mproc);
			if(mom != null)
				acm.registerRoot(mom);
		} else {
			this.acm = acm;
		}
		
	
		
		construct();
	}
	
	public void update() {
		this.mio_in.setListData(mproc.getMidiInputLocations());
		this.mio_out.setListData(mom.getMidiOutputLocation());
		this.mprocPane.update();
	}
	
	private JPanel readOutPane = new JPanel();
	private JTextArea jtf = new JTextArea(50, 30);//JTextField jtf = new JTextField();
	
	private void construct() {
		this.setPreferredSize(DimUtil.scaleScreen(0.5, 0.8));
		this.jtp.setPreferredSize(this.getPreferredSize());
		this.mprocPane.setPreferredSize(this.getPreferredSize());
		this.mprocPane.construct(mproc, acm);
		jtp.addTab("midi IO", midiIO);
		jtp.addTab("midi processor", mprocPane);	
		
		jtf.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 0.6, 0.8));
		readOutPane.add(jtf);
		jtp.addTab("midi input text", readOutPane);
		
		// midi readout
		mproc.getSequencer().addMidiInputListener(new MidiInputListener() {
			public void newEvent(Event e) {
				if(e instanceof CChange) {
					//e = (CChange)e;
					
					jtf.append("chan: " + ((CChange)e).getMidiChannel() + 
							    " ctrl: " + ((CChange)e).getControllerNum() +
							    " val: " + ((CChange)e).getValue() + "\n");
				//	PO.p("length = " + jtf.getDocument().getLength());
					if(jtf.getLineCount() > jtf.getRows()) {
						try {
							jtf.getDocument().remove(0, 
													jtf.getLineEndOffset(0));
						} catch(Exception ex) {ex.printStackTrace();}
					}
				}
			}
		});
		
		// Midi Input
		JScrollPane inScroll = new JScrollPane(mio_in);
		inScroll.setPreferredSize(DimUtil.scale(jtp.getPreferredSize(), 0.25, 0.8));
		this.mio_in_buttons.setPreferredSize(DimUtil.scale(jtp.getPreferredSize(), 0.2, 0.8));
		
		this.mio_in_buttons.add(mio_in_add);
		this.mio_in_buttons.add(mio_in_rem);
		this.mio_in_buttons.add(mio_in_set);
		this.mio_in_buttons.add(mio_in_link);
		//this.mio_in_buttons.add(mio_in_remLink);
		
		Box inputBox = new Box(0);
		inputBox.add(inScroll);
		inputBox.add(mio_in_buttons);
		inputBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(2), "inputs"));
		midiIO.add(inputBox);
		
		// Midi output
		JScrollPane outScroll = new JScrollPane(mio_out);
		outScroll.setPreferredSize(DimUtil.scale(jtp.getPreferredSize(), 0.25, 0.8));
		this.mio_out_buttons.setPreferredSize(DimUtil.scale(jtp.getPreferredSize(), 0.2, 0.8));
		
		this.mio_out_buttons.add(mio_out_add);
		this.mio_out_buttons.add(mio_out_rem);
		this.mio_out_buttons.add(mio_out_set);
		
		//this.mio_in_buttons.add(mio_in_remLink);
		
		Box outBox = new Box(0);
		outBox.add(outScroll);
		outBox.add(mio_out_buttons);
		outBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(2), "outputs"));
		midiIO.add(outBox);
		
		this.add(jtp);
		
		//learn input popup
		setter.construct(null, this.mproc);
		setterDialog = new JDialog((Frame)null, "Midi Location Setter", true);
		setterDialog.getContentPane().add(setter);
		setterDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(setter.getMidiLocation() instanceof MidiInputLocation)
					mio_in.updateUI();
				else if(setter.getMidiLocation() instanceof MidiOutputLocation) {
					mio_out.updateUI();
				}
			}
		});
		setterDialog.pack();
		
		//act finder popup
		actFinder.construct(this.acm, null);
		actFindDialog = new JDialog((Frame)null, "Link midi location to parameters", true);
		actFindDialog.getContentPane().add(actFinder);
		actFindDialog.pack();
		
	}

}
