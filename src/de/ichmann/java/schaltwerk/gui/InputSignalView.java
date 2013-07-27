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

import de.ichmann.java.schaltwerk.blocks.Input;

/**
 * Represents an input of a circuit as a switch to change between zero and one.
 * 
 * @author Christian Wichmann
 */
public class InputSignalView extends BaseView {

	private final Input signal;

	private static final int RADIUS_SIGNAL = 15;

	public InputSignalView(final Input input) {

		this.signal = input;

		final int WIDTH = 40;
		final int HEIGHT = 40;

		blockBounds.setBounds(0, 0, WIDTH, HEIGHT);
		signalShapes.add(new SignalShape(blockBounds.x - RADIUS_SIGNAL / 2,
				blockBounds.y + blockBounds.height / 2 - RADIUS_SIGNAL / 2,
				RADIUS_SIGNAL, RADIUS_SIGNAL, signal));
	}

	/**
	 * Draw this input signal on graphics object.
	 * 
	 * @param g
	 *            graphics context to paint on
	 */
	@Override
	void drawBlock(Graphics g) {

	}
}
