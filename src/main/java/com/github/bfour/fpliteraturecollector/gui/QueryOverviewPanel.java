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
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.gui.components.ScrollableJPanel;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class QueryOverviewPanel extends JXPanel implements FeedbackProvider,
		ListLikeValueContainer<Query> {

	private static final long serialVersionUID = 5529685995539560855L;

	private ServiceManager servMan;
	private FeedbackProviderProxy feedbackProxy;
	private BiMap<QueryPanel, Query> componentQueryMap;

	private JComponent container;
	private JPanel createPanel;
	private JPanel crawlingPanel;
	private JPanel queuePanel;

	public QueryOverviewPanel(final ServiceManager servMan) {

		this.servMan = servMan;
		feedbackProxy = new FeedbackProviderProxy();
		componentQueryMap = HashBiMap.create();

		setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));

		container = new ScrollableJPanel();
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

		// bottom toolbar
		PlainToolbar toolbar = new PlainToolbar(true);
		add(toolbar, "cell 0 1, growx");

		JButton createButton = new JButton("Create new query",
				Icons.ADD_24.getIcon());
		createButton.setIconTextGap(6);
		createButton.setMargin(new Insets(4, 8, 4, 8));
		toolbar.add(createButton);

		JButton stopButton = new JButton("Stop", Icons.STOP_24.getIcon());
		stopButton.setIconTextGap(6);
		stopButton.setMargin(new Insets(4, 8, 4, 8));
		toolbar.add(stopButton);

		JButton playButton = new JButton("Crawl", Icons.PLAY_24.getIcon());
		playButton.setIconTextGap(6);
		playButton.setMargin(new Insets(4, 8, 4, 8));
		toolbar.add(playButton);

		JButton rerunButton = new JButton("Rerun", Icons.RERUN_24.getIcon());
		rerunButton.setIconTextGap(6);
		rerunButton.setMargin(new Insets(4, 8, 4, 8));
		toolbar.add(rerunButton);

		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNew();
			}
		});

		// ==== loader ====
		EntityLoader<Query> loader = new EntityLoader<Query>() {
			@Override
			public List<Query> get() {
				List<Query> list = new ArrayList<>();
				try {
					list = servMan.getQueryService().getAll();
				} catch (ServiceException e) {
					feedbackProxy.fireFeedback(new Feedback(
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

	private synchronized void createNew() {
		QueryEditPanel editPanel = new QueryEditPanel(servMan, null);
		PanelDecorator.decorateWithDropShadow(editPanel);
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
		QueryPanel queryPanel = new QueryPanel(query);
		PanelDecorator.decorateWithDropShadow(queryPanel);
		queuePanel.add(queryPanel, "cell 0 " + query.getQueuePosition()
				+ ", wrap, growx");
		componentQueryMap.put(queryPanel, query);
		revalidate();
		repaint();
	}

	@Override
	public synchronized boolean containsEntry(Query query) {
		return componentQueryMap.containsValue(query);
	}

	@Override
	public synchronized void deleteEntry(Query arg0) {
		Component panel = componentQueryMap.inverse().get(arg0);
		remove(panel);
		componentQueryMap.remove(panel);
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
	public synchronized void updateEntry(Query arg0) {
		// TODO
		// check if queue position has changed
	}

}
