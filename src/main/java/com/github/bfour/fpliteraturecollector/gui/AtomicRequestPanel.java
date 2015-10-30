package com.github.bfour.fpliteraturecollector.gui;

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

import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.CRUDService;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUILabelPanel;
import com.github.bfour.fpjgui.components.FPJGUIMultilineLabel;
import com.github.bfour.fpjgui.components.FPJGUIScrollableTextPane;
import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpjgui.components.SearchComboBox;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.components.composite.EntityEditPanel;
import com.github.bfour.fpjgui.components.composite.EntityTableBrowsePanel;
import com.github.bfour.fpjgui.components.table.FPJGUITable;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.builders.AtomicRequestBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class AtomicRequestPanel extends
		EntityEditPanel<AtomicRequest, AtomicRequestBuilder> {

	private static final long serialVersionUID = -6108218045598314837L;

	/**
	 * Create the panel.
	 */
	public AtomicRequestPanel(final ServiceManager servMan,
			final FPJGUITable<AtomicRequest> table) {

		super(AtomicRequest.class,
				new BuilderFactory<AtomicRequest, AtomicRequestBuilder>() {
					@Override
					public AtomicRequestBuilder getBuilder() {
						return new AtomicRequestBuilder();
					}

					@Override
					public AtomicRequestBuilder getBuilder(AtomicRequest entity) {
						return new AtomicRequestBuilder(entity);
					}

				}, new CRUDService<AtomicRequest>() {

					@Override
					public AtomicRequest create(AtomicRequest a) {
						table.addEntry(a);
						return a;
					}

					@Override
					public void delete(AtomicRequest a) throws ServiceException {
						table.deleteEntry(a);
					}

					@Override
					public boolean exists(AtomicRequest a)
							throws ServiceException {
						return table.containsEntry(a);
					}

					@Override
					public DataIterator<AtomicRequest> getAllByStream()
							throws ServiceException {
						final Iterator<AtomicRequest> iter = table.getEntries()
								.iterator();
						return new DataIterator<AtomicRequest>() {
							@Override
							public boolean hasNext() throws DatalayerException {
								return iter.hasNext();
							}

							@Override
							public AtomicRequest next()
									throws DatalayerException {
								return iter.next();
							}

							@Override
							public void remove() throws DatalayerException {
								iter.remove();
							}
						};
					}

					@Override
					public List<AtomicRequest> getAll() throws ServiceException {
						return table.getEntries();
					}

					@Override
					public AtomicRequest get(AtomicRequest arg0)
							throws ServiceException {
						List<AtomicRequest> list = table.getEntries();
						return list.get(list.indexOf(arg0));
					}

					@Override
					public AtomicRequest update(AtomicRequest oldEntry,
							AtomicRequest newEntry) throws ServiceException {
						table.updateEntry(oldEntry, newEntry);
						return newEntry;
					}
				});

		setCRUDButtonsVisible(false);

		getContentPane().setLayout(
				new MigLayout("insets 0", "[grow]", "[][][grow]"));

		// crawler
		EntityTableBrowsePanel<Crawler> crawlerBrowsePanel = new CrawlerBrowsePanel(
				servMan);
		crawlerBrowsePanel.setDeleteEntityEnabled(false);
		crawlerBrowsePanel.setCreateEntityEnabled(false);
		crawlerBrowsePanel.setEditEntityEnabled(false);
		crawlerBrowsePanel.setPreferredSize(new Dimension(486, 186));

		Getter<Crawler, String> searchBoxGetter = new Getter<Crawler, String>() {
			@Override
			public String get(Crawler crawler) {
				return servMan.getCrawlerService().getIdentifierForCrawler(
						crawler);
			}
		};

		SearchComboBox<Crawler> crawlerBox = new SearchComboBox<Crawler>(
				crawlerBrowsePanel, searchBoxGetter);
		crawlerBox.setEditable(true);
		crawlerBox.setValueRequired(true);
		crawlerBox.setValidationRule(new ValidationRule<Crawler>() {
			@Override
			public ValidationRuleResult evaluate(Crawler obj) {
				if (obj == null)
					return new ValidationRuleResult(false,
							"Please select a crawler.");
				else
					return ValidationRuleResult.getSimpleTrueInstance();
			}
		});

		FPJGUILabel<Crawler> categoryLabel = new FPJGUILabel<Crawler>();
		ToggleEditFormComponent<Crawler> crawlerToggle = new ToggleEditFormComponent<Crawler>(
				categoryLabel, crawlerBox);
		registerToggleComponent(crawlerToggle);
		getContentPane().add(new FPJGUILabelPanel("Crawler", crawlerToggle),
				"cell 0 0,growx");

		// max. page turns
		FPJGUITextField maxPageTurnsField = new FPJGUITextField();
		maxPageTurnsField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String arg0) {
				if (arg0 == null || arg0.isEmpty())
					return new ValidationRuleResult(false,
							"Please specify the maximum amount of page turns (page switches on website).");
				try {
					int num = Integer.parseInt(arg0);
					if (num < 0)
						return new ValidationRuleResult(false,
								"Please enter a positive number or 0.");
				} catch (NumberFormatException e) {
					return new ValidationRuleResult(false,
							"Please enter a valid number.");
				}
				return ValidationRuleResult.getSimpleTrueInstance();
			}
		});
		FPJGUIMultilineLabel maxPageTurnsLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> maxPageTurnsToggle = new ToggleEditFormComponent<String>(
				maxPageTurnsLabel, maxPageTurnsField);
		registerToggleComponent(maxPageTurnsToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Max. page turns", maxPageTurnsToggle),
				"cell 0 1,growx");

		// request
		FPJGUIScrollableTextPane requestStringField = new FPJGUIScrollableTextPane();
		requestStringField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String arg0) {
				if (arg0 == null || arg0.isEmpty())
					return new ValidationRuleResult(false,
							"Please specify a request string (eg. q=\"e-health\").");
				else
					return ValidationRuleResult.getSimpleTrueInstance();
			}
		});
		FPJGUIMultilineLabel requestStringLabel = new FPJGUIMultilineLabel();
		ToggleEditFormComponent<String> requestStringToggle = new ToggleEditFormComponent<String>(
				requestStringLabel, requestStringField);
		registerToggleComponent(requestStringToggle);
		getContentPane().add(
				new FPJGUILabelPanel("Request string", requestStringToggle),
				"cell 0 2,grow");

		// mappings
		ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, Crawler> crawlerMapper = new ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, Crawler>(
				crawlerToggle) {
			@Override
			public Crawler getValue(AtomicRequestBuilder object) {
				return object.getCrawler();
			}

			@Override
			public void setValue(AtomicRequestBuilder object, Crawler value) {
				object.setCrawler(value);
			}
		};
		getMappers().add(crawlerMapper);

		ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, String> maxPageTurnsMapper = new ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, String>(
				maxPageTurnsToggle) {
			@Override
			public String getValue(AtomicRequestBuilder object) {
				if (object.getMaxPageTurns() == null)
					return "";
				return object.getMaxPageTurns() + "";
			}

			@Override
			public void setValue(AtomicRequestBuilder object, String value) {
				try {
					object.setMaxPageTurns(Integer.parseInt(value));
				} catch (NumberFormatException e) {
				}
			}
		};
		getMappers().add(maxPageTurnsMapper);

		ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, String> requestStringMapper = new ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, String>(
				requestStringToggle) {
			@Override
			public String getValue(AtomicRequestBuilder object) {
				return object.getSearchString();
			}

			@Override
			public void setValue(AtomicRequestBuilder object, String value) {
				if (value != null)
					value = value.replace("\"", "%22");
				object.setSearchString(value);
				// try {
				// object.setSearchString(URLEncoder.encode(value, "UTF-8"));
				// } catch (UnsupportedEncodingException e) {
				// e.printStackTrace();
				// getFeedbackProxy().feedbackBroadcasted(
				// new Feedback(requestStringToggle,
				// "URL cannot be encoded", e.getMessage(),
				// FeedbackType.ERROR));
				// }
			}
		};
		getMappers().add(requestStringMapper);

	}
}