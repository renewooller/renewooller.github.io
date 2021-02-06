

package ren.gui;

public class ParamEvent {
    
    private Object source;
    private double value;

    public ParamEvent(Object source, double value) {
	this.source = source;
	this.value = value;
    }

    public Object getSource() {
	return source;
    }

    public double getValue() {
	return value;
    }
}
