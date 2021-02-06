/*xs
 * Created on 18/10/2005
 *
 * @author Rene Wooller
 */
package gui;

import gui.musicArea.MorphComponent;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import music.BasicMorphMusicGen;
import ren.gui.LabelledView;
import ren.util.GB;

public class BasicMorphEditor extends JPanel{

    protected JFrame frame;
    
    protected MorphComponent mc;
    
    protected JComboBox mtempoCB = new JComboBox();
    
    protected JMenuBar menuBar;
    
    public BasicMorphEditor() {
        super(new GridBagLayout());
    }
    
    /**
     * this method is needed for resizing purposes
     * if the panel if going to be in a frame
     */
    public void setContainingFrame(JFrame f) {
        this.frame = f;
    }
    
    protected void construct(MorphComponent nmc, BasicMorphMusicGen bmmg) {
        this.mc = nmc;
        
        
        
        if(bmmg.getMorphTempo() == null)
            bmmg.setMorphTempo(new DefaultComboBoxModel());
        else
            this.mtempoCB.setModel(bmmg.getMorphTempo());
                
        //this.setSize(DimUtil.scaleScreen(0.5, 0.5));//this.add(new JLabel("morph editor"));
        LabelledView lv = (new LabelledView()).construct(bmmg.getMorphLength(), true, true, 
                "how long (in beats) it takes to complete morph", 0);
        
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("actions");
        menuBar.add(menu);
        JMenuItem prev = new JMenuItem("preview");
        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {   
                mc.select(null);
            }
        });
        menu.add(prev);
        
        GB.add(this, 0, 0, menuBar, 9, 1);
        
        GB.add(this, 0, 1, lv, 3, 1);//, 0, 0, 0, 0, 1.0, 1.0);
        
        GB.add(this, 0, 2, mtempoCB, 3, 1);
    }
}
