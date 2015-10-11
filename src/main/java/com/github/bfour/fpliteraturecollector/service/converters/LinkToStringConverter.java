package com.github.bfour.fpliteraturecollector.service.converters;

import java.util.Base64;

import org.springframework.core.convert.converter.Converter;

import com.github.bfour.fpliteraturecollector.domain.Link;

public class LinkToStringConverter implements Converter<Link, String> {

	public LinkToStringConverter() {

	}

	@Override
	public String convert(Link source) {
		return Base64.getUrlEncoder().encodeToString(
				source.getName().getBytes())
				+ ";"
				+ Base64.getUrlEncoder().encodeToString(
						source.getUri().toString().getBytes());
	}

}
