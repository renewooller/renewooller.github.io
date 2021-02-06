/**
 * A button appears on the music area, and a little note editor is <br>
 * produced when it is clicked on.
 * 
 * The LPlayer asks for a score for every resolutionhmm.... it would be better
 * not to use jMusic for this one.. it would mean rewriting the player code
 * 
 * The morphComponent is a wrapper for the actuall musicGeneratorComponent which
 * is a private class of this. This is necessary to draw the line between the
 * two pattern components
 *  
 */

package gui.musicArea;

//import gui.Dim;
import gui.BasicMorphEditor;
import gui.LFrame;
import gui.MorphEditor;
import gui.MultiMorphEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;

import jmms.TickEvent;
import lplay.LPlayer;
import music.BasicMorphMusicGen;
import music.MorphTracker;
import music.MultiMorph;
import music.singlePart.MorphMusicGenerator;
import ren.util.JUtil;
import ren.util.PO;

//import ren.gui.seqEdit.*;

public class MorphComponent extends MGComponent implements MorphEndListener {

	private MorphHandle mhandle = new MorphHandle();

	private MTrackComp mtc = new MTrackComp();

	private MusicArea area;

	private BasicMorphEditor editor;

    private BasicMorphMusicGen mmg;
    
	private LFrame lf = new LFrame();

	public MGComponent m1, m2;

//    private int m12dist;
    
	public MorphComponent() {
		super();
		
	}
	
	public MorphComponent construct(MGComponent m1, MGComponent m2,
			LPlayer lplay, MusicArea area) {
	    return this.construct(m1, m2, 
            lplay, 
            area,
            (new MultiMorph()).
            constructMorph(m1.getMusicGenerator(), m2.getMusicGenerator()));
	}
	
	public MorphComponent construct(MGComponent nm1, MGComponent nm2,
			LPlayer lplay, MusicArea area, BasicMorphMusicGen mmg) {
		this.setLayout(null);

        this.mmg = mmg;
		super.construct(mmg, area);

        if(nm1 == null)
            PO.p("m1null");
        if(nm2 == null)
            PO.p("m2null");
        if(mmg == null)
            PO.p("mmgnull");
        
        
		//check to see if the generator has the same generators as the components
		if(nm1.getMusicGenerator() != mmg.getFrom()||
				nm2.getMusicGenerator() != mmg.getTo()) {
            PO.p("constructing morph explicitly, and need to reassign musicGenerators");
			mmg.setFrom(nm1.getMusicGenerator());
			mmg.setTo(nm2.getMusicGenerator());
		
		}
		
		this.m1 = nm1;
		this.m2 = nm2;

		this.area = area;
	
		initUI();
		
		this.addMenuItem("edit morph");
		this.addMenuItem("delete");
		
		mtc = (new MTrackComp()).construct(((BasicMorphMusicGen)this.mg).getMorphTracker());
		this.removeMouseListener(this);
		this.removeMouseMotionListener(this);
		
		mtc.addMouseListener(this);
		mtc.addMouseMotionListener(this);
		
		mhandle.addMouseListener(this);
		mhandle.addMouseMotionListener(this);
		
		updateBounds();

		szDf = (mhandle.getWidth() - mtc.getWidth())/2;

		((BasicMorphMusicGen)this.mg)
				.setMorphEndListener(this);
		((BasicMorphMusicGen)this.mg).setCueManager(area);

		this.add(mtc);
		this.add(mhandle);
		this.setPopupRelativeTo(mhandle);
		
		return this;
	}
	
	/**
	 * this is for setting the position of the tracker manually
	 */
	
