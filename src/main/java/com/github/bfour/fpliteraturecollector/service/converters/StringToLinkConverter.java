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
