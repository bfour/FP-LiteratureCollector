package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.events.BatchCreateEvent;
import com.github.bfour.fpjcommons.events.BatchDeleteEvent;
import com.github.bfour.fpjcommons.events.BatchUpdateEvent;
import com.github.bfour.fpjcommons.events.ChangeHandler;
import com.github.bfour.fpjcommons.events.ChangeListener;
import com.github.bfour.fpjcommons.events.CreateEvent;
import com.github.bfour.fpjcommons.events.DeleteEvent;
import com.github.bfour.fpjcommons.events.UpdateEvent;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.DefaultListLikeChangeListener;
import com.github.bfour.fpjgui.abstraction.EntityFilter;
import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.components.FPJGUIAutocompleteComboBox;
import com.github.bfour.fpjgui.components.FPJGUIButton;
import com.github.bfour.fpjgui.components.FPJGUIButton.ButtonFormats;
import com.github.bfour.fpjgui.components.FPJGUIButton.FPJGUIButtonFactory;
import com.github.bfour.fpjgui.components.SearchComboBox;
import com.github.bfour.fpjgui.components.composite.EntityConfirmableOperationPanel;
import com.github.bfour.fpjgui.components.tilePanel.FPJGUITilePanel;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;
import com.github.bfour.fpliteraturecollector.gui.tags.TagsBrowsePanel;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class TaggingPanel extends
		EntityConfirmableOperationPanel<List<Literature>> {

	private static final long serialVersionUID = 9017251344583323559L;
	private ServiceManager servMan;
	private FPJGUITilePanel<Tag> tagsPanel;
	private FPJGUIAutocompleteComboBox<Tag> tagCombo;

	public TaggingPanel(ServiceManager servMan) {

		super("Set tags", "", "Set tags for selection",
				new Getter<List<Literature>, String>() {
					@Override
					public String get(List<Literature> input) {
						if (input.size() > 1)
							return "Set tags for the selected " + input.size()
									+ " instances of literature.";
						else
							return "Set tags for the selected literature.";
					}
				});

		this.servMan = servMan;

		Getter<Tag, JXPanel> panelGetter = new Getter<Tag, JXPanel>() {
			@Override
			public JXPanel get(final Tag input) {
				JXPanel panel = new JXPanel(new MigLayout("insets 4", "[]",
						"[]"));
				panel.setOpaque(true);
				panel.setBackground(input.getColour());
				// adjust text colour
				JLabel nameLabel = new JLabel(input.getName());
				int colourSum = input.getColour().getRed()
						+ input.getColour().getGreen()
						+ input.getColour().getBlue();
				if (colourSum <= 382)
					nameLabel.setForeground(Color.WHITE);
				panel.add(nameLabel);
				FPJGUIButton removeButton = FPJGUIButtonFactory
						.createButton(ButtonFormats.NAKED);
				removeButton.setIcon(Icons.CROSS_12.getIcon());
				removeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tagsPanel.deleteEntry(input);
					}
				});
				panel.add(removeButton);
				return panel;
			}
		};
		tagsPanel = new FPJGUITilePanel<Tag>(panelGetter);

		// hook up tags panel with change event system
		DefaultListLikeChangeListener<Tag> changeListener = new DefaultListLikeChangeListener<Tag>(
				tagsPanel, new EntityFilterPipeline<Tag>(
						new EntityFilter<Tag>() {
							@Override
							public boolean include(Tag entity) {
								return tagsPanel.containsEntry(entity);
							}
						}));
		ChangeHandler.getInstance(Tag.class).addEventListener(changeListener);

		tagCombo = new FPJGUIAutocompleteComboBox<Tag>();
		updateTagsCombo(); // initial load
		// listen for changes and update combo
		ChangeHandler.getInstance(Tag.class).addEventListener(
				new ChangeListener<Tag>() {
					@Override
					public void handle(BatchCreateEvent<Tag> arg0) {
						for (Tag tag : arg0.getAffectedObjects())
							tagCombo.addItem(tag);
					}

					@Override
					public void handle(BatchDeleteEvent<Tag> arg0) {
						for (Tag tag : arg0.getAffectedObjects())
							tagCombo.removeItem(tag);
					}

					@Override
					public void handle(BatchUpdateEvent<Tag> arg0) {
						updateTagsCombo();
					}

					@Override
					public void handle(CreateEvent<Tag> arg0) {
						tagCombo.addItem(arg0.getCreatedObject());
					}

					@Override
					public void handle(DeleteEvent<Tag> arg0) {
						tagCombo.removeItem(arg0.getDeletedObject());
					}

					@Override
					public void handle(UpdateEvent<Tag> arg0) {
						updateTagsCombo();
					}
				});

		FPJGUIButton addTagButton = FPJGUIButtonFactory
				.createButton(ButtonFormats.DEFAULT);
		addTagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tag selectedTag = (Tag) tagCombo.getSelectedItem();
				tagsPanel.addEntry(selectedTag);
			}
		});
		addTagButton.setText("Add tag");

		// assemble
		getContentPanel()
				.setLayout(new MigLayout("insets 0", "[grow]", "[][]"));
		getContentPanel().add(tagsPanel, "cell 0 0, w 6cm::, h 1cm::, grow");
		getContentPanel().add(tagCombo, "cell 0 1, growx");
		getContentPanel().add(addTagButton, "cell 0 1, growy");

	}

	public void setTags(List<Tag> tags) {
		tagsPanel.setEntries(tags);
	}

	public List<Tag> getTags() {
		return tagsPanel.getEntries();
	}

	private void updateTagsCombo() {
		// TODO update combo on change to tags
		try {
			tagCombo.removeAllItems();
			Tag selection = (Tag) tagCombo.getSelectedItem();
			for (Tag tag : servMan.getTagService().getAll())
				tagCombo.addItem(tag);
			tagCombo.setSelectedItem(selection);
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
