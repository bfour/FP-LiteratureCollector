package com.github.bfour.fpliteraturecollector.gui.tags;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import org.apache.commons.beanutils.BeanUtils;

import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.DefaultListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable.FPJGUITableFieldGetter;
import com.github.bfour.fpjgui.components.table.FPJGUITableColumn;
import com.github.bfour.fpjgui.util.DefaultActionInterfacingHandler;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.TagService;

public class TagsBrowsePanel extends EntityBrowsePanel<Tag> implements
		FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public TagsBrowsePanel(final ServiceManager servMan) {

		// show default buttons for CRUD options
		setDeleteEntityEnabled(true);
		setEditEntityEnabled(true);
		setCreateEntityEnabled(true);

		// ==== columns ====
		FPJGUITableColumn<Tag> nameColumn = new FPJGUITableColumn<Tag>("Name",
				new FPJGUITableFieldGetter<Tag>() {
					@Override
					public String get(Tag item) {
						return item.getName();
					}
				}, true, 30, 30, "name", false);
		getTable().addColumn(nameColumn);

		this.table.setPreferredColumnWidth(nameColumn, 200);

		this.table.setMinimumColumnWidth(nameColumn, 100);

		// ==== loader ====
		this.loader = new EntityLoader<Tag>() {
			@Override
			public List<Tag> get() {
				List<Tag> list = new ArrayList<>();
				try {
					list = servMan.getTagService().getAll();
				} catch (ServiceException e) {
					feedbackProxy.feedbackBroadcasted(new Feedback(
							TagsBrowsePanel.this,
							"Sorry, failed to get tags from TagService.",
							FeedbackType.ERROR));
				}
				return list;
			}
		};

		// hook up table with change event system
		DefaultListLikeChangeListener<Tag> changeListener = new DefaultListLikeChangeListener<Tag>(
				getTable(), new EntityFilterPipeline<Tag>());
		ChangeHandler.getInstance(Tag.class).addEventListener(changeListener);

		// load initial data
		load();

		// set button actions
		ActionListener deleteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TagService custServ = servMan.getTagService();
				Component source;
				// TODO (high) clean up mess (maybe set back to delete button as
				// source only)
				if (e.getSource() != null && e.getSource() instanceof Component) {
					source = (Component) e.getSource();
					if (source instanceof JMenuItem) {
						try {
							source = (Component) BeanUtils.cloneBean(source);
						} catch (IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException e1) {
							source = deleteButton;
						}
					}
				} else {
					source = deleteButton;
				}
				DefaultActionInterfacingHandler.getInstance()
						.requestDeleteFromList(source, feedbackProxy,
								table.getSelectedItem(), custServ);
			}
		};
		addDeleteAction(deleteListener);

	}
}
