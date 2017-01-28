package com.github.bfour.fpliteraturecollector.gui.literature;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.components.FPJGUIPopover;
import com.github.bfour.fpjgui.components.composite.EntityConfirmableOperationPanel;
import com.github.bfour.fpjsearch.SearchException;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

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
				if (validator.isComplete(entity))
					getContentPanel().add(new JLabel("tagging is complete"));
				else
					getContentPanel().add(new JLabel("tagging is incomplete"));
				if (validator.isValid(entity))
					getContentPanel().add(new JLabel("tagging is valid"));
				else
					getContentPanel().add(new JLabel("tagging is invalid"));

				// int row = 0;
				// for (Tag tag : servMan.getTagService().getAll()) {
				// if (contains(tagsToSet, "Quality")) {
				// remove("Quality");
				// } if (contains(tagsToSet, "Quality")
				// tag.getName().startsWith("Quality")) {
				// FPJGUIButton qualityButton = FPJGUIButtonFactory
				// .createButton(
				// ButtonFormats.DEFAULT,
				// Lengths.LARGE_BUTTON_HEIGHT.getLength(),
				// "Semantic Tagging",
				// com.github.bfour.fpliteraturecollector.gui.design.Icons.TAG_16
				// .getIcon());
				// getContentPanel().add(semanticsButton, "cell 0 2");
				// row++;
				// } else if (tag.getName().startsWith("Access")) {
				//
				// }
				// }
			} catch (SearchException e) {
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
