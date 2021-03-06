/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.DefaultListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpjgui.abstraction.valueContainer.ListLikeValueContainer;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpjgui.components.ScrollableJXPanel;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpjgui.layout.Orientation;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.gui.design.Colors;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker.FinishListener;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlExecutor;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class QueryOverviewPanel extends JXPanel implements FeedbackProvider,
		FeedbackListener, ListLikeValueContainer<Query> {

	private static final long serialVersionUID = 5529685995539560855L;

	private ServiceManager servMan;
	private FeedbackProviderProxy feedbackProxy;
	private BiMap<QueryPanel, Query> componentQueryMap;

	private JComponent container;
	private JPanel createPanel;
	private JPanel crawlingPanel;
	private JPanel queuePanel;
	private JPanel finishedPanel;
	private JPanel idlePanel;

	public QueryOverviewPanel(final ServiceManager servMan) {

		this.servMan = servMan;
		feedbackProxy = new FeedbackProviderProxy();
		componentQueryMap = HashBiMap.create();

		setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));

		container = new ScrollableJXPanel();
		container.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		JScrollPane wrapper = new JScrollPane(container);
		wrapper.setBorder(null);
		add(wrapper, "cell 0 0,grow");

		createPanel = new JPanel();
		createPanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		container.add(createPanel, "growx, wrap");

		crawlingPanel = new JPanel();
		crawlingPanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		container.add(crawlingPanel, "growx, wrap");

		queuePanel = new JPanel();
		queuePanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		container.add(queuePanel, "growx, wrap");

		idlePanel = new JPanel();
		idlePanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		container.add(idlePanel, "growx, wrap");

		finishedPanel = new JPanel();
		finishedPanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		container.add(finishedPanel, "growx, wrap");

		// create interface
		FPJGUIButton createButton = FPJGUIButtonFactory
				.createButton(ButtonFormats.LINK);
		createButton.setText("Create a new query");
		createButton.setIcon(Icons.ADD_16.getIcon());
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNew();
			}
		});
		createPanel.add(createButton, "alignx center, wrap");

		// bottom toolbar
		PlainToolbar toolbar = new PlainToolbar(Orientation.CENTERED);
		add(toolbar, "cell 0 1, growx");

		final JButton stopButton = new JButton("Stop", Icons.STOP_24.getIcon());
		stopButton.setIconTextGap(6);
		stopButton.setMargin(new Insets(4, 8, 4, 8));
		stopButton.setEnabled(false);
		toolbar.add(stopButton);

		final JButton playButton = new JButton("Crawl", Icons.PLAY_24.getIcon());
		playButton.setIconTextGap(6);
		playButton.setMargin(new Insets(4, 8, 4, 8));
		toolbar.add(playButton);

		final JButton rerunButton = new JButton("Rerun",
				Icons.RERUN_24.getIcon());
		rerunButton.setIconTextGap(6);
		rerunButton.setMargin(new Insets(4, 8, 4, 8));
		toolbar.add(rerunButton);

		// ==== logic ====
		final CrawlExecutor exec = CrawlExecutor.getInstance(servMan);

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exec.abort();
			}
		});

		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playButton.setEnabled(false);
				stopButton.setEnabled(false);
				boolean started = exec.start();
				if (started) {
					stopButton.setEnabled(true);
				} else {
					playButton.setEnabled(true);
				}
			}
		});

		rerunButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exec.rerunAll();
			}
		});

		// listen to crawler executer
		exec.registerFinishListener(new FinishListener() {
			@Override
			public void receiveFinished() {
				rerunButton.setEnabled(true);
				playButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		});

		exec.addFeedbackListener(this);

		exec.initialize();

		// ==== loader ====
		EntityLoader<Query> loader = new EntityLoader<Query>() {
			@Override
			public List<Query> get() {
				List<Query> list = new ArrayList<>();
				try {
					list = servMan.getQueryService().getAll();
				} catch (ServiceException e) {
					feedbackProxy.feedbackBroadcasted(new Feedback(
							QueryOverviewPanel.this, e.getMessage(),
							FeedbackType.ERROR));
				}
				return list;
			}
		};

		// hook up table with change event system
		DefaultListLikeChangeListener<Query> changeListener = new DefaultListLikeChangeListener<>(
				this, new EntityFilterPipeline<Query>());
		ChangeHandler.getInstance(Query.class).addEventListener(changeListener);

		// load initial data
		for (Query q : loader.get())
			addEntry(q);

	}

	public synchronized void createNew() {
		final QueryEditPanel editPanel = new QueryEditPanel(servMan, null);
		PanelDecorator.decorateWithDropShadow(editPanel,
				Colors.CREATE_QUERY_DROPSHADOW.getColor());
		createPanel.add(editPanel, "growx, wrap");
		ActionListener closeProxy = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPanel.remove(editPanel);
				revalidate();
				repaint();
			}
		};
		editPanel.addPostDiscardChangesListener(closeProxy);
		editPanel.addPostSaveListener(closeProxy);
		revalidate();
		repaint();
	}

	public synchronized void edit(Query query) {

		final QueryEditPanel editPanel = new QueryEditPanel(servMan, query);
		PanelDecorator.decorateWithDropShadow(editPanel,
				Colors.EDIT_QUERY_DROPSHADOW.getColor());
		ActionListener saveProxy = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPanel.remove(editPanel);
				revalidate();
				repaint();
			}
		};
		ActionListener discardProxy = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPanel.remove(editPanel);
				addEntry(query);
				revalidate();
				repaint();
			}
		};
		editPanel.addPostSaveListener(saveProxy);
		editPanel.addPostDiscardChangesListener(discardProxy);

		deleteEntry(query);
		createPanel.add(editPanel, "growx, wrap");

		revalidate();
		repaint();

	}

	@Override
	public void addFeedbackListener(FeedbackListener arg0) {
		feedbackProxy.addFeedbackListener(arg0);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener arg0) {
		feedbackProxy.removeFeedbackListener(arg0);
	}

	@Override
	public synchronized void addEntries(Collection<? extends Query> queries) {
		for (Query q : queries)
			addEntry(q);
	}

	@Override
	public synchronized void addEntry(Query query) {
		QueryPanel queryPanel = new QueryPanel(servMan, query, this);
		queryPanel.addFeedbackListener(feedbackProxy);
		int position = (query.getQueuePosition() == null ? 0 : query
				.getQueuePosition());
		if (query.getStatus() == QueryStatus.CRAWLING) {
			crawlingPanel.add(queryPanel, "cell 0 " + position
					+ ", wrap, growx");
		} else if (query.getStatus() == QueryStatus.QUEUED) {
			queuePanel.add(queryPanel, "cell 0 " + position + ", wrap, growx");
		} else if (query.getStatus() == QueryStatus.FINISHED
				|| query.getStatus() == QueryStatus.FINISHED_WITH_ERROR) {
			finishedPanel.add(queryPanel, "growx, wrap");
		} else if (query.getStatus() == QueryStatus.IDLE) {
			idlePanel.add(queryPanel, "growx, wrap");
		}
		updateBorderColor(queryPanel, query);
		componentQueryMap.put(queryPanel, query);
		revalidate();
		repaint();
	}

	@Override
	public synchronized boolean containsEntry(Query query) {
		return componentQueryMap.containsValue(query);
	}

	@Override
	public synchronized void deleteEntry(Query query) {
		QueryPanel panel = componentQueryMap.inverse().get(query);
		createPanel.remove(panel);
		crawlingPanel.remove(panel);
		queuePanel.remove(panel);
		finishedPanel.remove(panel);
		idlePanel.remove(panel);
		componentQueryMap.remove(panel);
		componentQueryMap.inverse().remove(query);
		revalidate();
		repaint();
	}

	@Override
	public synchronized List<Query> getEntries() {
		return new ArrayList<>(componentQueryMap.values());
	}

	@Override
	public synchronized boolean isEmpty() {
		return componentQueryMap.isEmpty();
	}

	@Override
	public synchronized void setEntries(List<Query> arg0) {
		// remove all
		for (Component comp : componentQueryMap.keySet())
			remove(comp);
		componentQueryMap.clear();
		// add all
		for (Query q : arg0)
			addEntry(q);
	}

	@Override
	public synchronized void updateEntry(Query oldQuery, Query newQuery) {
		deleteEntry(oldQuery);
		addEntry(newQuery);
	}

	private void updateBorderColor(QueryPanel queryPanel, Query query) {

		if (query.getStatus() == QueryStatus.CRAWLING) {
			PanelDecorator.decorateWithDropShadow(queryPanel,
					Colors.QUERY_CRAWLING_DROPSHADOW.getColor());
		} else if (query.getStatus() == QueryStatus.QUEUED) {
			PanelDecorator.decorateWithDropShadow(queryPanel,
					Colors.QUERY_QUEUED_DROPSHADOW.getColor());
		} else if (query.getStatus() == QueryStatus.FINISHED
				|| query.getStatus() == QueryStatus.FINISHED_WITH_ERROR) {
			PanelDecorator.decorateWithDropShadow(queryPanel);
		} else if (query.getStatus() == QueryStatus.IDLE) {
			PanelDecorator.decorateWithDropShadow(queryPanel);
		}

	}

	@Override
	public void feedbackBroadcasted(Feedback arg0) {
		feedbackProxy.feedbackBroadcasted(arg0);
	}

	@Override
	public void feedbackChanged(Feedback arg0, Feedback arg1) {
		feedbackProxy.feedbackChanged(arg0, arg1);
	}

	@Override
	public void feedbackRevoked(Feedback arg0) {
		feedbackProxy.feedbackRevoked(arg0);
	}

	@Override
	public void deleteAllEntries() {
		// TODO Auto-generated method stub

	}

	@Override
	public Long getEntryCount() {
		// TODO Auto-generated method stub
		return (long) 0;
	}
}
