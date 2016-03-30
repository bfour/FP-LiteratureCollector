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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.FPJGUIPopover;
import com.github.bfour.fpjgui.components.composite.EntityCheckboxTreeBrowsePanel;
import com.github.bfour.fpjgui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.design.Lengths;
import com.github.bfour.fpjgui.events.SelectionChangeEvent;
import com.github.bfour.fpjgui.events.SelectionChangeSubscriber;
import com.github.bfour.fpjguiextended.tagging.TaggingPanel;
import com.github.bfour.fpjsearch.SearchEvent;
import com.github.bfour.fpjsearch.SearchException;
import com.github.bfour.fpjsearch.fpjsearch.ContainsExpression;
import com.github.bfour.fpjsearch.fpjsearch.OrExpression;
import com.github.bfour.fpjsearch.fpjsearch.SearchSpecification;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteratureBrowsePanel extends EntityTableBrowsePanel<Literature> {

	private static final long serialVersionUID = 4500980555674670335L;

	public LiteratureBrowsePanel(final ServiceManager servMan) {
		this(servMan, new EntityFilterPipeline<Literature>());
	}

	public LiteratureBrowsePanel(final ServiceManager servMan,
			EntityFilterPipeline<Literature> filters) {

		super(Literature.class, servMan.getLiteratureService(), false);

		setFilters(filters);

		final TaggingPanel<Tag, Literature> taggingPanel = new TaggingPanel<>(
				Tag.class, servMan.getTagService());
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
						feedbackBroadcasted(new Feedback(
								LiteratureBrowsePanel.this,
								"Sorry, failed to set tags for " + selectedLit,
								e1.getMessage(), FeedbackType.ERROR));
					}
				}
				feedbackBroadcasted(new Feedback(LiteratureBrowsePanel.this,
						"Tags for " + successCounter
								+ " literature entries set.",
						FeedbackType.SUCCESS));
				tagPopover.hidePopup();
			}
		});

		// show default buttons for CRUD options
		setEditEntityEnabled(false);
		setCreateEntityEnabled(true);
		setDeleteEntityEnabled(true);

		// extra buttons
		FPJGUIButton exportButton = FPJGUIButtonFactory
				.createButton(
						ButtonFormats.DEFAULT,
						Lengths.LARGE_BUTTON_HEIGHT.getLength(),
						"Export to MODS",
						com.github.bfour.fpliteraturecollector.gui.design.Icons.EXPORT_20
								.getIcon());
		getMainPanel().add(exportButton, "cell 0 2");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Literature> selectedLiterature = getValue();
				Feedback statusFeedback = new Feedback(
						LiteratureBrowsePanel.this, "Exporting "
								+ selectedLiterature.size()
								+ " literature entries to MODS.", "",
						FeedbackType.PROGRESS.getColor(), FeedbackType.PROGRESS
								.getIcon(), FeedbackType.PROGRESS, true);
				feedbackBroadcasted(statusFeedback);
				try {
					JFileChooser fileChooser = new JFileChooser();
					if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						servMan.getReportService().exportToMODSFile(
								selectedLiterature, file);
						feedbackBroadcasted(new Feedback(
								LiteratureBrowsePanel.this,
								"Export to MODS finished for "
										+ selectedLiterature.size()
										+ " literature entries.",
								FeedbackType.SUCCESS));
					} else {
						feedbackBroadcasted(new Feedback(
								LiteratureBrowsePanel.this,
								"Export to MODS cancelled.", FeedbackType.WARN));
					}
				} catch (FileNotFoundException e1) {
					feedbackBroadcasted(new Feedback(
							LiteratureBrowsePanel.this,
							"Sorry, export to MODS failed, because export file not found.",
							e1.getMessage(), FeedbackType.ERROR));
				}
				feedbackRevoked(statusFeedback);
			}
		});

		FPJGUIButton downloadFullTextButton = FPJGUIButtonFactory
				.createButton(
						ButtonFormats.DEFAULT,
						Lengths.LARGE_BUTTON_HEIGHT.getLength(),
						"Download Fulltext",
						com.github.bfour.fpliteraturecollector.gui.design.Icons.DOWNLOAD_20
								.getIcon());
		getMainPanel().add(downloadFullTextButton, "cell 0 2");
		downloadFullTextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Literature> selectedLiterature = getValue();
				Feedback statusFeedback = new Feedback(
						LiteratureBrowsePanel.this, "Downloading fulltext for "
								+ selectedLiterature.size()
								+ " literature entries.", FeedbackType.PROGRESS);
				feedbackBroadcasted(statusFeedback);
				for (Literature lit : selectedLiterature) {
					try {
						servMan.getLiteratureService().downloadFullTexts(lit);
					} catch (ServiceException e1) {
						feedbackBroadcasted(new Feedback(
								LiteratureBrowsePanel.this,
								"Sorry, failed to download fulltext for literature ID "
										+ lit.getID(), e1.getMessage(),
								FeedbackType.ERROR));
					}
				}
				feedbackRevoked(statusFeedback);
				feedbackBroadcasted(new Feedback(LiteratureBrowsePanel.this,
						"Fulltext download finished for "
								+ selectedLiterature.size()
								+ " literature entries.", FeedbackType.SUCCESS));
			}
		});

		final FPJGUIButton tagButton = FPJGUIButtonFactory.createButton(
				ButtonFormats.DEFAULT, Lengths.LARGE_BUTTON_HEIGHT.getLength(),
				"Tag",
				com.github.bfour.fpliteraturecollector.gui.design.Icons.TAG_16
						.getIcon());
		getMainPanel().add(tagButton, "cell 0 2");
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
		getListLikeContainer().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// additional search controls
		EntityCheckboxTreeBrowsePanel<Tag> categoryTree = new EntityCheckboxTreeBrowsePanel<>(
				Tag.class, new Getter<Tag, Tag>() {
					@Override
					public Tag get(Tag input) {
						return null;
					}
				}, servMan.getTagService(), true);
		categoryTree.setEditEntityEnabled(false);
		categoryTree.setCreateEntityEnabled(false);
		categoryTree.setDeleteEntityEnabled(false);
		getSidebarPanel().add(categoryTree, "grow, w 2cm:4cm:");
		setSidebarVisible(true);

		categoryTree.getListLikeContainer().addTreeCheckingListener(
				new SelectionChangeSubscriber<Tag>() {
					@Override
					public void receive(SelectionChangeEvent<Tag> ev) {
						OrExpression orExpr = new OrExpression();
						for (Tag tag : ev.getEntity()) {
							orExpr.addExpression(new ContainsExpression("tags",
									tag));
						}
						try {
							getSearchHandler().searchPerformed(
									new SearchEvent<SearchSpecification>(this,
											new SearchSpecification(null,
													orExpr)));
						} catch (SearchException e) {
							e.printStackTrace();
							feedbackBroadcasted(new Feedback(
									categoryTree,
									"Sorry, something went wrong with your search.",
									e.getMessage(), FeedbackType.ERROR));
						}
					}
				});

		// load
		load();

		// ==== columns ====
		FPJGUITableColumn<Literature> titleColumn = new FPJGUITableColumn<Literature>(
				"Title", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						return item.getTitle();
					}
				}, true, 30, 30, "title", false);
		getListLikeContainer().addColumn(titleColumn);

		FPJGUITableColumn<Literature> authorsColumn = new FPJGUITableColumn<Literature>(
				"Authors", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						Set<Author> authors = item.getAuthors();
						if (authors == null || authors.isEmpty())
							return "";
						StringBuilder builder = new StringBuilder();
						for (Author author : authors) {
							builder.append(author.getLastName());
							builder.append(", ");
						}
						return builder.substring(0, builder.length() - 2);
					}
				}, true, 30, 30, "authors", false);
		getListLikeContainer().addColumn(authorsColumn);

		FPJGUITableColumn<Literature> tagsColumn = new FPJGUITableColumn<Literature>(
				"Tags", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						Set<Tag> tags = item.getTags();
						if (tags == null || tags.isEmpty())
							return "";
						StringBuilder builder = new StringBuilder();
						for (Tag tag : tags) {
							builder.append(tag.getName());
							builder.append(", ");
						}
						return builder.substring(0, builder.length() - 2);
					}
				}, true, 30, 30, "tags", false);
		getListLikeContainer().addColumn(tagsColumn);

		FPJGUITableColumn<Literature> yearColumn = new FPJGUITableColumn<Literature>(
				"Year", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						if (item.getYear() == null)
							return "";
						return item.getYear() + "";
					}
				}, true, 30, 30, "year", false);
		getListLikeContainer().addColumn(yearColumn);

		getListLikeContainer().setPreferredColumnWidth(titleColumn, 200);
		getListLikeContainer().setPreferredColumnWidth(authorsColumn, 40);
		getListLikeContainer().setPreferredColumnWidth(tagsColumn, 30);
		getListLikeContainer().setPreferredColumnWidth(yearColumn, 30);

		getListLikeContainer().setMinimumColumnWidth(titleColumn, 100);
		getListLikeContainer().setMinimumColumnWidth(authorsColumn, 40);
		getListLikeContainer().setMinimumColumnWidth(tagsColumn, 30);
		getListLikeContainer().setMinimumColumnWidth(yearColumn, 30);

		load();

	}
}
