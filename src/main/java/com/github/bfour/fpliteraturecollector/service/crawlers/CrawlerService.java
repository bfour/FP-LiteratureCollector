package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CrawlerService {

	private static CrawlerService instance;
	private BiMap<String,Crawler> identifierInstanceMap;
	
	private CrawlerService() {
		this.identifierInstanceMap = HashBiMap.create();
		this.identifierInstanceMap.put("scholar.py", new ScholarPyCrawler());
	}
	
	public static CrawlerService getInstance() {
		if (instance == null) instance = new CrawlerService();
		return instance;
	}

	public Set<Crawler> getAvailableCrawlers() {
		return identifierInstanceMap.values();
	}
	
	public Crawler getCrawlerForIdentifier(String identifier) {
		return identifierInstanceMap.get(identifier);
	}
	
	public String getIdentifierForCrawler(Crawler crawler) {
		return identifierInstanceMap.inverse().get(crawler);
	}
	
}
