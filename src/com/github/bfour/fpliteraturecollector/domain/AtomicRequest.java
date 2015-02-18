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

public class AtomicRequest {

	private SupportedSearchEngine searchEngine;
	private String searchString;

	public AtomicRequest(SupportedSearchEngine searchEngine, String searchString) {
		super();
		this.searchEngine = searchEngine;
		this.searchString = searchString;
	}

	public SupportedSearchEngine getSearchEngine() {
		return searchEngine;
	}

	public String getSearchString() {
		return searchString;
	}

}
