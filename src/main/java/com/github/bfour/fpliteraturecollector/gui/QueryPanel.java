package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class QueryPanel extends JXPanel {

	private static final long serialVersionUID = -4594624554195026146L;

	private Query query;
	private FPJGUILabel<String> nameLabel;
	private FPJGUILabel<ImageIcon> statusIconLabel;
	private FPJGUILabel<String> statusLabel;

	/**
	 * Create the panel.
	 */
	public QueryPanel(final ServiceManager servMan, Query q) {
		setLayout(new MigLayout("insets 0", "[][grow][]", "[]"));

		statusIconLabel = new FPJGUILabel<ImageIcon>();
		add(statusIconLabel, "cell 0 0");
		statusLabel = new FPJGUILabel<String>();
		add(statusLabel, "cell 0 0");

		nameLabel = new FPJGUILabel<String>();
		add(nameLabel, "cell 1 0, center");

		// Component horizontalGlue = Box.createHorizontalGlue();
		// add(horizontalGlue, "cell 2 0");

		// toolbar
		PlainToolbar toolbar = new PlainToolbar(true);
		add(toolbar, "cell 2 0");

		JButton stopButton = new JButton("Stop", Icons.STOP_20.getIcon());
		stopButton.setIconTextGap(4);
		stopButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(stopButton);

		JButton editButton = new JButton("Edit", Icons.EDIT_20.getIcon());
		editButton.setIconTextGap(4);
		editButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(editButton);

		JButton deleteButton = new JButton("Delete", Icons.DELETE_20.getIcon());
		deleteButton.setIconTextGap(4);
		deleteButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(deleteButton);

		JButton queueUpButton = new JButton("Queue up",
				Icons.QUEUE_UP_20.getIcon());
		queueUpButton.setIconTextGap(4);
		queueUpButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(queueUpButton);

		JButton queueDownButton = new JButton("Queue down",
				Icons.QUEUE_DOWN_20.getIcon());
		queueDownButton.setIconTextGap(4);
		queueDownButton.setMargin(new Insets(1, 4, 2, 4));
		toolbar.add(queueDownButton);

		// logic
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					servMan.getQueryService().delete(query);
				} catch (ServiceException e1) {
					// TODO Auto-generated catch block
				}
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
		this.query = q;
		nameLabel.setValue(q.getName());
		statusLabel.setValue(q.getStatus().getTellingName());
		statusIconLabel.setValue(q.getStatus().getIcon());
		repaint();
	}

}
