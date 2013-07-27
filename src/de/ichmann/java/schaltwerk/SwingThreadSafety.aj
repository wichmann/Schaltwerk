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

import javax.swing.SwingUtilities;
import javax.swing.JTextPane;
import javax.swing.JComponent;
import java.util.EventListener;

/**
 * Aspect securing that all swing methods are only called from the awt event
 * thread.
 * 
 * Source: http://www.ibm.com/developerworks/java/library/j-jtp08226/index.html
 */
public aspect SwingThreadSafety {

	pointcut swingMethods() : call(* javax.swing..*.*(..))
		        || call(javax.swing..*.new(..));

	pointcut extendsSwing() : target(javax.swing.JComponent+)
		        || call(* javax.swing..*Model+.*(..))
		        || call(* javax.swing.text.Document+.*(..));

	pointcut safeMethods() : call(void JComponent.revalidate())
		        || call(void JComponent.invalidate(..))
		        || call(void JComponent.repaint(..))
		        || call(void add*Listener(EventListener+))
		        || call(void remove*Listener(EventListener+))
		        || call(boolean SwingUtilities.isEventDispatchThread())
		        || call(void SwingUtilities.invokeLater(Runnable))
		        || call(void SwingUtilities.invokeAndWait(Runnable))
		        || call(void JTextPane.replaceSelection(..))
		        || call(void JTextPane.insertComponent(..))
		        || call(void JTextPane.insertIcon(..))
		        || call(void JTextPane.setLogicalStyle(..))
		        || call(void JTextPane.setCharacterAttributes(..))
		        || call(void JTextPane.setParagraphAttributes(..));

	pointcut edtMethods() : (swingMethods() || extendsSwing()) && !safeMethods();

	before() : edtMethods() {
		if (!SwingUtilities.isEventDispatchThread())
			throw new AssertionError(thisJoinPointStaticPart.getSignature()
					+ " called from " + Thread.currentThread().getName());
	}
}
