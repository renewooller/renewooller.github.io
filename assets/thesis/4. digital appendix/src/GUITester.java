import gui.LPartEditor;
import gui.MultiMorphEditor;
import gui.PMGEditor;
import gui.musicArea.MorphComponent;
import gui.musicArea.MusicArea;
import gui.musicArea.PatternComponent;
import java.awt.*;
import javax.swing.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jm.music.data.Part;
import lplay.LPlayer;
import music.LPart;
import music.MultiMorph;
import music.PatternMusicGenerator;
import ren.gui.ParameterMap;
import ren.gui.components.NumTexFieldDouble;
import ren.gui.seqEdit.NotePanel;
import ren.util.PO;

/*
 * Created on 13/02/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 * 
 * 13/02/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public class GUITester {
	LPlayer lp = new LPlayer();

	public static void main(String[] args) {
		new GUITester();
	}

	/**
	 * 
	 */
	public GUITester() {

		//this.multiMorphEditor();
		
		// notePanel();

		//lpartEditor();

		//cceditor();
		
		pmgEditor();
	}

	private void numTextField() {
		JFrame jf = new JFrame();
		
		NumTexFieldDouble ntf = (new NumTexFieldDouble()).constructThin(
				(new ParameterMap()).construct(0,8, 1, "test"));
		
		ntf.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
		
		ntf.setInsets(0);
		
		jf.getContentPane().add(ntf);
					
		jf.pack();
		jf.show();
	}
	
	private void cceditor() {
		JFrame jf = new JFrame();

		// ed.constructMulti(dmc, mm);

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();

		ParameterMap pm = (new ParameterMap()).construct(0, 100, -1, 1, 1.0, "new paramMap");
		
		NumTexFieldDouble nd = (new NumTexFieldDouble()).construct(pm, 100);
		
		jp.add(nd);
		
		/*
		LPart lp = new LPart();
		lp.construct(new Part());

		PO.p("constructing ccedit");
		CCEditor cedit = (new CCEditor()).construct(lp);
*/
		PO.p("finished constructing");
	//	jp.add(cedit);
		jf.getContentPane().add(jp);
		
		jf.pack();
		jf.setVisible(true);
	}

	private void lpartEditor() {
		JFrame jf = new JFrame();

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		
		
		
		LPart lp = new LPart();
		lp.construct(new Part());

		jp.add((new LPartEditor(lp)));
		
		jf.getContentPane().add(jp);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));

		jf.pack();
		jf.setVisible(true);
	}
	
	private void pmgEditor() {
		JFrame jf = new JFrame();

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		
		
		//LPart lp = new LPart();
		//lp.construct(new Part());

		PatternMusicGenerator pmg1 = new PatternMusicGenerator();
		pmg1.constructPattern();
		
		PMGEditor ped = (new PMGEditor()).construct(pmg1, lp);
		
		jp.add(ped);
		
		jf.getContentPane().add(jp);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));

		jf.pack();
		jf.setVisible(true);
	}

	private void notePanel() {

		JFrame jf = new JFrame();

		// ed.constructMulti(dmc, mm);

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		jp.add((new NotePanel()).construct());
		jf.getContentPane().add(jp);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));
		
		
		jf.pack();
		jf.setVisible(true);

	}

	private void multiMorphEditor() {
		MusicArea area = new MusicArea();

		PatternMusicGenerator pmg1 = new PatternMusicGenerator();
		pmg1.constructPattern();
		pmg1.constructPattern();

		PatternMusicGenerator pmg2 = new PatternMusicGenerator();
		pmg2.constructPattern();
		pmg2.constructPattern();

		MultiMorph mm = new MultiMorph();
		mm.constructMultiMorph(pmg1, pmg2);

		MorphComponent dmc = new MorphComponent();
		dmc.construct((new PatternComponent()).construct(0, 0, area, pmg1),
				(new PatternComponent()).construct(1, 1, area, pmg2), lp, area);

		JFrame jf = new JFrame();
		
		MultiMorphEditor ed = new MultiMorphEditor();
				
		
		ed.constructMulti(dmc, mm, jf);
		// ed.constructMulti(dmc, mm);

		jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);

		jf.getContentPane().add(ed);
		// jf.getContentPane().add((new
		// PartMorphPanel()).construct(mm.getPartMorph(0)));

		jf.pack();
		jf.setVisible(true);

		/*
		 * JPanel jp = new JPanel();
		 * 
		 * LJSlider ljs = new LJSlider(1, 1, 10, 1);
		 * 
		 * //DefaultBoundedRangeModel dm = new DefaultBoundedRangeModel(1, 1, 1,
		 * 20); // ljs.setModel(dm); ljs.addMouseListener(new MouseAdapter() {
		 * public void mouseEntered(MouseEvent e) { PO.p("entered"); } public
		 * void mouseExited(MouseEvent e) { PO.p("exited"); } });
		 * 
		 * jp.add(ljs);
		 */

	}

}
