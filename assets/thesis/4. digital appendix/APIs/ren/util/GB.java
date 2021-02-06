//
// /  GB.java
//  
//
//  Created by Rene Wooller on Sat Nov 09 2002.
//  Copyright (c) 2002 PZO. All rights reserved.
//
package ren.util;

import java.awt.*;

public class GB {

	/**
	 * @param container
	 *            that will have the thing put into it
	 * @param x
	 *            position in the grid
	 * @param y
	 *            position in the grid
	 * @param component
	 *            that is to be put into the container
	 * @param width
	 *            in squares on the grid that this component will cover
	 * @param height
	 *            in squares on the grid that this component will cover
	 * @param itop
	 *            inset
	 * @param iright
	 *            inset
	 * @param ibottom
	 *            inset
	 * @param ileft
	 *            inset
	 * @param weightx
	 * @param weighty
	 * @param fillx -
	 *            expand across x
	 * @param filly -
	 *            expand across y
	 * @param anchor -
	 *            north east south or west
	 * @param posInContainer -
	 *            position in the vector where it is to be added
	 */
	public static void add(Container container, int x, int y,
			Component component, int width, int height, int itop, int iright,
			int ibottom, int ileft, double weightx, double weighty,
			boolean fillx, boolean filly, String anchor, int posInContainer) {
		GridBagConstraints c = ((GridBagLayout) container.getLayout())
				.getConstraints(container);

		c.insets.top = itop;
		c.insets.right = iright;
		c.insets.bottom = ibottom;
		c.insets.left = ileft;

		c.gridwidth = width;
		c.gridheight = height;

		c.gridx = x;
		c.gridy = y;

		c.weightx = weightx;
		c.weighty = weighty;

		//
		//c.anchor = c.;
		if (anchor == "center") {
			c.anchor = c.CENTER;
		} else if (anchor == "north") {
			c.anchor = c.NORTH;
		} else if (anchor == "east") {
			c.anchor = c.EAST;
		} else if (anchor == "south") {
			c.anchor = c.SOUTH;
		} else if (anchor == "west") {
			c.anchor = c.WEST;
		}

		if (fillx) {
			if (filly)
				c.fill = c.BOTH;
			else
				c.fill = c.HORIZONTAL;
		} else if (filly) {
			c.fill = c.VERTICAL;
		} else {
			c.fill = c.NONE;
		}

		container.add(component, c, posInContainer);
		//container.validate();
	}

	public static void add(Container container, int x, int y,
			Component component, int width, int height, int itop, int iright,
			int ibottom, int ileft, double weightx, double weighty,
			boolean fillx, boolean filly, String anchor) {
		add(container, x, y, component, width, height, itop, iright, ibottom,
				ileft, weightx, weighty, fillx, filly, anchor, container
						.getComponentCount());
	}

	public static void add(Container container, int x, int y,
			Component component, int width, int height, int itop, int iright,
			int ibottom, int ileft, double weightx, double weighty,
			boolean fillx, boolean filly) {
		add(container, x, y, component, width, height, itop, iright, ibottom,
				ileft, weightx, weighty, fillx, filly, "center", container
						.getComponentCount());
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int itop, int iright, int ibottom, int ileft,
			double weightx, double weighty) {

		add(container, x, y, component, 1, 1, itop, iright, ibottom, ileft,
				weightx, weighty, false, false, "center");
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int width, int height, int itop, int iright,
			int ibottom, int ileft, double weightx, double weighty) {

		add(container, x, y, component, width, height, itop, iright, ibottom,
				ileft, weightx, weighty, false, false, "center");
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int itop, int iright, int ibottom, int ileft) {

		add(container, x, y, component, itop, iright, ibottom, ileft, 0.0, 0.0);
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int width, int height, int insets) {

		add(container, x, y, component, width, height, insets, insets, insets,
				insets, 0.0, 0.0);
	}

	public static void add(Container container, int x, int y,
			Component component, int width, int height, double weight) {

		add(container, x, y, component, width, height, 0, 0, 0, 0, weight,
				weight);
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int width, int height) {

		add(container, x, y, component, width, height, 0);
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int width, int height, String anchor) {

		add(container, x, y, component, width, height, 0, 0, 0, 0, 0.0, 0.0,
				false, false, anchor);
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, int width, int height, boolean xfill,
			boolean yfill) {
		add(container, x, y, component, width, height, 0, 0, 0, 0, 0.0, 0.0,
				xfill, yfill, "center");
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component, boolean xfill, boolean yfill) {
		add(container, x, y, component, 1, 1, 0, 0, 0, 0, 0.0, 0.0, xfill,
				yfill, "center");
	}

	/*
	 */
	public static void add(Container container, int x, int y,
			Component component) {
		add(container, x, y, component, 1, 1, 0);
	}

}