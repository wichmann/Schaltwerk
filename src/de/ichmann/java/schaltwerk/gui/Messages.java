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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides localized messages from a resource bundle for the current locale.
 * 
 * @author Christian Wichmann
 */
public final class Messages {

	private static final String BUNDLE_NAME = "resources.i18n.Schaltwerk";

	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(BUNDLE_NAME);

	/**
	 * Get an i18n message for a given key depending on default locale.
	 * 
	 * @param key
	 *            given key for which to get string
	 * @return string with message
	 */
	public static String getString(final String key) {

		try {
			return resourceBundle.getString(key);

		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
