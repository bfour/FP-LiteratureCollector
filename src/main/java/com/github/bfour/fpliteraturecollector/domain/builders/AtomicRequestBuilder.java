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

package com.github.bfour.fpliteraturecollector.domain.builders;

import java.util.Set;

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class AtomicRequestBuilder extends EntityBuilder<AtomicRequest>
		implements Builder<AtomicRequest> {

	private Crawler crawler;
	private String searchString;
	private Integer maxPageTurns;
	private Set<Literature> results;
	private boolean processed;
	private String processingError;

	public AtomicRequestBuilder() {
		super();
	}

	public AtomicRequestBuilder(AtomicRequest a) {

		setID(a.getID());
		setCreationTime(a.getCreationTime());
		setLastChangeTime(a.getLastChangeTime());

		setCrawler(a.getCrawler());
		setSearchString(a.getSearchString());
		setMaxPageTurns(a.getMaxPageTurns());
		setResults(a.getResults());
		setProcessed(a.isProcessed());
		setProcessingError(a.getProcessingError());

	}

	@Override
	public AtomicRequest getObject() {
		return new AtomicRequest(getID(), getCreationTime(),
				getLastChangeTime(), getCrawler(), getSearchString(),
				getMaxPageTurns(), getResults(), isProcessed(),
				getProcessingError());
	}

	public Crawler getCrawler() {
		return crawler;
	}

	public AtomicRequestBuilder setCrawler(Crawler crawler) {
		this.crawler = crawler;
		return this;
	}

	public String getSearchString() {
		return searchString;
	}

	public AtomicRequestBuilder setSearchString(String searchString) {
		this.searchString = searchString;
		return this;
	}

	public Integer getMaxPageTurns() {
		return maxPageTurns;
	}

	public AtomicRequestBuilder setMaxPageTurns(Integer maxPageTurns) {
		this.maxPageTurns = maxPageTurns;
		return this;
	}

	public Set<Literature> getResults() {
		return results;
	}

	public AtomicRequestBuilder setResults(Set<Literature> results) {
		this.results = results;
		return this;
	}

	public boolean isProcessed() {
		return processed;
	}

	public AtomicRequestBuilder setProcessed(boolean processed) {
		this.processed = processed;
		return this;
	}

	public String getProcessingError() {
		return processingError;
	}

	public AtomicRequestBuilder setProcessingError(String processingError) {
		this.processingError = processingError;
		return this;
	}

}
