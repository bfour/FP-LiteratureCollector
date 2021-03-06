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

package com.github.bfour.fpliteraturecollector.domain;

public enum SearchEngine {

	GOOGLE_SCHOLAR, MICROSOFT_ACADEMIC, ACM_DIGITAL_LIBRARY, PUBMED, IEEE_XPLORE;

	/**
	 * A random double representing a token that is given to this engine's one
	 * and only user, or null if there is currently no user.
	 */
	private Double token;

	public Double getToken() {
		return token;
	}

	public void setToken(Double token) {
		this.token = token;
	}

}
