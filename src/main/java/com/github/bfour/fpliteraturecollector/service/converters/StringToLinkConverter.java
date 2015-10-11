package com.github.bfour.fpliteraturecollector.service.converters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.springframework.core.convert.converter.Converter;

import com.github.bfour.fpliteraturecollector.domain.Link;

public class StringToLinkConverter implements Converter<String, Link> {

	public StringToLinkConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Link convert(String source) {
		String[] split = source.split(";");
		String name = new String(Base64.getUrlDecoder().decode(split[0]));
		URI uri = null;
		try {
			uri = new URI(new String(Base64.getUrlDecoder().decode(split[1])));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Link(name, uri);
	}

}
