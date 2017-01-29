/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.gui.authors;

import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.jlib.gui.components.FPJGUIWindow;
import com.github.bfour.jlib.gui.components.composite.EntityCRUDPanel;

public class AuthorsWindow extends FPJGUIWindow {

	private static final long serialVersionUID = 3394920842182483985L;
	private static AuthorsWindow instance;

	private AuthorsWindow(ServiceManager servMan) {

		super("Authors", 881, 611);

		AuthorsBrowsePanel litLookPanel = new AuthorsBrowsePanel(servMan);

		AuthorsPanel litPanel = new AuthorsPanel(servMan);
		litPanel.setCRUDButtonsVisible(false);

		EntityCRUDPanel crudPanel = new EntityCRUDPanel(litLookPanel, litPanel);
		getContentPane().add(crudPanel, "grow");

		// pack
		// pack();

	}

	public static AuthorsWindow getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new AuthorsWindow(servMan);
		return instance;
	}

}
