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

import java.util.List;

/**
 * Class providing factory methods to get combinational circuits based on given
 * input/output behavior.
 * 
 * @author Christian Wichmann
 */
public class CombinationalCircuits {

	private static CombinationalCircuits combinationalCircuitsFactory = null;

	private CombinationalCircuits() {
	}

	public static CombinationalCircuits getInstance() {

		if (combinationalCircuitsFactory == null) {
			combinationalCircuitsFactory = new CombinationalCircuits();
		}
		return combinationalCircuitsFactory;
	}

	public CompoundBlock getCombinationalCircuit(List<List<Boolean>> input,
			List<Boolean> output) {

		// test if data is valid
		if (input == null || output == null) {
			throw new IllegalArgumentException(
					"Input or output parameter is null.");
		}
		final int lines = output.size();
		int variables = 0;
		for (List<Boolean> list : input) {
			if (list.size() != lines) {
				throw new IllegalArgumentException(
						"Input and output data don't have the same length.");
			}
			variables += 1;
		}
		if (lines != Math.pow(2, variables)) {
			throw new IllegalArgumentException(
					"Not enought data sets for given number of independent variables.");
		}

		// simplify given combinational circuit

		// build compound block for combinational circuit
		CompoundBlock combinationalCircuit = new CompoundBlock(
				"CombinationalCircuit", variables, 1);

		return combinationalCircuit;
	}
}
