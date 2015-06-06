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


import java.util.ArrayList;
import java.util.List;

import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjgui.abstraction.DefaultMultiListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class CrawlerBrowsePanel extends EntityBrowsePanel<Crawler> implements
		FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public CrawlerBrowsePanel(final ServiceManager servMan) {

		// show default buttons for CRUD options
		setDeleteEntityEnabled(false);
		setEditEntityEnabled(false);
		setCreateEntityEnabled(false);

		// ==== columns ====
		FPJGUITableColumn<Crawler> nameColumn = new FPJGUITableColumn<Crawler>(
				"Name", new FPJGUITableFieldGetter<Crawler>() {
					@Override
					public String get(Crawler item) {
						return servMan.getCrawlerService()
								.getIdentifierForCrawler(item);
					}
				}, true, 30, 30, "name", false);
		getTable().addColumn(nameColumn);

		this.table.setPreferredColumnWidth(nameColumn, 186);
		this.table.setMinimumColumnWidth(nameColumn, 86);

		// ==== loader ====
		this.loader = new EntityLoader<Crawler>() {
			@Override
			public List<Crawler> get() {
				return new ArrayList<>(servMan.getCrawlerService()
						.getAvailableCrawlers());
			}
		};

		// hook up table with change event system
		DefaultMultiListLikeChangeListener<Crawler> changeListener = new DefaultMultiListLikeChangeListener<>();
		changeListener
				.addTable(getTable(), new EntityFilterPipeline<Crawler>());
		ChangeHandler.getInstance(Crawler.class).addEventListener(
				changeListener);

		// load initial data
		load();

	}
}
