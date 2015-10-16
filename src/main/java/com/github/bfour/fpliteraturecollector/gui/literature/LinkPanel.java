package com.github.bfour.fpliteraturecollector.gui.literature;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule.ValidationRuleResult;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValueContainer;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpliteraturecollector.domain.Link;

public class LinkPanel extends JPanel implements ValueContainer<Link>,
		FeedbackProvider {

	private static final long serialVersionUID = -3671888082769721944L;
	private FeedbackProviderProxy feedbackProxy = new FeedbackProviderProxy();
	private FPJGUIButton fulltextURLLabel;
	private Link value;
	private ValidationRule<Link> rule;

	public LinkPanel() {

		rule = new ValidationRule<Link>() {
			@Override
			public ValidationRuleResult evaluate(Link obj) {
				return ValidationRuleResult.getSimpleTrueInstance();
			}
		};

		setLayout(new MigLayout("insets 0"));

		fulltextURLLabel = FPJGUIButtonFactory.createButton(ButtonFormats.LINK);
		fulltextURLLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (getValue().getUri().getScheme().equals("file"))
						Desktop.getDesktop()
								.open(new File(getValue().getUri()));
					else
						Desktop.getDesktop().browse(getValue().getUri());
				} catch (IOException e1) {
					feedbackProxy.feedbackBroadcasted(new Feedback(
							fulltextURLLabel, "Sorry, failed to open link.", e1
									.getMessage(), FeedbackType.ERROR));
				}
			}
		});

		add(fulltextURLLabel, "grow");

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
	public synchronized Link getValue() {
		return value;
	}

	@Override
	public synchronized void setValue(Link value) {
		this.value = value;
		fulltextURLLabel.setText(value.getName());
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
		return ValidationRuleResult.getSimpleTrueInstance();
	}

}
