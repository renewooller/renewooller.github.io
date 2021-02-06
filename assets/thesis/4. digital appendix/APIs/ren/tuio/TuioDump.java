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


public class TuioDump implements TuioListener {

	public void addTuioObj(long s_id, int f_id) {
		System.out.println("add " + f_id + " (" + s_id + ")");
	}

	public void updateTuioObj(long s_id, int f_id, float x, float y, float a, float X, float Y,
			float A, float m, float r) {

		System.out.println(f_id + " (" + s_id + ") " + x + " " + y + " " + a + " "
				+ (float) Math.sqrt(X * X + Y * Y) + " " + A + " " + m + " " + r);
	}

	public void removeTuioObj(long s_id, int f_id) {
		System.out.println("remove " + f_id + " (" + s_id + ")");
	}

	public void refresh() {
	}

	public static void main(String argv[]) {

		TuioDump demo = new TuioDump();
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
		} else
			System.out.println("usage: java TuioDump [port]");
	}
}
