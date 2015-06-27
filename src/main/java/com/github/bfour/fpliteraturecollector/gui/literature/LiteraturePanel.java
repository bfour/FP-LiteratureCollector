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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.EntityEditPanel;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.SearchComboBox;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.gui.tags.TagTilePanel;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteraturePanel extends
		EntityEditPanel<Literature, LiteratureBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;

	/**
	 * Create the panel.
	 */
	public LiteraturePanel(ServiceManager servMan) {

		super(new BuilderFactory<Literature, LiteratureBuilder>() {
			@Override
			public LiteratureBuilder getBuilder() {
				return new LiteratureBuilder();
			}

			@Override
			public LiteratureBuilder getBuilder(Literature entity) {
				return new LiteratureBuilder(entity);
			}

		}, servMan.getLiteratureService());

		getContentPane().setLayout(
				new MigLayout("insets 0, w 60:80:100", "[grow]",
						"[]8[]8[]8[]8[]"));

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel),
				"cell 0 0,growx");

		// title
		FPJGUIMultilineLabel titleField = new FPJGUIMultilineLabel();
		titleField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				return new ValidationRuleResult(obj != null && !obj.isEmpty(),
						"Name must not be empty.");
			}
		});
		titleField.setValueRequired(true);
		FPJGUIMultilineLabel nameLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> titleToggle = new ToggleEditFormComponent<String>(
				nameLabel, titleField);
		registerToggleComponent(titleToggle);
		getContentPane().add(new FPJGUILabelPanel("Title", titleToggle),
				"cell 0 1,growx");

		// type
		EntityBrowsePanel<LiteratureType> litTypeBrowsePanel = new EntityBrowsePanel<LiteratureType>();
		litTypeBrowsePanel
				.setLoader(new EntityLoader<Literature.LiteratureType>() {
					private final List<LiteratureType> list = new ArrayList<LiteratureType>(
							Arrays.asList(LiteratureType.values()));

					@Override
					public List<LiteratureType> get() {
						return list;
					}
				});
		SearchComboBox<LiteratureType> typeCombo = new SearchComboBox<LiteratureType>(
				litTypeBrowsePanel, new Getter<LiteratureType, String>() {
					@Override
					public String get(LiteratureType type) {
						return type.getTellingName();
					}
				});
		FPJGUILabel<LiteratureType> typeLabel = new FPJGUILabel<>();
		ToggleEditFormComponent<LiteratureType> typeToggle = new ToggleEditFormComponent<LiteratureType>(
				typeLabel, typeCombo);
		registerToggleComponent(typeToggle);
		getContentPane().add(new FPJGUILabelPanel("Type", typeToggle),
				"cell 0 2,growx");

		// authors
		FPJGUIMultilineLabel authorLabel = new FPJGUIMultilineLabel();
		FPJGUIMultilineLabel authorField = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> authorToggle = new ToggleEditFormComponent<String>(
				authorLabel, authorField);
		registerToggleComponent(authorToggle);
		getContentPane().add(new FPJGUILabelPanel("Author", authorToggle),
				"cell 0 3,growx");

		// tags
		TagTilePanel tagLabel = new TagTilePanel(false);
		TagTilePanel tagField = new TagTilePanel(false);
		ToggleEditFormComponent<List<Tag>> tagToggle = new ToggleEditFormComponent<List<Tag>>(
				tagLabel, tagField);
		registerToggleComponent(tagToggle);
		getContentPane().add(new FPJGUILabelPanel("Tags", tagToggle),
				"cell 0 4,growx");

		// mappings
		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> IDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				IDLabel) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getID() + "";
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				try {
					object.setID(Long.parseLong(value));
				} catch (NumberFormatException e) {
				}
			}
		};
		getMappers().add(IDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> titleMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				titleToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getTitle();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setTitle(value);
			}
		};
		getMappers().add(titleMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, LiteratureType> typeMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, LiteratureType>(
				typeToggle) {
			@Override
			public LiteratureType getValue(LiteratureBuilder object) {
				return object.getType();
			}

			@Override
			public void setValue(LiteratureBuilder object, LiteratureType value) {
				object.setType(value);
			}
		};
		getMappers().add(typeMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> authorMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				authorToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				Set<Author> authors = object.getAuthors();
				if (authors == null)
					return "";
				StringBuilder builder = new StringBuilder();
				for (Author auth : authors) {
					builder.append(auth.getFirstName());
					builder.append(" ");
					builder.append(auth.getLastName());
					builder.append(", ");
				}
				return builder.substring(0, builder.length() - 2);
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				// TODO
			}
		};
		getMappers().add(authorMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, List<Tag>> tagMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, List<Tag>>(
				tagToggle) {
			@Override
			public List<Tag> getValue(LiteratureBuilder object) {
				Set<Tag> set = object.getTags();
				if (set != null)
					return new ArrayList<>(set);
				else
					return new ArrayList<Tag>(0);
			}

			@Override
			public void setValue(LiteratureBuilder object, List<Tag> value) {
				object.setTags(new HashSet<>(value));
			}
		};
		getMappers().add(tagMapper);

	}
}
