/*****************************************************************************
 * Schaltwerk - A free and extensible digital simulator
 * Copyright (c) 2013 Christian Wichmann
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/

package de.ichmann.java.schaltwerk.gui;

import java.awt.Point;
import java.awt.geom.Ellipse2D;

import de.ichmann.java.schaltwerk.blocks.Signal;

/**
 * Stores a shape describing a signal (input or output of a block). It is used
 * to determine where a block can be connected to another.
 * 
 * This component is the view/controller for a underlying signal.
 * 
 * @author Christian Wichmann
 */
public class SignalShape extends Ellipse2D.Float {

	private static final long serialVersionUID = -1254640466415111925L;

	private final int radius;

	private final Signal attachedSignal;

	/**
	 * Initializes a shape that is attached to a specific signal, either an
	 * input or an output of a block.
	 * 
	 * @param x
	 *            the X coordinate of the upper-left corner of the framing
	 *            rectangle
	 * @param y
	 *            the Y coordinate of the upper-left corner of the framing
	 *            rectangle
	 * @param w
	 *            the width of the framing rectangle
	 * @param h
	 *            the height of the framing rectangle
	 * @param attachedSignal
	 *            sihnal that is attached to this shape
	 */
	public SignalShape(float x, float y, float w, float h, Signal attachedSignal) {

		super(x, y, w, h);

		this.attachedSignal = attachedSignal;

		radius = (int) w;
	}

	/**
	 * Returns the attached signal for this shape.
	 * 
	 * @return attached signal for this shape
	 */
	public Signal getAttachedSignal() {

		return attachedSignal;
	}

	/**
	 * Returns a point for this signal. The point is the center of the signal.
	 * 
	 * @return center point for given signal
	 */
	public Point pointForSignal() {

		Point p = null;

		p = new Point((int) x, (int) y - radius);

		assert p != null;
		return p;
	}
}
