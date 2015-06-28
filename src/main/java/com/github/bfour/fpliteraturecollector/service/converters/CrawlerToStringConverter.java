package com.github.bfour.fpliteraturecollector.service.converters;

import org.springframework.core.convert.converter.Converter;

import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;

public class CrawlerToStringConverter implements Converter<Crawler, String> {

	public CrawlerToStringConverter() {
	}

	@Override
	public String convert(Crawler source) {
		return CrawlerService.getInstance().getIdentifierForCrawler(source);
	}

}
