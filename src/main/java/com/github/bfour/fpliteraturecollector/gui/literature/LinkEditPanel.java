package com.github.bfour.fpliteraturecollector.gui.literature;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule.ValidationRuleResult;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValueContainer;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
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

		setLayout(new MigLayout("insets 0", "[][]", "[]"));

		nameField = new FPJGUITextField();
		add(new FPJGUILabelPanel("Name", nameField), "cell 0 0, growx");

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
		add(new FPJGUILabelPanel("URL", uriField), "cell 1 0, growx");

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
		// TODO Auto-generated method stub
		return rule;
	}

	@Override
	public ValidationRuleResult validateValue() {
		// TODO Auto-generated method stub
		return ValidationRuleResult.getSimpleTrueInstance();
	}

}
