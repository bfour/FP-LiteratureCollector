package com.github.bfour.fpliteraturecollector.service.converters;

import java.util.Base64;

import org.springframework.core.convert.converter.Converter;

import com.github.bfour.fpliteraturecollector.domain.Link;

public class LinkToStringConverter implements Converter<Link, String> {

	@Override
	public String convert(Link source) {

		String str = "";
		str += source.getName() == null ? " " : Base64.getUrlEncoder()
				.encodeToString(source.getName().getBytes());
		str += ";";
		str += Base64.getUrlEncoder().encodeToString(
				source.getUri().toString().getBytes());
		str += ";";
		str += source.getReference() == null ? " " : Base64.getUrlEncoder()
				.encodeToString(source.getReference().getBytes());

		return str;

	}

}
