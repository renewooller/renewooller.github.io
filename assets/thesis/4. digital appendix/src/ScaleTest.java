import java.util.Vector;

import ren.tonal.Scales;
import ren.util.PO;

/*
 * Created on 31/01/2006
 *
 * @author Rene Wooller
 */

public class ScaleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scales scales = new Scales();

		Vector all = scales.createAllScales(7);
		// 4:0, 5:5 , 6:87, 7:168, 8:62, 9:3, 10:0

		PO.p("number of scales = " + all.size());

		for (int i = 0; i < all.size(); i++) {
			PO.p("scale = ", ((int[]) (all.get(i))));
		}

	}

}
