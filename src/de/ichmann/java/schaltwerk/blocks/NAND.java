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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a "NAND" block which output is only ZERO when all inputs are ONE.
 * 
 * @author Christian Wichmann
 */
public class NAND extends BaseBlock {

	private static final Logger LOG = LoggerFactory.getLogger(AND.class);

	private static int nandBlockCount = 0;

	/**
	 * Initializes a NAND block.
	 * 
	 * @param inputs
	 *            number of inputs
	 */
	public NAND(int inputs) {

		super(generateBlockID(), inputs);
	}

	/**
	 * Generates a new block id by combining name of this base block and a
	 * incrementing number.
	 * 
	 * @return new block id
	 */
	private static String generateBlockID() {

		String s = "NAND." + Integer.toString(nandBlockCount);

		nandBlockCount += 1;

		return s;
	}

	@Override
	public void evaluate() {

		if (!wasAlreadyEvaluated()) {
			// calculate output of NAND block and...
			output("1").setSignalValue(Signals.ZERO);
			for (String s : inputList()) {
				if (input(s).getSignalValue() == Signals.ZERO) {
					output("1").setSignalValue(Signals.ONE);
					break;
				}
			}

			propagateBlockOutputs();

			hasBeenEvaluated();

			evaluateNextHops();
		}
	}
}
