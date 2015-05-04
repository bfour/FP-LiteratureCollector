package com.github.bfour.fpliteraturecollector.gui.tags;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.StaticLocationFeedbackNotificationSpawner;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class TagsWindow extends JFrame implements FeedbackListener {

	private static final long serialVersionUID = 3394920842182483985L;
	private static TagsWindow instance;
	private JPanel contentPane;
	private StaticLocationFeedbackNotificationSpawner notifSpawner;
	
	/**
	 * Create the frame.
	 */
	private TagsWindow(ServiceManager servMan) {

		setTitle("Tags");
		setIconImages(Icons.getAppIcons());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 618, 418);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		notifSpawner = new StaticLocationFeedbackNotificationSpawner(this);

		contentPane.setLayout(new MigLayout("", "[grow][]", "[grow]"));

		final TagsBrowsePanel tagLookPanel = new TagsBrowsePanel(servMan);
		PanelDecorator.decorateWithDropShadow(tagLookPanel);
		tagLookPanel.addFeedbackListener(this);
		contentPane.add(tagLookPanel, "cell 0 0, grow, w 116:218");

		final TagsPanel tagPanel = new TagsPanel(servMan);
		PanelDecorator.decorateWithDropShadow(tagPanel);
		tagPanel.setCRUDButtonsVisible(false);
		tagPanel.addFeedbackListener(this);
		contentPane.add(tagPanel, "cell 1 0, growy, w 186:268, h 116:");
		
		tagLookPanel.addCreateAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tagPanel.createNew(tagLookPanel);
			}
		});
		
		tagLookPanel.addEditAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Literature selectedLiterature =
				// custLookPanel.getTable().getSelectedItem();
				// custPanel.setEntity(selectedLiterature);
				tagPanel.edit();
			}
		});
		
		// automatically set Tags in TagsPanel on selection change in TagsLookupPanel
		tagLookPanel.subscribeEntitySelectionChangeSubscriber(tagPanel);

		// pack
//		pack();

	}

	public static TagsWindow getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new TagsWindow(servMan);
		return instance;
	}

	@Override
	public void setVisible(boolean arg0) {
		setLocationRelativeTo(null); // center on screen
		super.setVisible(arg0);
	}

	@Override
	public void feedbackBroadcasted(Feedback feedback) {
		notifSpawner.feedbackBroadcasted(feedback);
	}

	@Override
	public void feedbackRevoked(Feedback feedback) {
		notifSpawner.feedbackRevoked(feedback);
	}

	@Override
	public void feedbackChanged(Feedback old, Feedback changed) {
		notifSpawner.feedbackChanged(old, changed);
	}

}
