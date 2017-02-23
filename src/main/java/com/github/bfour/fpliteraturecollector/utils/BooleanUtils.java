package com.github.bfour.fpliteraturecollector.utils;

import java.util.HashSet;
import java.util.Set;

import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.jlib.commons.logic.ContainsAnyOfExpression;
import com.github.bfour.jlib.commons.logic.ContainsExpression;
import com.github.bfour.jlib.commons.services.ServiceException;

public class BooleanUtils {

	public static ContainsExpression hasTag(ServiceManager servMan,
			String tagName) throws ServiceException {
		return new ContainsExpression("tags", servMan.getTagService()
				.getByName(tagName));
	}

	public static ContainsAnyOfExpression hasAnyOfPrefixedTags(
			ServiceManager servMan, String prefix) throws ServiceException {
		return new ContainsAnyOfExpression("tags", servMan.getTagService()
				.getByPrefix(prefix));
	}

	public static ContainsAnyOfExpression hasAnyOfTags(ServiceManager servMan,
			String... tagNames) throws ServiceException {
		Set<Tag> tags = new HashSet<Tag>(tagNames.length);
		for (String tagName : tagNames) {
			tags.add(servMan.getTagService().getByName(tagName));
		}
		return new ContainsAnyOfExpression("tags", tags);
	}

}
