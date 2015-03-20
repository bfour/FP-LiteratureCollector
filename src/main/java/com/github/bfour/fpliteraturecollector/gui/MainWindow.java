package com.github.bfour.fpliteraturecollector.gui;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.services.localization.LocalizationService;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.StaticLocationFeedbackNotificationSpawner;
import com.github.bfour.fpjgui.design.Icons;
import com.github.bfour.fpjsearch.SearchActionListener;
import com.github.bfour.fpjsearch.SearchEvent;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

/**
 * 
 * @author Florian Pollak
 *
 */
public class MainWindow extends JFrame implements SearchActionListener,
		FeedbackListener {

	private static final long serialVersionUID = 9057132203074048466L;
	private static MainWindow instance;

	private LocalizationService localizer;
	private MainPanel mainPanel;
	private StaticLocationFeedbackNotificationSpawner feedbackSpawner;

	/**
	 * Initializes the main windows.
	 */
	private MainWindow(ServiceManager serviceManager) {
		initialize(serviceManager);
	}

	/**
	 * Gets an instance of MainWindow (singleton).
	 * 
	 * @return an instance of MainWindow (subsequent calls return the same
	 *         instance)
	 */
	public static MainWindow getInstance(ServiceManager serviceManager) {
		if (instance == null) {
			instance = new MainWindow(serviceManager);
		}
		return instance;
	}

	/**
	 * Initializes the main windows.
	 */
	private void initialize(ServiceManager serviceManager) {

		feedbackSpawner = new StaticLocationFeedbackNotificationSpawner(this);

		localizer = LocalizationService.getFailsafeInstance();

		// configure container
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImages(Icons.getAppIcons());
		this.setTitle(localizer.getMessage("applicationName", "FP-LiteratureCollector"));

		mainPanel = new MainPanel(serviceManager);

		// add components
		String width = new Integer((int) (886 * GUIService.getInstance()
				.getScalingFactor())).toString();
		this.setLayout(new MigLayout("insets 10, width 500:" + width + ":",
				"[grow,fill]", "[grow,fill]"));
		this.add(mainPanel,
				"cell 0 0, growx, growy, shrinky, height 350:618:, width 618:861:");

		this.setLocationRelativeTo(getRootPane()); // center on screen

		// set behaviour
		mainPanel.addFeedbackListener(this);

		this.pack();

	}

	/**
	 * Sets the focus to a predefined component.
	 */
	public void setFocus() {
		mainPanel.requestFocusInWindow();
	}

	@Override
	public void searchPerformed(SearchEvent search) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(boolean arg0) {
		setLocationRelativeTo(null); // center on screen
		super.setVisible(arg0);
		setFocus();
	}

	@Override
	public void feedbackBroadcasted(Feedback feedback) {
		feedbackSpawner.feedbackBroadcasted(feedback);
	}

	@Override
	public void feedbackRevoked(Feedback feedback) {
		feedbackSpawner.feedbackRevoked(feedback);
	}

	@Override
	public void feedbackChanged(Feedback old, Feedback changed) {
		feedbackSpawner.feedbackChanged(old, changed);
	}

}
