
package ren.gui.seqEdit;

import ren.gui.ParameterMap;
import ren.tonal.TonalManager;
import ren.util.PO;

public class TonalNTGC extends ParamNTGC {
    
    private TonalManager tm;

    public TonalNTGC(){}
    
    
    
    public TonalNTGC construct(TonalManager tm, ParameterMap scope, ParameterMap quantise, ParameterMap shuffle) {
       // PO.p("scope max in tonal ntgc = " + scope.getMax()); //this.getScope().getMax());
        super.construct(scope, quantise, shuffle);
        
    	this.tm = tm;
        return this;
    }
    
    
    
    //public TonalNTCG() {
    //	this(new TonalManager());
    //}
      
    /**
     * Get the Tm value.
     * @return the Tm value.
     */
    public TonalManager getTm() {
	return tm;
    }

    /**
     * Set the Tm value.
     * @param newTm The new Tm value.
     */
    public void setTm(TonalManager newTm) {
	this.tm = newTm;
    }

  ///  public int [] getTonalPitchesY() {}
	
    
    
}
