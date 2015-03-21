package com.github.bfour.fpliteraturecollector.gui;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjcommons.utils.Getter;
import com.github.bfour.fpjgui.abstraction.EntityEditPanel;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUILabel;
import com.github.bfour.fpjgui.components.FPJGUITextPane;
import com.github.bfour.fpjgui.components.SearchComboBox;
import com.github.bfour.fpjgui.components.ToggleEditFormComponent;
import com.github.bfour.fpjgui.components.composite.EntityBrowsePanel;
import com.github.bfour.fpjgui.design.Colors;
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
	public AtomicRequestPanel(final ServiceManager servMan) {

		super(new BuilderFactory<AtomicRequest, AtomicRequestBuilder>() {
			@Override
			public AtomicRequestBuilder getBuilder() {
				return new AtomicRequestBuilder();
			}

			@Override
			public AtomicRequestBuilder getBuilder(AtomicRequest entity) {
				return new AtomicRequestBuilder(entity);
			}

		}, servMan.getAtomicRequestService());

		getContentPane()
				.setLayout(
						new MigLayout("insets 0, w 60:80:100", "[grow]",
								"[]0[]8[]0[]"));

		JLabel dummy = new JLabel();
		Font labelFont = dummy.getFont().deriveFont(
				dummy.getFont().getSize() - 2f);

		// crawler
		JLabel lblCrawler = new JLabel("Crawler");
		lblCrawler.setFont(labelFont);
		lblCrawler.setForeground(Colors.VERY_STRONG_GRAY.getColor());
		getContentPane().add(lblCrawler, "cell 0 0,growx");

		EntityBrowsePanel<Crawler> crawlerBrowsePanel = new CrawlerBrowsePanel(
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
		getContentPane().add(crawlerToggle, "cell 0 1,growx");

		// request
		JLabel lblRequestString = new JLabel("RequestString");
		lblRequestString.setFont(labelFont);
		lblRequestString.setForeground(Colors.VERY_STRONG_GRAY.getColor());
		getContentPane().add(lblRequestString, "cell 0 2,growx");

		FPJGUITextPane requestStringField = new FPJGUITextPane();
		FPJGUILabel<String> requestStringLabel = new FPJGUILabel<String>();
		ToggleEditFormComponent<String> requestStringToggle = new ToggleEditFormComponent<String>(
				requestStringLabel, requestStringField);
		registerToggleComponent(requestStringToggle);
		getContentPane().add(requestStringToggle, "cell 0 3,growx");

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

		ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, String> requestStringMapper = new ObjectGraphicalValueContainerMapper<AtomicRequestBuilder, String>(
				requestStringToggle) {
			@Override
			public String getValue(AtomicRequestBuilder object) {
				return object.getSearchString();
			}

			@Override
			public void setValue(AtomicRequestBuilder object, String value) {
				object.setSearchString(value);
			}
		};
		getMappers().add(requestStringMapper);

	}

}