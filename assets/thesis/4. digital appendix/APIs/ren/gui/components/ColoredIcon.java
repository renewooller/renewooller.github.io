package ren.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import ren.gui.lookFeel.CustomColors;

public class ColoredIcon implements Icon {
	private int height;
	private int width;
        
        private Color color;
        
	public int getIconHeight() {
		return height;
	}
	public int getIconWidth() {
		return width;
	}
        
        public void setColor(Color c) {
            color = c;
        }
        
        public Color getColor() {
            return color;
        }
        
        public ColoredIcon(Color c) {
            super();
            this.color = c;
        }
        
	public void paintIcon(Component c, Graphics g, int x, int y) {
 
            int height = c.getBounds().height;// +10;
            int width = c.getBounds().width;// + 10;
            if(color == null) {	
                
		g.setColor(CustomColors.back.darker());
		g.fillRect(3, 3, width-6, height-6);
		
		g.setColor(new Color(0, 00, 0));
		g.drawRect(0, 0, width-1, height-1);
		g.setColor(new Color(40, 40, 40));
		g.drawRect(1, 1, width-3, height-3);
		g.setColor(new Color(80, 80, 80));
		g.drawRect(2, 2, width-5, height-5);
            } else {
                g.setColor(color);
                g.fillRect(3, 3, width-6, height-6);
		
                g.setColor(Color.black);
		g.drawRect(0, 0, width-1, height-1);
		g.setColor(color.darker().darker());
		g.drawRect(1, 1, width-3, height-3);
		g.setColor(color.darker());
		g.drawRect(2, 2, width-5, height-5);
            }
	}
        
     
}

