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
