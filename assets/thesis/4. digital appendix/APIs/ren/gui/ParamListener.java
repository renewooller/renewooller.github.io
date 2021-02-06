/**
 * ParamListener
 * This is to be implemented by classes that need to recive data from a
 * parameter.  
 * The class implementing this need to be registered with the Parameter
 * using 
 */
package ren.gui;

public interface ParamListener {
    // The value of the parameter comes through this hole
    public void paramFired(ParamEvent e);//double pv, String id);
}
