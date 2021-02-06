// rene wooller
package ren.gui.seqEdit;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import jm.music.data.*;
import ren.gui.lookFeel.CustomColors;
import ren.gui.seqEdit.BeatTracker;
import ren.gui.seqEdit.BeatListener;
import java.awt.geom.*;

public class DraggableBTracker extends JComponent implements
						      MouseListener,
						      MouseMotionListener,
						      Accessible,
						      BeatListener{
    
    NoteToGraphicsConverter cv;
    BeatTracker bt;
    Handle han;
    public DraggableBTracker(NoteToGraphicsConverter cv, 
			     BeatTracker bt) {
	super();
        this.cv = cv;
        this.bt = bt;
	// this.setLayout(null);

	han = new Handle(cv.getX(0.2)+1, cv.getPixelsPerTone());// this.getBounds().height);
	//han.setLocation(0, 0);
	this.add(han);
	//	han.setLocation(2, 2);
        han.addMouseListener(this);
        han.addMouseMotionListener(this);
	bt.addBeatListener(this);
	this.setBounds(1, 1,
		       cv.getX(0.2), 
		       cv.getYRange()-2);
	
    }
    
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics.create();
        g.setColor(Color.GREEN);//this.getBackground());
        
        //int width = getWidth()-1;
        //int height = getHeight()-1;
	//    int x = cv.getX(bt.at());
	//        int y = 1;
        g.fillRect(1, 1,
		   getWidth(), getHeight());
        g.dispose();       
    }

    private boolean mouseDown = false;
    public void beatFired(double b) {
	if(!mouseDown)
	    this.setLocation(cv.getX(bt.at()), 0);
    }

    public void mouseDragged(MouseEvent e) {
	    bt.userModBeat(cv.getBeatsX(this.getX() + e.getX() - oldX));
	    this.setLocation(cv.getX(bt.at()), 0);
    }
    
    private int oldX = 0;
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        oldX = e.getX();
        bt.userMod(true);
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
        mouseDragged(e);
        bt.userMod(false);
    }


    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
    }

}

class Handle extends JComponent {
    private int w, h;
    public Handle(int w, int h) {
	super();
	this.setLayout(null);
	this.w = w;
	this.h = h;
	this.setBounds(1, 0, w, h);
    }

    public void paintComponent(Graphics g) {
	g.setColor(new Color(200, 100, 100));
	g.fillRect(0, 0, getWidth(), getHeight());
	g.dispose();
    }
}

