package com.github.bfour.fpliteraturecollector.service.converters;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.springframework.core.convert.converter.Converter;

import com.github.bfour.fpliteraturecollector.domain.Link;

public class StringToLinkConverter implements Converter<String, Link> {

	@Override
	public Link convert(String source) {

		String[] split = source.split(";");

		String name = split[0].equals(" ") ? null : new String(Base64
				.getUrlDecoder().decode(split[0]));

		URI uri = null;
		try {
			uri = new URI(new String(Base64.getUrlDecoder().decode(split[1])));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String reference = split[2].equals(" ") ? null : new String(Base64
				.getUrlDecoder().decode(split[2]));

		return new Link(name, uri, reference);

	}

}
