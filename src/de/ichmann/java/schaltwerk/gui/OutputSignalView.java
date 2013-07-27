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

import de.ichmann.java.schaltwerk.blocks.Output;
import de.ichmann.java.schaltwerk.blocks.Signals;

/**
 * Represents an output of a circuit as a lamp signaling zero or one.
 * 
 * @author Christian Wichmann
 */
public class OutputSignalView extends BaseView {

	private final Output signal;

	private static final int RADIUS_LAMP = 25;
	private static final int RADIUS_SIGNAL = 15;

	public OutputSignalView(final Output signal) {

		this.signal = signal;

		final int WIDTH = 40;
		final int HEIGHT = 40;

		blockBounds.setBounds(0, 0, WIDTH, HEIGHT);
		signalShapes.add(new SignalShape(blockBounds.x - RADIUS_SIGNAL / 2,
				blockBounds.y + blockBounds.height / 2 - RADIUS_SIGNAL / 2,
				RADIUS_SIGNAL, RADIUS_SIGNAL, signal));
	}

	/**
	 * Draw this output signal on graphics object.
	 * 
	 * @param g
	 *            graphics context to paint on
	 */
	@Override
	void drawBlock(Graphics g) {

		g.drawLine(blockBounds.x + blockBounds.width / 2, blockBounds.y
				+ blockBounds.height / 2, blockBounds.x, blockBounds.y
				+ blockBounds.height / 2);

		if (signal.getSignalValue() == Signals.ONE) {
			g.setColor(ColorFactory.getInstance().getHighlightColor());
		} else if (signal.getSignalValue() == Signals.ZERO) {
			g.setColor(ColorFactory.getInstance().getBackgroundColor());
		}

		g.fillOval(blockBounds.x + blockBounds.width / 2, blockBounds.y
				+ blockBounds.height / 2, RADIUS_LAMP, RADIUS_LAMP);
		g.setColor(ColorFactory.getInstance().getForegroundColor());

		g.drawOval(blockBounds.x + blockBounds.width / 2, blockBounds.y
				+ blockBounds.height / 2, RADIUS_LAMP, RADIUS_LAMP);

		// TODO add crossing lines through circle
	}
}
