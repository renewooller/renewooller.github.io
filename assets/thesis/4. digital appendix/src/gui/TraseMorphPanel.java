/*
 * Created on 1/03/2006
 *
 * @author Rene Wooller
 */
package gui;


import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import music.morph.transearch.DivMerge;
import music.morph.transearch.TransSearch;
import music.morph.transearch.TraseMorph;
import ren.gui.ParameterMap;
import ren.gui.components.NumTexFieldDouble;
import ren.util.GB;
import ren.util.PO;

public class TraseMorphPanel extends JPanel {

	private TraseMorph tsm;

	private JLabel jl = new JLabel("0");
	private JLabel kl = new JLabel("0"); 
	
	//private JButton save = new JButton("save"), load = new JButton("load");

	private JButton itb = new JButton("init tran search");

	private JButton stb = new JButton("stop transearch");
	
	private JButton ikb = new JButton("init key search");

	private TraseMorphPanel thistm = this;

	private FindThread fit;

	public TraseMorphPanel() {
		super();

		this.setLayout(new GridBagLayout());
	}

	public TraseMorphPanel construct(TraseMorph ntsm) {
		this.tsm = ntsm;

		// display number of steps
		Box stepBox = new Box(0);
		stepBox.add(new JLabel("number of steps :"));
		stepBox.add(jl);
		jl.setText(String.valueOf(ntsm.getLength()));

		int tr = 0; // top row
		GB.add(this, tr++, 0, stepBox, 1, 1);
		
		Box kstepBox = new Box(0);
		kstepBox.add(new JLabel("key steps :"));
		kstepBox.add(kl);
		kl.setText(String.valueOf(ntsm.getKeyLength()));

		GB.add(this, tr++, 0, kstepBox, 1, 1);

		// accuracy of finishing the comparison
		GB.add(this, tr++, 0, (new NumTexFieldDouble()).construct(tsm.getTraseChain().getNNacc(),
				50), 1, 1);
		
		GB.add(this, tr++, 0, (new NumTexFieldDouble()).construct(tsm.getTraseChain().getSimCutOff(),
				50), 1, 1);
/*
 * 		
 * 
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		GB.add(this, tr++, 0, save, 1, 1);

		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				load();
			}
		});
		GB.add(this, tr++, 0, load, 1, 1);
*/
		// button to recompile steps
		itb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!tsm.isReady()) {
					PO.p("click on re-init parts first");
					return;
				}

				// PO.p("starting thread");
				fit = new FindThread(tsm, thistm, itb);
				fit.setPriority(Thread.MIN_PRIORITY); // 1 to 10
				fit.start();

			}
		});
		GB.add(this, tr++, 0, itb, 1, 1);

		stb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fit != null)
				fit.interrupt();
			}
		});
		GB.add(this, tr++, 0, stb, 1, 1);

		int tn = 0;
		
