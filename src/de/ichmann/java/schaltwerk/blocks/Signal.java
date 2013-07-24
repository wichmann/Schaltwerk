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
 * Represents a point in a digital circuit which holds a specific signal. A
 * signal could be a Input or Output of a block.
 * 
 * @author Christian Wichmann
 */
public class Signal {

	private final Block owner;
	private Signals signalValue;
	private String signalID;

	public Signal(Block owner, String signalID, Signals signalValue) {

		this.owner = owner;
		this.signalID = signalID;
		this.signalValue = signalValue;
	}

	public Signal(Block owner, String signalID) {

		this.owner = owner;
		this.signalID = signalID;
		this.signalValue = Signals.ZERO;
	}

	/**
	 * Returns reference of block to which this signal belongs to. Every signal
	 * can only belong to a <b>single</b> block.
	 * 
	 * @return reference of block to which this signal belongs to
	 */
	public Block getOwnerBlock() {

		return owner;
	}

	/**
	 * Gets signal id identifying this signal inside its surrounding block.
	 * 
	 * @return signal id
	 */
	public String getSignalID() {

		return signalID;
	}

	/**
	 * Gets signal value for this signal.
	 * 
	 * @return signal value
	 */
	public Signals getSignalValue() {

		return signalValue;
	}

	/**
	 * Sets signal value for this signal.
	 * 
	 * @param newSignal
	 *            signal value to be set
	 */
	public void setSignalValue(Signals newSignal) {

		signalValue = newSignal;
	}

	@Override
	public String toString() {

		return owner.getBlockID() + ":" + getSignalID();
	}
}
