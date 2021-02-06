/*
 * Created on 7/01/2005
 *
 * @author Rene Wooller
 */
package ren.gui;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.Serializable;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import ren.gui.components.NumberTextField;
import ren.gui.components.VGJComboBox;
import ren.gui.lookFeel.CustomDimensions;
import ren.gui.lookFeel.CustomFonts;
import ren.util.GB;
import ren.util.PO;

/**
 * @author wooller
 *
 *7/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 * Labelled view is a component that provides the readout and label for components
 * 
 * It is not fully implemented:
 * It will link the bounded range model to the label directly and if you make a copy
 * of it, it will make a copy of the view and the component/ValueGenerator (if copy isn't 
 * implemented, might as well use valueGenerator) and puts the bounded range model into it.
 *
 * OK - it has the same paramcomponent, but it makes a copy of the slider view in that param
 * component.
 *
 */
public class LabelledView extends JPanel implements Serializable {

	private ParameterMap paramMap;
	private JComponent vg;
	private JLabel readout;
	private JLabel name;
	private BoundedRangeModel model;
	private Box b;
	private JLabel ptitl, prout;
	private boolean readoutOn;
	private boolean labelOn;
	private VGLabelListener vgll;
	private ViewMaker vmaker = new ViewMaker();
	
    private int vgcbOrient = 0; 
    
	public JComponent getModel() {
		return vg;
	}
	
	public void setOrientation(int orient) {
		setOrientation(orient, true);
	}
	
	public void setOrientation(int orient, boolean defaultSizes) {
		
		if(defaultSizes) {
			if(vg instanceof JSlider) {
				if(orient == 1) {
					((JSlider)vg).setPreferredSize(CustomDimensions.vslid());
				} else if(orient == 0) {
					((JSlider)vg).setPreferredSize(CustomDimensions.hslid());
				}
			}
		}
		if(vg instanceof JSlider) {
			((JSlider)vg).setOrientation(orient);
			
		}
		this.layoutLabels();	
		
	}
	
	public LabelledView() {}
	
    
    public LabelledView construct(VGJComboBox cb, String toolText, int orientation) {
        this.setLayout(new BorderLayout());
        ptitl = new JLabel(cb.getName());
        ptitl.setToolTipText(toolText);
        cb.setToolTipText(toolText);
        
        this.readoutOn = false;
        this.labelOn = true;
        this.vg = cb;
        this.layoutLabels();
       
        return this;
        
    }
    
	public LabelledView construct(ParameterMap pm) {
		return this.construct(pm, "");
	}
	public LabelledView construct(ParameterMap pm, String toolText) {
		return construct(pm, true, true, toolText, 1);
	}
	public LabelledView construct(ParameterMap pm, int orientation) {
		return construct(pm, true, true, "", orientation);
	}
	
	/**
	 * only creates sliders atm.  other versions will be made as needed
	 * @param pc
	 * @param labelOn
	 * @param readoutOn
	 * @param toolText
	 * @param orient
	 */
	public LabelledView construct(ParameterMap pc, boolean labelOn,
			boolean readoutOn, String toolText, int orient) {
		
		if(vg != null) {
			
		}
		
		this.readoutOn = readoutOn;
		this.labelOn = labelOn;
		this.paramMap = pc;
		this.setLayout(new BorderLayout());
		
		//construct
		prout = new JLabel(Double.toString(pc.getValue()));
		ptitl = new JLabel(pc.getName());
		if(ptitl.getText().length() > 4) {
			ptitl.setFont(ptitl.getFont().deriveFont((float)Math.max((ptitl.getFont().getSize()-
					(ptitl.getText().length()-4)*0.7), 9)));
		}
		prout.setFont(CustomFonts.parameterLabel());
		vgll = new VGLabelListener(prout);
		pc.addParamListener(vgll);
		
		vg = vmaker.createSlider(pc, orient);//(JSlider)(((SliderVG)pc.getValueGenerator()).copyVGLink());
		
		prout.setAlignmentX(prout.LEFT_ALIGNMENT);//LEFT_ALIGNMENT);//CENTER_ALIGNMENT);
		//put the labels in different places depening on slider orientation
		b = new Box(1);
		b.add(prout);
		b.setAlignmentX(b.CENTER_ALIGNMENT);
		b.setAlignmentY(b.TOP_ALIGNMENT); // nothing
		ptitl.setAlignmentY(ptitl.TOP_ALIGNMENT); // does nothing
	
		//make sure the box is always as wide as it will get, by increment
		//ing through all th possible values 
		int widest = 0;
		pc.resetIncrement();
	//	PO.p("pc value before = " + pc.getValue());
		while (pc.incrementValue()) {
		//	PO.p("pc.value = " + pc.getValue());
			prout.setText(Double.toString(pc.getValue()));
			if (widest < (int) prout.getPreferredSize().getWidth())
				widest = (int) prout.getPreferredSize().getWidth();
			
			
		}

		b.add(Box.createHorizontalStrut((int)(widest*2)));
		layoutLabels();
		
		if (toolText != null) {
			ptitl.setToolTipText(toolText);
			if (vg instanceof JComponent)
				((JComponent) vg).setToolTipText(toolText);
		} else {
			if (vg instanceof JComponent) {
				ptitl.setToolTipText(((JComponent) vg)
						.getToolTipText());
			}
		}
		return this;
	}	
	
	public void dispose() {
		
		paramMap.removeParamListener(vgll);
		
	}
	
	private void layoutLabels() { 
				
		if (vg instanceof JSlider) {
			
			if (((JSlider) vg).getOrientation() == 1) {
				this.setLayout(new GridBagLayout());
				//this.add(vg, BorderLayout.CENTER);
				
				if (readoutOn) {
					GB.add(this, 1, 13, b, 1, 1);
					//this.add(b, BorderLayout.SOUTH);
				}if (labelOn) {
					GB.add(this, 0, 0, ptitl, 3, 1);
					//this.add(ptitl, BorderLayout.NORTH);
				}
				GB.add(this, 0, 1, vg, 3, 12);
				
			} else {
				Box bo = new Box(0);
								
				this.add(vg, BorderLayout.SOUTH);
				
				if (labelOn)
					bo.add(ptitl);
					//this.add(ptitl, BorderLayout.CENTER);
				bo.add(Box.createHorizontalStrut(10));
				
				if (readoutOn)
					bo.add(b);
					//this.add(b, BorderLayout.EAST);
				this.add(bo, BorderLayout.WEST);
			}
		} else {
			this.add(vg, BorderLayout.CENTER);
			if (vg instanceof NumberTextField
				|| vg instanceof VGJComboBox) {
				if (labelOn) 
					this.add(ptitl, BorderLayout.WEST);
			} else if(vg instanceof VGJComboBox) {
			    if(labelOn) {
                    if(this.vgcbOrient == 0)
                        this.add(ptitl, BorderLayout.WEST);
                    else {
                        this.add(ptitl, BorderLayout.NORTH);
                    }
                }
			} else {
            
				System.out.println("unspecified component type in"
					+ "ParamGUIComponent:/n" + paramMap.getName());
			}
		}
	}
	
	public JComponent getView() {
		return this.vg;
	}
}

class VGLabelListener implements ParamListener, Serializable {
	private JLabel label;

	public VGLabelListener(JLabel label) {
		this.label = label;
	}

	public void paramFired(ren.gui.ParamEvent e) {
		
		label.setText(Double.toString(e.getValue()));
	}
}