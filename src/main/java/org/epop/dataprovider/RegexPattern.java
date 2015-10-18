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
