package ren.tuio;
/*
 TUIO Java backend - part of the reacTIVision project
 http://www.iua.upf.es/mtg/reacTable

 Copyright (c) 2006 Martin Kaltenbrunner <mkalten@iua.upf.es>

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
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TuioObject {

	private Shape square;

	private Color color;

	private int xpos, ypos;

	private float angle;

	private float motion_speed, rotation_speed;

	private float motion_accel, rotation_accel;

	private long session_id;

	private int fiducial_id;

	public TuioObject(long s_id, int f_id) {
		square = new Rectangle2D.Float(0, 0, 50, 50);
		xpos = ypos = 0;
		angle = 0.0f;
		motion_speed = rotation_speed = 0.0f;
		motion_accel = rotation_accel = 0.0f;

		session_id = s_id;
		fiducial_id = f_id;
	}

	public void paint(Graphics2D g) {
		g.setPaint(color);
		g.fill(square);
		g.setPaint(Color.white);
		g.drawString(fiducial_id + "", xpos + 15, ypos + 25);
	}

	public void update(int x, int y, float a, float ms, float rs, float ma, float ra) {

		double dx = x - 25 - xpos;
		double dy = y - 25 - ypos;
		double da = a - angle;

		xpos = x - 25;
		ypos = y - 25;
		angle = a;

		if ((dx != 0) || (dy != 0)) {
			AffineTransform trans = AffineTransform.getTranslateInstance(dx, dy);
			square = trans.createTransformedShape(square);
		}

		if (da != 0) {
			AffineTransform trans = AffineTransform.getRotateInstance(da, x, y);
			square = trans.createTransformedShape(square);
		}

		motion_speed = ms;
		rotation_speed = rs;
		motion_accel = ma;
		rotation_accel = ra;

		int red = (int) (255 * motion_speed);
		if (red > 255)
			red = 255;
		int blue = (int) (255 * rotation_speed);
		if (blue > 255)
			blue = 255;
		color = new Color(red, 0, blue);
	}

}
