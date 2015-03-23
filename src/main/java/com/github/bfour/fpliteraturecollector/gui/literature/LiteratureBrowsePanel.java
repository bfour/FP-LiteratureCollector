package com.github.bfour.fpliteraturecollector.gui.literature;

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
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteratureBrowsePanel extends EntityBrowsePanel<Literature>
		implements FeedbackProvider {

	private static final long serialVersionUID = 4500980555674670335L;

	/**
	 * Create the panel.
	 */
	public LiteratureBrowsePanel(final ServiceManager servMan) {

		// show default buttons for CRUD options
		setDeleteEntityEnabled(true);
		setEditEntityEnabled(true);
		setCreateEntityEnabled(true);

		// ==== columns ====
		FPJGUITableColumn<Literature> titleColumn = new FPJGUITableColumn<Literature>(
				"Title", new FPJGUITableFieldGetter<Literature>() {
					@Override
					public String get(Literature item) {
						return item.getTitle();
					}
				}, true, 30, 30, "title", false);
		getTable().addColumn(titleColumn);

		this.table.setPreferredColumnWidth(titleColumn, 200);

		this.table.setMinimumColumnWidth(titleColumn, 100);

		// ==== loader ====
		this.loader = new EntityLoader<Literature>() {
			@Override
			public List<Literature> get() {
				List<Literature> list = new ArrayList<>();
				try {
					list = servMan.getLiteratureService().getAll();
				} catch (ServiceException e) {
					feedbackProxy
							.fireFeedback(new Feedback(
									LiteratureBrowsePanel.this,
									"Sorry, failed to get literature from LiteratureService.",
									FeedbackType.ERROR));
				}
				return list;
			}
		};

		// hook up table with change event system
		DefaultListLikeChangeListener<Literature> changeListener = new DefaultListLikeChangeListener<Literature>(
				getTable(), new EntityFilterPipeline<Literature>());
		ChangeHandler.getInstance(Literature.class).addEventListener(
				changeListener);

		// load initial data
		load();

		// set button actions
		ActionListener deleteListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LiteratureService custServ = servMan.getLiteratureService();
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
