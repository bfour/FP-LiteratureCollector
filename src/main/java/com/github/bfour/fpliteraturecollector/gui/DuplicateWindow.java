package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.events.BatchCreateEvent;
import com.github.bfour.fpjcommons.events.BatchDeleteEvent;
import com.github.bfour.fpjcommons.events.BatchUpdateEvent;
import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.events.ChangeListener;
import com.github.bfour.fpjcommons.events.CreateEvent;
import com.github.bfour.fpjcommons.events.DeleteEvent;
import com.github.bfour.fpjcommons.events.NonBatchChangeListener;
import com.github.bfour.fpjcommons.events.UpdateEvent;
import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.components.FPJGUIWindow;
import com.github.bfour.fpjgui.components.PlainToolbar;
import com.github.bfour.fpjgui.design.PanelDecorator;
import com.github.bfour.fpjgui.layout.Orientation;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.gui.literature.LiteraturePanel;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class DuplicateWindow extends FPJGUIWindow {

	private static final long serialVersionUID = 7959626307208135442L;
	private static DuplicateWindow instance;
	private ServiceManager servMan;
	private LiteraturePanel litPanelA;
	private LiteraturePanel litPanelB;
	private Literature currentLitA;
	private Literature currentLitB;

	private DuplicateWindow(ServiceManager servMan) {

		super("Manage Duplicates", 861, 681);

		this.servMan = servMan;

		PlainToolbar toolbar = new PlainToolbar(Orientation.CENTERED);
		getContentPane().add(toolbar, "growx, wrap");

		JButton autoDeleteButton = new JButton("Auto-delete duplicates",
				Icons.PERSON_GROUP.getIcon());
		autoDeleteButton.setIconTextGap(6);
		autoDeleteButton.setMargin(new Insets(4, 16, 4, 16));
		toolbar.add(autoDeleteButton);

		autoDeleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Feedback progressFeedback = new Feedback(
							DuplicateWindow.this, "Auto-deleting duplicates.",
							FeedbackType.PROGRESS);
					feedbackBroadcasted(progressFeedback);
					List<Literature> deleted = servMan.getLiteratureService()
							.autoDeleteDuplicates();
					feedbackRevoked(progressFeedback);
					feedbackBroadcasted(new Feedback(DuplicateWindow.this,
							"Deleted " + deleted.size()
									+ " literature entries.",
							FeedbackType.SUCCESS));
				} catch (ServiceException e1) {
					feedbackBroadcasted(new Feedback(DuplicateWindow.this,
							"Sorry, failed to auto-delete duplicates.", e1
									.getMessage(), FeedbackType.ERROR));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		litPanelA = new LiteraturePanel(servMan);
		PanelDecorator.decorateWithDropShadow(litPanelA);
		add(litPanelA, "w 50%, h :16cm:16cm");

		litPanelB = new LiteraturePanel(servMan);
		PanelDecorator.decorateWithDropShadow(litPanelB);
		add(litPanelB, "w 50%, h :16cm:16cm");

		ChangeHandler.getInstance(Literature.class).addEventListener(
				new NonBatchChangeListener<Literature>() {

					@Override
					public void handle(UpdateEvent<Literature> ev) {
						if (ev.getOldObject().equals(currentLitA)
								|| ev.getOldObject().equals(currentLitB))
							refreshProbableDuplicates();
					}

					@Override
					public void handle(DeleteEvent<Literature> ev) {
						if (ev.getDeletedObject().equals(currentLitA)
								|| ev.getDeletedObject().equals(currentLitB))
							refreshProbableDuplicates();
					}

					@Override
					public void handle(CreateEvent<Literature> ev) {
					}
				});

		refreshProbableDuplicates();

	}

	public static DuplicateWindow getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new DuplicateWindow(servMan);
		return instance;
	}

	private void refreshProbableDuplicates() {
		try {
			List<Tuple<Literature, Literature>> dups = servMan
					.getLiteratureService().getPossibleDuplicate();
			this.currentLitA = null;
			this.currentLitB = null;
			if (!dups.isEmpty()) {
				this.currentLitA = dups.get(0).getA();
				this.currentLitB = dups.get(0).getB();
			}
			litPanelA.setEntity(DuplicateWindow.this, this.currentLitA);
			litPanelB.setEntity(DuplicateWindow.this, this.currentLitB);
		} catch (ServiceException e) {
			e.printStackTrace();
			feedbackBroadcasted(new Feedback(DuplicateWindow.this,
					"Sorry, failed to get probable duplicates.",
					e.getMessage(), FeedbackType.ERROR));
		}
	}

}
