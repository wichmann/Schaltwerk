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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ichmann.java.schaltwerk.blocks.Block;
import de.ichmann.java.schaltwerk.blocks.CompoundBlock;
import de.ichmann.java.schaltwerk.blocks.Input;
import de.ichmann.java.schaltwerk.blocks.Output;

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

	private final CompoundBlock currentCircuit;
	private final List<BaseView> blocksInCircuit = new ArrayList<BaseView>();

	// private static final Map<Connection, Polygon> connectionLines = new
	// HashMap<Connection, Polygon>();

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

		private static final int X_GAP = 25;
		private static final int Y_GAP = 75;

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

			if (a.x > b.x) {
				if (b.getAttachedSignal() instanceof Output) {
					drawDirectConnection(g, b, a);
				} else {
					drawSurroundingConnection(g, a, b);
				}

			} else {
				if (a.getAttachedSignal() instanceof Output) {
					drawDirectConnection(g, a, b);
				} else {
					drawSurroundingConnection(g, b, a);
				}
			}
		}

		/**
		 * Draws a connection directly between the output of the left block to
		 * the input of the right block.
		 * 
		 * <p>
		 * <blockquote>
		 * 
		 * <pre>
		 *    ------
		 *    |    |         shape2
		 *    |    |----|    ------
		 *    |    |    |    |    |
		 *    ------    |----|    |
		 *    shape1         |    |
		 *                   ------
		 * </pre>
		 * 
		 * </blockquote>
		 * 
		 * @param g
		 *            graphics context in which to paint
		 * @param shape1
		 *            first shape
		 * @param shape2
		 *            second shape
		 */
		private void drawDirectConnection(Graphics2D g, SignalShape shape1,
				SignalShape shape2) {

			final int x1 = shape1.pointForSignal().x;
			final int y1 = shape1.pointForSignal().y;
			final int x2 = shape2.pointForSignal().x;
			final int y2 = shape2.pointForSignal().y;
			final int ridge;

			if (shape1.x > shape2.x) {
				ridge = calculateRidge(shape2, shape1);
				g.drawLine(x1, y1, x1 - ridge, y1);
				g.drawLine(x1 - ridge, y1, x1 - ridge, y2);
				g.drawLine(x1 - ridge, y2, x2, y2);
			} else {
				ridge = calculateRidge(shape1, shape2);
				g.drawLine(x1, y1, x1 + ridge, y1);
				g.drawLine(x1 + ridge, y1, x1 + ridge, y2);
				g.drawLine(x1 + ridge, y2, x2, y2);
			}
		}

		/**
		 * Draws a connection that has to be routed around another block from
		 * the output of the right block to the input of the left block.
		 * 
		 * <p>
		 * <blockquote>
		 * 
		 * <pre>
		 *  ----------------------------
		 *  |   ------                 |
		 *  ----|    |       shape1    |
		 *      |    |       ------    |
		 *      |    |       |    |    |
		 *      ------       |    |-----
		 *      shape2       |    |
		 *                   ------
		 * </pre>
		 * 
		 * </blockquote>
		 * 
		 * @param g
		 *            graphics context in which to paint
		 * @param shape1
		 *            first shape
		 * @param shape2
		 *            second shape
		 */
		private void drawSurroundingConnection(Graphics2D g,
				SignalShape shape1, SignalShape shape2) {

			final int x1 = shape1.pointForSignal().x;
			final int y1 = shape1.pointForSignal().y;
			final int x2 = shape2.pointForSignal().x;
			final int y2 = shape2.pointForSignal().y;

			// calculate the index of shape2's input and adjust x gap
			// accordingly
			int m = 1;
			for (String s : shape2.getAttachedSignal().getOwnerBlock()
					.inputList()) {
				if (s.equals(shape2.getAttachedSignal().getSignalID())) {
					break;
				} else {
					m += 1;
				}
			}
			final int xGap = m * X_GAP;

			if (shape1.y > shape2.y) {
				// shape 1 is lower than shape 2
				final int yMax = (int) (shape2.y - Y_GAP);
				g.drawLine(x1, y1, x1 + xGap, y1);
				g.drawLine(x1 + xGap, y1, x1 + xGap, yMax);
				g.drawLine(x1 + xGap, yMax, x2 - xGap, yMax);
				g.drawLine(x2 - xGap, yMax, x2 - xGap, y2);
				g.drawLine(x2 - xGap, y2, x2, y2);
			} else {
				// shape 1 is higher than shape 2
				final int yMin = (int) (shape2.y + Y_GAP);
				g.drawLine(x1, y1, x1 + xGap, y1);
				g.drawLine(x1 + xGap, y1, x1 + xGap, yMin);
				g.drawLine(x1 + xGap, yMin, x2 - xGap, yMin);
				g.drawLine(x2 - xGap, yMin, x2 - xGap, y2);
				g.drawLine(x2 - xGap, y2, x2, y2);
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

		currentCircuit = new CompoundBlock(circuitName, 0, 0);

		initialize();

		addListeners();

		addMouseListeners();

		// circuitImage = new BufferedImage(WIDTH, HEIGTH,
		// BufferedImage.TYPE_INT_ARGB);
	}

	public CircuitPanel(final String circuitName, final CompoundBlock circuit) {

		currentCircuit = circuit;

		// TODO Import compound block and paint it on screen
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

				drawBackground(g2d);

				// draw all blocks
				for (BaseView b : blocksInCircuit) {
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
				}

				// draw all connections that already exist
				for (Connection c : connections) {
					g2d.setColor(ColorFactory.getInstance().getLineColor());
					c.drawConnection(g2d);
				}
			}

			/**
			 * Draws background grid on panel.
			 * 
			 * @param g2d
			 *            graphics context to paint into
			 */
			private void drawBackground(final Graphics2D g2d) {

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
			}
		});
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
					mousePt = e.getPoint();
					signalConnectionEnd = checkIfSignalOnPoint(e.getPoint());
					repaint();

				} else {
					delta.setLocation(e.getX() - mousePt.x, e.getY()
							- mousePt.y);
					moveComponentsOnPanel(delta);
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
		for (BaseView block : blocksInCircuit) {
			signal = block.checkIfPointIsSignal(mousePoint);
			if (signal != null) {
				return signal;
			}
		}
		return signal;
	}

	private void selectBlockViews(Rectangle selectionRectangle) {

		for (BaseView b : blocksInCircuit) {
			b.setSelected(selectionRectangle.contains(b.getCenterPoint()));
		}
	}

	private void moveComponentsOnPanel(Point delta) {

		for (BaseView b : blocksInCircuit) {
			if (b.isSelected()) {
				b.moveBlockView(delta);
			}
		}
	}

	private void toggleSelection(Point point) {

		for (BaseView b : blocksInCircuit) {
			if (b.contains(point)) {
				b.setSelected(!b.isSelected());
			}
		}
	}

	private boolean selectOneBlock(Point point) {

		for (BaseView b : blocksInCircuit) {
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

		for (BaseView b : blocksInCircuit) {
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

	/**
	 * Adds new input view component to this circuit and stores it in list.
	 * 
	 * @see inputsInCircuit
	 */
	protected void addInputToCircuit() {

		Input newInput = new Input(currentCircuit, "xyz");
		InputSignalView i = new InputSignalView(newInput);
		i.moveBlockView(new Point(150, 150));
		blocksInCircuit.add(i);
	}

	/**
	 * Adds new block view component to this circuit and stores it in list.
	 * 
	 * @see outputsInCircuit
	 */
	protected void addOutputToCircuit() {

		Output newOutput = new Output(currentCircuit, "abc");
		OutputSignalView o = new OutputSignalView(newOutput);
		o.moveBlockView(new Point(150, 150));
		blocksInCircuit.add(o);
	}
}
