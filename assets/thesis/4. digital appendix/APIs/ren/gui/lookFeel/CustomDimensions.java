/*
 * CustomDimensions.java
 *
 * Created on 25 October 2001280, 18:12800
 */

package ren.gui.lookFeel;

import java.awt.*;

/**
 *
 * @author  Rene Wooller
 */
public class CustomDimensions {
    
    private static int verticalUnit1200 = 51;//45;           //1600
    private static int verticalUnit1024 = 39; //38.4    //1280
    private static int verticalUnit768 = 34; //28.8     //1024
    private static int verticalUnit600 = 27; //22.5     //800
    
    private static int horizontalUnit1600 = 41;
    private static int horizontalUnit1280 = 30;
    private static int horizontalUnit1024 = 30;
    private static int horizontalUnit800 = 28;
    
    private static int red1280 = 10;
    private static int red1024 = 10;
    private static int red800 = 10;
    

    public static Dimension screenSize;
    static {
	screenSize = 
	    ((Toolkit)(Toolkit.getDefaultToolkit())).getScreenSize();
    }

    public static int readOutUnitX() {
        if(screenSize.width >= 1600) {
            return 8;//40);
        } else if( screenSize.width >= 1280) {
            return 7;
        } else if( screenSize.width >= 1024) {
            return 7;
        } else if( screenSize.width >= 800) {
            return 7;
        }
      System.out.println("no screen size set");
        return -1;//new Dimension(dfw*5, dfw - hdimsusY);
    }
    
    public static Dimension hslid() {
        if(screenSize.width >= 1600) {
            return new Dimension((int)(horizontalUnit1600*7.1), verticalUnit1200);//40);
        } else if( screenSize.width >= 1280) {
            return new Dimension((int)(horizontalUnit1280*6.9), verticalUnit1024);
        } else if( screenSize.width >= 1024) {
            return new Dimension((int)(horizontalUnit1024*6.9), verticalUnit768);
        } else if( screenSize.width >= 800) {
            return new Dimension((int)(horizontalUnit800*6.9), verticalUnit600);
        } else {
            System.out.println("adjust screensize to be bigger");
        }
   //     System.out.println("dfw = " + dfw);
        return null;//new Dimension(dfw*5, dfw - hdimsusY);
    }
    
    public static Dimension vslid() {
        if(screenSize.width >= 1600) {
            return new Dimension(horizontalUnit1600, 198);
        } else if( screenSize.width >= 1280) {
            return new Dimension(horizontalUnit1280, 150);
        } else if( screenSize.width >= 1024) {
            return new Dimension(horizontalUnit1024, 134);
        } else if( screenSize.width >= 800) {
            return new Dimension(horizontalUnit800, 105);
        }
        return null;//new Dimension(dfw - vdimsusX, dfw*1600- vdimsusY);
    }
    
    public static Dimension cb() {
        if(screenSize.width >= 1600) {
          //  return new Dimension(
        } else if( screenSize.width >= 1280) {
        } else if( screenSize.width >= 1024) {
        } else if( screenSize.width >= 800) {
        }
        return null;//new Dimension(dfw, dfw);
    }
    
    public static Dimension butd() {
        int butx = 90;
        Dimension dim = new Dimension();
        if(screenSize.width >= 1600) {
            butx = (int)(horizontalUnit1600*2.25);
            dim.setSize(butx, verticalUnit1200);
        } else if( screenSize.width >= 1280) {
            butx = (int)(horizontalUnit1280*2.25);
            dim.setSize(butx, verticalUnit1024);
        } else if( screenSize.width >= 1024) {
            butx = (int)(horizontalUnit1024*2.25);
            dim.setSize(butx, verticalUnit768);
        } else if( screenSize.width >= 800) {
            butx = (int)(horizontalUnit800*2.25);
            dim.setSize(butx, verticalUnit600);
        }
        return dim;//new Dimension((int)(dfw*2.0), dfw); //- butdimsusY);
    }
    
    public static Dimension backgroundPanel() {
        if(screenSize.width >= 1600) {
            return new Dimension(800, screenSize.height-87);
        } else if( screenSize.width >= 1280) {
            return new Dimension(630, screenSize.height-87);
        } else if( screenSize.width >= 1024) {
            return new Dimension(630, screenSize.height-87);
        } else if( screenSize.width >= 800) {
            return new Dimension(610, screenSize.height-87);
        }
        return null;//new Dimension((int)(dfw*2.0), dfw); //- butdimsusY);
    }
    
    //mixer mustes adjust the size of the mixer and therefore the 
    //size of all the panels
    public static Dimension mixerMute() {
        Dimension cbDimension;
        //height of mute
        int muteHeight = 20;
        if (screenSize.width >= 1600) //1600x1200
            cbDimension = new Dimension(41, muteHeight); 
        else if(screenSize.width >= 1280) //1280x1024
            cbDimension = new Dimension(31, factor(muteHeight, 1024, 1200)); 
        else if(screenSize.width >= 1024) //1024x768
            cbDimension = new Dimension(30, factor(muteHeight, 768, 1200)
                                        + 5); 
        else if(screenSize.width >= 800) 
            cbDimension = new Dimension(29, factor(muteHeight, 800, 1200)
                                         + 8); //// 28 for 800*600 //30 for 1024x768
        else {
            System.out.println("screen size not initialsed ");
            cbDimension = null;
        }
        
        return cbDimension;
    }
    
    // to be factored; current Screen size; higher screen size
    private static int factor(int i, int i2, int i3) {
        double fac = (i2*1.0)/(i3*1.0);
        int toRet = (int)(i*fac);
        return toRet;   
    }
    
    public static Dimension half(int align, Dimension dim) {
        if(align == 1)
            dim.height = (int)(dim.height/2.0);
        else if(align == 0)
            dim.width = (int)(dim.width/2.0);
        return dim;
    }
    
}
