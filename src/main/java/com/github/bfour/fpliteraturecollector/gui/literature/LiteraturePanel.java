package com.github.bfour.fpliteraturecollector.gui.literature;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.EntityLoader;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpjgui.components.FPJGUITextPane;
import com.github.bfour.fpjgui.components.SearchComboBox;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.components.composite.EntityEditPanel;
import com.github.bfour.fpjgui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.fpjgui.design.Borders;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpjguiextended.tagging.TagTilePanel;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteraturePanel extends
		EntityEditPanel<Literature, LiteratureBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;

	/**
	 * Create the panel.
	 */
	public LiteraturePanel(ServiceManager servMan) {

		super(Literature.class,
				new BuilderFactory<Literature, LiteratureBuilder>() {
					@Override
					public LiteratureBuilder getBuilder() {
						return new LiteratureBuilder();
					}

					@Override
					public LiteratureBuilder getBuilder(Literature entity) {
						return new LiteratureBuilder(entity);
					}

				}, servMan.getLiteratureService());

		getContentPane().setLayout(
				new MigLayout("insets 0, w 80:100:120", "[grow]", "[]"));

		// accept file drops
		setDropTarget(new DropTarget() {
			private static final long serialVersionUID = -679032801574638314L;

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public synchronized void drop(DropTargetDropEvent event) {
				event.acceptDrop(DnDConstants.ACTION_MOVE);
				if (getEntityBuilder() == null) {
					getFeedbackProxy().feedbackBroadcasted(
							new Feedback(LiteraturePanel.this,
									"Please select an entity to add files to.",
									FeedbackType.ERROR));
					return;
				}
				Transferable t = event.getTransferable();
				List<File> fileList;
				try {
					fileList = (List) t
							.getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : fileList) {
						Literature oldEntity = getEntityBuilder().getObject();
						Link link = servMan.getFileServ().persist(file,
								oldEntity);
						if (getEntityBuilder().getFulltextFilePaths() == null)
							getEntityBuilder().setFulltextFilePaths(
									new HashSet<Link>());
						getEntityBuilder().getFulltextFilePaths().add(link);
						try {
							servMan.getLiteratureService().update(oldEntity,
									getEntityBuilder().getObject());
							getFeedbackProxy()
									.feedbackBroadcasted(
											new Feedback(LiteraturePanel.this,
													"Added " + file + ".",
													FeedbackType.SUCCESS));
							try {
								file.delete();
							} catch (SecurityException e) {
								getFeedbackProxy().feedbackBroadcasted(
										new Feedback(LiteraturePanel.this,
												"File added, but could not delete source file "
														+ file + ".", e
														.getMessage(),
												FeedbackType.WARN));
							}
						} catch (ServiceException e) {
							e.printStackTrace();
							getFeedbackProxy().feedbackBroadcasted(
									new Feedback(LiteraturePanel.this,
											"Sorry, failed to add file " + file
													+ ".", e.getMessage(),
											FeedbackType.ERROR));
						}
					}
				} catch (UnsupportedFlavorException | IOException e) {
					e.printStackTrace();
					getFeedbackProxy().feedbackBroadcasted(
							new Feedback(LiteraturePanel.this,
									"Sorry, failed to add files.", e
											.getMessage(), FeedbackType.ERROR));
				}
			}
		});

		// ID
		FPJGUILabel<String> IDLabel = new FPJGUILabel<String>();
		getContentPane().add(new FPJGUILabelPanel("ID", IDLabel), "growx,spanx 2,wrap");

		// title
		FPJGUITextPane titleField = new FPJGUITextPane();
		titleField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String obj) {
				return new ValidationRuleResult(obj != null && !obj.isEmpty(),
						"Name must not be empty.");
			}
		});
		titleField.setValueRequired(true);
		FPJGUIMultilineLabel titleLabel = new FPJGUIMultilineLabel();
		titleLabel.setFont(new JLabel().getFont().deriveFont(
				Collections.singletonMap(TextAttribute.WEIGHT,
						TextAttribute.WEIGHT_BOLD)));
		ToggleEditFormComponent<String> titleToggle = new ToggleEditFormComponent<String>(
				titleLabel, titleField);
		registerToggleComponent(titleToggle);
		getContentPane().add(new FPJGUILabelPanel("Title", titleToggle),
				"growx,spanx 2,wrap");

		// abstract
		FPJGUITextPane abstractField = new FPJGUITextPane();
		FPJGUIMultilineLabel abstractLabel = new FPJGUIMultilineLabel();
		abstractLabel.setContentType("text/html"); // TODO (optional) check why
													// auto-content detection
													// doesn't work
		abstractLabel.setFont(new JLabel().getFont());
		JScrollPane abstractLabelScrollPane = new JScrollPane(abstractLabel);
		JScrollPane abstractFieldScrollPane = new JScrollPane(abstractField);
		abstractLabelScrollPane.setBorder(abstractLabel.getBorder());
		abstractFieldScrollPane.setBorder(Borders.GENERIC.getBorder());
		abstractField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		abstractLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		ToggleEditFormComponent<String> abstractToggle = new ToggleEditFormComponent<String>(
				abstractLabel, abstractField, abstractLabelScrollPane,
				abstractFieldScrollPane);
		registerToggleComponent(abstractToggle);
		getContentPane().add(new FPJGUILabelPanel("Abstract", abstractToggle),
				"growx,spanx 2,wrap");

		// type
		EntityTableBrowsePanel<LiteratureType> litTypeBrowsePanel = new EntityTableBrowsePanel<LiteratureType>(
				LiteratureType.class);
		litTypeBrowsePanel
				.setLoader(new EntityLoader<Literature.LiteratureType>() {
					private final List<LiteratureType> list = new ArrayList<LiteratureType>(
							Arrays.asList(LiteratureType.values()));

					@Override
					public List<LiteratureType> get() {
						return list;
					}
				});
		SearchComboBox<LiteratureType> typeCombo = new SearchComboBox<LiteratureType>(
				litTypeBrowsePanel, new Getter<LiteratureType, String>() {
					@Override
					public String get(LiteratureType type) {
						return type.getTellingName();
					}
				});
		FPJGUILabel<LiteratureType> typeLabel = new FPJGUILabel<>();
		ToggleEditFormComponent<LiteratureType> typeToggle = new ToggleEditFormComponent<LiteratureType>(
				typeLabel, typeCombo);
		registerToggleComponent(typeToggle);
		getContentPane().add(new FPJGUILabelPanel("Type", typeToggle),
				"growx,spanx 2,wrap");

		// authors
		FPJGUIMultilineLabel authorLabel = new FPJGUIMultilineLabel();
		FPJGUIMultilineLabel authorField = new FPJGUIMultilineLabel();
		JScrollPane authorLabelScrollPane = new JScrollPane(authorLabel);
		JScrollPane authorFieldScrollPane = new JScrollPane(authorField);
		authorLabelScrollPane.setBorder(abstractLabel.getBorder());
		authorFieldScrollPane.setBorder(Borders.GENERIC.getBorder());
		authorField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		authorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		ToggleEditFormComponent<String> authorToggle = new ToggleEditFormComponent<String>(
				authorLabel, authorField, authorLabelScrollPane,
				authorFieldScrollPane);
		registerToggleComponent(authorToggle);
		getContentPane().add(new FPJGUILabelPanel("Authors", authorToggle),
				"growx,spanx 2,wrap,w ::100%,h ::4cm");

		// DOI
		FPJGUIMultilineLabel DOILabel = new FPJGUIMultilineLabel();
		FPJGUITextField DOIField = new FPJGUITextField();
		ToggleEditFormComponent<String> DOIToggle = new ToggleEditFormComponent<String>(
				DOILabel, DOIField);
		registerToggleComponent(DOIToggle);
		getContentPane().add(new FPJGUILabelPanel("DOI", DOIToggle),
				"growx, w 50%");

		// ISBN
		FPJGUIMultilineLabel ISBNLabel = new FPJGUIMultilineLabel();
		FPJGUITextField ISBNField = new FPJGUITextField();
		ToggleEditFormComponent<String> ISBNToggle = new ToggleEditFormComponent<String>(
				ISBNLabel, ISBNField);
		registerToggleComponent(ISBNToggle);
		getContentPane().add(new FPJGUILabelPanel("ISBN", ISBNToggle),
				"growx, w 50%, wrap");

		// gScholarID
		FPJGUIMultilineLabel gScholarIDLabel = new FPJGUIMultilineLabel();
		FPJGUITextField gScholarIDField = new FPJGUITextField();
		ToggleEditFormComponent<String> gScholarIDToggle = new ToggleEditFormComponent<String>(
				gScholarIDLabel, gScholarIDField);
		registerToggleComponent(gScholarIDToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Google Scholar ID", gScholarIDToggle),
				"growx, w 50%");

		// msAcademicID
		FPJGUIMultilineLabel msAcademicIDLabel = new FPJGUIMultilineLabel();
		FPJGUITextField msAcademicIDField = new FPJGUITextField();
		ToggleEditFormComponent<String> msAcademicIDToggle = new ToggleEditFormComponent<String>(
				msAcademicIDLabel, msAcademicIDField);
		registerToggleComponent(msAcademicIDToggle);
		getContentPane().add(
				new FPJGUILabelPanel("MS Academic ID", msAcademicIDToggle),
				"growx, w 50%, wrap");

		// pubMedID
		FPJGUIMultilineLabel pubMedIDLabel = new FPJGUIMultilineLabel();
		FPJGUITextField pubMedIDField = new FPJGUITextField();
		ToggleEditFormComponent<String> pubMedIDToggle = new ToggleEditFormComponent<String>(
				pubMedIDLabel, pubMedIDField);
		registerToggleComponent(pubMedIDToggle);
		getContentPane().add(new FPJGUILabelPanel("PubMed ID", pubMedIDToggle),
				"growx, w 50%");

		// acmID
		FPJGUIMultilineLabel acmIDLabel = new FPJGUIMultilineLabel();
		FPJGUITextField acmIDField = new FPJGUITextField();
		ToggleEditFormComponent<String> acmIDToggle = new ToggleEditFormComponent<String>(
				acmIDLabel, acmIDField);
		registerToggleComponent(acmIDToggle);
		getContentPane().add(new FPJGUILabelPanel("ACM ID", acmIDToggle),
				"growx, w 50%, wrap");

		// tags
		TagTilePanel<Tag> tagLabel = new TagTilePanel<>(false);
		TagTilePanel<Tag> tagField = new TagTilePanel<>(false);
		ToggleEditFormComponent<List<Tag>> tagToggle = new ToggleEditFormComponent<List<Tag>>(
				tagLabel, tagField);
		registerToggleComponent(tagToggle);
		getContentPane().add(new FPJGUILabelPanel("Tags", tagToggle),
				"growx,spanx 2,wrap");

		// website URL
		LinkSetPanel websiteURLPanel = new LinkSetPanel();
		LinkSetEditPanel websiteURLEditPanel = new LinkSetEditPanel();
		ToggleEditFormComponent<Set<Link>> websiteURLToggle = new ToggleEditFormComponent<>(
				websiteURLPanel, websiteURLEditPanel);
		registerToggleComponent(websiteURLToggle);
		getContentPane().add(new FPJGUILabelPanel("Website", websiteURLToggle),
				"growx,spanx 2,wrap,w ::100%");

		// fulltextURL
		LinkSetPanel fulltextURLPanel = new LinkSetPanel();
		LinkSetEditPanel fulltextURLEditPanel = new LinkSetEditPanel();
		ToggleEditFormComponent<Set<Link>> fulltextURLToggle = new ToggleEditFormComponent<>(
				fulltextURLPanel, fulltextURLEditPanel);
		registerToggleComponent(fulltextURLToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Full Text URL", fulltextURLToggle),
				"growx,spanx 2,wrap,w ::100%");

		// fulltextPath
		LinkSetPanel fulltextPathPanel = new LinkSetPanel();
		LinkSetEditPanel fulltextPathEditPanel = new LinkSetEditPanel();
		ToggleEditFormComponent<Set<Link>> fulltextPathToggle = new ToggleEditFormComponent<>(
				fulltextPathPanel, fulltextPathEditPanel);
		registerToggleComponent(fulltextPathToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Full Text Files", fulltextPathToggle),
				"growx,spanx 2,wrap,w ::100%");

		// notes
		FPJGUIMultilineLabel notesLabel = new FPJGUIMultilineLabel();
		FPJGUITextPane notesField = new FPJGUITextPane();
		JScrollPane notesLabelScrollPane = new JScrollPane(notesLabel);
		JScrollPane notesFieldScrollPane = new JScrollPane(notesField);
		notesLabelScrollPane.setBorder(abstractLabel.getBorder());
		notesFieldScrollPane.setBorder(Borders.GENERIC.getBorder());
		notesField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		notesLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		ToggleEditFormComponent<String> notesToggle = new ToggleEditFormComponent<>(
				notesLabel, notesField, notesLabelScrollPane,
				notesFieldScrollPane);
		registerToggleComponent(notesToggle);
		getContentPane().add(new FPJGUILabelPanel("Notes", notesToggle),
				"growx,wrap,spanx 2,w ::100%");

		// mappings
		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> IDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				IDLabel) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getID() + "";
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				try {
					object.setID(Long.parseLong(value));
				} catch (NumberFormatException e) {
				}
			}
		};
		getMappers().add(IDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> titleMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				titleToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getTitle();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setTitle(value);
			}
		};
		getMappers().add(titleMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> abstractMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				abstractToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getAbstractText();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setAbstractText(value);
			}
		};
		getMappers().add(abstractMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, LiteratureType> typeMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, LiteratureType>(
				typeToggle) {
			@Override
			public LiteratureType getValue(LiteratureBuilder object) {
				return object.getType();
			}

			@Override
			public void setValue(LiteratureBuilder object, LiteratureType value) {
				object.setType(value);
			}
		};
		getMappers().add(typeMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> authorMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				authorToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				Set<Author> authors = object.getAuthors();
				if (authors == null)
					return "";
				StringBuilder builder = new StringBuilder();
				for (Author auth : authors) {
					builder.append(auth.getFirstName());
					builder.append(" ");
					builder.append(auth.getLastName());
					builder.append(", ");
				}
				return builder.substring(0, builder.length() - 2);
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				// TODO
			}
		};
		getMappers().add(authorMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> DOIMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				DOIToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getDOI();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setDOI(value);
			}
		};
		getMappers().add(DOIMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> ISBNMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				ISBNToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				if (object.getISBN() == null)
					return null;
				return object.getISBN().getV13String();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				if (value == null)
					object.setISBN(null);
				else
					object.setISBN(new ISBN(value));
			}
		};
		getMappers().add(ISBNMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> gScholarIDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				gScholarIDToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getgScholarID();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setgScholarID(value);
			}
		};
		getMappers().add(gScholarIDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> msAcademicIDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				msAcademicIDToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getMsAcademicID();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setMsAcademicID(value);
			}
		};
		getMappers().add(msAcademicIDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> pubMedIDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				pubMedIDToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getPubmedID();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setPubmedID(value);
			}
		};
		getMappers().add(pubMedIDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> acmIDMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				acmIDToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getAcmID();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setAcmID(value);
			}
		};
		getMappers().add(acmIDMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, List<Tag>> tagMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, List<Tag>>(
				tagToggle) {
			@Override
			public List<Tag> getValue(LiteratureBuilder object) {
				Set<Tag> set = object.getTags();
				if (set != null)
					return new ArrayList<>(set);
				else
					return new ArrayList<Tag>(0);
			}

			@Override
			public void setValue(LiteratureBuilder object, List<Tag> value) {
				object.setTags(new HashSet<>(value));
			}
		};
		getMappers().add(tagMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, Set<Link>> websiteURLMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, Set<Link>>(
				websiteURLToggle) {
			@Override
			public Set<Link> getValue(LiteratureBuilder object) {
				return object.getWebsiteURLs();
			}

			@Override
			public void setValue(LiteratureBuilder object, Set<Link> value) {
				object.setWebsiteURLs(value);
			}
		};
		getMappers().add(websiteURLMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, Set<Link>> fulltextURLMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, Set<Link>>(
				fulltextURLToggle) {
			@Override
			public Set<Link> getValue(LiteratureBuilder object) {
				return object.getFulltextURLs();
			}

			@Override
			public void setValue(LiteratureBuilder object, Set<Link> value) {
				object.setFulltextURLs(value);
			}
		};
		getMappers().add(fulltextURLMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, Set<Link>> fulltextPathMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, Set<Link>>(
				fulltextPathToggle) {
			@Override
			public Set<Link> getValue(LiteratureBuilder object) {
				return object.getFulltextFilePaths();
			}

			@Override
			public void setValue(LiteratureBuilder object, Set<Link> value) {
				object.setFulltextFilePaths(value);
			}
		};
		getMappers().add(fulltextPathMapper);

		ObjectGraphicalValueContainerMapper<LiteratureBuilder, String> notesMapper = new ObjectGraphicalValueContainerMapper<LiteratureBuilder, String>(
				notesToggle) {
			@Override
			public String getValue(LiteratureBuilder object) {
				return object.getNotes();
			}

			@Override
			public void setValue(LiteratureBuilder object, String value) {
				object.setNotes(value);
			}
		};
		getMappers().add(notesMapper);

	}
}
