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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main frame for Schaltwerk displaying menu bar, status bar and circuit panels
 * etc.
 * 
 * @author Christian Wichmann
 * 
 */
public class SchaltwerkFrame extends JFrame {

	private static final long serialVersionUID = -9210487604950854484L;

	private static final Logger LOG = LoggerFactory
			.getLogger(SchaltwerkFrame.class);

	private JDesktopPane desktop;

	/**
	 * Initialize main frame of Schaltwerk.
	 */
	public SchaltwerkFrame() {

		setLookAndFeel();

		initialize();

		addListeners();

		addKeyBindings();
	}

	/**
	 * Sets look and feel for all swing components to "Nimbus".
	 */
	private void setLookAndFeel() {

		// set look and feel to new (since Java SE 6 Update 10 release standard
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e1) {

		}
	}

	private void initialize() {

		final int width = 800;
		final int height = 600;

		// setIconImage(new ImageIcon().getImage());
		setSize(width, height);
		setLocationRelativeTo(null);
		setName("Schaltwerk");
		setTitle("Schaltwerk");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setDefaultLookAndFeelDecorated(true);

		desktop = new JDesktopPane();
		// Make dragging a little faster but perhaps uglier.
		// desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		getContentPane().add(desktop);
		createInternalFrame();

		setJMenuBar(createMenuBar());

		// GridBagLayout gbl = new GridBagLayout();
		// GridBagConstraints gc = new GridBagConstraints();
		// getContentPane().setLayout(gbl);
		// gc.anchor = GridBagConstraints.CENTER;
		// gc.fill = GridBagConstraints.BOTH;
		// gc.gridx = 0;
		// gc.gridy = 0;
		// gc.gridheight = 1;
		// gc.gridwidth = 1;
		// gc.weightx = 0;
		// gc.weighty = 0;
		// getContentPane().add(desktop, gc);
		// gc.anchor = GridBagConstraints.SOUTHWEST;
		// gc.fill = GridBagConstraints.HORIZONTAL;
		// gc.gridx = 0;
		// gc.gridy = 1;
		// gc.gridheight = 1;
		// gc.gridwidth = 1;
		// gc.weightx = 0;
		// gc.weighty = 0;
		// getContentPane().add(getStatusBar(), gc);
		// pack();
	}

	/**
	 * Add listeners for handling window events.
	 */
	private void addListeners() {

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				handleQuit();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}

	/**
	 * Adds key bindings for main frame and all its children.
	 */
	private void addKeyBindings() {

	}

	/*
	 * ===== Methods providing ui components =====
	 */

	private JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();

		// Set up the lone menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// Set up the first menu item.
		JMenuItem menuItem = new JMenuItem("New");
		menuItem.setMnemonic(KeyEvent.VK_N);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.ALT_MASK));
		menuItem.setActionCommand("new");
		// TODO use named Actions to use them for menu and icon... (setAction())
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createInternalFrame();
			}
		});
		menu.add(menuItem);
		menu.addSeparator();

		// Set up the second menu item.
		menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				ActionEvent.ALT_MASK));
		menuItem.setActionCommand("quit");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleQuit();
			}
		});
		menu.add(menuItem);

		return menuBar;
	}

	private JToolBar getToolBar() {

		JToolBar toolBar = new JToolBar();

		JButton testButton = new JButton("Test");
		testButton.setSize(100, 100);
		testButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		toolBar.add(testButton);
		return toolBar;
	}

	private JToolBar getStatusBar() {

		JToolBar statusBar = new JToolBar();
		statusBar.add(new JMenuItem("Status: 42"));
		return statusBar;
	}

	/**
	 * Create a new internal frame.
	 */
	private void createInternalFrame() {

		CircuitPanel frame = new CircuitPanel("Beispielschaltung.circuit");
		frame.setVisible(true);
		desktop.add(frame);
		try {
			frame.setSelected(true);
			frame.setMaximum(true);
		} catch (PropertyVetoException e) {
			LOG.warn("Error encountered while selecting internal frame.");
		}
	}

	/*
	 * ===== Methods handling state of program =====
	 */

	/**
	 * Quit the application.
	 */
	private void handleQuit() {

		System.exit(0);
	}

	public static void main(String[] args) {

		new SchaltwerkFrame().setVisible(true);
	}
}
