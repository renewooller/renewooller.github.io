package jmms.processor;

import grame.midishare.Midi;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.swing.JFrame;

import jm.music.data.Score;
import jmms.MPlayer;
import jmms.Sequencer;
import jmms.TickEvent;
import jmms.processor.gui.MidiIOView;
import ren.gui.components.RJFrame;
import ren.util.PO;

/*
 * Created on 17/01/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 * 
 * 17/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MapperAppl extends MPlayer implements
        Serializable {

    public static void main(String[] args) {
        new MapperAppl();

    }

    Sequencer s = new Sequencer();

    MidiProcessor mproc = new MidiProcessor();

    MidiOutputManager mom = new MidiOutputManager();

    ActuatorContainerManager aconman = new ActuatorContainerManager();

    /*
     * FileOutputStream fos; BufferedOutputStream obuff;
     * ObjectOutputStream obOut;
     */
    /**
     *  
     */
    MidiIOView miov;

    public MapperAppl() {
        super();

        s.addMidiInputListener(mproc);
        mproc.setSequencer(s);

        //mproc.addProcess(ProcessFactory.create("absolute
        // acceleration"));
        mproc.addMidiInputLocation(4, 15);
        mproc.addMidiInputLocation(4, 16);

        mom.createMidiOutputLocation(4, 15, " ");

        aconman.registerRoot(mproc);
        aconman.registerRoot(mom);

        miov = new MidiIOView();
        miov.construct(mproc, mom, aconman);

        JFrame jf = new RJFrame(true) {
            public void save() {
                writeOut();
            }

            public void load() {
                readIn();
            }
        };
        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //writeOut();
                s.close();
                System.exit(0);
            }
        });

        jf.getContentPane()
            .add(miov);

        jf.pack();
        jf.show();
    }

    public void readIn() {
        //		file input setup
        try {

            BufferedReader br = new BufferedReader(
                    new FileReader("t.tmp"));

            //	BufferedInputStream buf = new
            // BufferedInputStream(ois);
            //	if(ois.available() > 1) {
            //	System.out.println("reading");

            String str = br.readLine();

            this.aconman.resetUnfinishedPaths();

            mproc.loadString(str);

            mom.loadString(str);

            this.aconman.loadUninishedPaths();

            miov.update();//.//construct(mproc, mom,
                          // aconman);

            //}
            //mom.setMidiOutputLocations();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeOut() {

        try {
            PrintWriter pw = new PrintWriter(
                    new FileWriter("t.tmp"));
            System.out.println("--------------------- Writing temp File -----------------------");
            StringBuffer sb = new StringBuffer(
                    mproc.saveString());
            sb.append(mom.saveString());
            //System.out.println(xmlString);
            pw.print(sb.toString());
            pw.close();

            System.out.println("-------------------------------------------------------------");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see jmms.MPlayer#nextScore()
     */
    public Score nextScore() {
        mproc.tick(new TickEvent(this, Midi.GetTime()));

        return null;
    }

}