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
package de.ichmann.java.schaltwerk.blocks;

/**
 * @author Christian Wichmann
 * 
 */
public class NOT extends BaseBlock {

	private static int notBlockCount = 0;

	/**
	 * Initializes a NOT block. This blocks have always only one input.
	 */
	public NOT() {

		super(generateBlockID(), 1);
	}

	/**
	 * Generates a new block id by combining name of this base block and a
	 * incrementing number.
	 * 
	 * @return new block id
	 */
	private static String generateBlockID() {

		String s = "NOT." + Integer.toString(notBlockCount);

		notBlockCount += 1;

		return s;
	}

	@Override
	public void evaluate() {

		if (!wasAlreadyEvaluated()) {
			// calculate output of NOT block and...
			if (input("1").getSignalValue() == Signals.ONE) {
				output("1").setSignalValue(Signals.ZERO);
			} else if (input("1").getSignalValue() == Signals.ZERO) {
				output("1").setSignalValue(Signals.ONE);
			}

			propagateBlockOutputs();

			hasBeenEvaluated();

			evaluateNextHops();
		}
	}
}
