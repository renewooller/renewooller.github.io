package ren.tuio;
/*
 TUIO Java backend - part of the reacTIVision project
 http://www.iua.upf.es/mtg/reacTable

 Copyright (c) 2005 Martin Kaltenbrunner <mkalten@iua.upf.es>

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files
 (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge,
 publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 Any person wishing to distribute modifications to the Software is
 requested to send the modifications to the original developer so that
 they can be incorporated into the canonical version.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;

public class TuioClient implements OSCListener {

	private int port = 3333;

	private OSCPortIn oscPort;

	private Hashtable objectList = new Hashtable();

	private Vector aliveList = new Vector();

	private Vector newList = new Vector();

	private int currentFrame = 0;

	private int lastFrame = 0;

	private Vector listenerList = new Vector();

	public TuioClient(int port) {
		this.port = port;
	}

	public TuioClient() {
	}

	public void connect() {
		try {
			oscPort = new OSCPortIn(port);
			oscPort.addListener("/tuio/2Dobj", this);
			oscPort.startListening();
		} catch (Exception e) {
			System.out.println("failed to connect to port " + port);
		}
	}

	public void disconnect() {
		oscPort.stopListening();
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		;
		oscPort.close();
	}

	public void addTuioListener(TuioListener listener) {
		listenerList.addElement(listener);
	}

	public void removeTuioListener(TuioListener listener) {
		listenerList.removeElement(listener);
	}

	public void acceptMessage(Date date, OSCMessage message) {

		Object[] args = message.getArguments();
		String command = (String) args[0];

		if ((command.equals("set")) && (currentFrame >= lastFrame)) {
			int s_id = ((Integer) args[1]).intValue();
			int f_id = ((Integer) args[2]).intValue();
			float x = ((Float) args[3]).floatValue();
			float y = ((Float) args[4]).floatValue();
			float a = ((Float) args[5]).floatValue();
			float X = ((Float) args[6]).floatValue();
			float Y = ((Float) args[7]).floatValue();
			float A = ((Float) args[8]).floatValue();
			float m = ((Float) args[9]).floatValue();
			float r = ((Float) args[10]).floatValue();

			boolean add_object = false;
			if (objectList.get(args[1]) == null) {
				objectList.put(args[1], args[2]);
				add_object = true;
			}

			// System.out.println(id+" "+c+" "+x+" "+y+" "+a+" "+X+" "+Y+" "+A+"
			// "+m+" "+r);
			for (int i = 0; i < listenerList.size(); i++) {
				TuioListener listener = (TuioListener) listenerList.elementAt(i);
				if (listener != null) {
					if (add_object)
						listener.addTuioObj(s_id, f_id);
					listener.updateTuioObj(s_id, f_id, x, y, a, X, Y, A, m, r);
				}
			}

		} else if ((command.equals("alive")) && (currentFrame >= lastFrame)) {

			for (int i = 1; i < args.length; i++) {
				// get the message content
				newList.addElement(args[i]);
				// reduce the object list to the lost objects
				if (aliveList.contains(args[i]))
					aliveList.removeElement(args[i]);
			}

			// remove the remaining objects
			for (int i = 0; i < aliveList.size(); i++) {
				int s_id = ((Integer) aliveList.elementAt(i)).intValue();
				int f_id = ((Integer) objectList.remove(aliveList.elementAt(i))).intValue();
				// System.out.println("remove "+id);
				for (int j = 0; j < listenerList.size(); j++) {
					TuioListener listener = (TuioListener) listenerList.elementAt(j);
					if (listener != null)
						listener.removeTuioObj(s_id, f_id);
				}
			}

			Vector buffer = aliveList;
			aliveList = newList;

			// recycling of the vector
			newList = buffer;
			newList.clear();

		} else if (command.equals("fseq")) {
			lastFrame = currentFrame;
			currentFrame = ((Integer) args[1]).intValue();

			if (currentFrame > lastFrame) {
				for (int i = 0; i < listenerList.size(); i++) {
					TuioListener listener = (TuioListener) listenerList.elementAt(i);
					if (listener != null)
						listener.refresh();
				}
			}
		}
	}
}
