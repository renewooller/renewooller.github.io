/*
 * Created on 1/03/2006
 *
 * @author Rene Wooller
 */
package ren.gui.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ren.gui.ParameterMap;

public class NumTexFieldDouble extends JPanel {

	private JTextField jtf;
	
	private NumTexFieldDouble thisTF;

	private transient int py = 0;

	private boolean dwn = false;

	private ParameterMap pm;

	private int pr;
	
	private double acdif = 0;
	private double orival; // original value when mouse pressed
	
	private JLabel label;
	
	public NumTexFieldDouble() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public NumTexFieldDouble construct(ParameterMap ppm) {
		return construct(ppm, (int)((ppm.getModel().getMaximum()-
							  ppm.getModel().getMinimum())*1.5));
							 
	}
	
	public NumTexFieldDouble constructThin(ParameterMap ppm) {
		return constructThin(ppm, (int)((ppm.getModel().getMaximum()-
							  ppm.getModel().getMinimum())*1.5));
							 
	}
	
	public NumTexFieldDouble construct(ParameterMap ppm, int pixelRange) {
		return construct(ppm, pixelRange, new FlowLayout());
							 
	}
	
	public NumTexFieldDouble constructThin(ParameterMap ppm, int pixelRange) {
		return construct(ppm, pixelRange, new FlowLayout(FlowLayout.LEFT, 2, 0));
						 
	}
	
	public NumTexFieldDouble construct(ParameterMap ppm, int pixelRange, LayoutManager lay) {
		this.setLayout(lay);//new BoxLayout(this, 0));
		
		this.pm = ppm;

		this.pr = pixelRange;
		
		jtf = new JTextField();
		
		jtf.setColumns(3);
		thisTF = this;
		
		jtf.setText(String.valueOf(pm.getValue()));

		jtf.setEditable(false);

		jtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				pm.setValue(thisTF.getNumber());
				jtf.setEditable(false);
			}
		});

		jtf.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 2) {
					jtf.setEditable(true);
					jtf.selectAll();
				}
			}

			public void mousePressed(MouseEvent e) {
				py = e.getY();
				acdif = 0;
				orival = pm.getValue();
			}

		});

		jtf.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (e.getY() == py)
					return;
		
				// the denominator decides the range in pixels
				//double dif = 
				
				acdif += ((e.getY() - py)*1.0/pr*1.0)*(pm.getMax()-pm.getMin());//dif;
				
				setValue(orival-acdif);

				py = e.getY();

			}
		});

		jtf.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == e.VK_ENTER) {
					KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
					setValue(thisTF.getNumber());
				}
			}
		});
		
		label = new JLabel(pm.getName());
		this.add(label);
		this.add(jtf);

		return this;
	}
	
	public void setInsets(int in) {
		
		this.getInsets().bottom = in;
		this.getInsets().top = in;
		this.getInsets().left = in;
		this.getInsets().right = in;
		this.jtf.getInsets().bottom = in;
		this.jtf.getInsets().top = in;
		this.jtf.getInsets().left = in;
		this.jtf.getInsets().right = in;
		this.jtf.setBorder((BorderFactory.createLineBorder(Color.BLACK, 1)));
		
		this.label.getInsets().bottom = in;
		this.label.getInsets().top = in;
		this.label.getInsets().left = in;
		this.label.getInsets().right = in;
		this.label.setBorder((BorderFactory.createLineBorder(Color.BLACK, 1)));
		
		
		
	}

	private double getNumber() {
		String s = jtf.getText();

		double toRet = Integer.MIN_VALUE * 1.0;
		try {
			toRet = Double.parseDouble(s);

		} catch (NumberFormatException e) {
			jtf.setText(String.valueOf(pm.getValue()));
			return pm.getValue();
		}
		return toRet;
	}

	/**
	 * sets om to be the value, and updates the gui
	 * @param v
	 */
	private void setValue(double v) {
		pm.setValue(v);
		jtf.setText(String.valueOf(pm.getValue()));
	}
	
	public double getValue() {
		return pm.getValue();
	}
	
	public void repaint() {
		if(pm != null)
			jtf.setText(String.valueOf(pm.getValue()));
		super.repaint();
	}
}