	public void mouseDragged(MouseEvent e) {
        double tdx = e.getX() + mtc.getLocation().x;
     //   double tdy = e.getY() + mtc.getLocation().y;
        // if it is lonitudinal
	    if(this.getWidth() > this.getHeight()) {
	        if(c1.x < c2.x) { // forwards
	            this.mtc.getMorphTracker().setPos(tdx/(c2.x-c1.x));
	        } else { // backwards
	            this.mtc.getMorphTracker().setPos(1 - tdx/(c1.x-c2.x));
	        }
	    } else {
	        if(c1.y < c2.y) { // top to bottom
	            this.mtc.getMorphTracker().setPos(tdx/(c2.y-c1.y));
	        } else {  // bottom to top
	            this.mtc.getMorphTracker().setPos(1 - (tdx/(c1.y-c2.y)));
	        }
	    }
	    
	    this.mtc.repaint();
	   
	}
	
	public void initUI() {

        initEditor();

		lf.getContentPane().add(editor);
        
		this.editor.setContainingFrame(lf);
        
		lf.pack();
	/*	lf.addWindowFocusListener(new WindowAdapter() {
			public void windowLostFocus(WindowEvent e) {
			    if(e.getOppositeWindow() == null)
			        return;
			    
			    if(e.getOppositeWindow().getFocusableWindowState())
			        e.getWindow().setVisible(false);	
			}
		}); */
		this.setDisableDrag(true);
		
	}
	
    private void initEditor() {

        // initialise the editor
        if(this.mmg instanceof MorphMusicGenerator) {
            this.editor = new MorphEditor();
            ((MorphEditor)editor).constructMedit(this);
        } else if(this.mmg instanceof MultiMorph) {
            this.editor = new MultiMorphEditor();
            ((MultiMorphEditor)editor).constructMulti(this, (MultiMorph)this.mmg, lf);
        } else {
            try {
                Exception ex = new Exception("unknown type of morph music generator");
                ex.fillInStackTrace();
                throw ex;
            } catch(Exception ex) {ex.printStackTrace();}
        }
            
    }
    
	private void updateBounds() {
		c1 = JUtil.getCentre(m1);
		c2 = JUtil.getCentre(m2);

		Rectangle r = new Rectangle(0, 0, 0, 0);
		if (c1.x < c2.x) {
			r.width = c2.x - c1.x;
			r.x = c1.x;
		} else {
			r.width = c1.x - c2.x;
			r.x = c2.x;
		}
		if (c1.y < c2.y) {
			r.height = c2.y - c1.y;
			r.y = c1.y;
		} else {
			r.height = c1.y - c2.y;
			r.y = c2.y;
		}
//		m12dist = RMath.getHyp(c1, c2); // used for converting to morphtracker

		c3 = JUtil.getCentre(m1.getX(), m1.getY(), m2.getX(), m2.getY());
		r.add(new Rectangle(c3.x, c3.y, mhandle.getWidth(), mhandle.getHeight()));

		c4 = JUtil.getCentrelinePos(m1.getX(), m1.getY(), m2.getX(), m2.getY(),
				mtc.getPos());
		c4.x += szDf;
		c4.y += szDf;
		r.add(new Rectangle(c4.x, c4.y, mtc.getWidth(), mtc.getHeight()));

		this.setBounds(r.x, r.y, r.width, r.height);
		c3.x -= this.getX();
		c3.y -= this.getY();

		c4.x -= this.getX();
		c4.y -= this.getY();

		mhandle.setBounds(c3.x, c3.y, mhandle.getWidth(), mhandle.getHeight());//c3.x-this.getX(),
																   // c3.y-this.getY(),
																   // 40, 40);

		mtc.setBounds(c4.x, c4.y, mtc.getWidth(), mtc.getHeight());
	}

	private int szDf; // difference in size

	private transient Point c1, c2, c3, c4;

//	private int rx, ry;

	

	public void paintComponent(Graphics graphics) {
		Graphics g = graphics.create();
		updateBounds();
		g.drawLine(c1.x - this.getX(), c1.y - this.getY(), c2.x - this.getX(),
				c2.y - this.getY());
		
		g.setColor(Color.GRAY);
		g.drawString("src", c1.x+50-this.getX(), c1.y +50-this.getY());
		
		g.dispose();
	}

