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

package com.github.bfour.fpliteraturecollector.gui.literature;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule.ValidationRuleResult;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValueContainer;
import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpliteraturecollector.domain.Link;

public class LinkEditPanel extends JPanel implements ValueContainer<Link> {

	private static final long serialVersionUID = -3671888082769721944L;

	private FPJGUITextField nameField;
	private FPJGUITextField uriField;

	private ValidationRule<Link> rule;

	/**
	 * Create the panel.
	 */
	public LinkEditPanel() {

		rule = new ValidationRule<Link>() {
			@Override
			public ValidationRuleResult evaluate(Link obj) {
				if ((obj.getName() == null || obj.getName().isEmpty())
						&& (obj.getUri() == null))
					return new ValidationRuleResult(false,
							"Please enter a name and a URL.");
				if (obj.getName() == null || obj.getName().isEmpty())
					return new ValidationRuleResult(false,
							"Please enter a name.");
				if (obj.getUri() == null)
					return new ValidationRuleResult(false,
							"Please enter a URL.");
				return ValidationRuleResult.getSimpleTrueInstance();
			}
		};

		setLayout(new MigLayout("insets 0", "[2cm::][3cm::]", "[]"));

		nameField = new FPJGUITextField();
		add(nameField, "cell 0 0, growx");

		uriField = new FPJGUITextField();
		uriField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				try {
					new URI(obj);
				} catch (URISyntaxException e) {
					return new ValidationRuleResult(false,
							"Please enter a valid URI, eg. http://www.example.com");
				}
				return ValidationRuleResult.getSimpleTrueInstance();
			}
		});
		add(uriField, "cell 1 0, growx");

	}

	@Override
	public Link getValue() {
		try {
			return new Link(nameField.getValue(), new URI(uriField.getValue()));
		} catch (URISyntaxException e) {
		}
		return null;
	}

	@Override
	public void setValue(Link value) {
		nameField.setValue(value.getName());
		uriField.setValue(value.getUri().toString());
	}

	@Override
	public void setValidationRule(ValidationRule<Link> rule) {
		this.rule = rule;
	}

	@Override
	public ValidationRule<Link> getValidationRule() {
		return rule;
	}

	@Override
	public ValidationRuleResult validateValue() {
		if (rule == null)
			return ValidationRuleResult.getSimpleTrueInstance();
		return rule.evaluate(getValue());
	}

}
