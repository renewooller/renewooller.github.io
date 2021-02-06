/*
 * Created on 19/01/2005
 *
 * @author Rene Wooller
 */
package jmms.processor;

/**
 * @author wooller
 *
 *19/01/2005
 *
 * Copyright JEDI/Rene Wooller
 *
 */
public interface MidiLocation {
	public void setMidiController(int chan, int ctrlType);
	public void setChannel(int channel);
	public int getChannel();
	public void setCtrlType(int ctrlType);
	public int getCtrlType();
	//public abstract static boolean isLearning();
	//public void setLearning(boolean l);
}