	public void hideBall(boolean hidden) {
		this.mtc.setVisible(!hidden);
	}

	/**
	 * sets the next (cued music generator) to play
	 * 
	 * hides the ball
	 * 
	 * @see gui.musicArea.MorphEndListener#morphEnded()
	 */
	public void morphEnded() {
		//	System.out.println("morphEnded - cued MG = " +
		// ((BasicMorphMusicGen)mhandle.getMorphMusicGenerator()).getCuedMG().toString());
		hideBall(true);

	//	this.area.mgPlay(((BasicMorphMusicGen)this.mg)
	//			.getCuedMG());

	}

	public void morphStarted() {
		hideBall(false);
	}

	public void setMusicArea(MusicArea a) {
		this.area = a;
	}

	public void tick(TickEvent e) {
		if (e.at() % this.mtc.getMorphTracker().getUpdateRes() == 0) {
			//System.out.println("updating graphics");
			if (mg.isPlaying()) // was meant to stop the ball appearing at the wrong end
				repaint();
		}
	}

	public void select(MouseEvent e) {

		//updates the number of parts
		this.mg.select();  // this happends in MGComponent.select again

		//handles the creation of morphs, sets the transform chain and cues the
		// music generator if the
		// mouseevent has ctrl pressed down
		super.select(e);

		//	make the green ball visible
		//	metamc.hideBall(false);

	}
	
	protected void menuFired(String s) {
		if (s.equals("edit morph")) {
			this.showEditor();
		} else if(s.equals("delete")) {
			this.area.delete(this);
			
		}
	}
	
	public void showEditor() {
		lf.setLocationRelativeTo(this.mhandle);
		lf.setVisible(true);
	}

	public BasicMorphMusicGen getMMG() {
		
		return this.mmg;
	}

	public Component getMTC() {
		return this.mtc;
	}
	
	
	
}

class MorphHandle extends JComponent {

	public MorphHandle() {
		this.setBounds(0, 0, 40, 40);
	}

	public void paintComponent(Graphics graphics) {
		Graphics g = graphics.create();
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.fillOval(0, 0, this.getWidth(), this.getHeight());
		//((Graphics2D)g).fill(this.getBounds());
		g.dispose();
	}

}

class MTrackComp extends JComponent implements MouseListener, MouseMotionListener {

	private MorphTracker mtracker;

	public MTrackComp() {
		super();
	}

	public MTrackComp construct(MorphTracker mtracker) {
		this.setSize(30, 30);
		this.setBounds(new Rectangle(0, 0, 30, 30));
		//this.x = 0;
		//this.y = 0;

		//super.construct(0, 0, 30, 30);
		//	this.setDisableDrag(true);
		this.mtracker = mtracker;
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		//	this.setSize(30, 30);
		return this;
	}

	public MorphTracker getMorphTracker() {
	    return mtracker;
	}
	
	public double getPos() {
		return mtracker.getPos();
	}

	public void paintComponent(Graphics graphics) {
		Graphics g = graphics.create();
		super.paintComponent(g);
		g.setColor(Color.GREEN);
		//if(!md1)
		//		g.setColor(Color.GREEN);
		//	else
		//		g.setColor(Color.RED);

		g.fillOval(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK);
		g.setFont(new Font("serif", 0, 15)); //Double.toString(mtracker.getPos())

		g.drawString(Double.toString(mtracker.getPos()), 2, 20);//this.getX(),
																// this.getY());
																// //, 0);
		//g.drawRect(0, 0, 20, 20);

		//((Graphics2D)g).fill(this.getBounds());
		g.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		this.mtracker.setUpdating(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		this.mtracker.setUpdating(true);
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
  //      System.out.println("x " + e.getX() + " y " + e.getY());
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

}