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
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingEntityCRUDService;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.OrientDBQueryDAO;

public class DefaultQueryService extends EventCreatingEntityCRUDService<Query>
		implements QueryService {

	private static DefaultQueryService instance;

	private DefaultQueryService(OrientDBGraphService graphService,
			boolean forceCreateNewInstance) {
		super(OrientDBQueryDAO
				.getInstance(graphService, forceCreateNewInstance));
	}

	public static DefaultQueryService getInstance(
			OrientDBGraphService graphService, boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultQueryService(graphService,
					forceCreateNewInstance);
		return instance;
	}

	@Override
	public Query create(Query entity) throws ServiceException {
		checkIntegrity(entity);
		return super.create(entity);
	}

	@Override
	public Query update(Query oldEntity, Query newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	private void checkIntegrity(Query entity) throws ServiceException {
		if (entity.getAtomicRequests() == null)
			throw new ServiceException(
					"atomic request list must not be null (may be empty)");
	}

}
