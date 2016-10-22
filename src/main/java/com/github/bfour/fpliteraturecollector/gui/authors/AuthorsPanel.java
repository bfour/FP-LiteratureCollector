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

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.components.composite.EntityEditPanel;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class AuthorsPanel extends EntityEditPanel<Author, AuthorBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;
	private FPJGUILabelPanel msAcademicLabelPanel;
	private FPJGUILabelPanel gScholarIDLabelPanel;
	private FPJGUILabelPanel acmLabelPanel;
	private FPJGUILabelPanel pubmedLabelPanel;
	private FPJGUILabelPanel ieeeLabelPanel;

	/**
	 * Create the panel.
	 */
	public AuthorsPanel(ServiceManager servMan) {

		super(Author.class, new BuilderFactory<Author, AuthorBuilder>() {
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
				new MigLayout("insets 0, w 60:80:100", "[grow]", "[]"));

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel),
				"cell 0 0,growx");

		// first name
		FPJGUITextField firstNameField = new FPJGUITextField();
		FPJGUIMultilineLabel firstNameLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> firstNameToggle = new ToggleEditFormComponent<String>(
				firstNameLabel, firstNameField);
		registerToggleComponent(firstNameToggle);
		getContentPane().add(
				new FPJGUILabelPanel("First Name", firstNameToggle),
				"cell 0 1,growx");

		// last name
		FPJGUITextField lastNameField = new FPJGUITextField();
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

		// gScholarID
		FPJGUITextField gScholarIDField = new FPJGUITextField();
		FPJGUIMultilineLabel gScholarIDLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> gScholarIDToggle = new ToggleEditFormComponent<String>(
				gScholarIDLabel, gScholarIDField);
		registerToggleComponent(gScholarIDToggle);
		gScholarIDLabelPanel = new FPJGUILabelPanel("Google Scholar ID",
				gScholarIDToggle);
		getContentPane().add(gScholarIDLabelPanel, "cell 0 3,growx");

		// msAcademicID
		FPJGUITextField msAcademicIDField = new FPJGUITextField();
		FPJGUIMultilineLabel msAcademicIDLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> msAcademicIDToggle = new ToggleEditFormComponent<String>(
				msAcademicIDLabel, msAcademicIDField);
		registerToggleComponent(msAcademicIDToggle);
		msAcademicLabelPanel = new FPJGUILabelPanel("Microsoft Academic ID",
				msAcademicIDToggle);
		getContentPane().add(msAcademicLabelPanel, "cell 0 4,growx");

		// acm
		FPJGUITextField acmIDField = new FPJGUITextField();
		FPJGUIMultilineLabel acmIDLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> acmIDToggle = new ToggleEditFormComponent<String>(
				acmIDLabel, acmIDField);
		registerToggleComponent(acmIDToggle);
		acmLabelPanel = new FPJGUILabelPanel("ACM ID", acmIDToggle);
		getContentPane().add(acmLabelPanel, "cell 0 5,growx");

		// pubmed
		FPJGUITextField pubmedIDField = new FPJGUITextField();
		FPJGUIMultilineLabel pubmedIDLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> pubmedIDToggle = new ToggleEditFormComponent<String>(
				pubmedIDLabel, pubmedIDField);
		registerToggleComponent(pubmedIDToggle);
		pubmedLabelPanel = new FPJGUILabelPanel("Pubmed ID", pubmedIDToggle);
		getContentPane().add(pubmedLabelPanel, "cell 0 6,growx");

		// ieee
		FPJGUITextField ieeeIDField = new FPJGUITextField();
		FPJGUIMultilineLabel ieeeIDLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> ieeeIDToggle = new ToggleEditFormComponent<String>(
				ieeeIDLabel, ieeeIDField);
		registerToggleComponent(ieeeIDToggle);
		ieeeLabelPanel = new FPJGUILabelPanel("ieee ID", ieeeIDToggle);
		getContentPane().add(ieeeLabelPanel, "cell 0 6,growx");

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

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> gScholarIDMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				gScholarIDToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				if (object.getgScholarID() == null)
					return "-";
				return object.getgScholarID();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setgScholarID(value);
			}
		};
		getMappers().add(gScholarIDMapper);

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> msAcademicIDMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				msAcademicIDToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				if (object.getMsAcademicID() == null)
					return "-";
				return object.getMsAcademicID();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setMsAcademicID(value);
			}
		};
		getMappers().add(msAcademicIDMapper);

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> acmIDMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				acmIDToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				if (object.getAcmID() == null)
					return "-";
				return object.getAcmID();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setAcmID(value);
			}
		};
		getMappers().add(acmIDMapper);

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> pubmedIDMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				pubmedIDToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				if (object.getPubmedID() == null)
					return "-";
				return object.getPubmedID();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setPubmedID(value);
			}
		};
		getMappers().add(pubmedIDMapper);

		ObjectGraphicalValueContainerMapper<AuthorBuilder, String> ieeeIDMapper = new ObjectGraphicalValueContainerMapper<AuthorBuilder, String>(
				ieeeIDToggle) {
			@Override
			public String getValue(AuthorBuilder object) {
				if (object.getIeeeID() == null)
					return "-";
				return object.getIeeeID();
			}

			@Override
			public void setValue(AuthorBuilder object, String value) {
				object.setIeeeID(value);
			}
		};
		getMappers().add(ieeeIDMapper);

	}

}
