/*
 * DragTab.java
 *
 * Created on 9 February 2004, 23:58
 */
package ren.gui.seqEdit;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import jm.music.data.*;
import ren.gui.lookFeel.CustomColors;
import java.awt.geom.*;
/**
 *
 * @author  Rene Wooller
 */
public class DragTab extends JComponent implements Accessible, MouseListener{
    
    public boolean mouseIn = false;
    public boolean mousePressed = false;
    
    private JComponent tabFor;
    public static int DEFAULT_WIDTH = 15;
    
    /** Creates a new instance of DragTab */
    public DragTab(JComponent tabFor) {
        this.addMouseListener(this);
        this.tabFor = tabFor;
        this.setBounds(1, 0, DEFAULT_WIDTH, tabFor.getHeight());
        this.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
    }
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
        mouseIn = true;
        this.repaint();
    }
    
    public void mouseExited(MouseEvent e) {
        mouseIn = false;
        this.repaint();
    }
    
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }
    
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }
    
    protected void paintComponent(Graphics graphics) {
        
        Graphics g = graphics.create();
        super.paintComponent(g);
        if(mouseIn)
            g.setColor(new Color(20, 255, 20));
        else 
            g.setColor(new Color(255, 20, 20));
        
      //  g.fillRect(0, 0, this.getWidth(), this.getHeight()); //, tabFor.getWidth()/4, tabFor.getHeight()/4);
        g.clearRect(this.getWidth()/2, 0, this.getWidth()/2 + 1, this.getHeight());
        
     //   g.fillOval(0, 0, this.getWidth(), this.getHeight());
        g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), tabFor.getWidth()/4, tabFor.getHeight()/4);
    //    g.dispose();
    }
}
