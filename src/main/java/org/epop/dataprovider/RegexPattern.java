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

package org.epop.dataprovider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public enum RegexPattern {

	DOI("\\b(10\\.\\d{3,4}(?:[.]\\d+)*\\/(?:(?![\"&\\'])\\S)+)\\b");

	private String string;
	private Pattern pattern;

	RegexPattern(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}

	public Matcher getMatcher(String testString) throws PatternSyntaxException {
		if (pattern == null)
			pattern = Pattern.compile(this.string);
		return pattern.matcher(testString);
	}

}
