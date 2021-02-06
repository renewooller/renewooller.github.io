/**
 *  this interface is designed for Sliders And dials that generate values, so 
 *  that they can be recognised as value generators.
 *
 * @author  Rene Wooller
 */
package ren.gui.components;

public interface ValueGenerator {
    
    public int getMaximum();
    
    public int getMinimum();
    
    public void setValue(int toSet);
    
    public int getValue();
    
    public boolean getValueIsAdjusting();

    public void addValueListener(ValueListener vl);
  
    public ValueGenerator copyVG();
    
  //  public ValueGenerator copyVGLink();
 }
