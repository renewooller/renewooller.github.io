/*
 * ParamNTGC.java
 *
 * Created on 17 February 2004, 13:40
 */

package ren.gui.seqEdit;

import ren.gui.ParameterMap;
import ren.util.PO;

/**
 *
 * @author  Rene Wooller
 */
public class ParamNTGC extends NoteToGraphicsConverter {

	private ParameterMap quantise, scope, shuffle;

	/** Creates a new instance of ParamNTGC */
	public ParamNTGC() {
		//this(new ParameterMap(4, 16, 4));
	}
	
	public ParamNTGC(ParameterMap scope) {
		this(null, scope);
	}

	public ParamNTGC(ParameterMap quantise, ParameterMap scope) {
		super();
		this.setQuantise(quantise);
		this.setScope(scope);
	}
	
	public void construct(ParameterMap scope, ParameterMap quantise) {
		setQuantise(quantise);
		setScope(scope);
	}
    
    public void construct(ParameterMap scope, ParameterMap quantise, ParameterMap shuffle) {
        construct(scope, quantise);
        setShuffle(shuffle);   
    }
    

    
    
	public void setQuantise(ParameterMap quantise) {
		this.quantise = quantise;
	}

	public double getQuantise() {
		if (quantise == null) {
            PO.p("the quantise parameter hasn't been set");
			return super.getQuantise();
           
        }
       
		return quantise.getValue();
	}
    
    public int getQuantiseInPixels() {
       // int ret = (int)(this.getQuantise()*this.getPixelsPerBeat());
     //   if(ret <2)
      //      ret = 2;
     //   return ret;
        return super.getQuantiseInPixels();
    }
    

	public void setScope(ParameterMap scope) {
		this.scope = scope;
	}

	// change the pitch and then put a method in Part to snap it to the scale
	/**
	 * getScope returns a ParameterMap because it is pushing it
	 */
	public ParameterMap getScope() {
		return scope;
	}

	public int getPixelsViewed() {
		if (scope == null)
			return super.getPixelsViewed();
		else
			return (int) (scope.getValue() * this.getPixelsPerBeat());
	}

    public double getShuffle() {
        if(shuffle == null)
            return super.getShuffle();
        else
            return shuffle.getValue();
    }

    public void setShuffle(ParameterMap shuffle) {
        this.shuffle = shuffle;
    }

    public ParameterMap getQuantiseParam() {
        return this.quantise;
    }

    public ParameterMap getShuffleParam() {
        return this.shuffle;
    }
}