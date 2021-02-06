package ren.util;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ren.gui.ParameterMap;
import ren.gui.components.NumberTextField;

public class Make {

	public static JPopupMenu popupMenu(String label, String[] items,
			ActionListener al) {

		JPopupMenu jpm;

		jpm = new JPopupMenu(label);

		JMenuItem jmi;

		for (int i = 0; i < items.length; i++) {
			jmi = new JMenuItem(items[i]);
			jmi.addActionListener(al);
			jpm.add(jmi);
		}
		return jpm;
	}

	public static JPopupMenu popupMenu(String[] items, ActionListener al) {

		JPopupMenu jpm;
		jpm = new JPopupMenu();

		JMenuItem jmi;

		for (int i = 0; i < items.length; i++) {
			jmi = new JMenuItem(items[i]);
			jmi.addActionListener(al);
			jpm.add(jmi);
		}
		return jpm;
	}

	public static JButton button(String name, String tip, ActionListener al) {
		JButton b = new JButton(name);
		b.setToolTipText(tip);
		b.addActionListener(al);
		return b;
	}
    
    public static ParameterMap crossOver(String name) {
        return (new ParameterMap()).construct(0, 12, 
            new double [] {-0.5, 0.0, 0.125, 0.25, 0.3333, 0.375, 0.5, 
                0.625, 0.6666, 0.75, 0.875, 1.0, 1.5}, 
                0.5, name);
    }

    public static ParameterMap gradient(String name) {
        return (new ParameterMap()).construct(0, 99, 1.0, 0.01, 1.0, name);
    }

	public static ParameterMap exponential(String name) {
		return (new ParameterMap()).construct(0, 1000, 5.0, 0.01, 1.0, name);
	}
    
}