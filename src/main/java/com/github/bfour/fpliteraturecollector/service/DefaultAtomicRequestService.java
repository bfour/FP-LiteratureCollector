package com.github.bfour.fpliteraturecollector.service;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
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
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.OrientDBAtomicRequestDAO;

public class DefaultAtomicRequestService extends
		EventCreatingEntityCRUDService<AtomicRequest> implements
		AtomicRequestService {

	private static DefaultAtomicRequestService instance;

	private DefaultAtomicRequestService(OrientDBGraphService graphService,
			boolean forceCreateNewInstance) {
		super(OrientDBAtomicRequestDAO.getInstance(graphService,
				forceCreateNewInstance));
	}

	public static DefaultAtomicRequestService getInstance(
			OrientDBGraphService graphService, boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultAtomicRequestService(graphService,
					forceCreateNewInstance);
		return instance;
	}

	@Override
	public AtomicRequest create(AtomicRequest entity) throws ServiceException {
		checkIntegrity(entity);
		return super.create(entity);
	}

	@Override
	public AtomicRequest update(AtomicRequest oldEntity, AtomicRequest newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	private void checkIntegrity(AtomicRequest entity) throws ServiceException {
		if (entity.getSearchEngine() == null)
			throw new ServiceException("search engine must not be null");
		if (entity.getSearchString() == null)
			throw new ServiceException("search string must not be null");
		if (entity.getSearchString().isEmpty())
			throw new ServiceException("search string must not be empty");
		if (entity.getResults() == null)
			throw new ServiceException(
					"results list must not be null (may be empty)");
	}

}
