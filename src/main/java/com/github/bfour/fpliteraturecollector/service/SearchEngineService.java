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

package com.github.bfour.fpliteraturecollector.service;

import com.github.bfour.fpliteraturecollector.domain.SearchEngine;

public class SearchEngineService {

	public SearchEngineService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Requests for this engine to be used (eg. in a crawler).
	 * 
	 * @return a random Double if no entity has requested this engine before or
	 *         the last one gave this engine back (ie. called
	 *         {@link SearchEngine#giveBack()}); otherwise null is returned
	 */
	public synchronized Double request(SearchEngine engine) {
		if (engine.getToken() != null)
			return null;
		engine.setToken(Math.random());
		return engine.getToken();
	}

	public synchronized void giveBack(SearchEngine engine, Double token) {
		if (token == engine.getToken())
			engine.setToken(null);
	}

}
