package ren.gui.seqEdit;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import ren.gui.ParamEvent;
import ren.gui.ParamListener;
import ren.tonal.TonalManager;
import ren.util.PO;

public class PitchLighter extends JComponent {
    // use pitch until tonalityManager
    int pitch = 60;

    TonalNTGC cv;

 //   TonalManager tm;

    public PitchLighter(TonalNTGC tntgc) {
        super();
        this.cv = tntgc;
    //    this.tm = tntgc.getTm();
        this.setLayout(null);
       
        // this.setLocation(0, 0);
        // this.setPreferredSize(cv.getDimensions());
        // System.out.println(" width = " + getWidth());

        this.setBounds(0, 0, cv.getPixelsViewed(),
            cv.getYRange());
        // PO.p("bounds of pl = " +
        // this.getBounds().toString());
        
        // we don't need to update the pitch lighter
        // because the scope alredy repaints stuff.
      //  cv.getScope()
      //      .addParamListener(this);
    }

    private int[] tonalPitches;

    public void paintComponent(Graphics g) {
        this.setBounds(0, 0, cv.getPixelsViewed(),
            cv.getYRange());
        
        tonalPitches = cv.getTm().getTonalPitches(
            cv.getLowestNote(), cv.getHighestNote());

        for (int i = 0; i < tonalPitches.length; i++) {
            if (cv.getTm().isPitchClass(tonalPitches[i], 0)) {
                g.setColor(new Color(250, 220, 190));// the root
            } else if (cv.getTm().isPitchClass(tonalPitches[i], 5) ||
                    (cv.getTm().isPitchClass(tonalPitches[i], 7))) {
                g.setColor(new Color(130, 140, 160)); //4th and 5th
            } else {
                g.setColor(new Color(170, 190, 170));// others
            }

            g.fillRect(0, cv.getY(tonalPitches[i]),
                cv.getPixelsViewed(),
                cv.getPixelsPerTone());
        }

        // g.fillRect(0, 0, 300, 300);//getWidth(),
        // getHeight());
        // g.drawString("sdfsadfasdf", 10, 10);
        g.dispose();
    }

}
