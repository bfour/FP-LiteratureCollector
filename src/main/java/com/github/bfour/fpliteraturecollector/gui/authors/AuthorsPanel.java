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


import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjgui.abstraction.EntityEditPanel;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class AuthorsPanel extends EntityEditPanel<Author, AuthorBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;

	/**
	 * Create the panel.
	 */
	public AuthorsPanel(ServiceManager servMan) {

		super(new BuilderFactory<Author, AuthorBuilder>() {
			@Override
			public AuthorBuilder getBuilder() {
				return new AuthorBuilder();
			}

			@Override
			public AuthorBuilder getBuilder(Author entity) {
				return new AuthorBuilder(entity);
			}

		}, servMan.getAuthorService());

		getContentPane().setLayout(
				new MigLayout("insets 0, w 60:80:100", "[grow]",
						"[]8[]8[]8[]8[]"));

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel),
				"cell 0 0,growx");

		// first name
		FPJGUIMultilineLabel firstNameField = new FPJGUIMultilineLabel();
		FPJGUIMultilineLabel firstNameLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> firstNameToggle = new ToggleEditFormComponent<String>(
				firstNameLabel, firstNameField);
		registerToggleComponent(firstNameToggle);
		getContentPane().add(
				new FPJGUILabelPanel("First Name", firstNameToggle),
				"cell 0 1,growx");

		// last name
		FPJGUIMultilineLabel lastNameField = new FPJGUIMultilineLabel();
		lastNameField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				return new ValidationRuleResult(obj != null && !obj.isEmpty(),
						"Last name(s) must not be empty.");
			}
		});
		lastNameField.setValueRequired(true);
		FPJGUIMultilineLabel lastNameLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> lastNameToggle = new ToggleEditFormComponent<String>(
				lastNameLabel, lastNameField);
		registerToggleComponent(lastNameToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Last Name(s)", lastNameToggle),
				"cell 0 2,growx");

		// mappings
		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> IDMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				IDLabel) {
			@Override
			public String getValue(AuthorBuilder object) {
				return object.getID() + "";
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				try {
					object.setID(Long.parseLong(value));
				} catch (NumberFormatException e) {
				}
			}
		};
		getMappers().add(IDMapper);

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> firstNameMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				firstNameToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				return object.getFirstName();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setFirstName(value);
			}
		};
		getMappers().add(firstNameMapper);

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> lastNameMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				lastNameToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				return object.getLastName();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setLastName(value);
			}
		};
		getMappers().add(lastNameMapper);

	}
}
