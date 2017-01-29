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

package com.github.bfour.fpliteraturecollector.gui;

import java.util.ArrayList;
import java.util.List;

import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.jlib.gui.abstraction.EntityLoader;
import com.github.bfour.jlib.gui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.jlib.gui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.jlib.gui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.jlib.gui.components.table.FPJGUITableColumn;

public class CrawlerBrowsePanel extends EntityTableBrowsePanel<Crawler>
		implements FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public CrawlerBrowsePanel(final ServiceManager servMan) {

		super(Crawler.class, servMan.getCrawlerService(), true);

		// hide default buttons for CRUD options
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
		getListLikeContainer().addColumn(nameColumn);

		getListLikeContainer().setPreferredColumnWidth(nameColumn, 186);
		getListLikeContainer().setMinimumColumnWidth(nameColumn, 86);

		// ==== loader ====
		setLoader(new EntityLoader<Crawler>() {
			@Override
			public List<Crawler> get() {
				return new ArrayList<>(servMan.getCrawlerService()
						.getAvailableCrawlers());
			}
		});

	}
}
