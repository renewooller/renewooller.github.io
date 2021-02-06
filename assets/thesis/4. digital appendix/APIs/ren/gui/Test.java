//import ren.gui.components.SliderVG;
//import ren.gui.ParameterMap;
package ren.gui;

public class Test {
    public static void main(String [] args) {
	new Test();
    }

    public Test() {
	Exception e = new Exception("tester");
	e.fillInStackTrace();
	
	try{throw e;}catch(Exception ex) {ex.printStackTrace();}
	
	/*
	ParameterMap pc = new ParameterMap("test",
					       new SliderVG(1, 0, 100, 50), 
					       -1.5, 
					       1.5);
	System.out.println("slider value = " + 
			   pc.getValueGenerator().getValue());
	System.out.println("pc value = " + pc.getValue());
	pc.setValue(-1.5);
	System.out.println("2 value = " + pc.getValue());
	pc.setValue(1.5);
	System.out.println("3 value = " + pc.getValue());
	*/
	//	System.exit(0);
    }
}
