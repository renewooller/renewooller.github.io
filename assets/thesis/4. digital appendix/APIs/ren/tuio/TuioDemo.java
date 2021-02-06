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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class TuioDemo extends JComponent implements TuioListener {

	private Hashtable objectList;

	public TuioDemo() {
		objectList = new Hashtable();
	}

	public void addTuioObj(long s_id, int f_id) {
		objectList.put(new Long(s_id), new TuioObject(s_id, f_id));
	}

	public void updateTuioObj(long s_id, int f_id, float x, float y, float a, float X, float Y,
			float A, float m, float r) {
		TuioObject tobj = (TuioObject) objectList.get(new Long(s_id));
		tobj.update((int) (x * 640), (int) (y * 480), a, (float) Math.sqrt(X * X + Y * Y), A, m, r);
	}

	public void removeTuioObj(long s_id, int f_id) {
		objectList.remove(new Long(s_id));
	}

	public void refresh() {
		repaint();
	}

	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		g2.setPaint(Color.white);
		g2.fillRect(0, 0, 640, 480);

		Enumeration e = objectList.elements();
		while (e.hasMoreElements()) {
			((TuioObject) (e.nextElement())).paint(g2);
		}
	}

	public static void main(String argv[]) {

		TuioDemo demo = new TuioDemo();
		TuioClient client = null;

		switch (argv.length) {
		case 1:
			try {
				client = new TuioClient(Integer.parseInt(argv[1]));
			} catch (Exception e) {
			}
			break;
		case 0:
			client = new TuioClient();
			break;
		}

		if (client != null) {
			client.addTuioListener(demo);
			client.connect();
		} else {
			System.out.println("usage: java TuioDemo [port]");
			System.exit(0);
		}

		JFrame window = new JFrame();
		window.getContentPane().add(demo);

		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

		window.pack();
		Insets ins = window.getInsets();
		int width = 640 + ins.left + ins.right;
		int height = 480 + ins.top + ins.bottom;
		window.setSize(width, height);

		window.setTitle("TuioDemo");
		window.setResizable(false);
		window.setVisible(true);
		window.repaint();
	}
}
