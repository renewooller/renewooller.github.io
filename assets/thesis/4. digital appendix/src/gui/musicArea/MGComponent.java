/*
 * Created on 2/12/2004
 *
 * Rene Wooller
 */
package gui.musicArea;

import gui.Dim;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JPopupMenu;

import jmms.TickEvent;
import music.MusicGenerator;
import ren.gui.DraggableComponent;
import ren.util.Make;

/**
 * @author wooller
 *
 * Music Generator Component - the parent component to all the other components that appear on the music area
 */
public abstract class MGComponent extends DraggableComponent implements MGCInterface {

	protected MusicGenerator mg;
	
	protected MusicArea area;
	
	/** this is the component that the popup appears on */
	protected Component popupRelativeTo = this;
	
	public MGComponent() {
		super();
		
		//System.out.println("pp = " + pp.toString());
	}
	
	public MGComponent construct(MusicGenerator mg, MusicArea musicArea) {
		return this.construct(0, 0, (int)Dim.sqr.getWidth(), (int)Dim.sqr.getHeight(),
				mg, musicArea);
	}
	
	public MGComponent construct(int x, int y, int w, int h, 
			MusicGenerator mg, MusicArea musicArea) {
		super.construct(x, y, w, h); 
		this.mg = mg;
		mg.setTickListener(this);
		this.area = musicArea;
		
		return this;
				
	}
	
	public void setMusicArea(MusicArea ma) {
		this.area = ma;
	}
	
	// overidden from Draggable component
	protected Point setInBounds(int x, int y) {
		if(y < 0)
			y = 0;
		else if(y > area.getHeight()-this.getHeight())
			y = area.getHeight()-this.getHeight();
		
		if(x <0)
			x = 0;
		else if(x > area.getWidth()-this.getWidth())
			x = area.getWidth()-this.getWidth();
	
		pp.x = x;
		pp.y = y;
		
		return pp;
	}
	
	/** sets the component that the popup should appear on.<br>
	 * it is useful when making components that have components inside them <br>
	 * @param repTo the component that the popoup will appear on
	 */
	public void setPopupRelativeTo(Component relTo) {
		this.popupRelativeTo = relTo;
	}
	
	/* (non-Javadoc)
	 * @see gui.musicArea.MGCInterface#getMusicGenerator()
	 */
	public MusicGenerator getMusicGenerator() {
		return this.mg;
	}

	/* (non-Javadoc)
	 * @see lplay.TickListener#tick(lplay.TickEvent)
	 */
	public abstract void tick(TickEvent e);
	
	private int msx, msy;
	public void mouseClicked(MouseEvent e) {
		msx = e.getX();
		msy = e.getY();
		if (e.getButton() == e.BUTTON3) {
			JPopupMenu pm = Make.popupMenu(this.getMenuItems(), new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					menuFired(e.getActionCommand());
				}
			});
			pm.show(popupRelativeTo, e.getX(), e.getY());
		} 
		select(e);
		super.mouseClicked(e);
	}

	
	
	/**
	 * 
	 * handles the creation of morphs, sets the transform chain and cues the music generator if the mouseevent
	 * has ctrl pressed down
	 */
	public void select(MouseEvent e) {
		area.mgcClicked(this, e);
		this.mg.select();
	}
	
	/**
	 *  Adds a string to the popupmenu that appears when the component is right clicked
	 *  make sure you override menuFired(string c) and call super.menuFired(c) when you add
	 * a menu item.
	 */
	private Vector menuItems = new Vector(1);
	protected void addMenuItem(String item) {
		menuItems.add(item);
	}
	
	private String [] getMenuItems() {
		Object [] arr = menuItems.toArray();
		String [] toRet = new String [arr.length];
		for(int i=0; i<toRet.length; i++) {
			toRet[i] = (String)(arr[i]);
		}
		return toRet;
	}
	
	protected void menuFired(String c) {
		
	}
	
	public abstract void initUI();
		
}
