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
package de.ichmann.java.schaltwerk.gui;

import java.awt.Font;

/**
 * Factory class for vending standard <code>Font</code> objects for consistent
 * font styles around the application. Wherever possible, this factory will hand
 * out references to shared <code>Font</code> instances.
 * <p>
 * Create methods return a reference to a shared <code>Font</code> object which
 * is ok because Font is immutable. The given object can then be adjusted by the
 * <code>deriveFont()</code> method.
 * 
 * @author Christian Wichmann
 */
public final class FontFactory {

	private static FontFactory fontFactory = null;

	private static String baseFont = "Ubuntu";
	private static int textSize = 12;
	private static String textFont = baseFont;
	private static int textStyle = Font.PLAIN;

	private static Font sharedTextFont;
	private static Font sharedDefaultFont;

	private FontFactory() {

		sharedTextFont = new Font(textFont, textStyle, textSize);
		sharedDefaultFont = new Font(textFont, textStyle, textSize);
	}

	public static FontFactory getInstance() {

		if (fontFactory == null) {
			fontFactory = new FontFactory();
		}
		return fontFactory;
	}

	/**
	 * Creates a font for displaying textual information, e.g. in dialog boxes
	 * etc.
	 * 
	 * @return text font
	 */
	public Font createTextFont() {

		return sharedTextFont;
	}

	/**
	 * Creates a default font for all other purposes.
	 * 
	 * @return default font
	 */
	public Font createDefaultFont() {

		return sharedDefaultFont;
	}
}
