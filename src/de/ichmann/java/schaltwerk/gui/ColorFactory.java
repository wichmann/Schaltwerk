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

import java.awt.Color;

/**
 * Provides common color objects throughout the entire application for
 * consistent look.
 * 
 * @author Christian Wichmann
 */
public final class ColorFactory {

	private static ColorFactory colorFactoryInstance = null;

	private static final Color backgroundColor = new Color(222, 222, 222);
	private static final Color foregroundColor = Color.BLACK;
	private static final Color lineColor = Color.BLACK;
	private static final Color shadowColor = Color.LIGHT_GRAY;
	private static final Color highlightColor = Color.RED;
	private static final Color gridColor = Color.LIGHT_GRAY;

	private ColorFactory() {

	}

	/**
	 * Returns a single instance of this factory to provide common colors.
	 * 
	 * @return instance of this factory
	 */
	public static ColorFactory getInstance() {

		if (colorFactoryInstance == null) {

			colorFactoryInstance = new ColorFactory();
		}
		return colorFactoryInstance;
	}

	/**
	 * Returns background color for entire application. This color is used in
	 * all panels and other components to paint their background.
	 * 
	 * @return background color
	 */
	public Color getBackgroundColor() {

		return backgroundColor;
	}

	/**
	 * Returns foreground color for entire application. This color is used in
	 * all panels and other components to paint their content.
	 * 
	 * @return foreground color
	 */
	public Color getForegroundColor() {

		return foregroundColor;
	}

	/**
	 * Returns shadow color for the entire application.
	 * 
	 * @return shadow color
	 */
	public Color getShadowColor() {

		return shadowColor;
	}

	/**
	 * Returns line color for the entire application.
	 * 
	 * @return line color
	 */
	public Color getLineColor() {

		return lineColor;
	}

	/**
	 * Returns highlight color for the entire application.
	 * 
	 * @return highlight color
	 */
	public Color getHighlightColor() {

		return highlightColor;
	}

	/**
	 * Returns grid color for the entire application.
	 * 
	 * @return grid color
	 */
	public Color getGridColor() {

		return gridColor;
	}
}
