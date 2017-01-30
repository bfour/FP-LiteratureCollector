package com.github.bfour.fpliteraturecollector.gui.literature;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.jlib.commons.events.BatchCreateEvent;
import com.github.bfour.jlib.commons.events.BatchDeleteEvent;
import com.github.bfour.jlib.commons.events.BatchUpdateEvent;
import com.github.bfour.jlib.commons.events.ChangeHandler;
import com.github.bfour.jlib.commons.events.ChangeListener;
import com.github.bfour.jlib.commons.events.CreateEvent;
import com.github.bfour.jlib.commons.events.DeleteEvent;
import com.github.bfour.jlib.commons.events.UpdateEvent;
import com.github.bfour.jlib.commons.logic.LogicException;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.commons.utils.Getter;
import com.github.bfour.jlib.gui.abstraction.feedback.Feedback;
import com.github.bfour.jlib.gui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.jlib.gui.abstraction.feedback.FeedbackListener;
import com.github.bfour.jlib.gui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.jlib.gui.components.FPJGUIButton;
import com.github.bfour.jlib.gui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.jlib.gui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.jlib.gui.components.FPJGUIPopover;
import com.github.bfour.jlib.gui.components.FPJGUITextField;
import com.github.bfour.jlib.gui.components.composite.EntityConfirmableOperationPanel;
import com.github.bfour.jlib.gui.design.Icons;
import com.github.bfour.jlib.gui.design.Lengths;

public class SemanticTaggingPopover extends FPJGUIPopover implements
		FeedbackProvider {

	private static class SemanticTaggingPanel extends
			EntityConfirmableOperationPanel<Literature> {

		private static final long serialVersionUID = -5904991467344386015L;
		List<Tag> tags = new LinkedList<>();
		private ServiceManager servMan;

		public SemanticTaggingPanel(ServiceManager servMan) {

			super("Semantic Tagging", "", "Done",
					new Getter<Literature, String>() {
						@Override
						public String get(Literature input) {
							return "";
						}
					});

			this.servMan = servMan;

			// register for entity change handling
			ChangeHandler.getInstance(Literature.class).addEventListener(
					new ChangeListener<Literature>() {
						@Override
						public void handle(BatchCreateEvent<Literature> arg0) {
						}

						@Override
						public void handle(BatchDeleteEvent<Literature> arg0) {
							if (arg0.getAffectedObjects().contains(getValue()))
								getContentPanel()
										.add(new JLabel(
												"The literature entry was deleted!",
												Icons.EXCLAMATION_20.getIcon(),
												SwingConstants.LEFT), "wrap");
						}

						@Override
						public void handle(BatchUpdateEvent<Literature> arg0) {
							for (UpdateEvent<Literature> ev : arg0.getChanges())
								if (ev.getOldObject().equals(getValue())) {
									setValue(ev.getNewObject());
									return;
								}
						}

						@Override
						public void handle(CreateEvent<Literature> arg0) {
						}

						@Override
						public void handle(DeleteEvent<Literature> arg0) {
							if (arg0.getDeletedObject().equals(getValue()))
								getContentPanel()
										.add(new JLabel(
												"The literature entry was deleted!",
												Icons.EXCLAMATION_20.getIcon(),
												SwingConstants.LEFT), "wrap");
						}

						@Override
						public void handle(UpdateEvent<Literature> arg0) {
							if (arg0.getOldObject().equals(getValue())) {
								setValue(arg0.getNewObject());
								return;
							}
						}
					});

			// assemble
			getContentPanel().setLayout(
					new MigLayout("insets 0", "[grow]", "[][]"));

		}

		@Override
		public void setValue(Literature entity) {

			super.setValue(entity);

			getContentPanel().removeAll();

			try {

				SemanticValidator validator = SemanticValidator
						.getInstance(servMan);
				validator.print();

				if (validator.isComplete(entity))
					getContentPanel().add(
							new JLabel("tagging is complete",
									Icons.GREENTICK_16.getIcon(),
									SwingConstants.LEFT), "wrap");
				else
					getContentPanel().add(
							new JLabel("tagging is incomplete",
									Icons.EXCLAMATION_20.getIcon(),
									SwingConstants.LEFT), "wrap");
				if (validator.isValid(entity))
					getContentPanel().add(
							new JLabel("tagging is valid",
									Icons.GREENTICK_16.getIcon(),
									SwingConstants.LEFT), "wrap");
				else
					getContentPanel().add(
							new JLabel("tagging is invalid",
									Icons.EXCLAMATION_20.getIcon(),
									SwingConstants.LEFT), "wrap");

				// buttons
				if (!validator.isTopicComplete("Year", entity)) {
					FPJGUITextField yearField = new FPJGUITextField();
					FPJGUIButton yearButton = FPJGUIButtonFactory
							.createButton(ButtonFormats.DEFAULT,
									Lengths.LARGE_BUTTON_HEIGHT.getLength(),
									"Set year");
					getContentPanel().add(yearField, "grow x");
					getContentPanel().add(yearButton, "wrap");
					yearButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								int year = Integer.parseInt(yearField
										.getValue());
								servMan.getLiteratureService().update(
										entity,
										new LiteratureBuilder(entity).setYear(
												year).getObject());
								getFeedbackProxy().feedbackBroadcasted(
										new Feedback(yearField, "Year set.",
												FeedbackType.SUCCESS));
							} catch (NumberFormatException numFormE) {
								numFormE.printStackTrace();
								getFeedbackProxy().feedbackBroadcasted(
										new Feedback(yearField,
												"Please enter a valid year.",
												numFormE.getMessage(),
												FeedbackType.ERROR));
							} catch (ServiceException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								getFeedbackProxy()
										.feedbackBroadcasted(
												new Feedback(
														yearField,
														"Failed to update literature entry.",
														e1.getMessage(),
														FeedbackType.ERROR));
							}
						}
					});
				}

			} catch (LogicException e) {
				e.printStackTrace();
				getFeedbackProxy().feedbackBroadcasted(
						new Feedback(SemanticTaggingPanel.this,
								"Semantic evaluation failed.", e.getMessage(),
								FeedbackType.ERROR));
			}

			revalidate();
			repaint();

		}

		/**
		 * Checks whether the given list of tags contains one or more tags with
		 * the given prefix in their name.
		 * 
		 * @param tags
		 * @param prefix
		 * @return
		 */
		private boolean contains(List<Tag> tags, String prefix) {
			for (Tag tag : tags)
				if (tag.getName().startsWith(prefix))
					return true;
			return false;
		}

	}

	private static final long serialVersionUID = -1267735079515329925L;
	private SemanticTaggingPanel taggingPanel;

	public SemanticTaggingPopover(ServiceManager servMan,
			Getter<Void, Literature> entitySelectionGetter) {

		taggingPanel = new SemanticTaggingPanel(servMan);

		taggingPanel.addCancelListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hidePopup();
			}
		});

		taggingPanel.addConfirmListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hidePopup();
			}
		});

		setContent(taggingPanel);

	}

	@Override
	public void showPopup(Component parent) {
		super.showPopup(parent);
		pack();
	}

	@Override
	public void addFeedbackListener(FeedbackListener listener) {
		taggingPanel.getFeedbackProxy().addFeedbackListener(listener);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener listener) {
		taggingPanel.getFeedbackProxy().removeFeedbackListener(listener);
	}

	public void setValue(Literature literature) {
		taggingPanel.setValue(literature);
	}

}
