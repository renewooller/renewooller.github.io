package gui;

import gui.musicArea.MorphComponent;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import music.morph.Morpher;
import music.singlePart.MorphMusicGenerator;
import ren.gui.LabelledView;
import ren.gui.ParameterMap;
import ren.util.GB;
import ren.util.PO;

/**
 * this is a jpanel so that you can use it in a panel, 
 * if you don't just want to have it in a jframe.
 * 
 * For the jframe implementation however, it's useful
 * to pass it the jframe that it is in, that it can be 
 * resived properly when parameters are used.
 * 
 * For the panel implementation, you'd need to implement
 * a "getParamView" and put that in a scroll bar or 
 * something, because when it changes size due to different
 * parameters, you won't be able to resize the frame to 
 * accomodate it
 * 
 * @author wooller
 *
 *29/05/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */

public class MorphEditor extends BasicMorphEditor implements Serializable {

	private MorphMusicGenerator mmg;

	private JComboBox morphCB;
	
	private JComboBox minstCB, msvolCB;
        
	private JPanel paramView = new JPanel();
		
	private MorphComponent mc;
	
	public MorphEditor() {
		super();
		morphCB = new JComboBox();
		minstCB = new JComboBox();
        msvolCB = new JComboBox();
	}
	
	public MorphEditor constructMedit(MorphComponent mc){ //MorphMusicGenerator mmg) {
	    this.mc = mc;
		this.setMorphMusicGenerator((MorphMusicGenerator)mc.getMusicGenerator());
        super.construct(this.mc, this.mmg);
		construct();
		updateParamView();
		return this;
	}
	
	
	/**
	 * if the models in the morphMusicGenerator are null, initiate them with new ones.
	 * @param mmg
	 */
	private void setMorphMusicGenerator(MorphMusicGenerator mmg) {
		this.mmg = mmg;
		if(mmg.getMorphStrucList() == null) {
			mmg.setMorphStrucList(new DefaultComboBoxModel());
			System.out.println(" algorithm models were null when setting morphMusicGenerator inot editor");
		}else {
			this.morphCB.setModel(mmg.getMorphStrucList());
			
		}
		
        this.minstCB.setModel(mmg.getMorphInst());
        
        this.msvolCB.setModel(mmg.getMorphSVol());
		
	}
	
	

	
    protected void construct() {
              
		GB.add(this, 0, 3, morphCB, 3, 1);
				
        GB.add(this, 0, 4, minstCB, 3, 1);
        
        GB.add(this, 0, 5, msvolCB, 3, 1);
        
		GB.add(this, 3, 1, this.paramView, 3, 5);
		
		this.morphCB.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
		     //  System.out.println("paramchanged");
		        if(e.getStateChange() == e.SELECTED)
		            updateParamView();
		    }
		});
	
	}
	
	ParameterMap [] params;
	
	private void updateParamView() {
	    this.paramView.removeAll();
	  	    
	    params = ((Morpher)(this.mmg.getMorphStrucList().getSelectedItem())).getPC();
	    if(params == null || params.length == 0) {
	        if(frame == null)
	            return;
	        
	        frame.pack();
	        frame.repaint();
	        
	        return;
	    }
		    
	    for(int i=0; i<params.length; i++) {
	        paramView.add((new LabelledView()).construct(params[i], true, true, "", 1));
	    }
	 	    
	    if(frame != null) {
	        frame.pack();
	        frame.repaint();
	    }else {
	        System.out.println("implement for no frame in morph editor.updateParamView");
	        this.validate();
	    }
	   // this.repaint();
	}
	
}