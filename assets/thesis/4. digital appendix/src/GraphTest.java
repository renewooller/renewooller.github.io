import javax.swing.JFrame;
import javax.swing.JPanel;

import ren.env.GraphView;
import ren.util.PO;

/*
 * Created on 24/01/2006
 *
 * @author Rene Wooller
 */

public class GraphTest {

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		GraphView tg = new GraphView();
		tg.construct();
		
		JPanel cp = new JPanel();
		cp.add(tg);
		
		GraphView tg2 = new GraphView();
		tg2.construct();
		
		cp.add(tg2);
		
		tg.getModel().addNode(new double [] {0.0, 0.5, 0.6, 0.7, 0.9}, 
							  new double [] {0,   50,  30,   70, 20});
				
		
	//	tg2.setModel(tg.getModel().copySegment(0.65, 2.0));
		PO.p(" tg1 = " + tg.getModel().toString());
		PO.p("tg2 = " + tg2.getModel().toString());
		
		jf.getContentPane().add(cp);
		jf.pack();
		jf.setVisible(true);
		
		
	}

}
