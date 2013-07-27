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

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.java.schaltwerk.blocks.Block;
import de.ichmann.java.schaltwerk.blocks.BlockFactory;
import de.ichmann.java.schaltwerk.blocks.Input;
import de.ichmann.java.schaltwerk.blocks.Output;
import de.ichmann.java.schaltwerk.gui.BlockView.SignalShape;

/**
 * Main panel for Schaltwerk which paints a circuit into given area. For
 * painting all blocks their draw method is invoked with the graphics object
 * from this component.
 * 
 * Heavily inspired by John B. Matthews GraphPanel:
 * https://sites.google.com/site/drjohnbmatthews/graphpanel
 * 
 * @author Christian Wichmann
 */
public class CircuitPanel extends JInternalFrame {

	private static final long serialVersionUID = -2686655674103287860L;

	private static final Logger LOG = LoggerFactory
			.getLogger(CircuitPanel.class);

	private static int openFrameCount = 0;
	private static final int xOffset = 30, yOffset = 30;

	private static final int CIRCUIT_PANEL_WIDTH = 1000;
	private static final int CIRCUIT_PANEL_HEIGTH = 800;

	private boolean selecting = false;
	private boolean connecting = false;
	private Point mousePt = new Point(CIRCUIT_PANEL_WIDTH / 2,
			CIRCUIT_PANEL_HEIGTH / 2);
	private Rectangle mouseRect = new Rectangle();
	private SignalShape signalConnectionBegin = null;
	private SignalShape signalConnectionEnd = null;

	private final List<BlockView> blocksInCircuit = new ArrayList<BlockView>();
	private static final Map<Connection, Polygon> connectionLines = new HashMap<Connection, Polygon>();

	// TODO use map to store all lines as polygons(?) and calculate them only
	// when blocks are moved

	// TODO Implement buffer rendering when necessary?!
	// private final BufferedImage circuitImage;

	/**
	 * Containing a connection between two signals where one is a input and one
	 * is a output. To draw a connection the method <code>drawConnection</code>
	 * has to be called with the current graphics context n which to paint.
	 * 
	 * @author Christian Wichmann
	 */
	private class Connection {

		private SignalShape a;
		private SignalShape b;

		public Connection(SignalShape a, SignalShape b) {

			if ((a.getAttachedSignal() instanceof Output)
					&& (b.getAttachedSignal() instanceof Output)
					|| (a.getAttachedSignal() instanceof Input)
					&& (b.getAttachedSignal() instanceof Input)) {
				throw new IllegalArgumentException(
						"One signal has to be an input and the other an output.");
			}

			this.a = a;
			this.b = b;

			makeActualConnection();
		}

		/**
		 * Creates actual connection in the block objects for simulation.
		 */
		private void makeActualConnection() {

			final Input thisInput;
			final Output thisOutput;

			if ((a.getAttachedSignal() instanceof Output)) {
				thisInput = (Input) b.getAttachedSignal();
				thisOutput = (Output) a.getAttachedSignal();
			} else {
				thisInput = (Input) a.getAttachedSignal();
				thisOutput = (Output) b.getAttachedSignal();
			}

			thisOutput.connectTo(thisInput);
		}

		/**
		 * Paints this connection from signal shape to signal shape.
		 * 
		 * @param g
		 *            graphics context in which to paint
		 */
		protected void drawConnection(Graphics2D g) {

			g.setColor(ColorFactory.getInstance().getLineColor());

			final int x1 = a.pointForSignal().x;
			final int y1 = a.pointForSignal().y;
			final int x2 = b.pointForSignal().x;
			final int y2 = b.pointForSignal().y;
			final int ridge;

			if (a.x > b.x) {
				if (b.getAttachedSignal() instanceof Output) {
					// output of b to input of a
					ridge = calculateRidge(b, a);
					g.drawLine(x1, y1, x1 - ridge, y1);
					g.drawLine(x1 - ridge, y1, x1 - ridge, y2);
					g.drawLine(x1 - ridge, y2, x2, y2);

				} else {
					// input of b to output of a while a is further to the right
					// TODO handle lines that have to surround blocks!
				}

			} else {
				if (a.getAttachedSignal() instanceof Output) {
					// output of a to input of b
					ridge = calculateRidge(a, b);
					g.drawLine(x1, y1, x1 + ridge, y1);
					g.drawLine(x1 + ridge, y1, x1 + ridge, y2);
					g.drawLine(x1 + ridge, y2, x2, y2);

				} else {
					// input of a to output of b while b is further to the right
					// TODO handle lines that have to surround blocks!
				}
			}
		}

