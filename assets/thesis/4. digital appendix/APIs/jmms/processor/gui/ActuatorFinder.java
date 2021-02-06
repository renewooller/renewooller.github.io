/*
 * Created on 19/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor.gui;

import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import jmms.processor.Actuator;
import jmms.processor.ActuatorCommander;
import jmms.processor.ActuatorContainer;
import jmms.processor.ActuatorContainerManager;
import jmms.processor.ActuatorPath;
import ren.util.DimUtil;
import ren.util.GB;

/**
 * @author wooller
 * 
 * 19/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class ActuatorFinder extends JPanel implements Serializable {

	private ActuatorContainerManager acm;

	private ActuatorTreeModel atm = new ActuatorTreeModel();

	private ActuatorCommander acomm; // to be mapped to actuator(s)
	
	private JTree tree;

	private JList list;

	private JList addedList;
	
//	private Popup popupParent;
	
	/**
	 *  
	 */
	public ActuatorFinder() {
		super(new GridBagLayout());
		//construct();
	}
	
	public void construct(ActuatorContainerManager acm, ActuatorCommander ac) {
		this.setAcm(acm);
		//this.setPopupParent(popupParent);
		this.setActuatorCommander(ac);
		construct();
	}
	
	

	private void construct() {
		this.setPreferredSize(DimUtil.scaleScreen(0.3, 0.2));
		this.setSize(this.getPreferredSize());
		
		if (acm == null)
			setAcm(new ActuatorContainerManager());

		tree = new JTree(atm);
		tree.setPreferredSize(DimUtil.scale(this.getSize(), 0.4, 0.8));
		tree.setEditable(true);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				//if(e.getNewLeadSelectionPath().getL instanceof
				// ActuatorContainer) {
				if (((ActuatorContainer) e.getNewLeadSelectionPath()
						.getLastPathComponent()).getActuators() != null) {
										
					list.setListData(((ActuatorContainer) e
							.getNewLeadSelectionPath().getLastPathComponent())
							.getActuators());
				}else {
					list.setListData(new String [] {"<-- navigate tree"});	
				}
			}
		});
		
		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(acomm.addActuatorPath(convertTreePath(tree.getSelectionPath(), (Actuator)list.getSelectedValue()))){
					addedList.updateUI();
				}
			}
		});
		/*list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//System.out.println("vadj " + e.getValueIsAdjusting());
				if(e.getValueIsAdjusting()) {
					return;
				}
				if(list.isSelectionEmpty()) 
					return;
				if(list.getSelectedValue() instanceof String [] )
					return;
				if(list.getSelectedIndices().length > 1) {
					//acomm.addActuatorPath(convertTreePath(tree.getSelectionPath(), list.getSelectedValues())); 
				//	addedList.updateUI();
				} else {
					// actuatorPath.equals tests for actuators if you give it an actuator:
				//	System.out.println(acomm.getActuatorPathVector().indexOf((Actuator)list.getSelectedValue()));
					
					if(!acomm.contains((Actuator)list.getSelectedValue())) {
						acomm.addActuatorPath(convertTreePath(tree.getSelectionPath(), list.getSelectedValue()));
						addedList.updateUI();
					}
				}
			}
		});*/
		
		addedList = new JList();
		addedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		addedList.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				if(addedList.getSelectedValue() == null)
					return;
				acomm.removeActuatorPath(((ActuatorPath)addedList.getSelectedValue()));
				addedList.updateUI();
			}
		});
		
		JScrollPane jsp = new JScrollPane(list);
		jsp.setPreferredSize(DimUtil.scale(this.getSize(), 0.3, 0.8));
		
		JScrollPane jsp2 = new JScrollPane(addedList);
		jsp2.setPreferredSize(DimUtil.scale(this.getSize(), 0.3, 0.8));
		
		
		GB.add(this, 0, 0, new JLabel("find parameters"), 2, 1);
		GB.add(this, 0, 1, tree, 2, 5);
		
		GB.add(this, 2, 0, new JLabel("click to add"), 1, 1);
		GB.add(this, 2, 1, jsp, 1, 5);
		
		GB.add(this, 3, 0, new JLabel("click to delete"), 1, 1);
		GB.add(this, 3, 1, jsp2, 1, 5);
		
	}

	private transient ActuatorPath apath;
	private transient ActuatorContainer [] tac;
	private transient Object [] tob;
	private ActuatorPath convertTreePath(TreePath tp, Object sel) {
		apath = new ActuatorPath();
		tob = tp.getPath();
		tac = new ActuatorContainer [tob.length];
		for(int i=0; i<tob.length; i++) {
			
			tac[i] = ((ActuatorContainer)tob[i]);
		}
		apath.setPath(tac, (Actuator)sel);
		return apath;
	}
	/*
	private transient ActuatorPath [] apathArr;
	private ActuatorPath [] convertTreePath(TreePath tp, Object [] sel) {
		ActuatorContainer [] acarr = ((ActuatorContainer []) tp.getPath());
		apathArr = new ActuatorPath [sel.length];
		for(int i=0; i<sel.length; i++) {
			apathArr[i].setPath(acarr, (Actuator)sel[i]);
		}
		return apathArr;
	}*/
	
	/*
	public void setPopupParent(Popup popupParent) {
		this.popupParent = popupParent;
	}*/
	
	public ActuatorContainerManager getAcm() {
		return acm;
	}

	public void setAcm(ActuatorContainerManager acm) {
		this.acm = acm;
		atm.setActuatorContainerManager(acm);
	}
	
	public void setActuatorCommander(ActuatorCommander ac) {
		this.acomm = ac;
		if(acomm != null)
			addedList.setListData(acomm.getActuatorPathVector());
		
	}
}