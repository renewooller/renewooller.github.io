import ren.util.PO;
import ren.util.RMath;

/*
 * Created on Mar 10, 2006
 *
 * @author Rene Wooller
 */

public class MathTest {

	public static void main(String [] args) {
		new MathTest();
	}
	
	public MathTest() {
		super();
	
		int a = 6;
		int b = 4;
		
		int r = RMath.getMultiple(a, b);
		
		PO.p("a = " + a + " b = " + b + " r = " + r);
		
		/*
		for(int i=-21; i< 21; i++) {
			PO.p(" i = " + i + " %10 = " + i%10);
		}
		
		/*
		BigDecimal bd = new BigDecimal(43.00001);
		bd = bd.setScale(7, bd.ROUND_HALF_EVEN);
		BigDecimal bd2 = BigDecimal.valueOf(5, 1);
		BigDecimal bd3 = bd.add(bd2);
	//	PO.p(" bd = " + bd.doubleValue() + " bd2 = " + bd2.doubleValue() + " bd3 = " + bd3.doubleValue());
		
		AD ad = new AD(0.00001, 20.03125);
		ad.add(0.03125);
		//PO.p("ad value = " + ad.v());
		
		PO.p("bd test " + BigDecimal.valueOf((long)((2.0/3.0)*Math.pow(10, 6) + 0.5), 6));
		
		PO.p("add mult " + AD.mult(0.5, 2.0/3.0));
		PO.p("reg mnult " + (0.5*(2.0/3.0)));
		/*
		AD ad = new AD();
		ad.add(21.00000000000000003);
	//	ad.add(21.203921568627443);
		PO.p(ad.toString());
		
		/*
		Point a = new Point(40, 40);
		Point b = new Point(70, 50);
		
		double [] uv = RMath.toUnitVec(a, b);

		PO.p(uv);
		
		PO.p("distance should = 1   " + RMath.norm(uv[0], uv[1]));
		*/
	}

}
