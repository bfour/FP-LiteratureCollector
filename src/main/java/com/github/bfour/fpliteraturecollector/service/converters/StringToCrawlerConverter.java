package com.github.bfour.fpliteraturecollector.service.converters;

import org.springframework.core.convert.converter.Converter;

import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;

public class StringToCrawlerConverter implements Converter<String, Crawler> {

	public StringToCrawlerConverter() {
	}

	@Override
	public Crawler convert(String source) {
		return CrawlerService.getInstance().getCrawlerForIdentifier(source);
	}

}
