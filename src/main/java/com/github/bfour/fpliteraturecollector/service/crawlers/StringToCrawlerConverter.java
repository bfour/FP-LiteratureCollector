package com.github.bfour.fpliteraturecollector.service.crawlers;

import org.springframework.core.convert.converter.Converter;

public class StringToCrawlerConverter implements Converter<String, Crawler> {

	public StringToCrawlerConverter() {
	}

	@Override
	public Crawler convert(String source) {
		return CrawlerService.getInstance().getCrawlerForIdentifier(source);
	}

}
