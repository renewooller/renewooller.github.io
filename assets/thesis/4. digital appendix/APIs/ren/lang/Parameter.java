/*
 * Created on 22/12/2004
 *
 * @author Rene Wooller
 */
package ren.lang;

import javax.swing.BoundedRangeModel;

/**
 * @author wooller
 *
 *	parameter has a bounded range model which is the single point of data storage
 *  
 *  it also has a map that is used to translate the value into a certain type eg from int to
 *  double
 * 
 *  ValueLabelledComponentHolders will use BRM's changelistener to trigger an update on the label, which
 *  will refer to this parameter or this parameter's map for the value...
 *
 */
public class Parameter {

	private BoundedRangeModel brm;
	
	/**
	 * 
	 */
	public Parameter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
	}
}
