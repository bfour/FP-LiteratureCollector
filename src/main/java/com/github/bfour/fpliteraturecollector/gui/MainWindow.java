package com.github.bfour.fpliteraturecollector.gui;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */


import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.services.localization.LocalizationService;
import com.github.bfour.fpjgui.FPJGUIManager;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.StaticLocationFeedbackNotificationSpawner;
import com.github.bfour.fpjgui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

/**
 * 
 * @author Florian Pollak
 *
 */
public class MainWindow extends JFrame implements FeedbackListener {

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
		String width = new Integer((int) (886 * FPJGUIManager.getInstance()
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
