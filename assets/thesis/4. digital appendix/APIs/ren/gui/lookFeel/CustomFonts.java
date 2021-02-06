/*
 * CustomFonts.java
 *
 * Created on 25 October 2003, 17:46
 */

package ren.gui.lookFeel;

import java.awt.font.*;
import java.awt.*;
/*
 *
 * @author  Rene Wooller
 */
public class CustomFonts {
    
   
    public static Font buttonLabel() {
        if(CustomDimensions.screenSize.width >= 1600) {
            return new Font("SansSerif", Font.PLAIN, 12);
        } else if(CustomDimensions.screenSize.width >= 1280) {
            return new Font("SansSerif", Font.PLAIN, 11);
        } else if(CustomDimensions.screenSize.width >= 1024) {
            return new Font("SansSerif", Font.PLAIN, 9);
        } else if(CustomDimensions.screenSize.width >= 800) {
            return new Font("SansSerif", Font.PLAIN, 9);
        }
        System.out.println("screenSize not done");
        return null;
    }
    
    public static Font parameterLabel() {
        if(CustomDimensions.screenSize.width >= 1600) {
            return new Font("SansSerif", Font.BOLD, 16);
        } else if(CustomDimensions.screenSize.width >= 1280) {
            return new Font("SansSerif", Font.BOLD, 12);
        } else if(CustomDimensions.screenSize.width >= 1024) {
            return new Font("SansSerif", Font.BOLD, 10); 
        } else if(CustomDimensions.screenSize.width >= 800) {
            return new Font("SansSerif", Font.BOLD, 9);
        }
        System.out.println("screenSize not done");
        return null;
    }
    
    public static Font autoScopeReadOut() {
        if(CustomDimensions.screenSize.width == 1600) {
            return new Font("SansSerif", Font.BOLD, 16);
        } else if(CustomDimensions.screenSize.width >= 1280) {
            return new Font("SansSerif", Font.BOLD, 12);
        } else if(CustomDimensions.screenSize.width >= 1024) {
            return new Font("SansSerif", Font.BOLD, 10); 
        } else if(CustomDimensions.screenSize.width >= 800) {
            return new Font("SansSerif", Font.BOLD, 9);
        }
        System.out.println("screenSize not done");
        return null;
    }
    
    /*
     *
     *
    if(CustomDimensions.screenSize.width == 1600) {
    } else if(CustomDimensions.screenSize.width == 1280) {
    } else if(CustomDimensions.screenSize.width == 1024) {
    } else if(CustomDimensions.screenSize.width == 800) {
    }
     **/
}
