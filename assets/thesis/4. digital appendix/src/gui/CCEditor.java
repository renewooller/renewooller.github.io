/*
 * Created on 24/01/2006
 *
 * @author Rene Wooller
 */
package gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import music.LPart;
import ren.env.GraphView;
import ren.env.ValueGraphModel;
import ren.gui.LabelledView;
import ren.gui.ParamEvent;
import ren.gui.ParamListener;
import ren.gui.ParameterMap;
import ren.gui.components.LJSlider;
import ren.gui.components.RJCheckBox;
import ren.gui.components.VGJComboBox;
import ren.util.GB;
import ren.util.PO;

public class CCEditor extends JPanel {

	private GraphView gv;
	
	private LPart lpart;
	
	private ParameterMap ccscope = new ParameterMap();
	private LJSlider ccscopeSlid;
	private LabelledView labCClen;
	
	private ValueGraphModel cm;
	
	private VGJComboBox typeSelect;
	
	private RJCheckBox mutecb;
	
	private static int DFCCTYPE = 7;
	
	public CCEditor() {
		super();
	
	}
	
	//	TODO work out why it loses the volume every now and then, maybe its
	// because the new cc system makes some wierd interpolation
	
	
	public void selectCCType(int type) {
	//	PO.p("type = " + type);
		Integer ty = new Integer(type);
		// if it has it already, switch to it
		if(lpart.getPart().getCCEnvs().containsKey(ty)) {
	//		PO.p("already has");
			cm = ((ValueGraphModel)(lpart.getPart().getCCEnvs().get(ty)));
		} else { // otherwise, make a new one
			cm = (new ValueGraphModel()).construct();
			lpart.getPart().getCCEnvs().put(ty, cm);
		}
		
	//	PO.p("ccenvs = " + lpart.getPart().getCCEnvs().toString());
		
		
	//	BoundedRangeModel brm = cm.getLoopLengthModel();
	//	PO.p("brm = min : "+ brm.getMinimum() +
	//		 " max : " + brm.getMaximum() + " val : " + brm.getValue());
		ccscope.setValueModel(cm.getLoopLengthModel());
		ccscopeSlid.setModel(cm.getLoopLengthModel());
		ccscope.fireParamListeners();
		//ccscopeSlid.setValue(ccscopeSlid.getValue()); // to make it update
		
		gv.setModel(cm);
		gv.repaint();
	}
	
	public void setLPart(LPart lp) {
		lpart = lp;
		if(lpart.getPart().getCCEnvs().isEmpty()) {
			selectCCType(DFCCTYPE);
		} else { // default to selecting the first one
			selectCCType(((Integer)(lpart.getPart().
					getCCEnvs().keys().nextElement())).intValue());
		}
	}

	public CCEditor construct(LPart lp) {
	//	PO.p("scope");
		ccscope.construct(0, ValueGraphModel.DFLL.length-1, 
						 ValueGraphModel.DFLL, 
						 16.0, "cc loop length");
		
		labCClen = (new LabelledView()).construct(ccscope, true, true, 
                "the length of the loop", 0);
		
		this.ccscopeSlid = (LJSlider)labCClen.getView();
	
	//	PO.p("ccscope = " + ccscope.getValue());
		
	//	PO.p("gv");
		gv = (new GraphView()).construct();
		
	//	PO.p("setlp");
		this.setLPart(lp); // does ccselect as well
		
		mutecb = new RJCheckBox("cc on", false, RJCheckBox.GREEN);
		mutecb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMute();
			}
		});
		
		//	PO.p("paramList");	
		// the scope will change the length of the loop, so need to repaint
		ccscope.addParamListener(new ParamListener() {
			public void paramFired(ParamEvent e) {
				//gv.scaleSize(e.getValue()/16.0, 1.0);
				updateGVSize();
			}
		});
		
		//PO.p("type select");
		// the type select will be a drop menu containing al lthe possible types
		String [] types = new String [128];
		for(int i=0; i< types.length; i++) {
			types[i] = String.valueOf((i+1));
		}
		typeSelect = new VGJComboBox(types);
		typeSelect.setValue(DFCCTYPE);
		// and it will actually select a type when triggered
		typeSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectCCType(typeSelect.getValue());
			}
		});
		
	//	PO.p("before before" + ccscope.getValue());
		
	//	PO.p("labelling");
		
	//	PO.p("labelling 2");
		LabelledView labTypeSelect = (new LabelledView()).
									  construct(this.typeSelect, 
									  "selects a controller change type",
									  1);
		
		LabelledView labZoom = (new LabelledView()).construct(this.gv.getZoom(),
				true, true, "pixels per beat", 0);
		
	//	PO.p("layout");
		this.setLayout(new GridBagLayout());
		
	//PO.p("putting");
		// put everything in place
		GB.add(this, 0, 0, labTypeSelect, 1, 1);
		GB.add(this, 1, 0, labCClen, 3, 1);
		GB.add(this, 4, 0, labZoom, 3, 1);
		GB.add(this, 7, 0, mutecb, 1, 1);
		
		JScrollPane jsp = new JScrollPane();
		Dimension jdim = new Dimension();
		jdim.width = gv.getPreferredSize().width + 3; //+
		//	jsp.getVerticalScrollBar().getPreferredSize().width + 3;
		jdim.height = gv.getPreferredSize().height + 
			jsp.getHorizontalScrollBar().getPreferredSize().height + 3;
		
		//PO.p("jdim .height = " + jdim.height);
		
		jsp.setPreferredSize(jdim);//DimUtil.scale(gv.getDFSize(), 1.0, 1.0));
		jsp.setViewportView(gv);
		GB.add(this, 0, 1, jsp, 8, 4);
		
		this.doLayout();
		this.repaint();
		return this;
	}
	
	public void setMute() {
		this.cm.setMuted(!this.mutecb.isSelected());
	}
	
	public void updateGVSize() {
		Dimension d = gv.DFDIM;
		
		d.width = (int)(this.ccscope.getValue()*gv.getZoom().getValue() + 0.5);
		if(d.width < gv.DFDIM.width)
			d.width = gv.DFDIM.width;
		//PO.p("new height = " + d.height);
		gv.setSizes(d);
		gv.repaint();
	}

}
