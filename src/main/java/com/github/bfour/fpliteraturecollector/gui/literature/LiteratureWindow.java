package com.github.bfour.fpliteraturecollector.gui.literature;

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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.StaticLocationFeedbackNotificationSpawner;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteratureWindow extends JFrame implements FeedbackListener {

	private static final long serialVersionUID = 3394920842182483985L;
	private JPanel contentPane;
	private StaticLocationFeedbackNotificationSpawner notifSpawner;

	public LiteratureWindow(ServiceManager servMan) {
		this(servMan, new EntityFilterPipeline<Literature>());
	}

	public LiteratureWindow(ServiceManager servMan,
			EntityFilterPipeline<Literature> filters) {

		setTitle("Literature");
		setIconImages(Icons.getAppIcons());
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 881, 611);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		notifSpawner = new StaticLocationFeedbackNotificationSpawner(this);

		contentPane.setLayout(new MigLayout("", "[grow][]", "[grow]"));

		final LiteratureBrowsePanel litLookPanel = new LiteratureBrowsePanel(
				servMan);
		PanelDecorator.decorateWithDropShadow(litLookPanel);
		litLookPanel.addFeedbackListener(this);
		contentPane.add(litLookPanel, "cell 0 0, grow, w 416:618");

		final LiteraturePanel litPanel = new LiteraturePanel(servMan);
		PanelDecorator.decorateWithDropShadow(litPanel);
		litPanel.setCRUDButtonsVisible(false);
		litPanel.addFeedbackListener(this);
		contentPane.add(litPanel, "cell 1 0, growy, w 4cm:30%:30%");

		litLookPanel.addCreateAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				litPanel.createNew(litLookPanel);
			}
		});

		litLookPanel.addEditAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Literature selectedLiterature =
				// custLookPanel.getTable().getSelectedItem();
				// custPanel.setEntity(selectedLiterature);
				litPanel.edit();
			}
		});

		// automatically set Literature in LiteraturePanel on selection change
		// in LiteratureLookupPanel
		litLookPanel.subscribeSelectionChangeSubscriber(litPanel);

		// pack
		// pack();

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
