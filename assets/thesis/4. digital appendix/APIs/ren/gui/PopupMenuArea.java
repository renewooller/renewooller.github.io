package ren.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import ren.util.Make;

/**
 * this is for an area in which you want a certain popup menuto appear when you
 * right click on it
 * 
 * @author wooller
 * 
 * 20/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */

public class PopupMenuArea extends JPanel implements ActionListener {

	private String[] menuCommands;

	public PopupMenuArea() {
		//this(new String [] {"no menu commands initialised"});
		super();
	}

	public PopupMenuArea construct(String[] menuCommands) {	
		this.setLayout(null);
		this.menuCommands = menuCommands;
		init();
		return this;
	}

	public void init() {
		this.setBackground(new Color(250, 250, 250));
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					((PopupMenuArea) e.getComponent()).clickMenu(e.getX(), e
							.getY());
				}
			}
		});
	}

	private JPopupMenu jpm;

	private int x, y;

	public void clickMenu(int x, int y) {
	//	System.out.println("menu clikced");
		this.x = x;
		this.y = y;
		//	ActionListener al = new ActionListener() {

		jpm = Make.popupMenu(menuCommands, this);
		jpm.show(this, x, y);

	}

	public void actionPerformed(ActionEvent e) {
		popupCommand(e.getActionCommand(), x, y);
	}

	protected void popupCommand(String command, int x, int y) {
		System.out.println("popupMenuClicked has not been overridden");
	}
	
	/*
	private void readObject(ObjectOutputStream oos) throws Exception {
		oos.defaultWriteObject();
		System.out.println("popupmenu " + this.getMouseListeners().length);
	}
	private void writeObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		System.out.println("popupMenu " + this.getMouseListeners().length);
	}*/
	
	
}