package com.github.bfour.fpliteraturecollector.gui.literature;

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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;

import org.apache.commons.beanutils.BeanUtils;

import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.DefaultListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.FPJGUIPopover;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.design.Lengths;
import com.github.bfour.fpjgui.util.DefaultActionInterfacingHandler;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.gui.TaggingPanel;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteratureBrowsePanel extends EntityBrowsePanel<Literature>
		implements FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public LiteratureBrowsePanel(final ServiceManager servMan) {

		final TaggingPanel taggingPanel = new TaggingPanel(servMan);
		final FPJGUIPopover tagPopover = new FPJGUIPopover(taggingPanel);

		taggingPanel.addCancelListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tagPopover.hidePopup();
			}
		});
		taggingPanel.addConfirmListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Tag> tags = taggingPanel.getTags();
				LiteratureService litServ = servMan.getLiteratureService();
				int successCounter = 0;
				for (Literature selectedLit : getValue()) {
					Literature newLiterature = new LiteratureBuilder(
							selectedLit).setTags(new HashSet<Tag>(tags))
							.getObject();
					try {
						litServ.update(selectedLit, newLiterature);
						successCounter++;
					} catch (ServiceException e1) {
						fireFeedback(new Feedback(LiteratureBrowsePanel.this,
								"Sorry, failed to set tags for " + selectedLit,
								e1.getMessage(), FeedbackType.ERROR));
					}
				}
				fireFeedback(new Feedback(LiteratureBrowsePanel.this,
						"Tags for " + successCounter
								+ " literature entries set.",
						FeedbackType.SUCCESS));
				tagPopover.hidePopup();
			}
		});

		// show default buttons for CRUD options
		setDeleteEntityEnabled(true);
		setEditEntityEnabled(false);
		setCreateEntityEnabled(true);

		// extra buttons
		final FPJGUIButton tagButton = FPJGUIButtonFactory.createButton(
				ButtonFormats.DEFAULT, Lengths.LARGE_BUTTON_HEIGHT.getLength(),
				"Tag",
				com.github.bfour.fpliteraturecollector.gui.design.Icons.TAG_16
						.getIcon());
		mainPanel.add(tagButton, "cell 0 2");
		tagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Literature> selectedLiterature = getValue();
				taggingPanel.setValue(selectedLiterature);
				Set<Tag> allTags = new HashSet<Tag>();
				for (Literature lit : selectedLiterature) {
					Set<Tag> litTags = lit.getTags();
					if (litTags != null)
						allTags.addAll(litTags);
				}
				taggingPanel.setTags(new ArrayList<>(allTags));
				tagPopover.pack();
				tagPopover.showPopup(tagButton);
			}
		});

		// selection mode
		getTable().getTable().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// ==== columns ====
		FPJGUITableColumn<Literature> titleColumn = new FPJGUITableColumn<Literature>(
				"Title", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						return item.getTitle();
					}
				}, true, 30, 30, "title", false);
		getTable().addColumn(titleColumn);

		FPJGUITableColumn<Literature> authorsColumn = new FPJGUITableColumn<Literature>(
				"Authors", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						Set<Author> authors = item.getAuthors();
						if (authors == null)
							return "";
						StringBuilder builder = new StringBuilder();
						for (Author author : authors) {
							builder.append(author.getLastName());
							builder.append(", ");
						}
						return builder.substring(0, builder.length() - 2);
					}
				}, true, 30, 30, "authors", false);
		getTable().addColumn(authorsColumn);

		this.table.setPreferredColumnWidth(titleColumn, 200);
		this.table.setPreferredColumnWidth(authorsColumn, 40);

		this.table.setMinimumColumnWidth(titleColumn, 100);
		this.table.setMinimumColumnWidth(authorsColumn, 40);

		// ==== loader ====
		this.loader = new EntityLoader<Literature>() {
			@Override
			public List<Literature> get() {
				List<Literature> list = new ArrayList<>();
				try {
					list = servMan.getLiteratureService().getAll();
				} catch (ServiceException e) {
					feedbackProxy
							.feedbackBroadcasted(new Feedback(
									LiteratureBrowsePanel.this,
									"Sorry, failed to get literature from LiteratureService.",
									FeedbackType.ERROR));
				}
				return list;
			}
		};

		// hook up table with change event system
		DefaultListLikeChangeListener<Literature> changeListener = new DefaultListLikeChangeListener<Literature>(
				getTable(), new EntityFilterPipeline<Literature>());
		ChangeHandler.getInstance(Literature.class).addEventListener(
				changeListener);

		// load initial data
		load();

		// set button actions
		ActionListener deleteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LiteratureService custServ = servMan.getLiteratureService();
				Component source;
				// TODO (high) clean up mess (maybe set back to delete button as
				// source only)
				if (e.getSource() != null && e.getSource() instanceof Component) {
					source = (Component) e.getSource();
					if (source instanceof JMenuItem) {
						try {
							source = (Component) BeanUtils.cloneBean(source);
						} catch (IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException e1) {
							source = deleteButton;
						}
					}
				} else {
					source = deleteButton;
				}
				DefaultActionInterfacingHandler.getInstance()
						.requestDeleteFromList(source, feedbackProxy,
								table.getSelectedItem(), custServ);
			}
		};
		addDeleteAction(deleteListener);

	}
}
