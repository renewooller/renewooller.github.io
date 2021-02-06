/*
 * Created on May 25, 2006
 *
 * @author Rene Wooller
 */
package gui.musicArea;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import ren.gui.DraggableComponent;
import ren.util.PO;

public class PartMorphComponent extends DraggableComponent {
	
	public static final int mx = 640;//320;
	public static final int my = 480;//240;
	public static int wic = 40; // estimated width of counter
	private static int snax = 20; // snap to side (0 or 1) factor
	
	
	private Sepamo sep;

	public PartMorphComponent() {
		super();
		// TODO Auto-generated constructor stub
		
	}
	
	public PartMorphComponent construct(Sepamo sep) {
		this.sep = sep;
		return (PartMorphComponent)super.construct(0, 0);
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		if (mo)
			g.setColor(Color.GRAY);
		if (md)
			g.setColor(Color.GREEN);

		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.CYAN);
		g.setFont(new Font("serif", 0, 15)); //Double.toString(mtracker.getPos())
		
		g.drawString(sep.getIDString(), 2, 20);
		
		g.dispose();
	}
	
	public void update() {
	//	PO.p("sep x = " + sep.x() + " mx = " + mx + " both = " + 
	//			(int)(sep.x()*this.mx));
		this.setLocation((int)(sep.x()*(this.mx*1.0)),
						 (int)(sep.y()*(this.my*1.0)));
	//	PO.p("new bounds " + this.getBounds().toString());
		repaint();
	}
}
