
import grame.midishare.Midi;
import grame.midishare.MidiAppl;
import grame.midishare.MidiException;

/*
 * Created on 10/02/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 * 
 * 10/02/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MSTest extends MidiAppl {

	public static void main(String [] args) {
		new MSTest();
	}
	
	/**
	 *  
	 */
	public MSTest() {

		super();

		try {
			this.Open("seq");
		} catch (MidiException e) {
			System.out.println(e);
		}

		Midi.Connect(this.refnum, 0, 1);
		Midi.Connect(0, this.refnum, 1);
	}
	
	public void ReceiveAlarm(int event) {
	
		switch (Midi.GetType(event)) {

		case Midi.typeCtrlChange: {
			//note controllerType = Midi.GetData0(event), controllerValue = Midi.GetData1(event);
			this.sendControllerData(Midi.GetChan(event), Midi.GetData0(event), Midi.GetData1(event));
			
			break;
		}
	
		default:
			
			break;
		}
		Midi.FreeEv(event); 
	}
	
	public int evCount = 0;
	public void sendControllerData(int channel, int type, int value) {
		//System.out.println("channel " + channel + " type " + type + " value " + value);
		if(value < 0 || value > 127) {
			try {
				Exception e = new Exception("ctrl value must be from 0-127");
				e.fillInStackTrace();
				throw e;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		int ev = Midi.NewEv(Midi.typeCtrlChange);
		if (ev != 0) { // if the allocation was succesfull

			Midi.SetChan(ev, channel); // set the Midi channel
			Midi.SetPort(ev, 0);
			Midi.SetField(ev, 0, type);
			Midi.SetField(ev, 1, value);
		//	Midi.SetDate(event, 0);
			Midi.SendIm(this.refnum, ev);
		}
		
		evCount++;
		
		if(evCount%200 == 0) 
			System.out.println(Midi.FreeSpace());
		
		if(evCount > 2000) {
			this.Close();
			System.exit(0);
		}
		
		/*
		if(evCount >= evToDispose.length-1) {
			evCount = 0;
		}
		evToDispose[evCount++] = event;
		if(evToDispose[evCount] != -1) {	
			Midi.FreeEv(evCount);
		//	Midi.F
		}*/
		
	}

}