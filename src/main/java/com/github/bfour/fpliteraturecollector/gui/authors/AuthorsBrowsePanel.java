package com.github.bfour.fpliteraturecollector.gui.authors;

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
import java.util.List;

import javax.swing.JMenuItem;

import org.apache.commons.beanutils.BeanUtils;

import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.DefaultListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.util.DefaultActionInterfacingHandler;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class AuthorsBrowsePanel extends EntityBrowsePanel<Author>
		implements FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public AuthorsBrowsePanel(final ServiceManager servMan) {

		// show default buttons for CRUD options
		setDeleteEntityEnabled(true);
		setEditEntityEnabled(false);
		setCreateEntityEnabled(true);

		// ==== columns ====
		FPJGUITableColumn<Author> firstNameColumn = new FPJGUITableColumn<Author>(
				"First name", new FPJGUITableFieldGetter<Author>() {
					@Override
					public String get(Author item) {
						return item.getFirstName();
					}
				}, true, 30, 30, "firstName", false);
		getTable().addColumn(firstNameColumn);
		
		FPJGUITableColumn<Author> lastNameColumn = new FPJGUITableColumn<Author>(
				"Last name(s)", new FPJGUITableFieldGetter<Author>() {
					@Override
					public String get(Author item) {
						return item.getLastName();
					}
				}, true, 30, 30, "lastName", false);
		getTable().addColumn(lastNameColumn);

		this.table.setPreferredColumnWidth(firstNameColumn, 200);
		this.table.setPreferredColumnWidth(lastNameColumn, 286);

		this.table.setMinimumColumnWidth(firstNameColumn, 100);
		this.table.setMinimumColumnWidth(lastNameColumn, 186);

		// ==== loader ====
		this.loader = new EntityLoader<Author>() {
			@Override
			public List<Author> get() {
				List<Author> list = new ArrayList<>();
				try {
					list = servMan.getAuthorService().getAll();
				} catch (ServiceException e) {
					feedbackProxy
							.feedbackBroadcasted(new Feedback(
									AuthorsBrowsePanel.this,
									"Sorry, failed to get authors from AuthorService.",
									FeedbackType.ERROR));
				}
				return list;
			}
		};

		// hook up table with change event system
		DefaultListLikeChangeListener<Author> changeListener = new DefaultListLikeChangeListener<Author>(
				getTable(), new EntityFilterPipeline<Author>());
		ChangeHandler.getInstance(Author.class).addEventListener(
				changeListener);

		// load initial data
		load();

		// set button actions
		ActionListener deleteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AuthorService custServ = servMan.getAuthorService();
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
