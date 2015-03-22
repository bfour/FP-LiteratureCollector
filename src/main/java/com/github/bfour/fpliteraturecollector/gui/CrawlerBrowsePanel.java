package com.github.bfour.fpliteraturecollector.gui;

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
