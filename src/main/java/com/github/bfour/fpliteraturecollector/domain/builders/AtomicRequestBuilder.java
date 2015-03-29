package com.github.bfour.fpliteraturecollector.domain.builders;

import java.util.List;

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
	private List<Literature> results;

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
		
	}

	@Override
	public AtomicRequest getObject() {
		return new AtomicRequest(getID(), getCreationTime(),
				getLastChangeTime(), getCrawler(), getSearchString(),
				getMaxPageTurns(), getResults());
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

	public List<Literature> getResults() {
		return results;
	}

	public AtomicRequestBuilder setResults(List<Literature> results) {
		this.results = results;
		return this;
	}

}
