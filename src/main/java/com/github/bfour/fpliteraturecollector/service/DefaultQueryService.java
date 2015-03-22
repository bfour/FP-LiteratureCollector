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

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingEntityCRUDService;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.OrientDBQueryDAO;

public class DefaultQueryService extends
		EventCreatingEntityCRUDService<Query, OrientDBQueryDAO> implements
		QueryService {

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
	public synchronized Query create(Query entity) throws ServiceException {
		checkIntegrity(entity);
		Query q = super.create(entity);
		return queue(q);
	}

	@Override
	public synchronized Query update(Query oldEntity, Query newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	private void checkIntegrity(Query entity) throws ServiceException {
		if (entity.getAtomicRequests() == null)
			throw new ServiceException(
					"atomic request list must not be null (may be empty)");
	}

	@Override
	public synchronized Query getByQueuePosition(int position) throws ServiceException {
		try {
			return getDAO().getByQueuePosition(position);
		} catch (DatalayerException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public synchronized Query queueUp(Query query) throws ServiceException {

		Query predecessor = getByQueuePosition(query.getQueuePosition() - 1);
		if (predecessor == null) {
			// no predecessor, we're already at beginning of queue do nothing
			return query;
		}

		// switch this with predecessor
		QueryBuilder qBuilder = new QueryBuilder(query);
		QueryBuilder predBuilder = new QueryBuilder(predecessor);
		qBuilder.setQueuePosition(predecessor.getQueuePosition());
		predBuilder.setQueuePosition(query.getQueuePosition());

		update(predecessor, predBuilder.getObject());
		return update(query, qBuilder.getObject());

	}

	@Override
	public synchronized Query queueDown(Query query) throws ServiceException {

		Query successor = getByQueuePosition(query.getQueuePosition() + 1);
		if (successor == null) {
			// no successor, we're already at end of queue do nothing
			return query;
		}

		// switch this with successor
		QueryBuilder qBuilder = new QueryBuilder(query);
		QueryBuilder sucBuilder = new QueryBuilder(successor);
		qBuilder.setQueuePosition(successor.getQueuePosition());
		sucBuilder.setQueuePosition(query.getQueuePosition());

		update(successor, sucBuilder.getObject());
		return update(query, qBuilder.getObject());

	}

	@Override
	public synchronized Query queue(Query query) throws ServiceException {

		// TODO (low) improve performance by using direct SQL max() query
		int max = 0;
		for (Query q : getAll()) {
			Integer pos = q.getQueuePosition();
			if (pos != null && pos > max)
				max = pos;
		}

		QueryBuilder qBuilder = new QueryBuilder(query);
		qBuilder.setQueuePosition(max + 1);

		return update(query, qBuilder.getObject());

	}

}
