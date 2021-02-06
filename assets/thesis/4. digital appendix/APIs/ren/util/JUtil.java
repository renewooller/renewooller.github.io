/**
 * JUtil
 * this class provides some static methods for working with the 
 * java swing library
 * Rene Wooller
 */
package ren.util;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

import ren.gui.ParameterMap;
import ren.gui.ParamListener;
import ren.gui.components.NumberTextField;
import ren.gui.components.VGJComboBox;
import ren.gui.lookFeel.CustomFonts;

public class JUtil {

	public static void addTabPane(JTabbedPane jtp, JPanel pane, String name,
			String toolTip) {
		pane.setName(name);
		pane.setPreferredSize(DimUtil.scale(jtp.getPreferredSize(), 0.8, 0.8));
		jtp.add(pane);
		jtp.setToolTipTextAt(jtp.getTabCount() - 1, toolTip);
	}

	/**
	 * enLabelVG puts the value generator into a labelled box,
	 * adds a numeric readout of the value and adds toolTipText
	 *
	 * it takes ParameterMap rather than VG because PC has
	 * the appropriate mapping for the parameter readout
	 *
	public static JPanel enlabelVG(ParameterMap pc, boolean labelOn,
			boolean readoutOn, String toolText) {
		
		JPanel jp = new JPanel(new BorderLayout());
		JLabel prout = new JLabel(Double.toString(pc.getValue()));
		JLabel ptitl = new JLabel(pc.getName());
		if(ptitl.getText().length() > 4) {
			ptitl.setFont(ptitl.getFont().deriveFont((float)(ptitl.getFont().getSize()-
					(ptitl.getText().length()-4)*0.7f)));
		}
		prout.setFont(CustomFonts.parameterLabel());
		pc.addParamListener(new VGLabelListener(prout));
		JComponent vg = ((JComponent) pc.getValueGenerator());
		//vg.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		//vg.setMaximumSize(vg.getPreferredSize());
		//vg.set
		
	//	jp.add((JComponent) pc.getValueGenerator(), BorderLayout.CENTER);

		prout.setAlignmentX(prout.CENTER_ALIGNMENT);
		//put the labels in different places depening on slider orientation
		Box b = new Box(1);
		b.add(prout);

		//make sure the box is always as wide as it will get, by increment
		//ing through all th possible values 
		int widest = 0;
		while (pc.incrementValue()) {
			prout.setText(Double.toString(pc.getValue()));
			if (widest < (int) prout.getPreferredSize().getWidth())
				widest = (int) prout.getPreferredSize().getWidth();
		}

		//	System.out.println("size jutil : " + 
		//	   ((int)prout.getPreferredSize().getWidth()));

		b.add(Box.createHorizontalStrut(widest));

		if (pc.getValueGenerator() instanceof JSlider) {
			if (((JSlider) pc.getValueGenerator()).getOrientation() == 1) {
				jp.add(vg, BorderLayout.EAST);
				if (readoutOn)
					jp.add(b, BorderLayout.SOUTH);
				if (labelOn)
					jp.add(ptitl, BorderLayout.NORTH);
			} else {
				jp.add(vg, BorderLayout.SOUTH);
				if (readoutOn)
					jp.add(b, BorderLayout.EAST);
				if (labelOn)
					jp.add(ptitl, BorderLayout.WEST);
			}
		} else {
			jp.add(vg, BorderLayout.CENTER);
			if (pc.getValueGenerator() instanceof NumberTextField
				|| pc.getValueGenerator() instanceof VGJComboBox) {
				if (labelOn) 
					jp.add(ptitl, BorderLayout.WEST);
			} else {
			System.out.println("unspecified component type in"
					+ "ParamGUIComponent:/n" + pc.getName());
			}
		}
		if (toolText != null) {
			ptitl.setToolTipText(toolText);
			if (pc.getValueGenerator() instanceof JComponent)
				((JComponent) pc.getValueGenerator()).setToolTipText(toolText);
		} else {
			if (pc.getValueGenerator() instanceof JComponent) {
				ptitl.setToolTipText(((JComponent) pc.getValueGenerator())
						.getToolTipText());
			}
		}
		return jp;
	}*/

	private static transient Rectangle b;

	public static Point getCentre(JComponent c) {
		b = c.getBounds();
		return new Point((int) (b.getX() + ((int) (b.getWidth() / 2.0))),
				(int) (b.getY() + ((int) (b.getHeight() / 2.0))));
	}

	public static Point getCentre(Rectangle r) {
		return JUtil.getCentre(r.x, r.y, r.x + r.width, r.y + r.height);
	}

	public static Point getCentre(int x1, int y1, int x2, int y2) {
		return getCentrelinePos(x1, y1, x2, y2, 0.5);
	}
	
	public static Point getCentrelinePos(int x1, int y1, int x2, int y2, double pos) {
		return new Point((int) (x1 - ((x1 - x2) * pos)),
				(int) (y1 - ((y1 - y2) * pos)));
	}

	public static JComponent entitle(JComponent to, String title) {
		to.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(3), title));
		return to;
	}
	
}

class VGLabelListener implements ParamListener {
	private JLabel label;

	public VGLabelListener(JLabel label) {
		this.label = label;
	}

	public void paramFired(ren.gui.ParamEvent e) {
		label.setText(Double.toString(e.getValue()));
	}
}