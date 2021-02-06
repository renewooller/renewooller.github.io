/*
 * NoteDragTab.java
 *
 * Created on 10 February 2004, 00:52
 */
package ren.gui.seqEdit;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import jm.music.data.*;
import ren.gui.lookFeel.*;
import java.awt.geom.*;

/* Wooller
 */
public class NoteDragTab extends DragTab implements MouseMotionListener {
    
    DraggableNote dn;
    
    /** Creates a new instance of NoteDragTab */
    public NoteDragTab(DraggableNote dn) {
        super(dn);
        this.dn = dn;
        this.addMouseMotionListener(this);
     //   this.set
    }
    
    public void mouseDragged(MouseEvent e) {
    //    System.out.println("dragged  " + (e.getX()-this.oldx));
        
        dn.dragDuration(e.getX()-this.oldx);
      //  this.oldx = e.getX();
    }
    
    public void mouseMoved(MouseEvent e) {}
    
    int oldx;
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        this.oldx = e.getX();
    }
    
}
