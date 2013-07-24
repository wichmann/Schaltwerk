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
 * Represents a base block for building digital circuits like AND, OR, NOT, ...
 * Only these blocks implement digital logic at its basis. All other blocks ( as
 * <code>CompoundBlock</code>) consists of at least on base block.
 * 
 * A base block can have many input signals but <b>only one</b> output signal!
 * 
 * @author Christian Wichmann
 */
public abstract class BaseBlock extends Block {

	/**
	 * Initializes a base block like an AND or an OR block. Only the number of
	 * inputs has to be given because a base block has <b>always</b> only one
	 * output.
	 * 
	 * @param blockID
	 *            block identification
	 * @param inputs
	 *            number of inputs for this block
	 */
	public BaseBlock(String blockID, int inputs) {

		super(blockID, inputs, 1);
	}

	@Override
	public abstract void evaluate();

	@Override
	public final void unevaluate() {

		resetEvaluated();

		unevaluateNextHops();
	}
}
