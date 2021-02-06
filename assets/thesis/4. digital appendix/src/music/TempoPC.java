/*
 * Created on 10/01/2005
 *
 * @author Rene Wooller
 */
package music;

import jm.music.data.Tempo;
import ren.gui.ParameterMap;

/**
 * @author wooller
 *
 *10/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public class TempoPC extends Tempo {

	private ParameterMap tempoParam;
	
	/**
	 * 
	 */
	public TempoPC() {
		this(120.0);
	}

	/**
	 * @param initTempo
	 */
	public TempoPC(double initTempo) {
		//super(initTempo);
		tempoParam = (new ParameterMap()).construct(0, 21000, 40.0, 250.0, initTempo, "tempo");
	}
	
	public TempoPC(ParameterMap pc) {
		this.tempoParam = pc;
	}
	
	
	protected double getValue() {
		return tempoParam.getValue();
	}
	
	public void setTempo(double newTempo) {
		tempoParam.setValue(newTempo);
	}
	
	public ParameterMap getTempoParam() {
		return tempoParam;
	}
	
	public void setTempoParam(ParameterMap tempoParam) {
		this.tempoParam = tempoParam;
	}
}
