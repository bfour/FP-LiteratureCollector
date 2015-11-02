package com.github.bfour.fpliteraturecollector.gui.literature;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
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
import com.github.bfour.fpjgui.abstraction.valueContainer.ValueContainer;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.design.Icons;
import com.github.bfour.fpjgui.util.GraphicalValueContainerValidationFeedbackHandler;
import com.github.bfour.fpliteraturecollector.domain.Link;

public class LinkSetEditPanel extends JPanel implements
		GraphicalValueContainer<Set<Link>>, FeedbackProvider {

	private static final long serialVersionUID = 1519718341197424458L;
	private FeedbackProviderProxy feedbackProxy = new FeedbackProviderProxy();
	private ValidationRule<Set<Link>> rule;
	private GraphicalValueContainerValidationFeedbackHandler<Set<Link>> validationFeedbackHandler;
	private Set<Link> value;

	public LinkSetEditPanel() {
		setLayout(new MigLayout("insets 0", "[]", "[]"));
		validationFeedbackHandler = new GraphicalValueContainerValidationFeedbackHandler<Set<Link>>(
				this);
	}

	@Override
	public Set<Link> getValue() {
		Set<Link> links = new HashSet<Link>();
		for (Component comp : getComponents()) {
			if (comp instanceof LinkEditPanel)
				links.add(((LinkEditPanel) comp).getValue());
		}
		if (links.isEmpty())
			return value; // this is necessary as the initially set value might
							// have been null
		return links;
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
			LinkEditPanel panel = new LinkEditPanel();
			panel.setValue(link);
			add(panel, "wrap");
			FPJGUIButton deleteButton = FPJGUIButtonFactory
					.createButton(ButtonFormats.NAKED);
			deleteButton.setIcon(Icons.DELETE_16.getIcon());
			deleteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					remove(panel);
				}
			});
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
		for (Component comp : getComponents()) {
			if (comp instanceof ValueContainer) {
				@SuppressWarnings("rawtypes")
				// TODO (low) improve
				ValidationRuleResult result = ((ValueContainer) comp)
						.validateValue();
				if (!result.getValue())
					return result;
			}
		}
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
		for (Component comp : getComponents()) {
			if (comp instanceof GraphicalValueContainer) {
				@SuppressWarnings("rawtypes")
				// TODO (low) improve
				ValidationRuleResult result = ((GraphicalValueContainer) comp)
						.validateValueAndGiveFeedback();
				if (!result.getValue())
					return result;
			}
		}
		return ValidationRuleResult.getSimpleTrueInstance();
	}

	@Override
	public void showInPlaceFeedback(Feedback feedback) {
		validationFeedbackHandler.feedbackBroadcasted(feedback);
	}

	@Override
	public void hideInPlaceFeedback() {
		validationFeedbackHandler.revokeAllFeedback();
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
