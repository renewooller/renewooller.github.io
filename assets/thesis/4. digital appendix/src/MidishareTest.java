
//import jm.midi.event.CChange;
import grame.midishare.Midi;
import grame.midishare.MidiAppl;
import grame.midishare.MidiException;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Created on 8/02/2005
 *
 * @author Rene Wooller
 */

/**
 * @author wooller
 * 
 * 8/02/2005
 * 
 * Copyright JEDI/Rene Wooller
 *  
 */
public class MidishareTest extends MidiAppl {
	
	public static void main(String [] args) {
		new MidishareTest();
		
	}
	JLabel lab = new JLabel();
	/**
	 *  
	 */
	public MidishareTest() {
		super();
		// TODO Auto-generated constructor stub
		
		//System.out.println("appls = " + Midi.CountAppls());
		//System.out.println("share = " + Midi.Share());
		System.out.println("version = " + Midi.GetVersion());
		
		try {
			this.Open("seq");
		} catch (MidiException e) {
			System.out.println(e);
		}
		
		Midi.Connect(this.refnum, 0, 1);
		Midi.Connect(0, this.refnum, 1);

		JFrame jf = new JFrame();
		jf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Close();
				System.exit(0);
			}
		});
		JPanel jp = new JPanel();
		lab.setText("       ");
		jp.add(lab);
		jf.getContentPane().add(jp);
		jf.pack();
		jf.setVisible(true);
	}

	public void ReceiveAlarm(int event) {
		String s = "defualt";
		switch (Midi.GetType(event)) {

		case Midi.typeCtrlChange: {
			s = ("ctrl" + Midi.GetChan(event) + " " + Midi.GetData0(event) + " " + Midi.GetData1(event));
			break;
		}
		case Midi.typeKeyOn: {
			s = ("key " + Midi.GetChan(event) + " " + Midi.GetData0(event) + " " + Midi.GetData1(event));
			break;
		}
		case Midi.typeKeyOff: {
			s = ("key " + Midi.GetChan(event) + " " + Midi.GetData0(event) + " " + Midi.GetData1(event));
			break;
		}
		case Midi.typeClock: {
			s = "clock";
		}
		default:
			Midi.FreeEv(event); // otherwise dispose the event
			break;
		}
		lab.setText(s);
		
		//System.out.println(s);
	}

}