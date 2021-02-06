package ren.gui.components;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class RJFrame extends JFrame implements ActionListener { //implements MenuListener{

	protected JMenu file = new JMenu("file");
	protected JMenuBar menuBar = new JMenuBar();
	
	public RJFrame(boolean menu) {
		super();
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		if (!menu)
			return;

		buildMenuBar();

	}

	public void buildMenuBar() {

		addMenuItem("save", file, this);
		addMenuItem("load", file, this);
		menuBar.add(file);

		this.setJMenuBar(menuBar);
	}

	protected void addMenuItem(String name, JMenu menu, ActionListener al) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(al);
		menu.add(item);
	}
	
	/**
	 * adds this action command to the "file" menu
	 * @param name
	 */
	protected void addMenuItem(String name) {
		addMenuItem(name, file, this);
	}
	

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("save"))
			save();
		else if (e.getActionCommand().equals("load"))
			load();
	}

	protected void save() {
	}

	protected void load() {
	}

	/*

	 public void menuSelected(MenuEvent e) {
	 System.out.println("e to string = " + e.toString());
	 
	 }

	 public void menuCanceled(MenuEvent e) {
	 }

	 
	 public void menuDeselected(MenuEvent e) {
	 }*/
}

