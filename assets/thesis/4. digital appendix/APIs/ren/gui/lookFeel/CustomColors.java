/*
 * CustomColors.java
 *
 * Created on 25 October 2003, 01:40
 * 11 October 2004
 */

package ren.gui.lookFeel;

import java.awt.color.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 *
 * @author  Rene Wooller
 */
public class CustomColors {
    
                                                            //R      G          B
    public static Color fore =                      new Color(10,  150,   30);
    public static Color back =                      new Color(10,  100,   30);
    public static Color hilight =                   new Color(50,  200,   30);
    public static Color light =                     new Color(50,  180,   30);
    public static Color shad =                      new Color(5,   40,    20);
    public static Color norm =                      new Color(10,  180,   40);
    public static Color dark =                      new Color(20,  100,   40);
    public static Color selected =                  new Color(20,  100,   150);
 //public are for use only within Lemu   
    public static Color selectedGreen =             new Color(80, 255, 60);
    public static Color unselectedGreen =           new Color(100, 125, 0);
    public static Color selectedRed =               new Color(210, 145, 35);
    public static Color unselectedRed =             new Color(155, 60, 0);
    
    public static Color selectedPlain =             new Color(200, 180, 200);
    public static Color unselectedPlain =           new Color(130, 165, 150).darker();
          
    //metal slider colors
    public static Color controlShadow =             new Color(10,  60,    10);
    public static Color controlDarkShadow =         new Color(5,   30,    5);
    public static Color controlHighlight =          new Color(40,  120,   60);
    
    //MetalUtils colors
    public static Color control =                   new Color(40,  100,   30);
//    public static Color primaryControl =            new Color(bf(60),  150,   bf(60));
//    public static Color primaryControlDarkShadow =  new Color(bf(50),  bf(80),    bf(60));
    
    public static Color focus =                     new Color(50,  100,   120);
    
    private static double FACTOR = 0.75;
    
    /**
     * 
     */
    public static Color brighterBy(Color c, double factor) {
        if(factor < 0 || factor > 1.0) {
	    Exception ex = new Exception("factor must be between 0 and 1");
	    ex.fillInStackTrace();
	    try{throw ex;} catch(Exception e) {e.printStackTrace();}
	}

	int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
           return new Color(i, i, i);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                         Math.min((int)(g/factor), 255),
                         Math.min((int)(b/factor), 255));
    }

}
