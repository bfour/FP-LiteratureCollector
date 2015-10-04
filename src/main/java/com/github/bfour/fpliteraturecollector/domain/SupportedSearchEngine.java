package com.github.bfour.fpliteraturecollector.domain;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
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

public enum SupportedSearchEngine {
	GOOGLE_SCHOLAR;

	/**
	 * A random double representing a token that is given to this engine's one
	 * and only user, or null if there is currently no user.
	 */
	private Double token;

	/**
	 * Requests for this engine to be used (eg. in a crawler).
	 * 
	 * @return a random Double if no entity has requested this engine before or
	 *         the last one gave this engine back (ie. called
	 *         {@link SupportedSearchEngine#giveBack()}); otherwise null is
	 *         returned
	 */
	public synchronized Double request() {
		Math.random();
		if (token != null)
			return null;
		token = Math.random();
		return token;
	}

	/**
	 * 
	 */
	public synchronized void giveBack(Double token) {
		if (token == this.token)
			this.token = null;
	}

}
