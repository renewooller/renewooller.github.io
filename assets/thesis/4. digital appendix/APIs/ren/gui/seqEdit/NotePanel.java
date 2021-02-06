/*
 * NotePanelGUI.java
 *
 * Created on 3 February 2004, 16:41
 */

package ren.gui.seqEdit;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import jm.music.data.*;
import ren.tonal.*;
/**
 *
 * @author  Rene Wooller
 */
public class NotePanel extends JScrollPane implements MouseListener, 
						      MouseMotionListener {
    
    protected JPanel notePanel;
    
    protected NoteToGraphicsConverter converter;
    
    protected Part containingPart = new Part();

    public NotePanel() { 
       // 
    }
    
    public NotePanel construct() {
        construct(new NoteToGraphicsConverter());
        return this;
    }
    
    public void construct(NoteToGraphicsConverter ngc) {
        // super();
        this.converter = ngc;

        notePanel = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.BLACK);

                for (int i = 0; i < converter.getNoteRange(); i++) {
                    g.drawLine(0, i * converter.getPixelsPerTone(),
                        this.getPreferredSize().width, i
                                * converter.getPixelsPerTone());
                }

                // labels
                paintLabels(g);

                g.setColor(Color.DARK_GRAY);

                for (int i = 0; i * converter.getPixelsPerBeat() < this.getPreferredSize().width; i++) {

                    g.drawLine(i * converter.getPixelsPerBeat(), 0, i
                            * converter.getPixelsPerBeat(),
                        converter.getNoteRange() * converter.getPixelsPerTone());
                }
            }
        };

        notePanel.setLayout(null);
        notePanel.addMouseListener(this);
        notePanel.addMouseMotionListener(this);
        // put this notePanel into the converter so that
        // when the
        // user changes the size etc it automatically
        // resizes this notePanel

        notePanel.setPreferredSize(converter.getDimensions());

        this.setPreferredSize(new Dimension(notePanel.getPreferredSize().width
                + this.verticalScrollBar.getWidth() + 5,
                notePanel.getPreferredSize().height
                        + this.horizontalScrollBar.getHeight() + 5));

        this.getViewport()
            .add(notePanel);
        //this.setAutoscrolls(true);

    }

    
    
    /**
     * paints the labelling of the pitches
     */
    public void paintLabels(Graphics g) {
	for(int i=0; i<converter.getNoteRange(); i++) {
	    for(int j=0; 
		j< converter.getPixelsViewed()/converter.getPixelsPerBar(); 
		j++) {
		g.drawString(Integer.toString(converter.getHighestNote()-i),
			     2+converter.getPixelsPerBar()*j,
			     i*converter.getPixelsPerTone()+
			     (int)(converter.getPixelsPerTone()*0.8));
	    }
	}
    }

    public void scrollRectToNotes() {

        if(notePanel.getComponentCount() > 0) {
            Rectangle rect = ((JComponent)notePanel.getComponent(0)).getBounds();
            this.getViewport().scrollRectToVisible(
                new Rectangle(rect.x, 
			      rect.y-(notePanel.getPreferredSize().height/8), 
			      rect.width, rect.height)); 
            
        }
    }

    private DraggableNote dn;
    public void addPhrase(Phrase phr) {
	if(containingPart == null)
	    containingPart = new Part();

	containingPart.addPhrase(phr);
	//System.out.println("Adding Phrase in NotePanel - part = " + containingPart.toString());
	addDraggableNote(phr);

    }
    
    private void addDraggableNote(Phrase phr) {
	dn = new DraggableNote(phr, converter);
        dn.setBounds(converter.getX(phr), converter.getY(phr), 
                     converter.getWidth(phr), converter.getHeight());
        dn.setBackground(Color.RED);
        dn.setOpaque(true);

	// adding the dragnote on top of everything
        notePanel.add(dn, 0);
        notePanel.repaint();
    }
	

    public Part getPart() {
	return containingPart;
    }
    
    public void addPart(Part p) {
        Phrase [] phrases = p.getPhraseArray();
        for(int i=0; i< phrases.length; i++) {
            addPhrase(phrases[i]);  
        }
    }
    
    public void addPartToView(Part p) {
	Phrase [] phrases = p.getPhraseArray();
        for(int i=0; i< phrases.length; i++) {
            addDraggableNote(phrases[i]);  
        }
    }

    public void setPart(Part p) {
	removeDraggableNotes();
	jm.music.tools.Mod.wrapNotesInPhrases(p);
	containingPart = p;
	addPartToView(p);
	notePanel.repaint();
	//System.out.println(
    }

    public void removeNotes() {
	removeDraggableNotes();
	containingPart.empty();
    }

    private void removeDraggableNotes() {
	 for(int i=0; i<notePanel.getComponentCount(); i++) {
	     if(notePanel.getComponent(i) instanceof DraggableNote) {
		 notePanel.remove(i--);
	     }
	 }
    }

    public JPanel getNotePanel() {
	return notePanel;
    }
    
    public void mouseClicked(MouseEvent e) {
	/*
	if(e.getButton()== e.BUTTON1) {
	    this.addPhrase(new Phrase(new Note(converter
						 .getPitch(e.getY()),
						 converter.getQuantise()),
					converter.getBeatsX(startx)));
					} */
	//else if(e.getButton() == e.BUTTON2) {
    }

    

    public static double DEFAULT_RV = 0.25;
    private int startx;//, xdif;
    public void mousePressed(MouseEvent e) {
        startx = e.getX();
        lastDrag = e.getX();
        this.addPhrase(new Phrase(new Note(converter.getPitch(e.getY()),
                DEFAULT_RV), converter.getBeatsX(e.getX())));
    }
    private int lastDrag;
    public void mouseDragged(MouseEvent e) {
        // System.out.println("dragging");
        if (e.getX() < startx) {
            dn.dragStartTime(e.getX() - lastDrag);
            // System.out.println(" np drage start time
            // : " +( e.getX()-lastDrag));

            // dn.mouseDragged(e);
            // if(e.getX() < lastDrag)
            dn.dragDuration(lastDrag - e.getX());
            // else {
            // dn.dragDuration(e.getX( - lastDrag);
            // System.out.println("np d dur = " +
            // (e.getX()-lastDrag));

        } else {
            dn.dragDuration(e.getX() - lastDrag);
            //	System.out.println("n dur drag = "+ nn.getDuration());
        }
        lastDrag = e.getX();
    }
    
    public void mouseReleased(MouseEvent e) {
        dn.quantise();
        dn = null;
        /*
         * this.addPhrase(new Phrase(new
         * Note(converter.getPitch(e.getY()),
         * converter.getBeatsX(e.getX()- startx)),
         * converter.getBeatsX(startx)));
         */
    }

    
    public void mouseEntered(MouseEvent e) {
    }
	
    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {}
    
}
