package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class MainPanel extends JPanel implements FeedbackProvider,
		FeedbackListener {

	private static final long serialVersionUID = 1144467669251836304L;
	private FeedbackProviderProxy feedbackProxy;

	/**
	 * Create the panel.
	 * 
	 * @param serviceManager
	 */
	public MainPanel(final ServiceManager serviceManager) {

		this.feedbackProxy = new FeedbackProviderProxy();

		setLayout(new MigLayout("insets 0", "[grow]", "[]20[grow]"));

		// toolbar
		PlainToolbar toolbar = new PlainToolbar(true);

		JButton browsAuthorsButtonButton = new JButton("Browse all authors",
				Icons.PERSON_GROUP.getIcon());
		browsAuthorsButtonButton.setIconTextGap(6);
		browsAuthorsButtonButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(browsAuthorsButtonButton);
		
		JButton browseLiteratureButtonButton = new JButton("Browse all literature",
				Icons.BOOKS.getIcon());
		browseLiteratureButtonButton.setIconTextGap(6);
		browseLiteratureButtonButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(browseLiteratureButtonButton);		

		add(toolbar, "cell 0 0, grow");

		// content
		QueryOverviewPanel queryOverviewPanel = new QueryOverviewPanel(
				serviceManager);
		add(queryOverviewPanel, "cell 0 1,grow");
		queryOverviewPanel.addFeedbackListener(this);

		// logic
		browsAuthorsButtonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});

		browseLiteratureButtonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});

	}

	@Override
	public void addFeedbackListener(FeedbackListener listener) {
		feedbackProxy.addFeedbackListener(listener);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener listener) {
		feedbackProxy.addFeedbackListener(listener);
	}

	@Override
	public void feedbackBroadcasted(Feedback arg0) {
		feedbackProxy.fireFeedback(arg0);
	}

	@Override
	public void feedbackChanged(Feedback arg0, Feedback arg1) {
		feedbackProxy.changeFeedback(arg0, arg1);
	}

	@Override
	public void feedbackRevoked(Feedback arg0) {
		feedbackProxy.revokeFeedback(arg0);
	}

}