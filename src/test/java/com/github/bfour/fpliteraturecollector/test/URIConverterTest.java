/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
