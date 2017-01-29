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

import java.awt.Component;
import java.util.Set;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.jlib.gui.abstraction.feedback.Feedback;
import com.github.bfour.jlib.gui.abstraction.feedback.FeedbackListener;
import com.github.bfour.jlib.gui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.jlib.gui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.jlib.gui.abstraction.valueChangeHandling.ValueChangeEvent;
import com.github.bfour.jlib.gui.abstraction.valueChangeHandling.ValueChangeListener;
import com.github.bfour.jlib.gui.abstraction.valueContainer.GraphicalValueContainer;
import com.github.bfour.jlib.gui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.jlib.gui.abstraction.valueContainer.ValidationRule.ValidationRuleResult;

public class LinkSetPanel extends JPanel implements
		GraphicalValueContainer<Set<Link>>, FeedbackProvider {

	private static final long serialVersionUID = 1519718341197424458L;
	private FeedbackProviderProxy feedbackProxy = new FeedbackProviderProxy();
	private Set<Link> value;
	private ValidationRule<Set<Link>> rule;

	public LinkSetPanel() {

		setLayout(new MigLayout("insets 0", "[]", "[]"));

	}

	@Override
	public Set<Link> getValue() {
		return value;
	}

	@Override
	public void setValue(Set<Link> value) {

		this.value = value;

		for (Component comp : getComponents()) {
			remove(comp);
		}

		revalidate();
		repaint();

		if (value == null)
			return;

		for (Link link : value) {
			LinkPanel panel = new LinkPanel();
			panel.setValue(link);
			panel.addFeedbackListener(feedbackProxy);
			add(panel, "wrap");
		}

		revalidate();
		repaint();

	}

	@Override
	public void setValidationRule(ValidationRule<Set<Link>> rule) {
		this.rule = rule;
	}

	@Override
	public ValidationRule<Set<Link>> getValidationRule() {
		return rule;
	}

	@Override
	public ValidationRuleResult validateValue() {
		return ValidationRuleResult.getSimpleTrueInstance();
	}

	@Override
	public void addFeedbackListener(FeedbackListener listener) {
		feedbackProxy.addFeedbackListener(listener);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener listener) {
		feedbackProxy.removeFeedbackListener(listener);
	}

	@Override
	public void setValueRequired(boolean required) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireValueChange(ValueChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public ValidationRuleResult validateValueAndGiveFeedback() {
		return ValidationRuleResult.getSimpleTrueInstance();
	}

	@Override
	public void showInPlaceFeedback(Feedback feedback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideInPlaceFeedback() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addComponent(Component component, String formatting) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeComponent(Component component) {
		// TODO Auto-generated method stub

	}

}
