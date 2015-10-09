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
