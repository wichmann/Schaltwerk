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
 * Represents a "OR" block which output is ONE when one or more inputs are ONE.
 * 
 * @author Christian Wichmann
 */
public class OR extends BaseBlock {

	private static final Logger LOG = LoggerFactory.getLogger(OR.class);

	private static int orBlockCount = 0;

	/**
	 * Initializes a OR block.
	 * 
	 * @param inputs
	 *            number of inputs
	 */
	public OR(int inputs) {

		super(generateBlockID(), inputs);

		setBlockType(Blocks.OR);
	}

	/**
	 * Generates a new block id by combining name of this base block and a
	 * incrementing number.
	 * 
	 * @return new block id
	 */
	private static String generateBlockID() {

		String s = "OR." + Integer.toString(orBlockCount);

		orBlockCount += 1;

		return s;
	}

	@Override
	public void evaluate() {

		LOG.trace("Evaluating OR block...");

		if (!wasAlreadyEvaluated()) {
			// calculate output of OR block and...
			output("1").setSignalValue(Signals.ZERO);
			for (String s : inputList()) {
				if (input(s).getSignalValue() == Signals.ONE) {
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
