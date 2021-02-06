package ren.lang;

public class DoubleValue {
    private double value = -1.0;
    
    public DoubleValue() {
	this(-1.0);
    }

    public DoubleValue(double value) {
	this.value = value;
    }
    
    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    
}
