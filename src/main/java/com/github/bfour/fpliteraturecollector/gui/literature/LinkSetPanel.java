package com.github.bfour.fpliteraturecollector.gui.literature;

import java.awt.Component;
import java.util.Set;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpjgui.abstraction.valueChangeHandling.ValueChangeEvent;
import com.github.bfour.fpjgui.abstraction.valueChangeHandling.ValueChangeListener;
import com.github.bfour.fpjgui.abstraction.valueContainer.GraphicalValueContainer;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule.ValidationRuleResult;
import com.github.bfour.fpliteraturecollector.domain.Link;

public class LinkSetPanel extends JPanel implements GraphicalValueContainer<Set<Link>>,
		FeedbackProvider {

	private static final long serialVersionUID = 1519718341197424458L;
	private FeedbackProviderProxy feedbackProxy = new FeedbackProviderProxy();
	private Set<Link> value;

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
		if (value == null)
			return;
		for (Link link : value) {
			LinkPanel panel = new LinkPanel();
			panel.setValue(link);
			panel.addFeedbackListener(feedbackProxy);
			add(panel, "wrap");
		}
	}

	@Override
	public void setValidationRule(ValidationRule<Set<Link>> rule) {
	}

	@Override
	public ValidationRule<Set<Link>> getValidationRule() {
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
