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
package de.ichmann.java.schaltwerk;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.ichmann.java.schaltwerk.blocks.AND;
import de.ichmann.java.schaltwerk.blocks.CompoundBlock;
import de.ichmann.java.schaltwerk.blocks.NOR;
import de.ichmann.java.schaltwerk.blocks.OR;
import de.ichmann.java.schaltwerk.blocks.Signals;

/**
 * Tests class CompoundBlock and base blocks like and, or, not.
 * 
 * @author Christian Wichmann
 */
public class CompoundBlockTest {

	private CompoundBlock circuit1;
	private CompoundBlock circuit2;

	/**
	 * Sets up test environment by designing a small and simple logic circuit.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		// set up first circuit
		AND and1 = new AND(2);
		AND and2 = new AND(2);
		OR or1 = new OR(2);
		and1.output("1").connectTo(or1.input("1"));
		and2.output("1").connectTo(or1.input("2"));

		circuit1 = new CompoundBlock("Schaltung", 4, 1);
		circuit1.internalInput("1").connectTo(and1.input("1"));
		circuit1.internalInput("2").connectTo(and1.input("2"));
		circuit1.internalInput("3").connectTo(and2.input("1"));
		circuit1.internalInput("4").connectTo(and2.input("2"));
		or1.output("1").connectTo(circuit1.internalOutput("1"));

		// set up second circuit (rs-flip-flop)
		circuit2 = new CompoundBlock("RS-Flipflop", 2, 1);
		NOR nor1 = new NOR(2);
		NOR nor2 = new NOR(2);
		nor1.output("1").connectTo(nor2.input("1"));
		nor2.output("1").connectTo(nor1.input("2"));
		circuit2.internalInput("1").connectTo(nor1.input("1"));
		circuit2.internalInput("2").connectTo(nor2.input("2"));
		nor2.output("1").connectTo(circuit2.internalOutput("1"));
	}

	/**
	 * Test method for
	 * {@link de.ichmann.java.schaltwerk.blocks.CompoundBlock#evaluate()}.
	 */
	@Test
	public final void testEvaluate() {

		circuit1.evaluate();
		circuit1.unevaluate();

		assertEquals("", Signals.ZERO, circuit1.output("1").getSignalValue());

		circuit1.input("1").setSignalValue(Signals.ONE);
		circuit1.input("3").setSignalValue(Signals.ONE);
		circuit1.evaluate();
		circuit1.unevaluate();

		assertEquals("", Signals.ZERO, circuit1.output("1").getSignalValue());

		circuit1.input("2").setSignalValue(Signals.ONE);
		circuit1.evaluate();
		circuit1.unevaluate();

		assertEquals("", Signals.ONE, circuit1.output("1").getSignalValue());

		circuit1.input("1").setSignalValue(Signals.ZERO);
		circuit1.evaluate();
		circuit1.unevaluate();

		assertEquals("", Signals.ZERO, circuit1.output("1").getSignalValue());
	}

	/**
	 * Test method for
	 * {@link de.ichmann.java.schaltwerk.blocks.CompoundBlock#evaluate()}.
	 */
	@Test
	public final void testEvaluate2() {

		circuit2.evaluate();
		circuit2.unevaluate();
		assertEquals("", Signals.ZERO, circuit2.output("1").getSignalValue());

		circuit2.input("1").setSignalValue(Signals.ONE);
		circuit2.input("2").setSignalValue(Signals.ZERO);

		circuit2.evaluate();
		circuit2.unevaluate();
		assertEquals("", Signals.ONE, circuit2.output("1").getSignalValue());

		circuit2.input("1").setSignalValue(Signals.ONE);
		circuit2.input("2").setSignalValue(Signals.ZERO);

		circuit2.evaluate();
		circuit2.unevaluate();
		assertEquals("", Signals.ONE, circuit2.output("1").getSignalValue());

		circuit2.input("1").setSignalValue(Signals.ZERO);
		circuit2.input("2").setSignalValue(Signals.ZERO);

		circuit2.evaluate();
		circuit2.unevaluate();
		assertEquals("", Signals.ONE, circuit2.output("1").getSignalValue());

		circuit2.input("1").setSignalValue(Signals.ZERO);
		circuit2.input("2").setSignalValue(Signals.ONE);

		circuit2.evaluate();
		circuit2.unevaluate();
		assertEquals("", Signals.ZERO, circuit2.output("1").getSignalValue());

		circuit2.input("1").setSignalValue(Signals.ZERO);
		circuit2.input("2").setSignalValue(Signals.ZERO);

		circuit2.evaluate();
		circuit2.unevaluate();
		assertEquals("", Signals.ZERO, circuit2.output("1").getSignalValue());
	}
}
