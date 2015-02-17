package com.github.bfour.fpliteraturecollector.domain;

public class AtomicRequest {
	
	private SupportedSearchEngine searchEngine;
	private String searchString;
	
	public AtomicRequest(SupportedSearchEngine searchEngine,
			String searchString) {
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

