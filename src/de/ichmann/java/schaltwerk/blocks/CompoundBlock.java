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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a block consisting of at least one base block. A CompoundBlock can
 * contain also more CompoundBlock's and is defined through its input and output
 * signals.
 * 
 * @author Christian Wichmann
 */
public class CompoundBlock extends Block {

	private static final Logger LOG = LoggerFactory
			.getLogger(CompoundBlock.class);

	private final List<Output> internalInputsList = new ArrayList<Output>();
	private final List<Input> internalOutputsList = new ArrayList<Input>();

	/*
	 * TODO Hold block list for every compound block. Find a good way to set all
	 * included blocks, like using a addBlock() method or find them
	 * automatically by traversing the network.
	 */
	// private final List<Block> blockList = new ArrayList<Block>();

	/**
	 * Initializes a compound block with a given number of inputs and outputs.
	 * 
	 * @param blockID
	 *            identification of this block
	 * @param inputs
	 *            number of inputs
	 * @param outputs
	 *            number of outputs
	 */
	public CompoundBlock(final String blockID, final int inputs,
			final int outputs) {

		super(blockID, inputs, outputs);

		createInternalSignals();
	}

	/**
	 * Initializes a compound block with a given number of inputs and outputs
	 * and their names.
	 * 
	 * @param blockID
	 *            identification of this block
	 * @param inputs
	 *            names of inputs
	 * @param outputs
	 *            names of outputs
	 */
	public CompoundBlock(final String blockID, final List<String> inputs,
			final List<String> outputs) {

		super(blockID, inputs, outputs);

		createInternalSignals();
	}

	/**
	 * Creates internal input and output objects for connecting containing
	 * blocks.
	 */
	private void createInternalSignals() {

		for (String s : outputList()) {
			Input tmp = new Input(this, s);
			internalOutputsList.add(tmp);
		}

		for (String s : inputList()) {
			Output tmp = new Output(this, s);
			internalInputsList.add(tmp);
		}
	}

	/**
	 * Returns <code>Output</code> object representing the internal connection
	 * from input of compound block to internal blocks.
	 * 
	 * @param inputID
	 *            identification for input to which connect the internal blocks
	 * @return <code>Output</code> object representing the input internally
	 */
	public final Output internalInput(final String inputID) {

		for (Output o : internalInputsList) {
			if (inputID.equals(o.getSignalID())) {
				return o;
			}
		}
		throw new IllegalArgumentException("Given input id not found.");
	}

	/**
	 * Returns <code>Input</code> object representing the internal connection
	 * from internal blocks to output of compound block.
	 * 
	 * @param outputID
	 *            identification for output to which connect the internal blocks
	 * @return <code>Input</code> object representing the output internally
	 */
	public final Input internalOutput(final String outputID) {

		for (Input i : internalOutputsList) {
			if (outputID.equals(i.getSignalID())) {
				return i;
			}
		}
		throw new IllegalArgumentException("Given output id not found.");
	}

	@Override
	public final void evaluate() {

		/*
		 * TODO Decouple evaluation of block and evaluation of next blocks so at
		 * first only all blocks at first level are evaluated and then they call
		 * evaluate on their next hops.
		 */

		if (!wasAlreadyEvaluated()) {
			LOG.debug("Evaluate compound block " + getBlockID());

			// copy values from outer inputs to internal ones
			for (String s : inputList()) {
				internalInput(s).setSignalValue(input(s).getSignalValue());
				internalInput(s).propagateOutput();
			}

			// call evaluate for all children
			for (Output o : internalInputsList) {
				for (Input nextHop : o.listNextHops()) {
					nextHop.getOwnerBlock().evaluate();
				}
			}

			// set outer outputs to internal values
			for (String s : outputList()) {
				output(s).setSignalValue(internalOutput(s).getSignalValue());
			}

			propagateBlockOutputs();

			hasBeenEvaluated();

			evaluateNextHops();
		}
	}

	@Override
	public final void unevaluate() {

		// call unevaluate for all children
		for (Output o : internalInputsList) {
			for (Input nextHop : o.listNextHops()) {
				nextHop.getOwnerBlock().unevaluate();
			}
		}

		resetEvaluated();

		unevaluateNextHops();
	}
}
