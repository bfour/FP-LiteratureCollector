package com.github.bfour.fpliteraturecollector.gui;

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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.events.BatchCreateEvent;
import com.github.bfour.fpjcommons.events.BatchDeleteEvent;
import com.github.bfour.fpjcommons.events.BatchUpdateEvent;
import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.events.ChangeListener;
import com.github.bfour.fpjcommons.events.CreateEvent;
import com.github.bfour.fpjcommons.events.DeleteEvent;
import com.github.bfour.fpjcommons.events.UpdateEvent;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.DefaultListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilter;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.components.FPJGUIAutocompleteComboBox;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.composite.EntityConfirmableOperationPanel;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.gui.tags.TagTilePanel;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class TaggingPanel extends
		EntityConfirmableOperationPanel<List<Literature>> {

	private static final long serialVersionUID = 9017251344583323559L;
	private ServiceManager servMan;
	private TagTilePanel tagsPanel;
	private FPJGUIAutocompleteComboBox<Tag> tagCombo;

	public TaggingPanel(ServiceManager servMan) {

		super("Set tags", "", "Set tags for selection",
				new Getter<List<Literature>, String>() {
					@Override
					public String get(List<Literature> input) {
						if (input.size() > 1)
							return "Set tags for the selected " + input.size()
									+ " instances of literature.";
						else
							return "Set tags for the selected literature.";
					}
				});

		this.servMan = servMan;

		this.tagsPanel = new TagTilePanel(true);

		// hook up tags panel with change event system
		DefaultListLikeChangeListener<Tag> changeListener = new DefaultListLikeChangeListener<Tag>(
				tagsPanel, new EntityFilterPipeline<Tag>(
						new EntityFilter<Tag>() {
							@Override
							public boolean include(Tag entity) {
								return tagsPanel.containsEntry(entity);
							}
						}));
		ChangeHandler.getInstance(Tag.class).addEventListener(changeListener);

		tagCombo = new FPJGUIAutocompleteComboBox<Tag>();
		updateTagsCombo(); // initial load
		// listen for changes and update combo
		ChangeHandler.getInstance(Tag.class).addEventListener(
				new ChangeListener<Tag>() {
					@Override
					public void handle(BatchCreateEvent<Tag> arg0) {
						for (Tag tag : arg0.getAffectedObjects())
							tagCombo.addItem(tag);
					}

					@Override
					public void handle(BatchDeleteEvent<Tag> arg0) {
						for (Tag tag : arg0.getAffectedObjects())
							tagCombo.removeItem(tag);
					}

					@Override
					public void handle(BatchUpdateEvent<Tag> arg0) {
						updateTagsCombo();
					}

					@Override
					public void handle(CreateEvent<Tag> arg0) {
						tagCombo.addItem(arg0.getCreatedObject());
					}

					@Override
					public void handle(DeleteEvent<Tag> arg0) {
						tagCombo.removeItem(arg0.getDeletedObject());
					}

					@Override
					public void handle(UpdateEvent<Tag> arg0) {
						updateTagsCombo();
					}
				});

		FPJGUIButton addTagButton = FPJGUIButtonFactory
				.createButton(ButtonFormats.DEFAULT);
		addTagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tag selectedTag = (Tag) tagCombo.getSelectedItem();
				if (selectedTag != null)
					tagsPanel.addEntry(selectedTag);
			}
		});
		addTagButton.setText("Add tag");

		// assemble
		getContentPanel()
				.setLayout(new MigLayout("insets 0", "[grow]", "[][]"));
		getContentPanel().add(tagsPanel, "cell 0 0, w 6cm::, h 1cm::, grow");
		getContentPanel().add(tagCombo, "cell 0 1, growx");
		getContentPanel().add(addTagButton, "cell 0 1, growy");

	}

	public void setTags(List<Tag> tags) {
		tagsPanel.setEntries(tags);
	}

	public List<Tag> getTags() {
		return tagsPanel.getEntries();
	}

	private void updateTagsCombo() {
		// TODO update combo on change to tags
		try {
			tagCombo.removeAllItems();
			Tag selection = (Tag) tagCombo.getSelectedItem();
			for (Tag tag : servMan.getTagService().getAll())
				tagCombo.addItem(tag);
			tagCombo.setSelectedItem(selection);
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
