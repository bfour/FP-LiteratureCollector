package org.epop.dataprovider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.bfour.fpliteraturecollector.domain.builders.AuthorBuilder;

public class Utils {

	private static Pattern NAME_PATTERN;

	public static void setFirstMiddleLastNameFromNameString(
			AuthorBuilder builder, String name) throws PatternMismatchException {

		name = name.trim();
		name = name.replace("\r\n", "");
		name = name.replace("\n", "");
		name = cleanName(name);

		if (name.isEmpty())
			return;

		if (NAME_PATTERN == null)
			NAME_PATTERN = Pattern.compile("(((\\S+) )*)((\\S+)? )(\\S+)");
		Matcher matcher = NAME_PATTERN.matcher(name);
		if (!matcher.find())
			throw new PatternMismatchException("NAME_PATTERN did not match");
		String first = matcher.group(1).trim();
		String middle = matcher.group(5).trim();
		String last = matcher.group(6).trim();

		if (first.isEmpty()) {
			first = middle;
			middle = "";
		}

		builder.setFirstName(first);
		builder.setMiddleName(middle);
		builder.setLastName(last);

	}

	private static String cleanName(String name) {
		name = name.replaceAll("<\\D+?>", "");
		name = name.trim();
		return name;
	}

}
