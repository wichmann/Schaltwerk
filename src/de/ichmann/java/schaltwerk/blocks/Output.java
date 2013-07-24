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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an output of a block.
 * 
 * @author Christian Wichmann
 */
public class Output extends Signal {

	private List<Input> nextHops = new ArrayList<Input>();

	/**
	 * Initializes a signal as output for a block.
	 * 
	 * @param owner
	 *            block owning this signal
	 * @param signalID
	 *            identification of this signal
	 */
	public Output(final Block owner, final String outputID) {

		super(owner, outputID);
	}

	/**
	 * Connects this output of a block to one input of another block. This
	 * method returns a reference to itself so the following code is possible:
	 * 
	 * <code>and1.output("1").connectTo(or1.input("1")).connectTo(or2.input("3"));</code>
	 * 
	 * @param nextHop
	 *            input of next block to connect to
	 * @return reference to this output
	 */
	public final Signal connectTo(final Input nextHop) {

		nextHops.add(nextHop);
		return this;
	}

	/**
	 * Disconnects this output of a block from one input of another block.
	 * 
	 * @param nextHop
	 *            input of next block to disconnect from
	 * @return reference to this output
	 */
	public final Signal disconnectFrom(final Input nextHop) {

		nextHops.remove(nextHop);
		return this;
	}

	/**
	 * Returns a list of all next hops from this output on. To protect the
	 * internal list only a defensive copy is returned.
	 * 
	 * @return list of all next hops
	 */
	public final List<Input> listNextHops() {

		return Collections.unmodifiableList(nextHops);
	}

	/**
	 * Propagates value of this output to all next hops connected to this
	 * output.
	 */
	protected final void propagateOutput() {

		for (Input nextHop : listNextHops()) {
			nextHop.setSignalValue(getSignalValue());
		}
	}

	@Override
	public final String toString() {

		return getOwnerBlock().getBlockID() + ":(O)" + getSignalID();
	}
}
