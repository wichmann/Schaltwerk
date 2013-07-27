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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.ichmann.java.schaltwerk.blocks.AND;
import de.ichmann.java.schaltwerk.blocks.BlockFactory;
import de.ichmann.java.schaltwerk.blocks.Blocks;
import de.ichmann.java.schaltwerk.blocks.NAND;
import de.ichmann.java.schaltwerk.blocks.NOR;
import de.ichmann.java.schaltwerk.blocks.NOT;
import de.ichmann.java.schaltwerk.blocks.OR;

/**
 * Dialog to add new blocks to circuit. to algorithm maked
 * 
 * @author Christian Wichmann
 */
public class AddBlockDialog extends JDialog {

	private static final long serialVersionUID = -9112791773327630432L;

	private JList<Blocks> blockList;
	private Blocks chosenBlock = null;
	private PreviewPanel previewPanel;

	private class PreviewPanel extends JPanel {

		private static final long serialVersionUID = -6592781135849194612L;

		private BlockView blockView = null;

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(200, 200);
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (blockView != null) {
				g.setColor(ColorFactory.getInstance().getGridColor());
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(ColorFactory.getInstance().getForegroundColor());
				blockView.moveBlockView(new Point(50, 25));
				blockView.drawBlock(g);
			}
		}

		public void setBlock(Blocks block) {

			switch (block) {
			case AND:
				blockView = new BlockView(new AND(3));
				break;
			case NAND:
				blockView = new BlockView(new NAND(3));
				break;
			case NOR:
				blockView = new BlockView(new NOR(3));
				break;
			case NOT:
				blockView = new BlockView(new NOT());
				break;
			case OR:
				blockView = new BlockView(new OR(3));
				break;
			case RS_FLIPFLOP:
				blockView = new BlockView(BlockFactory.getInstance()
						.getRSFlipFLop(false));
				break;
			default:
				assert false : block;
				break;
			}
			repaint();
		}
	}

	public AddBlockDialog() {

		initialize();

		addKeyBindings();
	}

	private void initialize() {

		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Add new block...");

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		getContentPane().setLayout(layout);

		final int insets = 10;
		gc.insets = new Insets(insets, insets, insets, insets);

		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.CENTER;
		gc.fill = GridBagConstraints.BOTH;
		getContentPane().add(buildBlockList(), gc);

		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridheight = 1;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.fill = GridBagConstraints.HORIZONTAL;
		getContentPane().add(buildButtonPane(), gc);

		gc.gridx = 1;
		gc.gridy = 0;
		gc.gridheight = 2;
		gc.gridwidth = 1;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.anchor = GridBagConstraints.EAST;
		gc.fill = GridBagConstraints.VERTICAL;
		previewPanel = new PreviewPanel();
		getContentPane().add(previewPanel, gc);

		pack();
		setLocationRelativeTo(null);
	}

	/**
	 * Adds key bindings for this dialog to exit it.
	 */
	private void addKeyBindings() {

		JComponent rootPane = this.getRootPane();

		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("ESCAPE"), "QuitAddBlockDialog");
		rootPane.getActionMap().put("QuitAddBlockDialog", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
	}

	private JComponent buildBlockList() {

		if (blockList == null) {
			DefaultListModel<Blocks> listModel;
			listModel = new DefaultListModel<Blocks>();
			for (Blocks b : Blocks.values()) {
				listModel.addElement(b);
			}
			blockList = new JList<Blocks>(listModel);
			blockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			blockList.setSelectedIndex(0);
			blockList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					previewPanel.setBlock(blockList.getSelectedValue());
				}
			});
			// blockList.setVisibleRowCount(5);
		}
		return blockList;
	}

	/**
	 * Returns a panel containing all buttons on the lower margin of this
	 * dialog.
	 * 
	 * @return panel with all buttons
	 */
	private JPanel buildButtonPane() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		buttonPanel.add(cancelButton);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				chosenBlock = blockList.getSelectedValue();
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);

		return buttonPanel;
	}

	public Blocks getChosenBlock() {

		return chosenBlock;
	}
}
