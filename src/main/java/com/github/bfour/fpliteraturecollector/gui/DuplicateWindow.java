package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;

import com.github.bfour.fpjcommons.events.ChangeHandler;
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
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
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
	private List<Tuple<Literature, Literature>> duplicates;

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
		add(litPanelA, "w 50%, growy, h 16cm::");

		litPanelB = new LiteraturePanel(servMan);
		PanelDecorator.decorateWithDropShadow(litPanelB);
		add(litPanelB, "w 50%, growy, h 16cm::, wrap");

		// ChangeHandler.getInstance(Literature.class).addEventListener(
		// new NonBatchChangeListener<Literature>() {
		//
		// @Override
		// public void handle(UpdateEvent<Literature> ev) {
		// if (ev.getOldObject().equals(currentLitA)
		// || ev.getOldObject().equals(currentLitB))
		// refreshProbableDuplicates();
		// }
		//
		// @Override
		// public void handle(DeleteEvent<Literature> ev) {
		// if (ev.getDeletedObject().equals(currentLitA)
		// || ev.getDeletedObject().equals(currentLitB))
		// refreshProbableDuplicates();
		// }
		//
		// @Override
		// public void handle(CreateEvent<Literature> ev) {
		// }
		// });

		refreshProbableDuplicates();

		JButton mergeIntoAButton = new JButton("Merge into this entry",
				Icons.MERGE.getIcon());
		mergeIntoAButton.setIconTextGap(6);
		mergeIntoAButton.setMargin(new Insets(4, 16, 4, 16));
		add(mergeIntoAButton, "cell 0 2");

		JButton mergeIntoBButton = new JButton("Merge into this entry",
				Icons.MERGE.getIcon());
		mergeIntoBButton.setIconTextGap(6);
		mergeIntoBButton.setMargin(new Insets(4, 16, 4, 16));
		add(mergeIntoBButton, "cell 1 2");

		mergeIntoAButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				merge(mergeIntoAButton, currentLitB, currentLitA);
			}
		});

		mergeIntoBButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				merge(mergeIntoBButton, currentLitA, currentLitB);
			}
		});

	}

	private void merge(Component source, Literature from, Literature into) {

		Feedback statusFeedback = new Feedback(source, "Merging.",
				FeedbackType.PROGRESS);

		try {
			feedbackBroadcasted(statusFeedback);
			servMan.getLiteratureService().mergeInto(from, into);
			servMan.getProtocolEntryService().create(
					new ProtocolEntry("manually merged " + from.getID()
							+ " into " + into.getID()));
			feedbackRevoked(statusFeedback);
			feedbackBroadcasted(new Feedback(source,
					"<html>Manually merged <br/>" + from + " into <br/>" + into
							+ ".</html>", FeedbackType.SUCCESS));
		} catch (ServiceException e1) {
			e1.printStackTrace();
			feedbackRevoked(statusFeedback);
			feedbackBroadcasted(new Feedback(source, "Sorry, merging failed.",
					e1.getMessage(), FeedbackType.ERROR));
		}

		try {
			setNextDuplicate();
		} catch (ServiceException e) {
			e.printStackTrace();
			feedbackBroadcasted(new Feedback(source,
					"Sorry, failed to set next duplicate.", e.getMessage(),
					FeedbackType.ERROR));
		}

	}

	public static DuplicateWindow getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new DuplicateWindow(servMan);
		return instance;
	}

	private void setNextDuplicate() throws ServiceException {
		if (duplicates.isEmpty()) {
			this.currentLitA = null;
			this.currentLitB = null;
			litPanelA.setEntity(DuplicateWindow.this, this.currentLitA);
			litPanelB.setEntity(DuplicateWindow.this, this.currentLitB);
		} else if (!servMan.getLiteratureService().exists(
				duplicates.get(0).getA())
				|| !servMan.getLiteratureService().exists(
						duplicates.get(0).getB())) {
			// skip this entry
			duplicates.remove(0);
			setNextDuplicate();
		} else {
			this.currentLitA = duplicates.get(0).getA();
			this.currentLitB = duplicates.get(0).getB();
			duplicates.remove(0);
		}
	}

	private void refreshProbableDuplicates() {
		try {
			duplicates = servMan.getLiteratureService().getPossibleDuplicates();
			this.currentLitA = null;
			this.currentLitB = null;
			setNextDuplicate();
		} catch (ServiceException e) {
			e.printStackTrace();
			feedbackBroadcasted(new Feedback(DuplicateWindow.this,
					"Sorry, failed to get probable duplicates.",
					e.getMessage(), FeedbackType.ERROR));
		}
	}

}
