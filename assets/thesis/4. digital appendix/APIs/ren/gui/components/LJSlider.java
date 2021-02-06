/*
 * LJSlider.java
 *
 * Created on 18 March 2003, 23:19
 */

package ren.gui.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicSliderUI;
import ren.gui.lookFeel.*;
/**
 *
 * @author  Rene Wooller
 */
public class LJSlider extends JSlider {
        
    private int defaultStartValue;
    
    //TODO make this into the constructor type pattern for saving
    
    /** Creates a new instance of LJSlider */
    public LJSlider() {
    	this(1);
    }
    
    public LJSlider(int orientation) {
//          Creates a slider using the specified orientation with the range 0 to 100 and an initial value of 50.
        this(orientation, 1, 8, 1);
        
    }
    public LJSlider(int min, int max) {
        this(min, max, min);
    }
//          Creates a horizontal slider using the specified min and max with an initial value equal to the average of the min plus max.
    public LJSlider(int min, int max, int value) {
        this(1, min, max, value);
    }
//          Creates a horizontal slider using the specified min, max and value.
    public LJSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
        
        LSliderUI lsu = new LSliderUI(this);
        
        this.setUI(lsu);
        init(); //min, max, value);
    }
    
    public void init() { //int min, int max, int value) {
    	if(orientation == 0) {this.setPreferredSize(LDimensions.hslid());
        } else if(orientation == 1) {this.setPreferredSize(LDimensions.vslid());}
        this.defaultStartValue = this.getValue();
        
        if ((this.getMaximum() - this.getMinimum()) < 30) {
			this.setMinorTickSpacing(1);
			this.setSnapToTicks(true);
			this.setPaintTicks(true);
			//((LSliderUI) this.getUI()).setSpecialTicks(value, 0);
		} else {
			this.setMinorTickSpacing(0);
			this.setSnapToTicks(false);
			this.setPaintTicks(false);
		}
    }
    
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        if(!this.isEnabled()){
            //g.drawLine(0, 0, this.getWidth(), this.getHeight());
            g.setColor(CustomColors.back.darker());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            
        }
    }
    
    public int getDefaultStartValue() {
        return defaultStartValue;
    }
       
    public void setDefaultStartValue(int toSet) {
        this.defaultStartValue = toSet;
    }
    
}
