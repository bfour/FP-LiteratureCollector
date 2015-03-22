package com.github.bfour.fpliteraturecollector.gui;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpliteraturecollector.domain.Query;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JButton;

public class QueryPanel extends JXPanel {

	private static final long serialVersionUID = -4594624554195026146L;
	
	private FPJGUILabel<String> nameLabel;

	/**
	 * Create the panel.
	 */
	public QueryPanel(Query q) {
		setLayout(new MigLayout("", "[][grow][][]", "[]"));

		nameLabel = new FPJGUILabel<String>();
		add(nameLabel, "cell 0 0");

		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue, "cell 1 0");

		JLabel lblStatus = new JLabel("Status");
		add(lblStatus, "cell 2 0");

		JButton btnAbort = new JButton("Abort");
		add(btnAbort, "flowx,cell 3 0");

		JButton btnEdit = new JButton("Edit");
		add(btnEdit, "cell 3 0");

		JButton btnQueueUp = new JButton("Queue up");
		add(btnQueueUp, "cell 3 0");

		JButton btnQueueDown = new JButton("Queue down");
		add(btnQueueDown, "cell 3 0");
		
		setEntity(q);

	}

	public void setEntity(Query q) {
		nameLabel.setValue(q.getName());
		
	}

}
