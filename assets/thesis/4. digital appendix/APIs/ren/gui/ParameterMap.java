/*
 * Created on 26/01/2005
 *
 * @author Rene Wooller
 */
package ren.gui;


import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.w3c.dom.Element;

import jmms.processor.Actuator;
import jmms.processor.ActuatorEvent;
import ren.io.Domable;
import ren.util.Err;
import ren.util.PO;
import ren.util.RMath;
import ren.util.Save;

/**
 * The useful thing about a parameterMap is that you can
 * use a bounded range model and mapp double values
 * between a totally different range. You can also give
 * it an array of doubles if you need it to map to
 * arbitrary values.
 * 
 * The model can easily be put into sliders etc.
 * 
 * It is an actuator for incoming MIDI (or other) events
 * also
 * 
 * You can register listeners to it that recieve the
 * current value when the fire method is called.
 * 
 * The number of decimal places that the map uses can
 * also be set.
 * 
 * Double accuracy errors are conquered by the map
 * 
 * @author wooller
 * 
 * 26/01/2005
 * 
 * Copyright JEDI/Rene Wooller
 * 
 */
public class ParameterMap implements Actuator, Domable {

    public static final String ssn = "pmap";

    protected String name = "unnamed";

    protected BoundedRangeModel valueModel;

    protected Map map = new Map();

    protected transient Vector paramListeners = new Vector(15);

