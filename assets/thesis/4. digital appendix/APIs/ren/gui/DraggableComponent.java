package ren.gui;

/**

 */

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import ren.util.PO;

public class DraggableComponent extends JComponent implements MouseListener,
		MouseMotionListener {
	
	private boolean disableDrag = false;
	
	public DraggableComponent() {
		super();
		//this.construct(0, 0);
		pp = new Point(0,0);
		//System.out.println("constructing dc");
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public DraggableComponent construct(int x, int y) {
		return this.construct(x, y, 40, 40);
	}

	public DraggableComponent construct(int x, int y, int w, int h) {
		
		this.setSize(w, h);
		this.setBounds(new Rectangle(x, y, w, h));
		this.x = x;
		this.y = y;
		return this;
	}

	public void setDisableDrag(boolean b){
		this.disableDrag = b;
	}
	
	protected transient boolean mo = false; // mouseOver

	protected transient boolean md = false; // mouseDown

	protected int x, y, ox, oy; //xo, yo - original position

	public void mouseDragged(MouseEvent e) {
		if(disableDrag)
			return;
			
		x = e.getX() + x - ox;
		y = e.getY() + y - oy;
		pp = setInBounds(x, y);
		x = pp.x;
		y = pp.y;
						
		this.setLocation(pp.x, pp.y);
		this.repaint();
		
	//	PO.p("mouse dragged");
		
	}
	protected Point pp;// = new Point(0, 0);
	protected Point setInBounds(int x, int y) {
		pp.x = x;
		pp.y = y;
		return pp;
	}
	
	public void mouseMoved(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		if(disableDrag)
			return;
		mo = true;
		repaint();
	}

	public void mouseExited(MouseEvent e) {
		if(disableDrag)
			return;
		mo = false;
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		if(disableDrag)
			return;
		ox = e.getX();
		oy = e.getY();
		if(e.getButton() == e.BUTTON1) {
			md1 = true;
		} else if(e.getButton() == e.BUTTON2) {
			md2 = true;
		} else if(e.getButton() == e.BUTTON3) {
			md3 = true;
		}
		md = true;
		repaint();
	}

	protected transient boolean md1, md2, md3;
	public void mouseReleased(MouseEvent e) {
		if(disableDrag)
			return;
		if(e.getButton() == e.BUTTON1) {
			md1 = false;
		} else if(e.getButton() == e.BUTTON2) {
			md2 = false;
		} else if(e.getButton() == e.BUTTON3) {
			md3 = false;
		}
		md = false;
		repaint();
	}

}