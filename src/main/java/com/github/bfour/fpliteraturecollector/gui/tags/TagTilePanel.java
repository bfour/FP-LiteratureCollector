package com.github.bfour.fpliteraturecollector.gui.tags;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.tilePanel.FPJGUITilePanel;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;

public class TagTilePanel extends FPJGUITilePanel<Tag> {

	// public static interface DeleteActionListener<T> {
	// public void handle(DeleteEvent<T> event);
	// }

	private static final long serialVersionUID = 5172009020895888316L;

	// private List<DeleteActionListener<Tag>> deleteListeners;

	public TagTilePanel(final boolean enableDelete) {

		super(null);
		setPanelGetter(new Getter<Tag, JXPanel>() {
			@Override
			public JXPanel get(final Tag input) {
				JXPanel panel = new JXPanel(new MigLayout("insets 4", "[]",
						"[]"));
				panel.setOpaque(true);
				panel.setBackground(input.getColour());
				// adjust text colour
				JLabel nameLabel = new JLabel(input.getName());
				int colourSum = input.getColour().getRed()
						+ input.getColour().getGreen()
						+ input.getColour().getBlue();
				if (colourSum <= 382)
					nameLabel.setForeground(Color.WHITE);
				panel.add(nameLabel);
				if (enableDelete) {
					FPJGUIButton removeButton = FPJGUIButtonFactory
							.createButton(ButtonFormats.NAKED);
					removeButton.setIcon(Icons.CROSS_12.getIcon());
					removeButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							deleteEntry(input);
						}
					});
					panel.add(removeButton);
				}
				return panel;
			}
		});

		// this.deleteListeners = new LinkedList<>();

	}

	// private void handleDelete(Tag tag) {
	// DeleteEvent<Tag> event = new DeleteEvent<Tag>(tag);
	// for (DeleteActionListener<Tag> listener: deleteListeners)
	// listener.handle(event);
	// }
	//
	// public void addDeleteActionListener(DeleteActionListener<Tag> listener) {
	// deleteListeners.add(listener);
	// }
	//
	// public void removeDeleteActionListener(DeleteActionListener<Tag>
	// listener) {
	// deleteListeners.remove(listener);
	// }

}
