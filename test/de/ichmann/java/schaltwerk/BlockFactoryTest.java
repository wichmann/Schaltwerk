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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ichmann.java.schaltwerk.blocks.BlockFactory;
import de.ichmann.java.schaltwerk.blocks.CompoundBlock;
import de.ichmann.java.schaltwerk.blocks.Signals;

/**
 * Tests all factory methods on BlockFactory to create compound blocks like
 * flip-flops etc.
 * 
 * @author Christian Wichmann
 */
public class BlockFactoryTest {

	private CompoundBlock rsff_s;
	private CompoundBlock rsff_r;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		rsff_s = BlockFactory.getInstance().getRSFlipFLop(false);
		rsff_r = BlockFactory.getInstance().getRSFlipFLop(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	/**
	 * Tests rs-flip-flops for correct behavior. Test method for
	 * {@link de.ichmann.java.schaltwerk.blocks.BlockFactory#getRSFlipFLop(boolean)}
	 */
	@Test
	public final void testGetRSFlipFLop() {

		rsff_s.input("R").setSignalValue(Signals.ZERO);
		rsff_s.input("S").setSignalValue(Signals.ZERO);
		rsff_s.evaluate();
		rsff_s.unevaluate();
		assertEquals("", Signals.ZERO, rsff_s.output("Q").getSignalValue());

		rsff_s.input("R").setSignalValue(Signals.ZERO);
		rsff_s.input("S").setSignalValue(Signals.ONE);
		rsff_s.evaluate();
		rsff_s.unevaluate();
		assertEquals("", Signals.ONE, rsff_s.output("Q").getSignalValue());

		rsff_s.input("R").setSignalValue(Signals.ZERO);
		rsff_s.input("S").setSignalValue(Signals.ZERO);
		rsff_s.evaluate();
		rsff_s.unevaluate();
		assertEquals("", Signals.ONE, rsff_s.output("Q").getSignalValue());

		rsff_s.input("R").setSignalValue(Signals.ONE);
		rsff_s.input("S").setSignalValue(Signals.ZERO);
		rsff_s.evaluate();
		rsff_s.unevaluate();
		assertEquals("", Signals.ZERO, rsff_s.output("Q").getSignalValue());

		rsff_s.input("R").setSignalValue(Signals.ZERO);
		rsff_s.input("S").setSignalValue(Signals.ZERO);
		rsff_s.evaluate();
		rsff_s.unevaluate();
		assertEquals("", Signals.ZERO, rsff_s.output("Q").getSignalValue());

		// rsff_s.input("R").setSignalValue(Signals.ONE);
		// rsff_s.input("S").setSignalValue(Signals.ONE);
		// rsff_s.evaluate();
		// rsff_s.unevaluate();
		// assertEquals("", Signals.ONE, rsff_s.output("Q").getSignalValue());
	}

}
