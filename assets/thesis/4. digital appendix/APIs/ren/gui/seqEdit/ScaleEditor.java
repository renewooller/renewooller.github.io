/**
 *
 * uses a boolean representation so that it can update from the gui
 * easily, and then change the scale in the tuning system every time
 * it updates.
 */

package ren.gui.seqEdit;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import ren.tonal.*;

public class ScaleEditor extends JComponent {
    
    TuningSystem ts;
    TonalManager tm;
    boolean [] scale;

    /**
     *
     * 
     */
   public ScaleEditor (TuningSystem ts, TonalManager tm) {
    }
}
