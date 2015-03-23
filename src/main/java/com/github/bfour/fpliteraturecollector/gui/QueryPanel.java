package com.github.bfour.fpliteraturecollector.gui;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class QueryPanel extends JXPanel {

	private static final long serialVersionUID = -4594624554195026146L;

	private FPJGUILabel<String> nameLabel;
	private FPJGUILabel<ImageIcon> statusIconLabel;
	private FPJGUILabel<String> statusLabel;

	/**
	 * Create the panel.
	 */
	public QueryPanel(Query q) {
		setLayout(new MigLayout("insets 0", "[][grow][]", "[]"));

		statusIconLabel = new FPJGUILabel<ImageIcon>();
		add(statusIconLabel, "cell 0 0");
		statusLabel = new FPJGUILabel<String>();
		add(statusLabel, "cell 0 0");
		
		nameLabel = new FPJGUILabel<String>();
		add(nameLabel, "cell 1 0, center");

//		Component horizontalGlue = Box.createHorizontalGlue();
//		add(horizontalGlue, "cell 2 0");

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

		setEntity(q);

	}

	public void setEntity(Query q) {
		nameLabel.setValue(q.getName());
		statusLabel.setValue(q.getStatus().getTellingName());
		statusIconLabel.setValue(q.getStatus().getIcon());
	}

}
