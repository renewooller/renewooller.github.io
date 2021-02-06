/*
 * Created on 3/10/2005
 *
 * @author Rene Wooller
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import music.LPart;
import ren.gui.LabelledView;
import ren.gui.components.VGJComboBox;
import ren.gui.seqEdit.BeatTracker;
import ren.gui.seqEdit.DraggableBTracker;
import ren.gui.seqEdit.PartEditor;
import ren.gui.seqEdit.PitchLighter;
import ren.gui.seqEdit.TonalNTGC;
import ren.tonal.Scales;
import ren.tonal.TonalManager;
import ren.util.DimUtil;
import ren.util.GB;
import ren.util.PO;

public class LPartEditor extends PartEditor{

    private PitchLighter pl;
    
    private VGJComboBox vkey, vsca; // view of key signature and scale
    
    private LPart lp;

    private BeatTracker beatTracker;
    
    private CCEditor ccedit;
    
    public LPartEditor(LPart lp) {
        super(((new TonalNTGC()).construct(lp.getTonalManager(), 
            lp.getScope(), lp.getQuantise(), lp.getShuffle())), 
            lp.getPart());
        
      //  PO.p("lpeditor constructor scope = " + lp.getTransformChain().getScopeParam().getMax());
        
        pl = new PitchLighter((TonalNTGC)this.getParamNTGC());
       
        this.lp = lp;
        
        super.getNotePanel().getNotePanel().add(pl, -1);
        
        vkey.setSelectedIndex(this.lp.getTonalManager().getRoot());
        vsca.setSelectedIndex(this.lp.getTonalManager().getScaleType());
        
        
        this.ccedit = (new CCEditor()).construct(lp);
       /*
        PO.p("ccedit height = " + ccedit.getPreferredSize().height);
        PO.p(" nopanel pref height = " + super.getNotePanel().getPreferredSize().height);
        super.getNotePanel().setPreferredSize(
        		DimUtil.mod(super.getNotePanel().getPreferredSize(), 0, 
        				-ccedit.getPreferredSize().height));
        super.getNotePanel().setMaximumSize(super.getNotePanel().getPreferredSize());
        
        PO.p(" nopanel pref height 2 = " + super.getNotePanel().getPreferredSize().height);
        //this.doLayout();
        */
        GB.add(this, 0, 16, ccedit, 15, 5);
        
       // this.doLayout();
       // this.getLayout().layoutContainer(this);
       // PO.p(" nopanel actual height = " + super.getNotePanel().getSize().height);
    }
    
    public void initialise() {
        super.initialise();
                
        vkey = new VGJComboBox(new String [] {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"});
        vkey.setName("key");
        
        LabelledView lvk = (new LabelledView()).construct(vkey, "set the key signature of the part", 0);
        
        vkey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                lp.getTonalManager().setRoot(vkey.getSelectedIndex());
                repaint();
            }
        });
               
        vsca = new VGJComboBox(Scales.getInstance().getScales());
        vsca.setName("scale");
       
        LabelledView lvs = (new LabelledView()).construct(vsca, "set the scale/tonality of this part", 0);
        
        vsca.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lp.getTonalManager().setScale(vsca.getSelectedIndex());
                repaint();
            }
        });  
        
        GB.add(this, 11, 0, lvk, 1, 1, 0, 0, 0, 2, 0.0, 0.0, false, false);
        GB.add(this, 12, 0, lvs, 1, 1, 0, 0, 0, 3, 0.0, 0.0, false, false);
        
    }

    public void setLPart(LPart lp) {
        this.lp = lp;
        
        setTonalManager(lp.getTonalManager());
        
        this.ccedit.setLPart(lp);
       // super.setQuantise(lp.getQuantise());
      //  super.setShuffle(lp.getShuffle());
         
        this.setPart(lp.getPart());
        
        super.setScope(lp.getScope());          
        super.setQuantise(lp.getQuantise());
        super.setShuffle(lp.getShuffle());
        
        if(beatTracker != null)
        this.beatTracker.setScope(lp.getScope());
     //   setScope(getScope());
     //   this.repaint();
      //  this.doLayout();
  //      this.setVisible(true);
  //      super.getNotePanel().setComponentZOrder(pl, 0);
        pl.setVisible(true);
    //    PO.p("z order" + super.getNotePanel().getComponentZOrder(pl));
      //  PO.p("size = " + pl.getBounds().toString());
      //  PO.p("visble rect = " + pl.getVisibleRect());
     //   PO.p(" preferredSize = " + pl.getPreferredSize().toString());
    }
    
    public void setTonalManager(TonalManager tm) {
        if(this.getParamNTGC() instanceof TonalNTGC) {
            ((TonalNTGC)this.getParamNTGC()).setTm(lp.getTonalManager());
        }
        
        this.updateTonalView();
    }
    
    private void updateTonalView() {
        vkey.setSelectedIndex(this.lp.getTonalManager().getRoot());
        //PO.p("tm root = " + this.lp.getTonalManager().getRoot());
        vsca.setSelectedIndex(this.lp.getTonalManager().getScaleType());
        vkey.repaint();
        vsca.repaint();
    }
    public void addBeatTracker(BeatTracker beatTracker) {
        this.beatTracker = beatTracker;
        DraggableBTracker dbt = new DraggableBTracker(this.getParamNTGC(), beatTracker);
        super.getNotePanel().getNotePanel().add(dbt, 0);
        beatTracker.setScope(this.getScope());
        this.validate();
    }
    
    // 
    public CCEditor getCCEditor() {
    	return this.ccedit;
    }
    
}
