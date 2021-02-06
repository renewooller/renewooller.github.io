/*
 * Created on 27/10/2005
 *
 * @author Rene Wooller
 */
package lplay;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.ButtonModel;
import javax.swing.JToggleButton;

import jmms.TickEvent;
import music.LPart;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ren.io.Domable;
import ren.io.Domc;
import ren.util.ODouble;
import ren.util.ODoubleCompare;
import ren.util.PO;

public class AutoPlayer implements Domable {

    // private double startBeat = -1;
    private double at = 0;

    private static final double INC = 0.0001;

    private TreeMap events = new TreeMap(new ODoubleCompare());

    private Object[] evlist;

    private int evi = -1;

    private JToggleButton.ToggleButtonModel rec, play;

    private int startSc = 0;

    private int endSc = 8;

    public AutoPlayer() {
        super();
        rec = new JToggleButton.ToggleButtonModel();
        play = new JToggleButton.ToggleButtonModel();

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == rec) {
                    if (rec.isSelected()) {
                        // startRec();
                    } else
                        stopRec();
                } else if (e.getSource() == play) {
                    if (play.isSelected()) {
                        // startPlay();
                    } else {
                        // stopPlay();
                    }
                }

            }
        };
        rec.addActionListener(al);
        play.addActionListener(al);
    }

    public void clearRecorded() {
        events.clear();
    }

    public void stopRec() {
        // rec.setSelected(false)
        // events.put((new ODouble()).construct(at), new
        // Finish());
    }

    public void initForStartPlay() {
        // this.startBeat = -1;
     //   PO.p("initing for startPlay: getting evlist");
        evlist = this.events.values().toArray();
  //      for (int i = 0; i < evlist.length; i++) {
   //         PO.p(evlist[i].toString());
  //      }
        evi = 0;
        
   //     PO.p("done");
    }

    public void tick(TickEvent e) {

        // set startbeat to be the nearest four, when it
        // comes up
        // if(startBeat == -1) {
        // if(e.at()%4.0 == 0)
        // startBeat = e.at();
        // else // don't do anything until it starts
        // return;
        // }

        at = (e.at() % (endSc - startSc)) + 1.0 * startSc;

        if (!this.play.isSelected())
            return;
        // PO.p("2 at = " + at);
    //    PO.p("    ev = " + evi);

  //      PO.p("at = " + at);
        if (evlist != null && evlist.length > 0 && play.isSelected()) {
            // play all the ones on this tick
            while (evi < evlist.length
                    && ((AutoEvent) (evlist[evi])).getAt() == at) {
                ((AutoEvent) evlist[evi]).play();
                evi++;
            }

            evi = evi % evlist.length;
        }

    }

    public void record(LPart lp, String name, boolean v) {
        // we are recording
        if (rec.isSelected()) {
            // PO.p("recording new event: " + name + "
            // v: " + v + " at : " + at);
            addToggleEvent(lp, name, v, at);
        }
    }

    private void addToggleEvent(LPart lp, String name, boolean v, double cat) {
        ToggleAutoEvent te = (new ToggleAutoEvent()).construct(lp, cat, name, v);
        addToggleEvent(te);
    }

    private void addToggleEvent(ToggleAutoEvent e) {
        ODouble tek = new ODouble();
        tek.v = e.getAt();

        // PO.p("recording\n1 tec v = " + tek.v);

        // so the tree can put two actions on the same
        // start
        while (events.containsKey(tek)) {
            // PO.p("shifting st...................");
            if (events.get(tek)
                .equals(e))
                break;
            else
                tek.v = tek.v + INC;
        }
        // PO.p("2 tec v = " + tek.v + " e.getAt() = " +
        // e.getAt());

        events.put(tek, e);
    }

    public ButtonModel getPlayModel() {
        return play;
    }

    public ButtonModel getRecModel() {
        return rec;
    }

    public void setStartScope(int n) {
        this.startSc = n;
    }

    public void setEndScope(int n) {
        this.endSc = n;
    }

    public int getStartScope() {
        return this.startSc;
    }

    public int getEndScope() {
        return this.endSc;
    }

    public void dload(Element e) {
        startSc = Integer.parseInt(e.getAttribute("startSc"));
        endSc = Integer.parseInt(e.getAttribute("endSc"));
        this.events.clear();
        NodeList teas = e.getElementsByTagName("TAE");
        for (int i = 0; i < teas.getLength(); i++) {
            ToggleAutoEvent t = (ToggleAutoEvent) Domc.lo(
                (Element) teas.item(i), ToggleAutoEvent.class, e);
            // PO.p("loading toggleEvent" + i);
            addToggleEvent(t);
        }

    }

    public void dsave(Element e) {
        e.setAttribute("startSc", String.valueOf(startSc));
        e.setAttribute("endSc", String.valueOf(endSc));
        Object[] evs = this.events.values()
            .toArray();
        for (int i = 0; i < evs.length; i++) {
            if (evs[i] instanceof ToggleAutoEvent) {
                e.appendChild(Domc.sa((AutoEvent) evs[i], "TAE",
                    e.getOwnerDocument()));
            }
        }

    }

    public void updateToPos(double d) {
        PO.p("\n1.events size = " + events.size());
        PO.p("pos = " + d);
        this.initForStartPlay();
        SortedMap past = events.subMap((new ODouble()).construct(0.0),
                                     (new ODouble()).construct(d+0.25));
        
        PO.p("events size = " + events.size());
        Object [] pastArr = past.values().toArray();
        PO.p("past length  = " + pastArr.length);
        for(int i=0; i< pastArr.length; i++) {
           ((AutoEvent)pastArr[i]).play();
           this.evi++;
           
        }
        PO.p("evi after update " + evi);
    }

    public int size() {
        return events.size();
    }

}
