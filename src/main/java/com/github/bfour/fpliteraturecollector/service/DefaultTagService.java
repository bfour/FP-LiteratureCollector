package com.github.bfour.fpliteraturecollector.service;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */


import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingCRUDService;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;

public class DefaultTagService extends EventCreatingCRUDService<Tag> implements
		TagService {

	private static DefaultTagService instance;

	private DefaultTagService(OrientDBGraphService graphService) {
		// TODO implement
		super(null);
		// super(OrientDBPersonDAO.getInstance(graphService));
	}

	public static DefaultTagService getInstance(
			OrientDBGraphService graphService) {
		if (instance == null)
			instance = new DefaultTagService(graphService);
		return instance;
	}

	@Override
	public Tag create(Tag entity) throws ServiceException {
		integrityCheck(entity);
		return super.create(entity);
	}

	@Override
	public Tag update(Tag oldEntity, Tag newEntity) throws ServiceException {
		integrityCheck(newEntity);
		return super.update(oldEntity, newEntity);
	}
	
	private void integrityCheck(Tag tag) throws ServiceException {
		if (tag.getName() == null)
			throw new ServiceException("name of tag must be specified");
		if (tag.getColour() == null)
			throw new ServiceException("colour of tag must be specified");
	}

}