    private ChangeListener vmlistener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
        	//PO.p("firing paramListeners");
            fireParamListeners();
        }
    };
    
    // the default value
    protected double defVal;
    
    /**
     * 
     */
    public ParameterMap() {
    }

    /**
     * construct for scalar mapping
     */
    public ParameterMap construct(BoundedRangeModel model) {
        return construct(model, model.getMinimum(), model.getMaximum());
    }

    public ParameterMap construct(int min, int max, double mapMin, double mapMax) {
        return construct(min, max, mapMin, mapMax, mapMin);
    }

    public ParameterMap construct(int min, int max, double mapMin,
            double mapMax, double value) {
        return construct(min, max, mapMin, mapMax, value, "unnamed");
    }

    public ParameterMap construct(int min, int max, int value, String name) {
        return construct(min, max, min, max, value, name);
    }

    public ParameterMap construct(int min, int max, double mapMin,
            double mapMax, double value, String name) {
        // System.out.println("constructing ... min " +
        // min + " max " + max + " mapMin " + " mapMax "
        // + value + " " + name);
        return construct(new DefaultBoundedRangeModel(min, 0, min, max),
            mapMin, mapMax, value, name);

    }

    public ParameterMap construct(BoundedRangeModel model, double mapMin,
            double mapMax) {
        return construct(model, mapMin, mapMax, mapMin, "unnamed");
    }

    public ParameterMap construct(BoundedRangeModel model, double mapMin,
            double mapMax, double value, String name) {
        valueModel = model;
        map.construct(model.getMinimum(), model.getMaximum(), mapMin, mapMax);
        this.setValue(value);
        this.setName(name);
        this.defVal = value;
        valueModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireParamListeners();
            }
        });
        return this;
    }

    /**
     * construct for double arrays
     */
    public ParameterMap construct(int min, int max, double[] mapArr) {
        return construct(min, max, mapArr, min);
    }

    public ParameterMap construct(int min, int max, double[] mapArr,
            double value) {
        return construct(min, max, mapArr, value, "unnamed");
    }

    public ParameterMap construct(int min, int max, double[] mapArr,
            double value, String name) {
        if (max - min != mapArr.length - 1) {
            try {
                Exception e = new Exception(
                        "ParameterMap.construct: max-min must equal the length of the array -1");
                e.fillInStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return construct(new DefaultBoundedRangeModel(min, 0, min, max),
            mapArr, value, name);
    }

    public ParameterMap construct(BoundedRangeModel model, double[] mapArr,
            double value) {
        return construct(model, mapArr, value, "unnamed");
    }

    public ParameterMap construct(BoundedRangeModel model, double[] mapArr,
            double value, String name) {

        valueModel = model;
        map.construct(model.getMinimum(), model.getMaximum(), mapArr);
        this.name = name;
        this.setValue(value);
        this.defVal = value;
        valueModel.addChangeListener(vmlistener);

        return this;
    }

    public void setValueModel(BoundedRangeModel newModel) {
    	this.valueModel.removeChangeListener(vmlistener);
    	this.valueModel = newModel;
    	valueModel.addChangeListener(vmlistener);
    //	valueModel.setValue(valueModel.getValue()-1);
    }
    
    /**
     * resets the parameter map back to the default
     * value
     * 
     */
    public void reset() {
        this.setValue(this.defVal);
    }

    public void addParamListener(ParamListener pl) {
        // System.out.println("adding paramlistener i
        // nparamMap called : " + this.getName());
        if (pl == null)
            System.out.println("pl is null");
        if (this.paramListeners == null)
            System.out.println("paramListeners is null");

        this.paramListeners.add(pl);
    }

    public void removeParamListener(ParamListener pl) {
        this.paramListeners.remove(pl);
    }

    public void fireParamListeners() {
        Enumeration e = this.paramListeners.elements();
        while (e.hasMoreElements()) {
        //	PO.p("param listener ");
            ((ParamListener) e.nextElement()).paramFired(new ParamEvent(this,
                    this.getValue()));
        }
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return map.getValue(this.valueModel.getValue());
    }

    public String getValueStr() {
        return String.valueOf(this.getValue());
    }

    public void setValue(double value) {
        this.valueModel.setValue(map.setValue(value));
    }

    public void setValue(String value) {
        if(value == null || value.length() == 0) {
            System.out.println("WARNING: attempt to set null value string for parameterMap " + this.getName());
            return;
        }
        this.setValue(Double.parseDouble(value));
    }

    int changedValue = Integer.MAX_VALUE;

    public boolean hasChanged() {
        boolean toRet = true;

        if (changedValue == this.valueModel.getValue())
            toRet = false;
        else
            toRet = true;

        changedValue = this.valueModel.getValue();

        return toRet;

    }

    /**
     * Return the value as a rounded integer
     */
    public int getValueInt() {
        return (int) (getValue() + 0.5);
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setName(double name) {
    	this.name = String.valueOf(name);
    }

    public String toString() {
        return this.name;
    }

    /**
     * gets the max and min values pf this pc
     */
    public double getMax() {

        return map.getValue(this.valueModel.getMaximum());
    }

    public double getMin() {
        return map.getValue(this.valueModel.getMinimum());
    }

    public void setMax(int max) {
        if (max > map.getFromMax()) {
            Error e = new Error("setMax: too large");
            try {
                e.fillInStackTrace();
                throw e;
            } catch (Error er) {
                er.printStackTrace();
            }
        }

        this.valueModel.setMaximum(max);
    }

    double firstValue = Double.NEGATIVE_INFINITY;

    /**
     * useful for when you need to iterate through every
     * value in the parameter \n if you only need to
     * iterate through a few, make sure you call reset
     * increment after
     * 
     * @return false when the incrementation has gone
     *         full cycle
     */
    public boolean incrementValue() {
    	
        // if it is the first time it has been
        // incremented, record initial val
        if (firstValue == Double.NEGATIVE_INFINITY)
            firstValue = this.valueModel.getValue();

        // increment the value, making sure it goes to
        // minumum if it goes over
        int inc = (this.valueModel.getValue() + 1);
        if (inc > this.valueModel.getMaximum())
            inc = this.valueModel.getMinimum();

        // set the value
        this.valueModel.setValue(inc);

        // if it has gone back to it's initial value,
        // let whoever is calling
        // this know, and set the firstVal back to null
        if (inc == firstValue) {
            firstValue = Double.NEGATIVE_INFINITY;
            return false;
        } else {
            // true as in "it was incremented with no
            // problems"
            return true;
        }
    }

    /**
     * in case the incrementation has cut off
     * prematurely, you can reset it to the \n original
     * setting with this
     */
    public void resetIncrement() {
        if (firstValue != Double.NEGATIVE_INFINITY)
            this.valueModel.setValue((int) firstValue);
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.map.setDecimalPlaces(decimalPlaces);
    }

    public int getDecimalPlaces() {
        return this.map.getDecimalPlaces();
    }

    public BoundedRangeModel getModel() {
        return this.valueModel;
    }

    /**
     * overridden from Actuator. this is used for MIDI
     * input and other such things that want to control
     * parameters
     * 
     * @see jmms.processor.Actuator#commandRecieved(jmms.processor.ActuatorEvent)
     */
    public void commandRecieved(ActuatorEvent e) {
        this.setValue(RMath.scaleValue(e.getMin(), e.getMax(), this.getMin(),
            this.getMax(), e.getValue()));
    }

    /**
     * tries to load the first appearance of "val" into
     * this parameterMap
     * 
     * @param s
     */
    public void loadString(String s) {
        double d = Save.getDoubleXML(s, "val");
        if (d == Double.MIN_VALUE)
            Err.e("problem loading value into parameter.  string = " + s);

        this.setValue(d);
    }

    public String saveString() {
        StringBuffer sb = new StringBuffer();
        sb.append(Save.st(ssn));

        // the value
        sb.append(Save.st("val"));
        sb.append(Double.toString(this.getValue()));
        sb.append(Save.et("val"));

        sb.append(Save.et(ssn));
        return sb.toString();
    }

    public void setClosestValue(double d) {
        this.valueModel.setValue(map.setClosestValue(d));
    }

	public void dload(Element e) {
		this.setClosestValue(Double.parseDouble(e.getAttribute("value")));
	}

	public void dsave(Element e) {
		e.setAttribute(this.PARAM_NAME, this.getName());
		e.setAttribute(this.VALUE, this.getValueStr());
	}

	public static final String PARAM_NAME = "paramName";
	public static final String VALUE = "value";
	
}

class Map {
    private double toMin, toRange;

    private double min, range;

    private double[] abMap; // an abstract aribtrary

    // mapping

    public Map() {
    }

    public void construct(int min, int max, double toMin, double toMax) {
        this.min = min * 1.0;
        this.range = (max - min) * 1.0;
        this.toMin = toMin;
        this.toRange = toMax - toMin;
        this.setDecimalPlaces(2);
    }

    public void construct(int min, int max, double[] abMap) {
        this.min = min * 1.0;
        this.range = (max - min) * 1.0;
        this.abMap = abMap; // error already checked
        this.setDecimalPlaces(2);
    }

    public int getFromMax() {
        return ((int) (min + range));
    }

    public int getFromMin() {
        return (int) (min);
    }

    public double[] getAbMap() {
        return abMap;
    }

    private double dppow;

    public double getValue(int iv) {
        /*
         * for(int i=0; i <abMap.length; i++) {
         * System.out.println("amap " + abMap[i]); }
         */

        if (abMap == null)
            // get rid of double accuracy errors by
            // using *10000
            return ((int) ((((iv - min) / range) * toRange + toMin) * dppow))
                    / dppow;
        else {
            // System.out.println("min = " + min + " iv
            // = " + iv);
            return abMap[(int) (iv - min)];
        }
    }

    public int setClosestValue(double pv) {

        if (abMap == null)
            return (int) (((pv - toMin) / toRange) * range + min);
        else {// go through the map until it matches
            // the pv
            int ci = 0;
            double cv = Double.MAX_VALUE;
            for (int i = 0; i < abMap.length; i++) {
                if (abMap[i] > pv - 0.001 && abMap[i] < pv + 0.001)
                    return (int) (i + min); // return

                // if the distance is smaller the the
                // least so far,
                if (Math.abs(abMap[i] - pv) < cv) {
                    // record the index
                    ci = i;
                    // record the value
                    cv = abMap[ci];
                }

            }
            return ci;
        }

    }

    public int setValue(double pv) {
        if (abMap == null)
            return (int) (((pv - toMin) / toRange) * range + min);
        else {// go through the map until it matches
            // the pv
            for (int i = 0; i < abMap.length; i++) {
                if (abMap[i] > pv - 0.001 && abMap[i] < pv + 0.001)
                    return (int) (i + min); // return
                // the
                // index,
                // offsetted
                // by
                // min
            }
            // if it gets here, theres a problem,
            // because the pv can't be found
            try {
                Exception ex = new Exception("no matching integer for "
                        + " param value of " + pv);

                ex.fillInStackTrace();
                throw ex;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Integer.MIN_VALUE;
        }
    }

    private int decimalPlaces = 2;

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        this.dppow = Math.pow(10.0, 1.0 * this.decimalPlaces);
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void printAbMap() {
        for (int i = 0; i < this.abMap.length; i++) {
            System.out.println("abMap[" + i + "] = " + abMap[i]);
        }
    }

}
