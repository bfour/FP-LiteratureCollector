package com.github.bfour.fpliteraturecollector.service.crawlers;

import org.springframework.core.convert.converter.Converter;

public class CrawlerToStringConverter implements Converter<Crawler, String> {

	public CrawlerToStringConverter() {
	}

	@Override
	public String convert(Crawler source) {
		return CrawlerService.getInstance().getIdentifierForCrawler(source);
	}

}
