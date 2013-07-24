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
 * Singleton class providing factory methods for common logic
 * 
 * @author Christian Wichmann
 */
public final class BlockFactory {

	private static BlockFactory blockFactory = null;

	private BlockFactory() {
	}

	public static BlockFactory getInstance() {

		if (blockFactory == null) {
			blockFactory = new BlockFactory();
		}
		return blockFactory;
	}

	/**
	 * Returns a new rs-flip-flop block with inputs "R" and "S" and outputs "Q"
	 * and "~Q".
	 * 
	 * A rs-flip-flop is defined as a sequential logic circuit which can be set
	 * or reset by an ONE at two different inputs. If the flip-flop is set the
	 * output "Q" is set to ONE. Output "~Q" has always the inverted value of
	 * "Q".
	 * 
	 * @param resetDominant
	 *            defines whether flip-flop should be reset dominant
	 * @return rs-flip-flop block
	 */
	public CompoundBlock getRSFlipFLop(boolean resetDominant) {

		// TODO add set reset dominance!

		List<String> inputs = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();
		inputs.add("S");
		inputs.add("R");
		outputs.add("Q");
		outputs.add("~Q");

		CompoundBlock circuit = new CompoundBlock("RS-FF.1", inputs, outputs);
		NOR nor1 = new NOR(2);
		NOR nor2 = new NOR(2);
		nor1.output("1").connectTo(nor2.input("1"));
		nor2.output("1").connectTo(nor1.input("2"));
		circuit.internalInput("S").connectTo(nor1.input("1"));
		circuit.internalInput("R").connectTo(nor2.input("2"));
		nor2.output("1").connectTo(circuit.internalOutput("Q"));
		nor1.output("1").connectTo(circuit.internalOutput("~Q"));

		return circuit;
	}
}
