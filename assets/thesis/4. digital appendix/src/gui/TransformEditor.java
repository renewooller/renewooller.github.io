/*
 * Created on 2/12/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;

import music.transform.TFactory;
import music.transform.TransformChain;
import music.transform.Transformer;
import ren.gui.LabelledView;
import ren.gui.ParameterMap;
import ren.util.DimUtil;

/**
 * @author wooller
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TransformEditor extends JPanel {

	private TransformChain tc;

	private JPopupMenu pm;
	
	public TransformEditor() {
		this(null);
	}

	/**
	 *  
	 */
	public TransformEditor(TransformChain tc) {
		super();
		this.tc = tc;
		this.setLayout(new FlowLayout());//new BoxLayout(this, 1));
		this.setBorder(BorderFactory.createTitledBorder("transform editor"));
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
				if(e.getButton() == e.BUTTON3) {
					createTransformer(e);
				}
			}
		});
		
		pm = new JPopupMenu("create transformer");
		String [] str = TFactory.getTypes();
		
		ActionListener al = new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				addTransformer(TFactory.create(e.getActionCommand()));
			}};
			
		for(int i=0; i<str.length; i++) {
			pm.add(str[i]).addActionListener(al);
		}
		
		//create all the slots
		for(int i=0; i<tcomps.length; i++) {
			tcomps[i] = new TransComponent(this);
			tcomps[i].setVisible(false);
			this.add(tcomps[i]);
		}
		construct();
	}

	private void createTransformer(MouseEvent e) {
			if(tc == null)
				return;
			
			pm.setLocation(e.getPoint());
			
			pm.show(this, e.getX(), e.getY());	
	}
	
	private void construct() {
		updateTransComponents();
	}
	
	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
		//this.setMinimumSize(d);
		//this.add(new Box.Filler(d, d, d));
	}

	private void updateTransComponents() {
		if (tc == null)
			return;
		tc.resetIterate();
		Transformer t = tc.iterate();
		
		//go through and set the transformer for each slot to reflect the chain.
		
		//any left over slots are made to be invisible
		int i=0;
		while (t != null) {
			tcomps[i].setTransformer(t);
			tcomps[i].setVisible(true);
			i++;
			t = tc.iterate();
		}
		for(;i<tcomps.length; i++) {
			tcomps[i].setVisible(false);
		}
		
		this.setVisible(false);
		this.setVisible(true);
	}

	private TransComponent [] tcomps = new TransComponent [40];
	
	public void setTransformChain(TransformChain tc) {
		this.tc = tc;
		updateTransComponents();
		
	}
	
	public void addTransformer(Transformer trans) {
		tc.add(trans);
		updateTransComponents();
	}
	
	public void removeTransformer(Transformer trans) {
		for(int i=0; i<this.getComponentCount(); i++) {
			if(((TransComponent)this.getComponent(i)).getTransformer() == trans) {
				this.remove(i);
				tc.remove(trans);
				this.repaint();
				return;
			}
		}
		System.out.println("transformer " + trans.getClass().getName() + ", cannot be found, " +
				"contrary to popupar belief in it's existence");
		return;
	}

}

class TransComponent extends JPanel {

	private Transformer tr;

	private TransTab tab;// = new TransTab();

	private LabelledView [] lv = new LabelledView [16];
	
	private TransformEditor tedit;
	
	TransComponent(TransformEditor tedit) {
		this(null, tedit);
	}
	
	TransComponent(Transformer tr, TransformEditor tedit) {
		super();
		this.tr = tr;
		tab = new TransTab(tedit, tr);
		this.setLayout(new FlowLayout(0));//GridBagLayout());
		this.setBorder(BorderFactory.createEtchedBorder());
		init();
	}

	public void init() {
		if(tr == null) {
			return;
		}
		ParameterMap[] pcs = tr.getParamMaps();
		for (int i = 0; i < pcs.length; i++) {
			lv[i] = (new LabelledView()).construct(pcs[i], true, true, "", 1);  
			this.add(lv[i]);
			//this.add(pcs[i].getValueGenerator);
		}
		this.add(tab);
	}

	public void setTransformer(Transformer t) {
		for(int i=0; i<lv.length; i++){
			if(lv[i] != null) {
				lv[i].dispose();
				lv[i] = null;
			} else {
				break;
			}
		}
		this.removeAll();
		this.tr = t;
		this.tab.setTransformer(t);
		init();
	}

	public Transformer getTransformer() {
		return tr;
	}

}

class TransTab extends JComponent implements MouseListener, MouseMotionListener {

	private TransformEditor tedit;
	private Transformer trans;
	public TransTab(TransformEditor tedit, Transformer trans) {
		super();
		this.tedit = tedit;
		this.trans = trans;
		this.setPreferredSize(DimUtil.scale(Dim.sqr, 0.5, 1.0));
		// dropTarget(this, new DropTargetListener
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.addMouseListener(this);
	}

	public void setTransformer(Transformer tr) {
		this.trans = tr;
	}
	
	public void paintComponent(Graphics graph) {
		Graphics g = graph.create();

		g.setColor(Color.BLACK);

		g.fillRect(0, 0, 20, 20);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == e.BUTTON3) {
			tedit.removeTransformer(trans);
			tedit.repaint();
		}
	}

	private transient boolean me = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		me = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		me = false;
	}

	private transient boolean mp = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		mp = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		mp = false;
	}
}

