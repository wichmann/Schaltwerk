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

/**
 * Class as base of all block classes e.g. BaseBlock and CompoundBlock.
 * 
 * @author Christian Wichmann
 */
public abstract class Block {

	/*
	 * TODO add block description for users to add comments e.g. block id is
	 * AND.4 or RS-FF.17 but description is "Switch led" or "Flip-flop motor"
	 */

	private final List<Input> inputsList = new ArrayList<Input>();
	private final List<Output> outputsList = new ArrayList<Output>();

	private boolean alreadyEvaluated = false;

	private String blockID = "";

	/**
	 * Initializes a block with number of input and output signals. Signal ids
	 * for inputs and outputs are numbers starting by one.
	 * 
	 * @param inputs
	 * @param outputs
	 */
	public Block(String blockID, int inputs, int outputs) {

		setBlockID(blockID);

		for (int i = 1; i <= inputs; i++) {

			inputsList.add(new Input(this, Integer.toString(i)));
		}

		for (int i = 1; i <= outputs; i++) {

			outputsList.add(new Output(this, Integer.toString(i)));
		}
	}

	/**
	 * Initializes a block with number of input and output signals. Signal ids
	 * for inputs and outputs are given by caller.
	 * 
	 * @param inputs
	 * @param outputs
	 */
	public Block(String blockID, List<String> inputs, List<String> outputs) {

		setBlockID(blockID);

		for (String string : inputs) {
			inputsList.add(new Input(this, string));
		}

		for (String string : outputs) {
			outputsList.add(new Output(this, string));
		}
	}

	public final void addInput(Input newInput) {

		inputsList.add(newInput);
	}

	public final void removeInput(Input oldInput) {

		inputsList.remove(oldInput);
	}

	public final void addOutput(Output newOutput) {

		outputsList.add(newOutput);
	}

	public final void removeOutput(Output oldOutput) {

		outputsList.remove(oldOutput);
	}

	public final int countInputs() {

		return inputsList.size();
	}

	public final int countOutputs() {

		return outputsList.size();
	}

	/**
	 * Returns a list of the identification string for all outputs of this
	 * block.
	 * 
	 * @return list of the identification string for all outputs
	 */
	public final String[] outputList() {

		int j = 0;
		String[] strings = new String[outputsList.size()];

		for (Output i : outputsList) {
			strings[j++] = i.getSignalID();
		}

		return strings;
	}

	/**
	 * Returns a list of the identification string for all inputs of this block.
	 * 
	 * @return list of the identification string for all inputs
	 */
	public final String[] inputList() {

		int j = 0;
		String[] strings = new String[inputsList.size()];

		for (Input i : inputsList) {
			strings[j++] = i.getSignalID();
		}

		return strings;
	}

	/**
	 * Returns <code>Output</code> object for given string.
	 * 
	 * @param outputID
	 *            string representing output to return
	 * @return <code>Output</code> object for given string
	 * @throws IllegalArgumentException
	 *             if given string is not found
	 */
	public final Output output(String outputID) {

		for (Output o : outputsList) {
			if (outputID.equals(o.getSignalID())) {
				return o;
			}
		}
		throw new IllegalArgumentException("Given output id not found.");
	}

	/**
	 * Returns <code>Input</code> object for given string.
	 * 
	 * @param inputID
	 *            string representing input to return
	 * @return <code>Input</code> object for given string
	 * @throws IllegalArgumentException
	 *             if given string is not found
	 */
	public final Input input(String inputID) {

		for (Input i : inputsList) {
			if (inputID.equals(i.getSignalID())) {
				return i;
			}
		}
		throw new IllegalArgumentException("Given input id not found.");
	}

	/**
	 * Gets identification for this block.
	 * 
	 * @return block identification
	 */
	public final String getBlockID() {

		return blockID;
	}

	/**
	 * Sets identification for this block.
	 * 
	 * @param blockID
	 *            block identification to be set
	 */
	private final void setBlockID(String blockID) {

		this.blockID = blockID;
	}

	/**
	 * Returns whether this block has been already evaluated in this cycle.
	 * 
	 * @return whether this block was already evaluated
	 */
	protected final boolean wasAlreadyEvaluated() {

		return alreadyEvaluated;
	}

	/**
	 * Sets internal field to remember that this block was already evaluated.
	 */
	protected final void hasBeenEvaluated() {

		this.alreadyEvaluated = true;
	}

	/**
	 * Resets internal field so block can be newly evaluated in next cycle.
	 */
	protected final void resetEvaluated() {

		this.alreadyEvaluated = false;
	}

	/**
	 * Evaluates this block. This method reads input values, changes internal
	 * states according to inputs and sets values for all outputs and next hops
	 * in the net.
	 * <p>
	 * At the end of evaluation the method <code>hasBeenEvaluated()</code> has
	 * to be called to remember evaluation. If this is not done evaluation can
	 * result in endless loops.
	 * <p>
	 * It is guaranteed that all signals in logical circuits without feedback
	 * reached a stable state after one call of this method on its surrounding
	 * CompoundBlock. Logical networks with feedbacks/loops may require more
	 * than one evaluation cycle. After each call of <code>evaluate()</code> the
	 * method <code>unevaluate()</code> has to be called.
	 */
	public abstract void evaluate();

	/**
	 * Resets this block in a state where it can be evaluated. This is necessary
	 * to prevent endless loops due to feedback and loops in the logical circuit
	 * when <code>evaluate()</code> is called.
	 */
	public abstract void unevaluate();

	/**
	 * Propagates output values to all next hops connected to this block.
	 */
	protected final void propagateBlockOutputs() {

		for (Output o : outputsList) {

			o.propagateOutput();
		}
	}

	/**
	 * Helper method to call method <code>evaluate()</code> of all next hops.
	 */
	protected final void evaluateNextHops() {

		// call evaluate for all children
		for (String s : outputList()) {
			for (Input nextHop : output(s).listNextHops()) {
				Block tmp = nextHop.getOwnerBlock();
				if (!(tmp instanceof CompoundBlock)) {
					nextHop.getOwnerBlock().evaluate();
				}
			}
		}
	}

	/**
	 * Helper method to call method <code>evaluate()</code> of all next hops.
	 */
	protected final void unevaluateNextHops() {

		// call unevaluate for all children
		for (String s : outputList()) {
			for (Input nextHop : output(s).listNextHops()) {
				Block tmp = nextHop.getOwnerBlock();
				if (!(tmp instanceof CompoundBlock)
						&& tmp.wasAlreadyEvaluated()) {
					nextHop.getOwnerBlock().unevaluate();
				}
			}
		}
	}

	@Override
	public final String toString() {

		return blockID;
	}
}