		/**
		 * Calculates x-coordinate for ridge in connection based on how many
		 * outputs there are and which one has to be connected.
		 * 
		 * @return x-coordinate for ridge
		 */
		private int calculateRidge(SignalShape left, SignalShape right) {

			final int ridge;

			int n = left.getAttachedSignal().getOwnerBlock().countOutputs();
			int m = 1;
			for (String s : left.getAttachedSignal().getOwnerBlock()
					.outputList()) {
				if (s.equals(left.getAttachedSignal().getSignalID())) {
					break;
				} else {
					m += 1;
				}
			}
			ridge = (int) (right.x - left.x) / (n + 1) * (n + 1 - m);

			return ridge;
		}
	}

	private List<Connection> connections = new ArrayList<Connection>();

	/**
	 * Initializes a panel to show a logical circuit with its name as title.
	 * 
	 * @param circuitName
	 *            name of circuit to show as title
	 */
	public CircuitPanel(final String circuitName) {

		super(circuitName, true, true, true, true);

		initialize();

		addListeners();

		addMouseListeners();

		// circuitImage = new BufferedImage(WIDTH, HEIGTH,
		// BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Initializes a new internal frame component to show a specific compound
	 * block in.
	 */
	private void initialize() {

		setSize(400, 400);
		setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
		setForeground(ColorFactory.getInstance().getForegroundColor());
		setBackground(ColorFactory.getInstance().getBackgroundColor());
		setOpaque(false);

		// create necessary stroke types for painting grid and blocks
		float[] dash = { 4, 2 };
		final BasicStroke gridStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 1, dash, 0);
		final BasicStroke defaultStroke = new BasicStroke(1);

		setContentPane(new JPanel() {

			private static final long serialVersionUID = -2286655611111287860L;

			@Override
			protected void paintComponent(final Graphics g) {

				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_SPEED);

				// draw background
				g2d.setColor(ColorFactory.getInstance().getBackgroundColor());
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.setColor(ColorFactory.getInstance().getGridColor());
				g2d.setStroke(gridStroke);
				for (int i = 0; i < CIRCUIT_PANEL_WIDTH; i += 50) {
					g2d.drawLine(i, 0, i, CIRCUIT_PANEL_HEIGTH);
				}
				for (int i = 0; i < CIRCUIT_PANEL_HEIGTH; i += 50) {
					g2d.drawLine(0, i, CIRCUIT_PANEL_WIDTH, i);
				}
				g2d.setColor(ColorFactory.getInstance().getForegroundColor());
				g2d.setStroke(defaultStroke);

				// draw all blocks
				for (BlockView b : blocksInCircuit) {
					b.drawBlock(g);
				}

				// draw selection rectangle
				if (selecting) {
					g2d.setColor(ColorFactory.getInstance().getShadowColor());
					g2d.drawRect(mouseRect.x, mouseRect.y, mouseRect.width,
							mouseRect.height);
				}

				// draw currently made connection
				if (connecting) {
					g2d.setColor(ColorFactory.getInstance().getHighlightColor());
					int x1 = signalConnectionBegin.pointForSignal().x;
					int y1 = signalConnectionBegin.pointForSignal().y;
					int x2 = mousePt.x;
					int y2 = mousePt.y;
					g2d.drawLine(x1, y1, x2, y2);
					// TODO better line alignment/layout (ConnectionPainter
					// class???)
				}

				// draw all connections that already exist
				for (Connection c : connections) {
					c.drawConnection(g2d);
				}
			}
		});

