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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

import com.github.bfour.fpjcommons.lang.BuilderFactory;
import com.github.bfour.fpjgui.abstraction.valueContainer.ValidationRule;
import com.github.bfour.fpjgui.components.FPJGUITextField;
import com.github.bfour.fpjgui.components.composite.EntityEditPanel;
import com.github.bfour.fpjgui.design.Colors;
import com.github.bfour.fpjgui.util.ObjectGraphicalValueContainerMapper;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class QueryEditPanel extends EntityEditPanel<Query, QueryBuilder> {

	private static final long serialVersionUID = -4752243326650967601L;

	/**
	 * Create the panel.
	 */
	public QueryEditPanel(ServiceManager servMan, Query query) {

		super(Query.class, new BuilderFactory<Query, QueryBuilder>() {
			@Override
			public QueryBuilder getBuilder() {
				return new QueryBuilder();
			}

			@Override
			public QueryBuilder getBuilder(Query entity) {
				return new QueryBuilder(entity);
			}

		}, servMan.getQueryService());

		setCRUDButtonsVisible(false);
		createNew(this);

		getContentPane()
				.setLayout(new MigLayout("", "[grow][]", "[]0[]8[]0[]"));

		// name
		JLabel lblName = new JLabel("Name");
		lblName.setForeground(Colors.VERY_STRONG_GRAY.getColor());
		getContentPane().add(lblName, "cell 0 0");

		FPJGUITextField nameField = new FPJGUITextField();
		getContentPane().add(nameField, "cell 0 1,growx");

		nameField.setValidationRule(new ValidationRule<String>() {
			@Override
			public ValidationRuleResult evaluate(String arg0) {
				if (arg0 == null || arg0.isEmpty())
					return new ValidationRuleResult(false,
							"Please specify a name.");
				else
					return ValidationRuleResult.getSimpleTrueInstance();
			}
		});

		// atomic requests
		JLabel lblAtomicRequests = new JLabel("Atomic Requests");
		lblAtomicRequests.setForeground(Colors.VERY_STRONG_GRAY.getColor());
		getContentPane().add(lblAtomicRequests, "cell 0 2 2 1");

		final AtomicRequestBrowsePanel atomReqBrowsePanel = new AtomicRequestBrowsePanel(
				servMan, query);
		final AtomicRequestPanel atomReqPanel = new AtomicRequestPanel(servMan,
				atomReqBrowsePanel.getListLikeContainer());

		JXPanel atomicPanel = new JXPanel();
		// PanelDecorator.decorateWithDropShadow(atomicPanel);
		atomicPanel.setLayout(new MigLayout("insets 0", "[grow]8[8cm!]",
				"[6cm:6cm:]"));
		atomicPanel.add(atomReqBrowsePanel, "cell 0 0, grow");
		atomicPanel.add(atomReqPanel, "cell 1 0, grow");
		getContentPane().add(atomicPanel, "cell 0 3 2 1, grow");

		atomReqBrowsePanel.addCreateAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				atomReqPanel.createNew(atomReqBrowsePanel);
			}
		});

		atomReqBrowsePanel.addEditAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				atomReqPanel.edit();
			}
		});

		// automatically set AtomicRequest in edit/view panel on selection
		// change
		atomReqBrowsePanel.subscribeSelectionChangeSubscriber(atomReqPanel);

		// mappings
		ObjectGraphicalValueContainerMapper<QueryBuilder, String> nameMapper = new ObjectGraphicalValueContainerMapper<QueryBuilder, String>(
				nameField) {
			@Override
			public String getValue(QueryBuilder object) {
				return object.getName();
			}

			@Override
			public void setValue(QueryBuilder object, String value) {
				object.setName(value);
			}
		};
		getMappers().add(nameMapper);

		ObjectGraphicalValueContainerMapper<QueryBuilder, List<AtomicRequest>> atomicReqMapper = new ObjectGraphicalValueContainerMapper<QueryBuilder, List<AtomicRequest>>(
				atomReqBrowsePanel) {
			@Override
			public List<AtomicRequest> getValue(QueryBuilder object) {
				if (object.getAtomicRequests() == null)
					return new ArrayList<AtomicRequest>(0);
				return new ArrayList<>(object.getAtomicRequests());
			}

			@Override
			public void setValue(QueryBuilder object, List<AtomicRequest> value) {
				object.setAtomicRequests(new HashSet<>(value));
			}
		};
		getMappers().add(atomicReqMapper);

	}

}
