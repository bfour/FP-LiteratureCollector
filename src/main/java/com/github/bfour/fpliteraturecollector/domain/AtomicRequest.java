package com.github.bfour.fpliteraturecollector.domain;

import java.util.LinkedList;
import java.util.List;

import com.github.bfour.fpjcommons.model.Entity;

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

public class AtomicRequest extends Entity {

	private SupportedSearchEngine searchEngine;
	private String searchString;
	private List<Literature> results;

	public AtomicRequest(SupportedSearchEngine searchEngine,
			String searchString, List<Literature> results) {
		this.searchEngine = searchEngine;
		this.searchString = searchString;
		this.results = results;
	}

	public AtomicRequest(SupportedSearchEngine searchEngine, String searchString) {
		this(searchEngine, searchString, new LinkedList<Literature>());
	}

	public AtomicRequest() {
		super();
	}

	public SupportedSearchEngine getSearchEngine() {
		return searchEngine;
	}

	public String getSearchString() {
		return searchString;
	}

	public List<Literature> getResults() {
		return results;
	}

	public void setSearchEngine(SupportedSearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public void setResults(List<Literature> results) {
		this.results = results;
	}

}
