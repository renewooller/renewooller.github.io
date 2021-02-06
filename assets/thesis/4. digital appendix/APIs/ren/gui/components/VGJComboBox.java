/*
 * VGJComboBox.java
 *
 * Created on 15 October 2003, 15:40
 */

package ren.gui.components;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 
 * @author Rene Wooller
 */
public class VGJComboBox extends JComboBox implements ValueGenerator {

	private String[] strings;

	/** Creates a new instance of VGJComboBox */
	public VGJComboBox() {
		super();
	}

	public VGJComboBox(String[] arr) {
		super(arr);
		strings = arr;
	}

	public ValueGenerator copyVG() {
		return new VGJComboBox(this.strings);
	}

	public int getMaximum() {
		return this.getItemCount();
	}

	public int getMinimum() {
		return 1;
	}

	public int getValue() {
		return this.getSelectedIndex() + 1;
	}

	public boolean getValueIsAdjusting() {
		return this.isFocusOwner();
		// return this.isPopupVisible();
	}

	public void setValue(int toSet) {
		this.setSelectedIndex(toSet - 1);
	}

	public int getDefaultStartValue() {
		return 0;
	}

	public void setDefaultStartValue(int toSet) {
	}

    
	public void addValueListener(ValueListener vl) {
		Exception uni = new Exception("unimplemented method");
		uni.fillInStackTrace();
		try {
			throw uni;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}