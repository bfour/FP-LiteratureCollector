package com.github.bfour.fpliteraturecollector.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.events.ChangeHandler;
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
	private FPJGUITilePanel<Tag> tagsPanel;

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

		Getter<Tag, JXPanel> panelGetter = new Getter<Tag, JXPanel>() {
			@Override
			public JXPanel get(final Tag input) {
				JXPanel panel = new JXPanel(new MigLayout("insets 4", "[]",
						"[]"));
				panel.setOpaque(true);
				panel.setBackground(input.getColour());
				panel.add(new JLabel(input.getName()));
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

		// GUI for adding tags
		FPJGUIAutocompleteComboBox<Tag> tagCombo = new FPJGUIAutocompleteComboBox<Tag>();
		// TODO update combo on change to tags
		try {
			for (Tag tag : servMan.getTagService().getAll())
				tagCombo.addItem(tag);
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FPJGUIButton addTagButton = FPJGUIButtonFactory.createButton(ButtonFormats.DEFAULT);
		addTagButton.setText("Add tag");

		// assemble
		getContentPanel()
				.setLayout(new MigLayout("insets 0", "[grow]", "[][]"));
		getContentPanel().add(tagsPanel, "cell 0 0, w 6cm::, h 1cm::, grow");
		getContentPanel().add(tagCombo, "cell 0 1, growx");
		getContentPanel().add(addTagButton, "cell 0 1, growy");

		try {
			tagsPanel.addEntries(servMan.getTagService().getAll());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setTags(List<Tag> tags) {
		tagsPanel.setEntries(tags);
	}

	public List<Tag> getTags() {
		return tagsPanel.getEntries();
	}

}
