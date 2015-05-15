package com.github.bfour.fpliteraturecollector.domain;

import java.util.Date;
import java.util.List;

import com.github.bfour.fpjcommons.model.Entity;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

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

	protected Crawler crawler;
	protected String searchString;
	protected Integer maxPageTurns;
	protected List<Literature> results;
	protected boolean processed;
	protected String processingError;

	public AtomicRequest(Long iD, Date creationTime, Date lastChangeTime,
			Crawler crawler, String searchString, Integer maxPageTurns,
			List<Literature> results, boolean processed, String processingError) {
		super(iD, creationTime, lastChangeTime);
		this.crawler = crawler;
		this.searchString = searchString;
		this.maxPageTurns = maxPageTurns;
		this.results = results;
		this.processed = processed;
		this.processingError = processingError;
	}

	public AtomicRequest(Crawler crawler, String searchString,
			Integer maxPageTurns, List<Literature> results, boolean processed,
			String processingError) {
		this.crawler = crawler;
		this.searchString = searchString;
		this.maxPageTurns = maxPageTurns;
		this.results = results;
		this.processed = processed;
		this.processingError = processingError;
	}

	public AtomicRequest() {
		super();
	}

	public Crawler getCrawler() {
		return crawler;
	}

	public String getSearchString() {
		return searchString;
	}

	public Integer getMaxPageTurns() {
		return maxPageTurns;
	}

	public List<Literature> getResults() {
		return results;
	}

	public boolean isProcessed() {
		return processed;
	}

	public String getProcessingError() {
		return processingError;
	}

	@Override
	public String toString() {
		if (getID() == null)
			return getCrawler() + ": " + getSearchString();
		else
			return "#" + getID() + " (" + getCrawler() + ": "
					+ getSearchString() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getID() == null) ? 0 : getID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AtomicRequest))
			return false;
		AtomicRequest other = (AtomicRequest) obj;
		if (getID() == null) {
			if (other.getID() != null)
				return false;
		} else if (!getID().equals(other.getID()))
			return false;
		return true;
	}

}
