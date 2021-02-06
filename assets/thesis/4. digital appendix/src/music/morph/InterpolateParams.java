/*
 * Created on 13/01/2005
 *
 * @author Rene Wooller
 */
package music.morph;

import javax.swing.BoundedRangeModel;

import org.w3c.dom.Element;

import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 *13/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class InterpolateParams extends ParamMorpher {

	/**
	 * 
	 */
	public InterpolateParams() {
		super();
		// TODO Auto-generated constructor stub
	}
	
    
    public double morph(double from, double to, double amount) {
        return ((from*(1-amount)) + (to*(amount)));
    }
    
	/**
	 * 
	 * @see music.morph.ParamMorpher#morph(ren.gui.ParameterMap, ren.gui.ParameterMap, ren.gui.ParameterMap, double)
	 */
	public ParameterMap morph(ParameterMap from, ParameterMap to,
			ParameterMap morphParam, double amount) {
		
	    BoundedRangeModel morphModel = morphParam.getModel();
	    BoundedRangeModel fromModel = from.getModel();
	    BoundedRangeModel toModel = to.getModel();
	    
	 //   System.out.println("amnt = " + amount + " b4    from = " + from.getValue() + "  to = " + to.getValue() + " morph = " + morphParam.getValue());
	    
	    
	    morphModel.setValue((int)(fromModel.getValue()*(1-amount) + toModel.getValue()*(amount)));
	    
        /*
        Exception e = new Exception();
        e.fillInStackTrace();
        
        try {throw e;
        }catch(Exception ex) {
            ex.printStackTrace();
        }*/
	//	morphParam.setValue(from.getValue()*(1-amount) + to.getValue()*(amount));//Math.abs((from.getValue() - to.getValue())*amount)+morphParam.getMin());
		
//	    System.out.println("after from = " + from.getValue() + "  to = " + to.getValue() + " morph = " + morphParam.getValue());
	
	 //   if(to == from)
	 //       System.out.println("to = from");
	    
		return morphParam;
	}

	/* (non-Javadoc)
	 * @see music.morph.ParamMorpher#getPC()
	 */
	public ParameterMap[] getPC() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see music.morph.ParamMorpher#getType()
	 */
	public String getType() {
		return "interpolate parameters";
	}

    public void dload(Element e) {
        e.setAttribute("type", this.getType());
    }

    public void dsave(Element e) {
        e.setAttribute("type", this.getType());
    }

}
