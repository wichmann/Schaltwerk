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
 * Represents a "NOR" block which output is only ONE when all inputs are ZERO.
 * 
 * @author Christian Wichmann
 */
public class NOR extends BaseBlock {

	private static final Logger LOG = LoggerFactory.getLogger(AND.class);

	private static int norBlockCount = 0;

	/**
	 * Initializes a NOR block. A valid block id will be generated
	 * automatically.
	 * 
	 * @param inputs
	 *            number of inputs
	 */
	public NOR(final int inputs) {

		super(generateBlockID(), inputs);

		setBlockType(Blocks.NOR);
	}

	/**
	 * Initializes a NOR block with a given block id.
	 * 
	 * @param inputs
	 *            number of inputs
	 * @param blockID
	 *            block identification
	 */
	public NOR(final String blockID, final int inputs) {

		super(blockID, inputs);

		setBlockType(Blocks.NOR);
	}

	/**
	 * Generates a new block id by combining name of this base block and a
	 * incrementing number.
	 * 
	 * @return new block id
	 */
	private static String generateBlockID() {

		String s = "NOR." + Integer.toString(norBlockCount);

		norBlockCount += 1;

		return s;
	}

	@Override
	public void evaluate() {

		LOG.trace("Evaluating NOR block...");

		if (!wasAlreadyEvaluated()) {
			// calculate output of NOR block and...
			output("1").setSignalValue(Signals.ONE);
			for (String s : inputList()) {
				if (input(s).getSignalValue() == Signals.ONE) {
					output("1").setSignalValue(Signals.ZERO);
					break;
				}
			}

			propagateBlockOutputs();

			hasBeenEvaluated();

			evaluateNextHops();
		}
	}
}
