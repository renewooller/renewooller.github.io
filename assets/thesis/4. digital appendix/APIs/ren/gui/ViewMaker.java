/*
 * Created on 26/01/2005
 *
 * @author Rene Wooller
 */
package ren.gui;

import javax.swing.DefaultBoundedRangeModel;

import ren.gui.components.LJSlider;

/**
 * This class creates a valuegenerator component using a ParameterMap to do it
 * It is so smart that it works out wether the parameterMap you give it has already been used.?
 * if it has been used, you have the option of just making a copy of the valueGenerator that has already
 * been made for it?
 * 
 * 
 * @author wooller
 *
 *26/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class ViewMaker {

	
	/**
	 * 
	 */
	public ViewMaker() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public LJSlider createSlider(ParameterMap pm, int orient) {
		DefaultBoundedRangeModel brm = (DefaultBoundedRangeModel)pm.getModel();
		LJSlider svg = new LJSlider();
		
		svg.setModel(brm);
		svg.setOrientation(orient);
		svg.init();
		return svg;
	}
	
}
