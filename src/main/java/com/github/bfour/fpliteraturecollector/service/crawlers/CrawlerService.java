package com.github.bfour.fpliteraturecollector.service.crawlers;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
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


import java.util.Set;

import com.github.bfour.fpliteraturecollector.service.crawlers.epop.EpopScholarCrawler;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CrawlerService {

	private static CrawlerService instance;
	private BiMap<String,Crawler> identifierInstanceMap;
	
	private CrawlerService() {
		this.identifierInstanceMap = HashBiMap.create();
		this.identifierInstanceMap.put("epop Google Scholar", new EpopScholarCrawler());
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
