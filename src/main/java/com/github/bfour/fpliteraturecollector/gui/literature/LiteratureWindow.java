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

import com.github.bfour.fpjgui.abstraction.EntityFilterPipeline;
import com.github.bfour.fpjgui.components.FPJGUIWindow;
import com.github.bfour.fpjgui.components.composite.EntityCRUDPanel;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;

public class LiteratureWindow extends FPJGUIWindow {

	private static final long serialVersionUID = 3394920842182483985L;

	public LiteratureWindow(ServiceManager servMan) {
		this(servMan, new EntityFilterPipeline<Literature>());
	}

	public LiteratureWindow(ServiceManager servMan,
			EntityFilterPipeline<Literature> filters) {

		super("Literature", 881, 611);

		LiteratureBrowsePanel litLookPanel = new LiteratureBrowsePanel(servMan);
		LiteraturePanel litPanel = new LiteraturePanel(servMan);
		litPanel.setCRUDButtonsVisible(false);

		EntityCRUDPanel crudPanel = new EntityCRUDPanel(litLookPanel, litPanel);
		crudPanel.addFeedbackListener(this);
		crudPanel.remove(litLookPanel);
		crudPanel.add(litLookPanel, "cell 0 0, grow, h 6cm:8cm:, w 10cm:14cm:80%");
		crudPanel.remove(litPanel);
		crudPanel.add(litPanel, "cell 1 0, growy, w 8cm:10cm:11cm");

		getContentPane().add(crudPanel, "grow");

	}

}
