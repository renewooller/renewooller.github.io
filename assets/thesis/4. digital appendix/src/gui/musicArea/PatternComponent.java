package gui.musicArea;

import gui.LFrame;
import gui.PMGEditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import jmms.TickEvent;
import music.PatternMusicGenerator;

public class PatternComponent extends MGComponent {
	
	//private ScoreEditor editor;// = new ScoreEditor();
	private transient PMGEditor editor;
	
	private transient LFrame edf; // frame in which the editor appears
	
	public PatternComponent() {
		//System.out.println("making a patternComponet");
	}
	
	public PatternComponent construct(MusicArea area, PatternMusicGenerator pmg) {
		return this.construct(pmg.getPointID().x, pmg.getPointID().y, area, pmg);
	}
	
	public PatternComponent construct(int x, int y, MusicArea area) {
		return this.construct(x, y, area, (new PatternMusicGenerator()).constructPattern());
	}
	
	public PatternComponent construct(int x, int y, MusicArea area, PatternMusicGenerator pmg) {
		super.construct(x, y, 40, 40, pmg, area);
		((PatternMusicGenerator)mg).setPointID(new Point(x, y));
		initUI();
		this.addMenuItem("edit pattern");
        this.addMenuItem("create morph");
        this.addMenuItem("delete pattern");
		return this;
	}
	
	
	

	//public transient static String LB = ("<" + "PatternComponent" + ">");
//	public static String RB = ("</" + "PatternComponent" + ">");
	/*
	public String getXML() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("xloc = " + "\"" + this.getX() + "\"");
		sb.append("yloc = " + "\"" + this.getY() + "\"");
		
		
		
		return sb.toString();
	}
	
	public static PatternComponent createFromXML(String xml, MusicArea a) {
		int x = Save.getIntVar(xml, "xloc");
		int y = Save.getIntVar(xml, "yloc");
		PatternComponent pc = new PatternComponent(x, y, a);
		return pc;
	}
	*/
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		if (mo)
			g.setColor(Color.GRAY);
		if (md)
			g.setColor(Color.GREEN);

		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.CYAN);
		g.setFont(new Font("serif", 0, 15)); //Double.toString(mtracker.getPos())
		g.drawString(Double.toString(at), 2, 20);
		
		g.dispose();
	}
	
	private transient double at;
	public void tick(TickEvent e) {
		if(e.at() % 0.25 == 0) {
			this.at = e.at()%8.0;
			this.repaint();
		}
		//use this to synchronise animation
	}
	
	
	/**
	 * 
	 */
	public PatternMusicGenerator getPatternMusicGenerator() {
		//
		return (PatternMusicGenerator) (this.getMusicGenerator());
	}
	
	
	/**
	 * called when the music generator is going to be saved
	 *
	 */
	public void updatePointID() {
		
		((PatternMusicGenerator)this.mg).setPointID(this.getLocation());
	}

	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		updatePointID();
	}
	
	protected void menuFired(String c) {
		super.menuFired(c);
		if(c.equals("edit pattern")) {
			showEditor();
		} else if(c.equals("create morph")) {
            area.createMorph(this.getX(), this.getY(), "multi");
            area.mgcClicked(this);
        } else if(c.equals("delete pattern")) {
        	
        	area.remove(this);
        	area.repaint(this.getBounds());
        }
	}
	
	protected void showEditor() {
		edf.show();
	}

	public void initUI() {
		this.editor = (new PMGEditor())
			.construct(((PatternMusicGenerator) this.mg), this.area.getLPlayer());
		
		
		edf = new LFrame();
		edf.getContentPane().add(editor);
		edf.pack();
		edf.addWindowFocusListener(new WindowAdapter() {
			public void windowLostFocus(WindowEvent e) {
				if(!(e.getOppositeWindow() instanceof JDialog)) {
		//			e.getWindow().hide();
				}
			}
		});
	//	edf.setResizable(false);
		
	}
	/*
	
	private void writeObject(ObjectOutputStream oos) throws Exception {
		oos.defaultWriteObject();
		oos.writeObject(this.getBounds());
	}
	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		this.setBounds((Rectangle)ois.readObject());
	}*/
	


}