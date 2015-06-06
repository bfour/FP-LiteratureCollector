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


import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.GUIOption;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpjgui.util.DefaultActionInterfacingHandler;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class QueryPanel extends JXPanel implements FeedbackProvider {

	private static final long serialVersionUID = -4594624554195026146L;

	private FeedbackProviderProxy feedbackProxy;

	private Query query;
	private FPJGUILabel<String> nameLabel;
	private FPJGUILabel<ImageIcon> statusIconLabel;
	private FPJGUILabel<String> statusLabel;

	private JButton stopButton;
	private JButton editButton;
	private JButton deleteButton;
	private JButton queueUpButton;
	private JButton queueDownButton;

	/**
	 * Create the panel.
	 */
	public QueryPanel(final ServiceManager servMan, Query q) {

		feedbackProxy = new FeedbackProviderProxy();

		setLayout(new MigLayout("insets 0", "[116lp][grow][]", "[]"));

		statusIconLabel = new FPJGUILabel<ImageIcon>();
		add(statusIconLabel, "cell 0 0");
		statusLabel = new FPJGUILabel<String>();
		add(statusLabel, "cell 0 0");

		nameLabel = new FPJGUILabel<String>();
		Font nameFont = nameLabel.getFont().deriveFont(
				nameLabel.getFont().getSize() + 4f);
		nameLabel.setFont(nameFont);
		add(nameLabel, "cell 1 0, center");

		// Component horizontalGlue = Box.createHorizontalGlue();
		// add(horizontalGlue, "cell 2 0");

		// toolbar
		PlainToolbar toolbar = new PlainToolbar(true);
		add(toolbar, "cell 2 0");

		stopButton = new JButton("Stop", Icons.STOP_20.getIcon());
		stopButton.setIconTextGap(4);
		stopButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(stopButton);

		editButton = new JButton("Edit", Icons.EDIT_20.getIcon());
		editButton.setIconTextGap(4);
		editButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(editButton);

		deleteButton = new JButton("Delete", Icons.DELETE_20.getIcon());
		deleteButton.setIconTextGap(4);
		deleteButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(deleteButton);

		queueUpButton = new JButton("Queue up", Icons.QUEUE_UP_20.getIcon());
		queueUpButton.setIconTextGap(4);
		queueUpButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(queueUpButton);

		queueDownButton = new JButton("Queue down",
				Icons.QUEUE_DOWN_20.getIcon());
		queueDownButton.setIconTextGap(4);
		queueDownButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(queueDownButton);

		// logic
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				List<Tuple<GUIOption, Tuple<Callable<Void>, String>>> actionsMap = new ArrayList<>(
						3);

				actionsMap.add(new Tuple<>(new GUIOption(
						"Delete with literature", "", null),
						new Tuple<Callable<Void>, String>(new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								servMan.getQueryService().deleteCascade(query);
								return null;
							}
						}, "Sorry, failed to delete query \"" + query.getName()
								+ "\" with its associated literature.")));

				actionsMap.add(new Tuple<>(new GUIOption(
						"Delete and keep literature", "", null),
						new Tuple<Callable<Void>, String>(new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								servMan.getQueryService().delete(query);
								return null;
							}
						}, "Sorry, failed to delete query \"" + query.getName()
								+ "\".")));

				actionsMap.add(new Tuple<>(new GUIOption("Cancel", "", null),
						new Tuple<Callable<Void>, String>(new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								return null;
							}
						}, "Sorry, failed to cancel.")));

				DefaultActionInterfacingHandler
						.getInstance()
						.abstractDialogueBasedAction(
								deleteButton,
								feedbackProxy,
								"<html>This query might be associated with literature that has been found for this query. <br/>"
										+ "You can delete this query without its associated literature or remove the associated literature as well. <br/>"
										+ "Literature that is associated with other queries will not be deleted in any case.</html>",
								actionsMap);

			}
		});

		queueUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					servMan.getQueryService().queueUp(query);
				} catch (ServiceException e1) {
					// TODO Auto-generated catch block
				}
			}
		});

		queueDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					servMan.getQueryService().queueDown(query);
				} catch (ServiceException e1) {
					// TODO Auto-generated catch block
				}
			}
		});

		// set entity
		setEntity(q);

	}

	public void setEntity(Query q) {

		nameLabel.setValue(q.getName());

		if (this.query == null || this.query.getStatus() != q.getStatus()) {
			statusLabel.setValue(q.getStatus().getTellingName());
			statusIconLabel.setValue(q.getStatus().getIcon());
			if (q.getStatus() == QueryStatus.FINISHED) {
				stopButton.setEnabled(false);
				queueUpButton.setEnabled(false);
				queueDownButton.setEnabled(false);
				deleteButton.setEnabled(true);
				editButton.setEnabled(true);
			} else if (q.getStatus() == QueryStatus.IDLE) {
				stopButton.setEnabled(false);
				queueUpButton.setEnabled(false);
				queueDownButton.setEnabled(false);
				deleteButton.setEnabled(true);
				editButton.setEnabled(true);
			} else if (q.getStatus() == QueryStatus.CRAWLING) {
				stopButton.setEnabled(true);
				queueUpButton.setEnabled(false);
				queueDownButton.setEnabled(false);
				deleteButton.setEnabled(false);
				editButton.setEnabled(false);
			} else if (q.getStatus() == QueryStatus.QUEUED) {
				stopButton.setEnabled(true);
				queueUpButton.setEnabled(true);
				queueDownButton.setEnabled(true);
				deleteButton.setEnabled(false);
				editButton.setEnabled(false);
			}
		}

		repaint();

		this.query = q;

	}

	@Override
	public void addFeedbackListener(FeedbackListener arg0) {
		feedbackProxy.addFeedbackListener(arg0);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener arg0) {
		feedbackProxy.removeFeedbackListener(arg0);
	}

}
