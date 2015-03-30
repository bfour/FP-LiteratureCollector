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
import com.github.bfour.fpliteraturecollector.gui.literature.LiteratureWindow;
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

		JButton createQueryButton = new JButton("Create query",
				Icons.ADD.getIcon());
		createQueryButton.setIconTextGap(6);
		createQueryButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(createQueryButton);
		
		JButton browseAuthorsButton = new JButton("Browse authors",
				Icons.PERSON_GROUP.getIcon());
		browseAuthorsButton.setIconTextGap(6);
		browseAuthorsButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(browseAuthorsButton);

		JButton browseLiteratureButton = new JButton("Browse literature",
				Icons.BOOKS.getIcon());
		browseLiteratureButton.setIconTextGap(6);
		browseLiteratureButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(browseLiteratureButton);

		JButton duplicatesButton = new JButton("Manage duplicates",
				Icons.DUPLICATE.getIcon());
		duplicatesButton.setIconTextGap(6);
		duplicatesButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(duplicatesButton);

		JButton settingsButton = new JButton("Edit settings",
				Icons.CONFIG.getIcon());
		settingsButton.setIconTextGap(6);
		settingsButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(settingsButton);

		add(toolbar, "cell 0 0, grow");

		// content
		final QueryOverviewPanel queryOverviewPanel = new QueryOverviewPanel(
				serviceManager);
		add(queryOverviewPanel, "cell 0 1,grow");
		queryOverviewPanel.addFeedbackListener(this);

		// logic
		createQueryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				queryOverviewPanel.createNew();
			}
		});
		
		browseAuthorsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});

		browseLiteratureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LiteratureWindow.getInstance(serviceManager).setVisible(true);
			}
		});

		settingsButton.addActionListener(new ActionListener() {
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