package com.github.bfour.fpliteraturecollector.gui.tags;

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


import java.awt.Color;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjgui.abstraction.EntityEditPanel;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUIColorChooser;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.FPJGUITextPane;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.TagBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class TagsPanel extends EntityEditPanel<Tag, TagBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;
	
	/**
	 * Create the panel.
	 */
	public TagsPanel(ServiceManager servMan) {

		super(new BuilderFactory<Tag, TagBuilder>() {
			@Override
			public TagBuilder getBuilder() {
				return new TagBuilder();
			}

			@Override
			public TagBuilder getBuilder(Tag entity) {
				return new TagBuilder(entity);
			}

		}, servMan.getTagService());

		getContentPane().setLayout(
				new MigLayout("insets 0, w 60:80:100", "[grow]",
						"[]8[]8[]8[]8[]"));

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel),
				"cell 0 0,growx");

		// name
		FPJGUITextPane nameField = new FPJGUITextPane();
		nameField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				return new ValidationRuleResult(obj != null && !obj.isEmpty(),
						"Name must not be empty.");
			}
		});
		nameField.setValueRequired(true);
		FPJGUIMultilineLabel nameLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> nameToggle = new ToggleEditFormComponent<String>(
				nameLabel, nameField);
		registerToggleComponent(nameToggle);
		getContentPane().add(new FPJGUILabelPanel("Name", nameToggle),
				"cell 0 1,growx");

		// color
		FPJGUIColorChooser chooser = new FPJGUIColorChooser();
		FPJGUIColorChooser colourViewer = new FPJGUIColorChooser();
		colourViewer.setEnabled(false);
		ToggleEditFormComponent<Color> colourToggle = new ToggleEditFormComponent<Color>(
				colourViewer, chooser);
		registerToggleComponent(colourToggle);
		getContentPane().add(new FPJGUILabelPanel("Colour", colourToggle),
				"cell 0 2,growx");

		// mappings
		ObjectGraphicalValueContainerMapper<TagBuilder, String> IDMapper = new ObjectGraphicalValueContainerMapper<TagBuilder, String>(
				IDLabel) {
			@Override
			public String getValue(TagBuilder object) {
				return object.getID() + "";
			}

			@Override
			public void setValue(TagBuilder object, String value) {
				try {
					object.setID(Long.parseLong(value));
				} catch (NumberFormatException e) {
				}
			}
		};
		getMappers().add(IDMapper);

		ObjectGraphicalValueContainerMapper<TagBuilder, String> nameMapper = new ObjectGraphicalValueContainerMapper<TagBuilder, String>(
				nameToggle) {
			@Override
			public String getValue(TagBuilder object) {
				if (object.getName() == null)
					return "";
				return object.getName();
			}

			@Override
			public void setValue(TagBuilder object, String value) {
				object.setName(value);
			}
		};
		getMappers().add(nameMapper);

		ObjectGraphicalValueContainerMapper<TagBuilder, Color> colorMapper = new ObjectGraphicalValueContainerMapper<TagBuilder, Color>(
				colourToggle) {
			@Override
			public Color getValue(TagBuilder object) {
				return object.getColour();
			}

			@Override
			public void setValue(TagBuilder object, Color value) {
				object.setColour(value);
			}
		};
		getMappers().add(colorMapper);

	}

}
