/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.gui.protocol;

import java.util.List;

import javax.swing.ListSelectionModel;

import com.github.bfour.fpjgui.abstraction.valueChangeHandling.ValueChangeEvent;
import com.github.bfour.fpjgui.abstraction.valueChangeHandling.ValueChangeListener;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUIWindow;
import com.github.bfour.fpjgui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class ProtocolWindow extends FPJGUIWindow {

	private static final long serialVersionUID = 5147571920321921998L;
	private static ProtocolWindow instance;

	private ProtocolWindow(ServiceManager servMan) {

		super("Protocol", 861, 681);

		EntityTableBrowsePanel<ProtocolEntry> browsePanel = new EntityTableBrowsePanel<ProtocolEntry>(
				ProtocolEntry.class, servMan.getProtocolEntryService(), true);
		browsePanel.getListLikeContainer().addColumn(
				new FPJGUITableColumn<ProtocolEntry>("Time",
						new FPJGUITableFieldGetter<ProtocolEntry>() {
							@Override
							public String get(ProtocolEntry item) {
								return item.getCreationTime() + "";
							}
						}));
		browsePanel.getListLikeContainer().addColumn(
				new FPJGUITableColumn<ProtocolEntry>("Text",
						new FPJGUITableFieldGetter<ProtocolEntry>() {
							@Override
							public String get(ProtocolEntry item) {
								return item.getString();
							}
						}));
		PanelDecorator.decorateWithDropShadow(browsePanel);
		browsePanel.setCreateEntityEnabled(false);
		browsePanel.setDeleteEntityEnabled(false);
		browsePanel.setEditEntityEnabled(false);
		getContentPane().add(browsePanel, "grow");

		// stats
		browsePanel.getListLikeContainer().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		FPJGUILabel<String> statsLabel = new FPJGUILabel<>();
		browsePanel.getMainPanel().add(statsLabel, "cell 0 2");
		browsePanel.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueChangeEvent event) {
				String text = "";
				List<ProtocolEntry> selection = browsePanel.getListLikeContainer().getSelectedItems();
				text += selection.size() + " selected";
				statsLabel.setText(text);
			}
		});
		
	}

	public static ProtocolWindow getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new ProtocolWindow(servMan);
		return instance;
	}

}
