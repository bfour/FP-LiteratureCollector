package com.github.bfour.fpliteraturecollector.gui.tags;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

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

				JLabel nameLabel = new JLabel(input.getName());
				nameLabel.setFont(nameLabel.getFont().deriveFont(11f));
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

	@Override
	public void addEntry(Tag entry) {
		if (!containsEntry(entry))
			super.addEntry(entry);
	}

	@Override
	public void addEntries(Collection<? extends Tag> entries) {
		for (Tag entry : entries)
			addEntry(entry);
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
