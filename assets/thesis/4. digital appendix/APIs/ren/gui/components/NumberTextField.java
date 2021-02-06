/*
 * NumberTextField.java
 *
 * Created on 28 May 2003, 01:15
 */

package ren.gui.components;
import java.awt.KeyboardFocusManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JTextField;

import ren.util.PO;
/**
 *
 * @author  Rene Wooller
 */
public class NumberTextField extends JTextField implements ValueGenerator {
    
    private int min = 0;
    private int max = 100;
    private int value = 0;
    private int startingValue = 1;
    
    private NumberTextField thisTF;
    
    private transient int py = 0;
    
    private boolean dwn = false;
    
    /** Creates a new instance of NumberTextField */
    public NumberTextField() {
        this.setColumns(3);
      
    }
    
    /** Creates a new instance of NumberTextField */
    public NumberTextField(int min, int max) {
        this(min, max, min);
    }
    
    /** Creates a new instance of NumberTextField */
    public NumberTextField(int min, int max, int starting) {
        this.setMaximum(max);
        this.setMinimum(min);
        startingValue = starting;
        value = startingValue;   
        this.setColumns(3);
        thisTF = this;
        this.setText(String.valueOf(value));
        
        this.setEditable(false);
        
        this.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                thisTF.fireActionPerformed();
                thisTF.setEditable(false);
            }
        });
        
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                
                if( e.getClickCount() == 2) {
                    thisTF.setEditable(true);
                    thisTF.selectAll();
                }
            }
            public void mousePressed(MouseEvent e) {
                py = e.getY();
            }
          
        });
        
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (e.getY() == py)
                    return;
               
                int dif = e.getY() - py;
               // PO.p(" y = " + e.getY() + "py = " + py);
                thisTF.setValue(thisTF.getValue() - (int)(dif));
                
                py = e.getY();
                
            }
        });
        
        this.addKeyListener(new KeyAdapter() {
           public void keyPressed(KeyEvent e) {
               if (e.getKeyCode() == e.VK_ENTER) {
                   KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                   thisTF.setValue(thisTF.getNumber());
               }
           }
        });
        
        /*
        this.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent e) {
                //((NumberTextField)e.getSource()).fireActionPerformed();
                System.out.println("caret " + 
                    ((javax.swing.text.JTextComponent)e.getSource()).getText());
            }
        });
         */     
    }
    
    
    /*
     *  if the number in the text field is invalid or out of bounds, 
     *  this method returns (min-1), so that the appropriate error handling
     *  can be done (done by getValue);
     */
    private int getNumber() {
        String s = this.getText();
   
        int toRet = min-1;
        try {
            toRet = Integer.parseInt(s);
     
//            if(toRet < min || toRet > max) {
//                toRet = min-1;
//            }
        } catch(NumberFormatException e) {
            //e.printStackTrace();
            toRet = min-1;
        }      
        return toRet;
    }
    
    public void setMaximum(int i) {
        max = i;
    }
    
    public int getMaximum() {
        return max;
    }    
    
    public void setMinimum(int i) {
        min = i;
    }
    
    public int getMinimum() {
        return min;
    }
    
    /*
     *  if the value  (== min-1) then it resets the text field to min.
     *
     */
    public int getValue() {
        //System.out.println(" getting value ");
    //    value = getNumber();
        /*
        if(value == min-1) {
            setValue(min);
            return min;
        }*/
        return value;
    }
    
    /*
     *  don't need to change value variable, because accessors will use getValue()
     *  
     */
    public void setValue(int toSet) {
        if(toSet > max) {
            toSet = max;
        } else if(toSet < min) {
            toSet = min;
        }
        this.setText(String.valueOf(toSet));
        this.fireActionPerformed(); // fire the actionlistener in pgc so that parameter value is changed
        
        //value = toSet;
    }
    
    public void setInBounds() {
        int n = getNumber();
        if(n>max) {
            n = max;
        }if(n<min) {
            n = min;
        } 
        value = n;
        this.setText(String.valueOf(n));
        
    }
    
    public void fireActionPerformed() {
        this.setInBounds();
        for(int i=0; i<vlcount; i++) {
        	vlarr[i].valueGeneratorUpdate(this.value);
        }
        super.fireActionPerformed();
    }
    
    public boolean getValueIsAdjusting() {
        return this.isFocusOwner();
    }    
    
    public int getDefaultStartValue() {
        return this.startingValue;
    }
    
    public void setDefaultStartValue(int toSet) {
	this.startingValue = toSet;
    }
    
    private ValueListener [] vlarr = new ValueListener [20];
    private int vlcount = 0;
    public void addValueListener(ValueListener vl) {
    	vlarr[vlcount++] = vl;
    }
    
    public ValueGenerator copyVG() {
	return new NumberTextField(this.getMinimum(), this.getMaximum(),
				   this.getDefaultStartValue());
    }
	

}
