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

package com.github.bfour.fpliteraturecollector.gui.authors;

import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class AuthorsBrowsePanel extends EntityTableBrowsePanel<Author>
		implements FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public AuthorsBrowsePanel(final ServiceManager servMan) {

		super(Author.class, servMan.getAuthorService(), true);

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
		getListLikeContainer().addColumn(firstNameColumn);

		FPJGUITableColumn<Author> lastNameColumn = new FPJGUITableColumn<Author>(
				"Last name(s)", new FPJGUITableFieldGetter<Author>() {
					@Override
					public String get(Author item) {
						return item.getLastName();
					}
				}, true, 30, 30, "lastName", false);
		getListLikeContainer().addColumn(lastNameColumn);

		getListLikeContainer().setPreferredColumnWidth(firstNameColumn, 200);
		getListLikeContainer().setPreferredColumnWidth(lastNameColumn, 286);

		getListLikeContainer().setMinimumColumnWidth(firstNameColumn, 100);
		getListLikeContainer().setMinimumColumnWidth(lastNameColumn, 186);

	}
}