		addBlockToCircuit(BlockFactory.getInstance().getRSFlipFLop(false));
	}

	private void addListeners() {

		addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {
			}

			@Override
			public void internalFrameIconified(InternalFrameEvent e) {
			}

			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
			}

			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				LOG.debug("Circuit should be saved?");
			}

			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
			}

			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
			}
		});
	}

	/**
	 * Adds listeners for reacting on mouse events concerning this block view
	 * component.
	 */
	private void addMouseListeners() {

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(final MouseEvent e) {

				// check if connection has been made
				if (connecting) {

					if (signalConnectionBegin != null
							&& signalConnectionEnd != null) {
						try {
							connections
									.add(new Connection(signalConnectionBegin,
											signalConnectionEnd));
						} catch (IllegalArgumentException argException) {
							LOG.warn("Two not connectable signals where chosen.");
						}
					}
					connecting = false;
				}

				selecting = false;
				mouseRect.setBounds(0, 0, 0, 0);

				if (e.isPopupTrigger()) {
					// showPopup(e);
				}
				repaint();
			}

			@Override
			public void mousePressed(final MouseEvent e) {

				mousePt = e.getPoint();
				if (signalConnectionBegin != null) {
					connecting = true;
				} else if (e.isShiftDown()) {
					toggleSelection(mousePt);
				} else if (e.isPopupTrigger()) {
					selectOneBlock(mousePt);
					// showPopup(e);
				} else if (selectOneBlock(mousePt)) {
					selecting = false;
				} else {
					selectNoBlock();
					selecting = true;
				}
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionListener() {

			Point delta = new Point();

			@Override
			public void mouseMoved(final MouseEvent e) {

				signalConnectionBegin = checkIfSignalOnPoint(e.getPoint());

				if (signalConnectionBegin != null) {
					setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}

			@Override
			public void mouseDragged(final MouseEvent e) {

				if (selecting) {
					mouseRect.setBounds(Math.min(mousePt.x, e.getX()),
							Math.min(mousePt.y, e.getY()),
							Math.abs(mousePt.x - e.getX()),
							Math.abs(mousePt.y - e.getY()));
					selectBlockViews(mouseRect);

				} else if (connecting) {
					signalConnectionEnd = checkIfSignalOnPoint(e.getPoint());
					mousePt = e.getPoint();

				} else {
					delta.setLocation(e.getX() - mousePt.x, e.getY()
							- mousePt.y);
					moveBlockViews(delta);
					mousePt = e.getPoint();
				}
				repaint();
			}
		});
	}

	/*
	 * ===== Helper methods for mouse handling =====
	 */

	/**
	 * Checks if mouse is over a signal of one block.
	 * 
	 * @param mousePoint
	 *            point to check for signals
	 */
	private SignalShape checkIfSignalOnPoint(Point mousePoint) {

		SignalShape signal = null;
		for (BlockView block : blocksInCircuit) {
			signal = block.checkIfPointIsSignal(mousePoint);
			if (signal != null) {
				break;
			}
		}
		return signal;
	}

	private void selectBlockViews(Rectangle selectionRectangle) {

		for (BlockView b : blocksInCircuit) {
			b.setSelected(selectionRectangle.contains(b.getCenterPoint()));
		}
	}

	private void moveBlockViews(Point delta) {

		for (BlockView b : blocksInCircuit) {
			if (b.isSelected()) {
				b.moveBlockView(delta);
			}
		}
	}

	private void toggleSelection(Point point) {

		for (BlockView b : blocksInCircuit) {
			if (b.contains(point)) {
				b.setSelected(!b.isSelected());
			}
		}
	}

	private boolean selectOneBlock(Point point) {

		for (BlockView b : blocksInCircuit) {
			if (b.contains(point)) {
				if (!b.isSelected()) {
					selectNoBlock();
					b.setSelected(true);
				}
				return true;
			}
		}
		return false;
	}

	public void selectNoBlock() {

		for (BlockView b : blocksInCircuit) {
			b.setSelected(false);
		}
	}

	/*
	 * ===== Other methods =====
	 */

	/**
	 * Adds new block view component to this circuit and stores new block view
	 * in list.
	 * 
	 * @param newBlock
	 *            block to be added to circuit
	 * @see blocksInCircuit
	 */
	protected void addBlockToCircuit(Block newBlock) {

		BlockView bv = new BlockView(newBlock);
		bv.moveBlockView(new Point(150, 150));
		blocksInCircuit.add(bv);
	}
}
