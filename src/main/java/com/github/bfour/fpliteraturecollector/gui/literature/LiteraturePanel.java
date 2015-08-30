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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.SearchComboBox;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.components.composite.EntityEditPanel;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpjguiextended.tagging.TagTilePanel;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteraturePanel extends
		EntityEditPanel<Literature, LiteratureBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;
	private URI webSiteURL;
	private URI fullTextURL;

	/**
	 * Create the panel.
	 */
	public LiteraturePanel(ServiceManager servMan) {

		super(Literature.class,
				new BuilderFactory<Literature, LiteratureBuilder>() {
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
				new MigLayout("insets 0, w 60:80:100", "[grow]", "[]"));

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel), "growx,wrap");

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
				"growx,wrap");

		// type
		EntityBrowsePanel<LiteratureType> litTypeBrowsePanel = new EntityBrowsePanel<LiteratureType>(
				LiteratureType.class);
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
				"growx,wrap");

		// authors
		FPJGUIMultilineLabel authorLabel = new FPJGUIMultilineLabel();
		FPJGUIMultilineLabel authorField = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> authorToggle = new ToggleEditFormComponent<String>(
				authorLabel, authorField);
		registerToggleComponent(authorToggle);
		getContentPane().add(new FPJGUILabelPanel("Authors", authorToggle),
				"growx,wrap");

		// DOI
		FPJGUIMultilineLabel DOILabel = new FPJGUIMultilineLabel();
		FPJGUIMultilineLabel DOIField = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> DOIToggle = new ToggleEditFormComponent<String>(
				DOILabel, DOIField);
		registerToggleComponent(DOIToggle);
		getContentPane().add(new FPJGUILabelPanel("DOI", DOIToggle),
				"growx,wrap");

		// ISBN
		FPJGUIMultilineLabel ISBNLabel = new FPJGUIMultilineLabel();
		FPJGUIMultilineLabel ISBNField = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> ISBNToggle = new ToggleEditFormComponent<String>(
				ISBNLabel, ISBNField);
		registerToggleComponent(ISBNToggle);
		getContentPane().add(new FPJGUILabelPanel("ISBN", ISBNToggle),
				"growx,wrap");

		// tags
		TagTilePanel<Tag> tagLabel = new TagTilePanel<>(false);
		TagTilePanel<Tag> tagField = new TagTilePanel<>(false);
		ToggleEditFormComponent<List<Tag>> tagToggle = new ToggleEditFormComponent<List<Tag>>(
				tagLabel, tagField);
		registerToggleComponent(tagToggle);
		getContentPane().add(new FPJGUILabelPanel("Tags", tagToggle),
				"growx,wrap");

		final FPJGUIButton websiteURLLabel = FPJGUIButtonFactory
				.createButton(ButtonFormats.LINK);
		websiteURLLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop desktop = java.awt.Desktop.getDesktop();
					desktop.browse(webSiteURL);
				} catch (IOException e1) {
					getFeedbackProxy().feedbackBroadcasted(
							new Feedback(websiteURLLabel,
									"Sorry, failed to open link.", e1
											.getMessage(), FeedbackType.ERROR));
				}
			}
		});
		FPJGUIMultilineLabel websiteURLField = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> websiteURLToggle = new ToggleEditFormComponent<String>(
				websiteURLLabel, websiteURLField);
		registerToggleComponent(websiteURLToggle);
		getContentPane().add(new FPJGUILabelPanel("Website", websiteURLToggle),
				"growx,wrap");

		// fulltextURL
		final FPJGUIButton fulltextURLLabel = FPJGUIButtonFactory
				.createButton(ButtonFormats.LINK);
		fulltextURLLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop desktop = java.awt.Desktop.getDesktop();
					desktop.browse(fullTextURL);
				} catch (IOException e1) {
					getFeedbackProxy().feedbackBroadcasted(
							new Feedback(fulltextURLLabel,
									"Sorry, failed to open link.", e1
											.getMessage(), FeedbackType.ERROR));
				}
			}
		});
		FPJGUIMultilineLabel fulltextURLField = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> fulltextURLToggle = new ToggleEditFormComponent<String>(
				fulltextURLLabel, fulltextURLField);
		registerToggleComponent(fulltextURLToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Full Text URL", fulltextURLToggle),
				"growx,wrap");

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

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> DOIMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				DOIToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				if (object.getDOI() == null)
					return "-";
				return object.getDOI();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setDOI(value);
			}
		};
		getMappers().add(DOIMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> ISBNMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				ISBNToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				if (object.getISBN() == null)
					return "-";
				return object.getISBN().getV13String();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setISBN(new ISBN(value));
			}
		};
		getMappers().add(ISBNMapper);

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

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> websiteURLMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				websiteURLToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				webSiteURL = null;
				if (object.getWebsiteURL() == null)
					return "-";
				try {
					URI uri = new URI(object.getWebsiteURL());
					webSiteURL = uri;
					return uri.getHost();
				} catch (URISyntaxException e) {
					return object.getFulltextURL();
				}
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setWebsiteURL(value);
			}
		};
		getMappers().add(websiteURLMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> fulltextURLMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				fulltextURLToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				fullTextURL = null;
				if (object.getFulltextURL() == null)
					return "-";
				try {
					URI uri = new URI(object.getFulltextURL());
					fullTextURL = uri;
					return uri.getHost();
				} catch (URISyntaxException e) {
					return object.getFulltextURL();
				}
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setFulltextURL(value);
			}
		};
		getMappers().add(fulltextURLMapper);

	}
}
