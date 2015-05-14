package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.epop.dataprovider.DataProvider;
import org.epop.dataprovider.DataUnavailableException;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;

public class Crawler {

	private DataProvider provider;
	private List<SupportedSearchEngine> engines;

	public Crawler(DataProvider provider, SupportedSearchEngine... engines) {
		this.provider = provider;
		this.engines = Arrays.asList(engines);
	}

	public List<Literature> process(AtomicRequest atomReq)
			throws DataUnavailableException, DatalayerException,
			URISyntaxException {
		return provider.runQuery(atomReq.getSearchString(),
				atomReq.getMaxPageTurns());
	}

	@Override
	public String toString() {
		return CrawlerService.getInstance().getIdentifierForCrawler(this);
	}

	/**
	 * Get the SupportedSearchEngines used by this Crawler. This information may
	 * be used by a scheduler to run crawlers in parallel.
	 * 
	 * @return SupportedSearchEngines used by this Crawler
	 */
	public List<SupportedSearchEngine> getSearchEnginesBeingAccessed() {
		return engines;
	}

}
