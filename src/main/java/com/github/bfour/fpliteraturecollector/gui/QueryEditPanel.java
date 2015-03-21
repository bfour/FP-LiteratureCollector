package com.github.bfour.fpliteraturecollector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class QueryEditPanel extends JPanel {

	private static final long serialVersionUID = -4752243326650967601L;

	/**
	 * Create the panel.
	 */
	public QueryEditPanel(ServiceManager servMan, Query query) {
		setLayout(new MigLayout("", "[grow]", "[]"));

		FPJGUITextField textField = new FPJGUITextField();
		add(textField, "flowx,cell 0 0,growx");

		JButton btnNewButton = new JButton("New button");
		add(btnNewButton, "cell 0 0");

		JButton btnNewButton_1 = new JButton("New button");
		add(btnNewButton_1, "cell 0 0");

		final AtomicRequestPanel atomReqPanel = new AtomicRequestPanel(servMan);

		final AtomicRequestBrowsePanel atomReqBrowsePanel = new AtomicRequestBrowsePanel(
				servMan, query);

		atomReqBrowsePanel.addCreateAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				atomReqPanel.createNew(atomReqBrowsePanel);
			}
		});

		atomReqBrowsePanel.addEditAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				atomReqPanel.edit();
			}
		});

		// automatically set AtomicRequest in edit/view panel on selection
		// change
		atomReqBrowsePanel.subscribeEntitySelectionChangeSubscriber(atomReqPanel);

	}

}
