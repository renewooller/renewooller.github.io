/**
 * Utilities for manipulating dimensions
 */
package ren.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JComponent;

public class DimUtil {
	public static Dimension scale(Dimension d, double x, double y) {
		Dimension d2 = new Dimension(d);
		d2.setSize(d.getWidth() * x, d.getHeight() * y);
		return d2;
	}

	public static Dimension getScreenSize() {
		return ((Toolkit) (Toolkit.getDefaultToolkit())).getScreenSize();
	}
	
	public static Dimension scaleScreen(double x, double y) {
		return scale(getScreenSize(), x, y);
	}
    
    public static void insets(JComponent jc, int left, int top, int right, int bot) {
        jc.getInsets().left = left;
        jc.getInsets().top = top;
        jc.getInsets().right = right;
        jc.getInsets().bottom = bot;
        
    }

	public static Dimension mod(Dimension sz, int x, int y) {
		sz.width = sz.width + x;
		sz.height = sz.height + y;
		return sz;
	}
}