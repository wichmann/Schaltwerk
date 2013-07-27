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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class providing helper methods for all view components like BlockView,
 * InputSignalView and OutputSignalView.
 * 
 * @author Christian Wichmann
 */
public abstract class BaseView {

	protected Rectangle blockBounds = new Rectangle();
	protected final List<SignalShape> signalShapes = new ArrayList<SignalShape>();

	private boolean highlighted = false;
	private boolean selected = false;

	abstract void drawBlock(final Graphics g);

	/**
	 * Checks whether a given point is inside this blocks boundary.
	 * 
	 * @param p
	 *            point to check whether it is inside this blocks boundary
	 * @return true, if point is inside this blocks boundary
	 */
	public boolean contains(final Point p) {

		return blockBounds.contains(p);
	}

	/**
	 * Returns center point of this block view component.
	 * 
	 * @return center point for this block
	 */
	public Point getCenterPoint() {

		return new Point((int) blockBounds.getCenterX(),
				(int) blockBounds.getCenterY());
	}

	/**
	 * Moves this blocks coordinates by a given delta. No repaint will
	 * automatically be done!
	 * 
	 * @param delta
	 *            delta to move this block by
	 */
	public void moveBlockView(final Point delta) {

		// TODO check bounds?!
		blockBounds.x += delta.x;
		blockBounds.y += delta.y;

		for (SignalShape s : signalShapes) {
			s.x += delta.x;
			s.y += delta.y;
		}
	}

	/**
	 * Checks whether a given point lies on one of this blocks signals (inputs,
	 * outputs).
	 * 
	 * @param p
	 *            point to check for
	 * @return signals shape for given point or null when point is not a signal
	 */
	public SignalShape checkIfPointIsSignal(final Point p) {

		for (SignalShape s : signalShapes) {
			if (s.contains(p)) {
				return s;
			}
		}
		return null;
	}

	/*
	 * ===== Getter and setter for block view attributes =====
	 */

	/**
	 * Sets whether this block is highlighted because mouse hovers over it.
	 * 
	 * @param highlighted
	 *            whether to highlight this block
	 */
	public void setHighlighted(final boolean highlighted) {

		this.highlighted = highlighted;
	}

	/**
	 * Returns whether this block is highlighted because mouse hovers over it.
	 * 
	 * @return whether this block is highlighted
	 */
	public boolean isHighlighted() {

		return highlighted;
	}

	/**
	 * Sets whether this block is selected.
	 * 
	 * @param selected
	 *            whether to selected this block
	 */
	public void setSelected(final boolean selected) {

		this.selected = selected;
	}

	/**
	 * Returns whether this block is selected.
	 * 
	 * @return whether this block is selected
	 */
	public boolean isSelected() {

		return selected;
	}
}
