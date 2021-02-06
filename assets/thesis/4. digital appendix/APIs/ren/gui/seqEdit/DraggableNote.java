/*
 * DraggableNoteButton.java
 *
 * Created on 3 February 2004, 16:30
 */
package ren.gui.seqEdit;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.accessibility.Accessible;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jm.music.data.Phrase;
import ren.gui.components.LJSlider;
import ren.gui.components.NumberTextField;
import ren.gui.components.ValueListener;
import ren.util.Colors;
import ren.util.PO;

/**
 * 
 * @author Rene Wooller
 */
public class DraggableNote extends JComponent implements
        MouseListener, MouseMotionListener, Accessible {

    Phrase notePhrase;

    NoteToGraphicsConverter converter;

    int edge; // the width of the tab (that the user
              // presses to change note length

    private DragTab dt;

    private Popup vpp;
    private JPanel velpop;
    private LJSlider vslid;
    private JLabel velro; // read out for the velocity slider
    
    private NumberTextField pri;
    
    // in the notePanelGUI.
    // the individual note converters will take a phrase
    // and then, will bridge it.

    /** Creates a new instance of DraggableNoteButton */
    public DraggableNote(Phrase notePhrase,
        NoteToGraphicsConverter converter) {
        pri = new NumberTextField(0, 100, notePhrase.getPriority());
        initPri();
        this.notePhrase = notePhrase;
        this.converter = converter;
        this.edge = (int) (converter.getQuantise() * converter.getPixelsPerBeat());

        this.setBackground(Color.BLACK);
        this.setLayout(null);
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        // addFocusListener(this);
        this.setBounds(converter.getX(notePhrase),
            converter.getY(notePhrase),
            converter.getWidth(notePhrase),
            converter.getHeight());

        dt = new NoteDragTab(this);

        this.add(dt);
        dt.setLocation(this.getWidth() - dt.getWidth(),
            1);
        
        this.add(pri);
        /*
         * this.foregroundArea = new Area(new
         * RoundRectangle2D.Double(1, 1,
         * this.getWidth()-1, this.getHeight()-1,
         * this.getWidth()/2, this.getHeight()/2));
         * this.tab = (Area)foregroundArea.clone();
         * tab.subtract(new Area(new Rectangle(1, 1,
         * this.getWidth()-edge, this.getHeight()))); //
         * tab.getBounds().x++; // tab.getBounds().y++; //
         * tab.getBounds().y++; this.backgroundArea =
         * new Area(new Rectangle(0, 0, this.getWidth(),
         * this.getHeight()));
         * backgroundArea.subtract(foregroundArea);
         */
        this.setSizes();
        
        initVelPop();
    }
    
    private void initPri() {
        this.pri.addValueListener(new ValueListener() {
            public void valueGeneratorUpdate(int rv) {
                notePhrase.setPriority(rv);
            }
        });
      
        pri.setBackground(null);
        pri.setSize(25, 20);
        pri.setLocation(5, this.getHeight()-2);
        
        
    }
    
    private void initVelPop() {
        this.velpop = new JPanel();
        velpop.setLayout(new BoxLayout(velpop, 1));
        
        JLabel vl = new JLabel("velocity");
        
        velpop.add(vl);
        
        // initialise the slider that will change the velociy
        vslid = new LJSlider(1, 0, 127, 127);
        vslid.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                notePhrase.getNote(0).setDynamic(vslid.getValue());
                velro.setText(Integer.toString(vslid.getValue()));
            }
        });
        
        /*  mouse listeners don't work when it is in a popup for some reason.
         * need to look it up on online forums
         *
        vslid.addMouseListener(new MouseAdapter() {
            public void MousePressed(MouseEvent e) {
                PO.p("pressed");
            }
            public void MouseReleased(MouseEvent e) {
             
                PO.p(" mouseReleased");
            }
            public void MouseExited(MouseEvent e) {
                PO.p("exited");
            }
            public void MouseEntered(MouseEvent e) {
                PO.p("entered");
            }
            
        });
        
        velpop.addMouseListener(new MouseAdapter() {
            public void MousePressed(MouseEvent e) {
                PO.p("v pressed");
            }
            public void MouseReleased(MouseEvent e) {
               
                PO.p("v mouseReleased");
            }
            public void MouseExited(MouseEvent e) {
                PO.p("v exited");
            }
            public void MouseEntered(MouseEvent e) {
                PO.p("v entered");
            }
            
        });*/
        
        
        velpop.add(vslid);
        
        velro = new JLabel();
        velro.setText(Integer.toString(vslid.getValue()));
        velpop.add(velro);
        
    }

    protected void paintComponent(Graphics graphics) {
        //     super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        //   super.paintComponent(g);
        g.setColor(Color.RED);//this.getBackground());

        int width = getWidth() - 1;
        int height = getHeight() - 1;
        int x = 1;
        int y = 1;

        g.fillRoundRect(x, y, width, height,
            (int) ((width) / 4), (int) ((height) / 4));

        for (int i = 12; i > -1; i--) {
            //   System.out.println("asdf");
            g.setColor(Colors.brighterBy(-18,
                g.getColor()));
            g.drawRoundRect(x + (int) (i / 2), y
                    + (int) (i / 2), width - 1 - i,
                height - 1 - i,
                (int) ((width) / 4 - i),
                (int) ((height) / 4 - i));
        }

        g.dispose();
    }

    int oldX, oldY;

    private boolean mouseDown = false;

    private boolean mouseInTab = false;

    public void velocityPopup(MouseEvent e) {
        Point p = this.getLocationOnScreen();
        
        
        if(PopupFactory.getSharedInstance() == null) {
            PopupFactory.setSharedInstance(new PopupFactory()); 
        }
        vpp = PopupFactory.getSharedInstance().getPopup(this, velpop, p.x, p.y+this.converter.getPixelsPerTone());
        
        vpp.show();
       
        
      //  p.x += e.getX();
      //  p.y += e.getY();
       
        //doesn't wortk 
     //   this.getParent().add(this.velpop, 0);
     //   velpop.setOpaque(true);
      //  velpop.setLocation(p);
      //  velpop.setBounds(p.x, p.y, vslid.getWidth(), vslid.getHeight() + 10);
       // PO.p("just added velpop p.x = " + p.x + " p.y = " + p.y);
        
        
     //   this.getParent().repaint();
    }
    
    /**
     * removes this component from the panel and the
     * phrase from the part
     */
    public void dispose() {
        this.notePhrase.getMyPart()
            .removePhrase(notePhrase);
        Container p = this.getParent();
        p.remove(this);
        p.repaint();
    }
    
    
    public void mousePressed(MouseEvent ev) {

        oldX = ev.getX();
        oldY = ev.getY();
        //   System.out.println("oldx = " + oldX);
        mouseDown = true;
        
    }

    public void mouseDragged(MouseEvent ev) {
        //      System.out.println("drag ev.getX() " +
        // (ev.getX()+oldX) + " y " + (ev.getY()+
        // oldY));
        // oldx
        converter.setNotePos(this.notePhrase,
            this.getX() + ev.getX() - oldX
                    + converter.getQuantiseInPixels()
                    / 2, this.getY() + ev.getY() - oldY);
        setLocation(converter.getX(notePhrase),
            converter.getY(notePhrase));

        this.scrollRectToVisible(new Rectangle(
                ev.getX(), ev.getY(), 1, 1));//this.getBounds());
        //      this.repaint();
    }
   
    public void mouseClicked(MouseEvent e) {
        
        if (e.getButton() == e.BUTTON2
            || e.getButton() == e.BUTTON3) {
            
            if(e.getModifiersEx() != e.CTRL_DOWN_MASK)
                this.dispose();
            else {
                velocityPopup(e);
            }
        }
        
        
        //Since the user clicked on us, let's get
        // focus!
        requestFocusInWindow();
    }

    

    public void mouseEntered(MouseEvent e) {
        if(vpp != null)
            vpp.hide();
    }

    public void mouseExited(MouseEvent e) {
        if (mouseInTab && !mouseDown) {
            mouseInTab = false;
            //        repaint();
            //         this.paintImmediately(tab.getBounds());
            //        this.paintImmediately(tab);
            /// repaint(tab.getBounds());
            //  this.update(this.getGraphics());
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(vpp != null)
            vpp.hide();
                
        mouseDown = false;
        //      if(mouseInTab && e.getX() <) {
        //     }
    }

    public void mouseMoved(MouseEvent e) {

    }

    //  public NoteToGraphicsConverter getNTGC() {
    /// return this.converter;
    //  }
    boolean inSmallSection = false;

    int oldSmallX;

    public void dragDuration(int amount) {

        converter.changeNoteDuration(this.notePhrase,
            amount);

        this.setSizes();
    }

    public void dragStartTime(int amount) {
        converter.changeNoteStartTime(this.notePhrase,
            amount);

        setLocation(converter.getX(notePhrase),
            converter.getY(notePhrase));

    }

    public void quantise() {
        converter.quantise(notePhrase);
        setLocation(converter.getX(notePhrase),
            converter.getY(notePhrase));
    }

    private void setSizes() {

        int widthToBe = converter.getWidth(notePhrase);
        if (widthToBe < dt.DEFAULT_WIDTH) {
            widthToBe = dt.DEFAULT_WIDTH;
        }
        this.setSize(widthToBe, this.getHeight());

        //tabWidthToBe is equal to
        int tabWidthToBe = widthToBe / 2;
        // if tabWidthToBe is bigger than the default,
        // it equals the default
        if (tabWidthToBe > dt.DEFAULT_WIDTH)
            tabWidthToBe = dt.DEFAULT_WIDTH;
        //set it
        dt.setSize(tabWidthToBe, dt.getHeight());
        this.dt.setLocation(widthToBe - tabWidthToBe,
            dt.getY());
    }

}