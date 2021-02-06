package ren.gui.seqEdit;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import ren.util.*;
import jm.music.data.*;

public class ScoreEditor extends JPanel {
    private PopupFactory popupFactory = new PopupFactory();

    private Score score;

    private PartEditor pedit = new PartEditor();

    public ScoreEditor() {
	this(new Score());
    }

    public ScoreEditor(Score s) {
	super();
	this.setLayout(new GridBagLayout());
	this.score = s;
	construct();
    }
    
    public PartEditor getPartEditor() {
	return pedit;
    }

    public void setScore(Score s) {
	this.score = s;
    }

    private void construct() {
	score.sortChan();
	Part [] parts = score.getPartArray();

	ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    //	    System.out.println("before set : " + score.toString());
		    
		    pedit.setPart(score.getPart(e.getActionCommand()));

		    //    System.out.println("after set : " + score.toString());
		    
		}
	    };

	GB.add(this, 0, 0,  new JLabel("Edit Part : "), 1, 1);
	
	for(int i=0; i<parts.length; i++) {
	    GB.add(this, i+1, 0, partButton(parts[i], al), 1, 1);
	}

	if(parts.length > 0) {
	    pedit.setPart(parts[0]);
	}
		
	pedit.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "current part"));
	GB.add(this, 0, 1, pedit, 10, 10);
	
    }

    private transient Popup pop;
    private ActionListener pma = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("rename")) {
		    JTextField tf = new JTextField();
		    tf.setText(currentButton.getText());
		    
		    tf.addActionListener(new ActionListener () {
			    public void actionPerformed(ActionEvent e) {
				score.getPart(currentButton.getText())
				    .setTitle(((JTextField)e.getSource())
					      .getText());
				currentButton
				    .setText(((JTextField)e.getSource())
					     .getText());
				pop.hide();
				
			    }
			});
		    Box b = new Box(0);
		    b.add(new JLabel("set part title to: "));
		    b.add(tf);
		    pop = popupFactory
			.getPopup(currentButton, b, 
				  (int)currentButton
				  .getLocationOnScreen().getX(),
				  (int)currentButton
				  .getLocationOnScreen().getY());
		    pop.show();
		    tf.requestFocus();
		    //   pop.hide();
		}
	    }
	};

    private transient JButton currentButton; //pointer to the current
    //button that is having the popup menu on it
    private JButton partButton(Part p, ActionListener al) {
	JButton b = new JButton(p.getTitle());
	b.addActionListener(al);
	b.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		  		    
		    if(e.getButton() == e.BUTTON3) {
			currentButton = (JButton)(e.getSource());
			JPopupMenu jm = new JPopupMenu();
			jm.add("rename").addActionListener(pma);
			jm.show(currentButton, 0, 0);
		    }
		}
	    });
			
	return b;
    }
}
