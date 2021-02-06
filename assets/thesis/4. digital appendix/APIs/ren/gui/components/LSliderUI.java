/*
 * LSliderUI.java
 *
 * Created on 14 April 2003, 14:36
 */

package ren.gui.components;

import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.TrackListener;
import javax.swing.JSlider;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
//import lib.greenLF.*;
/**
 * @author Rene Wooller  - Copyright 2003
 */
public class LSliderUI extends BasicSliderUI {
    
    private Dimension thumbSize;
    private int tshalf;
    protected boolean isDragging;
    
    protected int currentLMouseY, currentLMouseX;
    
    /** Creates a new instance of LSliderUI */
    public LSliderUI(JSlider js) {
        super(js);
    }
   
    /**
     * This function is called when a mousePressed was detected in the track, not
     * in the thumb.  The default behavior is to scroll by block.  You can
     *  override this method to stop it from scrolling or to add additional behavior.
     *
    protected void scrollDueToClickInTrack( int dir ) {
        if(slider.isEnabled())
            setThumbLocation(inBounds(currentLMouseX, currentLMouseY));
        //scrollByBlock( dir );
    }*/
    
    
    public TrackListener createTrackListener(JSlider js) {
        thumbSize = getThumbSize();
        if(slider.getOrientation() == 1) {
            tshalf = (int)(thumbSize.height/2 + 0.5);
        } else {
            tshalf = (int)(thumbSize.width/2 + 0.5);
        }
    //    System.out.println(thumbSize.width + " " + thumbSize.height);
        TrackListener tl = new TrackListener() {
            
            public void mousePressed(MouseEvent e) {
                //System.out.println("MOUSEPRESSED IN  track");
                if(!slider.isEnabled())
                    return;
                
                setThumbLocation(inBounds(e.getX(), e.getY()));
                
                super.mousePressed(e);
                super.mouseDragged(e);
                
            }
            public void mouseDragged(MouseEvent m) {
                super.mouseDragged(m);
            }
        };
        
        return tl;
    }
    
    public ScrollListener createScrollListener(JSlider js) {
        ScrollListener sl = new ScrollListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println("scrolled");
            }
        };
        return sl;
    } 
    private Point inBounds(int x, int y) {
        if(slider.getOrientation() == 1) {
            if(slider.getPaintTicks())
                x = 0;
            else 
                x = (int)(slider.getWidth()/2.0 - thumbSize.width/2.0);
            
            if(y < tshalf) y = tshalf;
            else if(y > trackRect.height) y = trackRect.height + tshalf;
            y -= tshalf;
        } else {
            y = 0;
            if(x < tshalf) x = tshalf;
            else if(x > trackRect.width) x = trackRect.width + tshalf;
            x -= tshalf;
        }
        return new Point(x, y);
    }
    
    protected Dimension getThumbSize() {
       
        Dimension size = new Dimension();

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
	    size.width = (int)(ren.gui.lookFeel.LDimensions.vslid().width*0.8);//625);
	    size.height = (int)(Math.max(ren.gui.lookFeel.LDimensions.vslid().height/16, 8));
	}
	else {
	    size.width = (int)(ren.gui.lookFeel.LDimensions.hslid().width/21);
	    size.height = (int)(ren.gui.lookFeel.LDimensions.hslid().height*0.8);
	}
	return size;
    }
    
    public void setThumbLocation(Point p) {
        setThumbLocation(p.x, p.y);   
    }
    
    private int darkTick, redTick;
    public void setSpecialTicks(int d, int r) {
        darkTick = d;
        redTick = r;
    }
    
    public void setRedTick(int r) {
        redTick = r;
    }
    
    public void paintTicks(Graphics g) {
        super.paintTicks(g);
        
    }
    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        int valAt = valueForYPosition(y);   
        if(valAt == darkTick) {
            Color origColor = g.getColor();
            g.setColor(origColor.darker());
            g.drawLine( 0, y-1,  tickBounds.width - 2, y-1 );
            g.drawLine( 0, y+1,  tickBounds.width - 2, y+1 );
            g.setColor(origColor);
        }
        if(valAt == redTick) { 
            Color origColor = g.getColor();
            g.setColor(Color.red);
            g.drawLine( 0, y,  tickBounds.width - 2, y );
            g.setColor(origColor);
            return;
        }   
        g.drawLine( 0, y,  tickBounds.width - 2, y );
    }
    
    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {   
        int valAt = valueForXPosition(x);
        
        if(valAt == darkTick) {
            Color origColor = g.getColor();
            g.setColor(origColor.darker());
            g.drawLine( x-1, 0, x-1, tickBounds.height - 2);    
            g.drawLine( x+1, 0, x+1, tickBounds.height - 2);    
            g.setColor(origColor);
        }
        if(valAt == redTick) {     
            Color origColor = g.getColor();
            g.setColor(Color.red);
            g.drawLine( x, 0, x, tickBounds.height - 2 );    
            g.setColor(origColor);
            return;
        }
        
        g.drawLine( x, 0, x, tickBounds.height - 2 );    
    }
   /* 
    protected void calculateTrackRect() {
	/*
        int centerSpacing = 0; // used to center sliders added using BorderLayout.CENTER (bug 4275631)
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    centerSpacing = thumbRect.height;
	   // if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
	  //  if ( slider.getPaintLabels() ) centerSpacing += getHeightOfTallestLabel();
	    trackRect.x = contentRect.x + trackBuffer;
	    trackRect.y = contentRect.y + (contentRect.height - centerSpacing - 1)/2;
	    trackRect.width = contentRect.width - (trackBuffer * 2);
	    trackRect.height = thumbRect.height;
	}
	else {
	    centerSpacing = thumbRect.width;
	    if (isLeftToRight(slider)) {
		//if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
	    	//if ( slider.getPaintLabels() ) centerSpacing += getWidthOfWidestLabel();
	    } else {
	       // if ( slider.getPaintTicks() ) centerSpacing -= getTickLength();
	    	//if ( slider.getPaintLabels() ) centerSpacing -= getWidthOfWidestLabel();
	    }
	    trackRect.x = contentRect.x ;//+ (contentRect.width - centerSpacing - 1)/2;
	    trackRect.y = contentRect.y ;//+ trackBuffer;
	    trackRect.width = contentRect.width;//thumbRect.width;
	    trackRect.height = contentRect.height; //- (trackBuffer * 2);
	}

    }*/
   
    private boolean isLeftToRight( Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }
    
}
