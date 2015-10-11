package com.github.bfour.fpliteraturecollector.test;

import java.net.URISyntaxException;

import org.junit.Test;

import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.service.converters.LinkToStringConverter;
import com.github.bfour.fpliteraturecollector.service.converters.StringToLinkConverter;

public class URIConverterTest {

	@Test
	public void isInvariant() throws URISyntaxException {
		
		Link link = new Link("Test ÖÄÜ;;;", "http://www.example.com/?q=search&p=x");
		LinkToStringConverter linkToString = new LinkToStringConverter();
		StringToLinkConverter stringToLink = new StringToLinkConverter();
		Link linkAfter = stringToLink.convert(linkToString.convert(link));
		
		assert(link.equals(linkAfter));
		
	}

}
