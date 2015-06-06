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


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;

import org.apache.commons.beanutils.BeanUtils;

import com.github.bfour.fpjcommons.events.BatchCreateEvent;
import com.github.bfour.fpjcommons.events.BatchDeleteEvent;
import com.github.bfour.fpjcommons.events.BatchUpdateEvent;
import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.events.ChangeListener;
import com.github.bfour.fpjcommons.events.CreateEvent;
import com.github.bfour.fpjcommons.events.DeleteEvent;
import com.github.bfour.fpjcommons.events.UpdateEvent;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.util.DefaultActionInterfacingHandler;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.service.AtomicRequestService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

/**
 * This class deals with the presentation of a list of AtomicRequests
 */
public class AtomicRequestBrowsePanel extends EntityBrowsePanel<AtomicRequest>
		implements FeedbackProvider {

	private static final long serialVersionUID = 1584008979044088377L;

	public AtomicRequestBrowsePanel(final ServiceManager servMan) {
		this(servMan, null);
	}

	/**
	 * 
	 * @param servMan
	 * @param query
	 *            limit the AtomicRequests to this Query, ie. only
	 *            AtomicRequests belonging to this Query will be shown
	 */
	public AtomicRequestBrowsePanel(final ServiceManager servMan,
			final Query query) {

		// show default buttons for CRUD options
		setDeleteEntityEnabled(true);
		setEditEntityEnabled(true);
		setCreateEntityEnabled(true);
		
		// hide search bar
		setSearchPanel(null);

		// ==== columns ====
		FPJGUITableColumn<AtomicRequest> crawlerColumn = new FPJGUITableColumn<AtomicRequest>(
				"Crawler", new FPJGUITableFieldGetter<AtomicRequest>() {
					@Override
					public String get(AtomicRequest item) {
						return servMan.getCrawlerService()
								.getIdentifierForCrawler(item.getCrawler());
					}
				}, true, 30, 30, "crawler", false);
		getTable().addColumn(crawlerColumn);

		FPJGUITableColumn<AtomicRequest> requestStringColumn = new FPJGUITableColumn<AtomicRequest>(
				"Request String", new FPJGUITableFieldGetter<AtomicRequest>() {
					@Override
					public String get(AtomicRequest item) {
						return item.getSearchString();
					}
				}, true, 30, 30, "requestStrings", false);
		getTable().addColumn(requestStringColumn);

		this.table.setPreferredColumnWidth(crawlerColumn, 100);
		this.table.setPreferredColumnWidth(requestStringColumn, 400);

		this.table.setMinimumColumnWidth(crawlerColumn, 100);
		this.table.setMinimumColumnWidth(requestStringColumn, 200);

		// ==== loader ====
		this.loader = new EntityLoader<AtomicRequest>() {
			@Override
			public List<AtomicRequest> get() {
				List<AtomicRequest> list = new ArrayList<>();
				if (query == null)
					return new ArrayList<AtomicRequest>(0);
				else
					list = query.getAtomicRequests();
				return list;
			}
		};

		// hook up table with change event system
		if (query == null) {
			// no query specified --> this browse panel is for a new query -->
			// do not register for any changes
		} else {
			// query is specified --> this browse panel is only for
			// AtomicRequests of this Query --> listen for changes to query
			ChangeListener<Query> changeListener = new ChangeListener<Query>() {
				@Override
				public void handle(BatchCreateEvent<Query> arg0) {
				}

				@Override
				public void handle(BatchDeleteEvent<Query> arg0) {
					if (arg0.getAffectedObjects().contains(query))
						getTable().clear();
				}

				@Override
				public void handle(BatchUpdateEvent<Query> arg0) {
					Iterator<UpdateEvent<Query>> iter = arg0.getChanges()
							.iterator();
					while (iter.hasNext()) {
						handle(arg0);
					}
				}

				@Override
				public void handle(CreateEvent<Query> arg0) {
				}

				@Override
				public void handle(DeleteEvent<Query> arg0) {
					if (arg0.getDeletedObject().equals(query))
						getTable().clear();
				}

				@Override
				public void handle(UpdateEvent<Query> arg0) {
					if (arg0.getOldObject().equals(query))
						getTable().setEntries(
								arg0.getNewObject().getAtomicRequests());
				}
			};
			ChangeHandler.getInstance(Query.class).addEventListener(
					changeListener);
		}

		// load initial data
		load();

		// set button actions
		ActionListener deleteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AtomicRequestService eServ = servMan.getAtomicRequestService();
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
				if (query == null) {
					getTable().deleteEntry(table.getSelectedItem());
				} else {
					DefaultActionInterfacingHandler.getInstance()
							.requestDeleteFromList(source, feedbackProxy,
									table.getSelectedItem(), eServ);
				}
			}
		};
		addDeleteAction(deleteListener);

	}

	@Override
	public List<AtomicRequest> getValue() {
		return getTable().getEntries();
	}

	@Override
	public void setValue(List<AtomicRequest> value) {
		if (value == null)
			getTable().setEntries(new ArrayList<AtomicRequest>(0));
		else
			getTable().setEntries(value);
	}

}
