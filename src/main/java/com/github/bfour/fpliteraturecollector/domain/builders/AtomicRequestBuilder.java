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
	private List<Literature> results;

	public AtomicRequestBuilder() {
		super();
	}
	
	public AtomicRequestBuilder(AtomicRequest a) {
		setCrawler(a.getCrawler());
		setSearchString(a.getSearchString());
		setResults(a.getResults());
	}

	@Override
	public AtomicRequest getObject() {
		return new AtomicRequest(getID(), getCreationTime(),
				getLastChangeTime(), getCrawler(), getSearchString(),
				getResults());
	}

	public Crawler getCrawler() {
		return crawler;
	}

	public void setCrawler(Crawler crawler) {
		this.crawler = crawler;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public List<Literature> getResults() {
		return results;
	}

	public void setResults(List<Literature> results) {
		this.results = results;
	}

}
