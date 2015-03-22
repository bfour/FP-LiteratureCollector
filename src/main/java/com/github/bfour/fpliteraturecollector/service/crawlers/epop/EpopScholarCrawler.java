package com.github.bfour.fpliteraturecollector.service.crawlers.epop;

import org.epop.dataprovider.googlescholar.GoogleScholarProvider;

import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class EpopScholarCrawler extends Crawler {

	public EpopScholarCrawler() {
		super(new GoogleScholarProvider(), SupportedSearchEngine.GOOGLE_SCHOLAR);
	}

}
