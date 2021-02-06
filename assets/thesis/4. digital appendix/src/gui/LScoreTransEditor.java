/*
 * Created on 3/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import music.LScore;
import music.TransformChainHolder;

/**
 * @author wooller
 * 
 * the bit with the transformEditor and the part selector in it
 
 */
public class LScoreTransEditor extends JPanel {

	private LScore lscore;
	private TransformEditor tedit; 
	private JComboBox partSelect;
	
	public LScoreTransEditor() {
		this(null);
	}
	
	/**
	 * 
	 */
	public LScoreTransEditor(LScore lscore) {
		super();
		this.lscore = lscore;
		this.setLayout(null);//new GridBagLayout());
		construct();
	}
	/*
	public void save(StringBuffer sb) {
		sb.append("<" + this.getClass().getName() + ">");
		sb.append("fullSize=");
		sb.append(fullSize);
		sb.append("</" + this.getClass().getName() + ">");
	}
	public void load(String s) {
		s.
	}*/
	
	int top = 20;
	int wid = 100;
	private void construct() {
		
		JLabel title = new JLabel("Tranform Editor");
		title.setBounds(wid, 0, wid, top);
		this.add(title);
		
		JLabel cp = new JLabel("edit part");
		cp.setBounds(wid+10, top, wid, top);
		this.add(cp);
		
		partSelect = new JComboBox();//new MutableComboBoxModel());(default is mutable)
		updatePartSelect();
		partSelect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				tedit.setTransformChain(((TransformChainHolder)e.getItem()).getTransformChain());
			}
		});
		
		//GB.add(this, 1, 0, partSelect, 2, 1);
		partSelect.setBounds(wid*2-40, top, 100, 20);
		this.add(partSelect);
		
		JButton shb = new JButton("hide");
		shb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(((JButton)e.getSource()).getText().equals("hide")) {
					((JButton)e.getSource()).setText("show");
					smallSize();
				} else {
					((JButton)e.getSource()).setText("hide");
					fullSize();
				}
			}
		});
	//	GB.add(this, 0, 0, shb, 1, 1, 0, 0, 0, 0, 1.0, 0.0, false, false, "WEST");
		shb.setBounds(0, top, 100, 20);
		this.add(shb);
		
		tedit = new TransformEditor();
		
		this.add(tedit);
		
		//tedit.setPreferredSize(DimUtil.scale(this.getPreferredSize(), 1.0, 0.8));
		//GB.add(this, 0, 1, tedit, 6, 12);
	}
	
	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
		tedit.setBounds(0, top + 20, this.getPreferredSize().width, this.getPreferredSize().height-20);
		tedit.setVisible(false);
		tedit.setVisible(true);
	}
	
	private boolean fullSize = true;
	public void fullSize() {
		this.setBounds((int)(this.getPreferredSize().width*2), 0, this.getPreferredSize().width,
				this.getPreferredSize().height);
	}
	
	public void smallSize() {
		this.setBounds((int)(this.getPreferredSize().width*3)-100, 0, this.getPreferredSize().width,
				this.getPreferredSize().height);
	}
	
	public void setLScore(LScore lscore) {
		this.lscore = lscore;
		//tedit.setTransformChain(lscore.get)
		updatePartSelect();
		partSelect.setSelectedIndex(0);
	}
	
	private void updatePartSelect() {
		if(lscore == null)
			return;
		int sz = lscore.size();
		
		partSelect.removeAllItems();
		
	//	partSelect.addItem(lscore);
		
		for(int i=0;i<sz;i++) {
			if(partSelect.getItemAt(i+1) == null) {
			    
				partSelect.addItem(lscore.getLPart(i));
			} else if(partSelect.getItemAt(i+1) != lscore.getLPart(i)) {
				partSelect.removeItemAt(i+1);		
				partSelect.insertItemAt(lscore.getLPart(i), i+1);
			}
		}
	}
}
