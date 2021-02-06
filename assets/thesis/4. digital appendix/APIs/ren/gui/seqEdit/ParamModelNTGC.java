/*
 * ParamNTGC.java
 *
 * Created on 17 February 2004, 13:40
 */

package ren.gui.seqEdit;
import ren.lang.DoubleValue;
//import ren.gui.ParameterMap;
/**
 *
 * @author  Rene Wooller
 */
public class ParamModelNTGC extends NoteToGraphicsConverter {
    
    private DoubleValue quantise, scope;
    
    /** Creates a new instance of ParamNTGC */
    //  public ParamNTGC() {
    //	super();
	//	scope = //new ParameterMap(4, 16, 4);
    //   }
    
    public ParamModelNTGC(DoubleValue quantise, DoubleValue scope) {
	super();
	this.setQuantise(quantise);
	this.setScope(scope);
    }
    
    public void setQuantise(DoubleValue quantise) {
        this.quantise = quantise;
    }
    
    public double getQuantise() {
	if(quantise == null)
	    return super.getQuantise();
       	return quantise.getValue();
    }
    
    public void setScope(DoubleValue scope) {
        this.scope = scope;
    }
    
    // change the pitch and then put a method in Part to snap it to the scale
    /**
     * getScope returns a DoubleValue because it is pushing it
     */
    public DoubleValue getScope() {
        return scope;
    }
    
    public int getPixelsViewed() {
        if(scope == null)
            return super.getPixelsViewed();
        else
            return (int)(scope.getValue()*this.getPixelsPerBeat());
    }
}