//		 frame limit
		NumTexFieldDouble fralim = (new NumTexFieldDouble()).construct(tsm.getTraseChain()
				.getFrameLimit());
		fralim.setToolTipText("upper limit of the number of frames that are processed");
		GB.add(this, tn++, 1, fralim, 1, 1);

		/*// these are made redundant by step factor
		// global deviance
		GB.add(this, tn++, 1, (new NumTexFieldDouble()).construct(tsm.getTraseChain().getDevFac(),
				50), 1, 1);

		// global decay
		GB.add(this, tn++, 1, (new NumTexFieldDouble()).construct(tsm.getTraseChain().getDecFac(),
				50), 1, 1);		
*/
		
		 
		// global transformSpeed
		NumTexFieldDouble traspeed = (new NumTexFieldDouble()).construct(
				tsm.getTraseChain().getGlobalTransformSpeed());
		traspeed.setToolTipText("scales all individual transform speed parameters");
		GB.add(this, tn++, 1, traspeed, 1, 1);
		
		
		// mutation rate
		NumTexFieldDouble murat = (new NumTexFieldDouble()).construct(tsm.getTraseChain()
				.getMutationLimit());
		murat.setToolTipText("controls how many operations per step are allowed");
		GB.add(this, tn++, 1, murat, 1, 1);

		// no change factor
		NumTexFieldDouble nochafa = (new NumTexFieldDouble()).construct(tsm.getTraseChain()
				.getNoChangeFactor(), 50);
		nochafa.setToolTipText("scales the cost factor applied to no change");
		GB.add(this, tn++, 1, nochafa, 1, 1);

		// 

		TransSearch[] tsch = this.tsm.getTraseChain().getChain();
		JTabbedPane jtp = new JTabbedPane(JTabbedPane.LEFT);
		
		for (int i = 0; i < tsch.length; i++) {
			JPanel pan = new JPanel(new GridLayout(10, 4));

			// PO.p("tsch [" + i + "] = " + tsch[i].getType());

			// add parameters into panel
			ParameterMap[] cps = tsch[i].getCostParams();
			
			// PO.p(" tsch i" + i + " cps length = " + cps.length);
			JCheckBox byp = new JCheckBox("bypass");
			// PO.p("getting th byoass got the giu = " + tsch[i].getBypass());
			byp.setSelected(tsch[i].getBypass());
			byp.setActionCommand(String.valueOf(i));
			byp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// PO.p("changing bypass");
					tsm.getTraseChain().getChain()[Integer.parseInt(e.getActionCommand())]
							.setBypass(((JCheckBox) e.getSource()).isSelected());
				}
			});
			pan.add(byp);

			pan.add((new NumTexFieldDouble()).construct(tsch[i].getDev(), 150));
			pan.add((new NumTexFieldDouble()).construct(tsch[i].getDevDec(), 150));
			pan.add((new NumTexFieldDouble()).construct(tsch[i].getTransformSpeed(), 150));
			pan.add((new NumTexFieldDouble()).construct(tsch[i].getTrackVsConsistence(), 150));
			for (int j = 0; j < cps.length; j++) {// cps.length; j++)
													// {//cps.length; j++) {
				pan.add((new NumTexFieldDouble()).construct(cps[j], 150));
			}

			ParameterMap [] othpar = tsch[i].getOtherParams();
			if(othpar != null ) {
				for(int j = 0; j< othpar.length; j++) {
					pan.add((new NumTexFieldDouble()).construct(othpar[j], 150));
				}
			}
			
			// add panel into tabbed pane
			jtp.add(pan, tsch[i].getType());

		}
		
		
		int t2 = 0;
		//  add paramters for key /scale searching
		// activation button
		ikb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tsm.initKeyTrase();
				updateKeyLengthView();
			}
		});
		GB.add(this, t2++, 2, ikb, 1, 1);
		
		// transform speed
		NumTexFieldDouble kespee = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getTransformSpeed());
		kespee.setToolTipText("control speed of transformation for key/scale search");
		GB.add(this, t2++, 2, kespee, 1, 1);
		
		// track vs consistence
		NumTexFieldDouble traco = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getTrackVsConsistence());
		traco.setToolTipText("level of deviation from speed for sake of consistence");
		GB.add(this, t2++, 2, traco, 1, 1);
		
		// key distance
		NumTexFieldDouble keyd = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getKeyd());
		keyd.setToolTipText("control influence of key distance on the search heuristic");
		GB.add(this, t2++, 2, keyd, 1, 1);
		
		// root difference
		NumTexFieldDouble rootd = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getRootd());
		rootd.setToolTipText("control influence of root difference on search heuristic");
		GB.add(this, t2++, 2, rootd, 1, 1);

		t2 = 0;
		
		//  CoF translation in root difference (which is already oct-equi
		NumTexFieldDouble coft = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getPitchClassCompare().getCof());
		coft.setToolTipText("level of Circle of 5th translation in root search heuristic");
		GB.add(this, t2++, 3, coft, 1, 1);
		
		//	Octave Equivalence in root difference (but linear, not COF)
		NumTexFieldDouble octet = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getPitchClassCompare().getOctEqu());
		octet.setToolTipText("level of Circle of Choma translation in root search heuristic");
		GB.add(this, t2++, 3, octet, 1, 1);
		
		// because it is dealing with root, we don't need to look at absolute difference
		tsm.getKeyMoSearch().getPitchClassCompare().getLin().setValue(0);
		
	//	NumTexFieldDouble lin = (new NumTexFieldDouble()).construct(
	//			tsm.getKeyMoSearch().getPitchClassCompare().getLin());
	//	octet.setToolTipText("level of linear pitch in root search heuristic");
	//	GB.add(this, t2++, 3, lin, 1, 1);
		
		// scale difference
		NumTexFieldDouble scad = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getScaled());
		scad.setToolTipText("control influence of scale difference on search heuristic");
		GB.add(this, t2++, 3, scad, 1, 1);
		
//		 scale difference
		NumTexFieldDouble sasclen = (new NumTexFieldDouble()).construct(
				tsm.getKeyMoSearch().getSameLength());
		scad.setToolTipText("wether or not scales of different length are considered very different");
		GB.add(this, t2++, 3, sasclen, 1, 1);
		
		// add tabbed pane
		GB.add(this, 0, 4, jtp, 10, 10);

		return this;
	}

	public void updateKeyLengthView() {
		kl.setText(Integer.toString(tsm.getKeyLength()));
		kl.repaint();
	}

	public void updateLengthView() {
		// PO.p("up length view");
		jl.setText(Integer.toString(tsm.getLength()));
		jl.repaint();
	}

}

class FindThread extends Thread {

	TraseMorph tsm;

	TraseMorphPanel tp;

	JButton itb;

	public FindThread(TraseMorph tsm, TraseMorphPanel tp, JButton itb) {
		super();
		this.tsm = tsm;
		this.tp = tp;
		this.itb = itb;
	}

	public void run() {
		itb.setEnabled(false);
		tsm.initTrase();
		tp.updateLengthView();
		itb.setEnabled(true);
	}

}
