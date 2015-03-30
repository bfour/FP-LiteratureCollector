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

import java.util.LinkedList;
import java.util.List;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingEntityCRUDService;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.OrientDBQueryDAO;

public class DefaultQueryService extends
		EventCreatingEntityCRUDService<Query, OrientDBQueryDAO> implements
		QueryService {

	private static DefaultQueryService instance;

	private DefaultQueryService(OrientDBGraphService graphService,
			boolean forceCreateNewInstance, AtomicRequestService atomReqServ,
			LiteratureService litServ, AuthorService authServ) {
		super(OrientDBQueryDAO.getInstance(graphService,
				forceCreateNewInstance, atomReqServ, litServ, authServ));
	}

	public static DefaultQueryService getInstance(
			OrientDBGraphService graphService, boolean forceCreateNewInstance,
			AtomicRequestService atomReqServ, LiteratureService litServ,
			AuthorService authServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultQueryService(graphService,
					forceCreateNewInstance, atomReqServ, litServ, authServ);
		return instance;
	}

	@Override
	public synchronized Query create(Query entity) throws ServiceException {
		entity = setStatus(entity);
		checkIntegrity(entity);
		return super.create(entity);
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
		if (entity.getStatus() == null)
			throw new ServiceException("status must not be null");
	}

	@Override
	public synchronized Query getByQueuePosition(int position)
			throws ServiceException {
		try {
			Query q = getDAO().getByQueuePosition(position);
			if (q == null)
				return null;
			return q;
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
		if (query.getStatus() == QueryStatus.IDLE
				|| query.getStatus() == QueryStatus.FINISHED_WITH_ERROR
				|| query.getStatus() == QueryStatus.QUEUED) {
			Query newQuery = new QueryBuilder(query)
					.setQueuePosition(getMaxQueuePosition() + 1)
					.setStatus(QueryStatus.QUEUED).getObject();
			return update(query, newQuery);
		}
		return query;
	}

	@Override
	public synchronized void queueAll() throws ServiceException {
		for (Query q : getAll()) {
			queue(q);
		}
	}

	@Override
	public synchronized Query getFirstInQueueForCrawler(Crawler crawler)
			throws ServiceException {
		// TODO (low) improve performance by using direct SQL max() query
		int pos = 1;
		Query q;
		while ((q = getByQueuePosition(pos)) != null) {
			pos++;
			if (getFirstUnprocessedRequestForCrawler(q, crawler) != null)
				return q;
		}
		return null;
	}

	@Override
	public boolean hasAnyUnprocessedRequest() throws ServiceException {
		DataIterator<Query> iter = getAllByStream();
		try {
			while (iter.hasNext()) {
				Query query;
				query = iter.next();
				for (AtomicRequest atomReq : query.getAtomicRequests()) {
					if (atomReq.getResults() == null
							|| atomReq.getResults().isEmpty())
						return true;
				}
			}
		} catch (DatalayerException e) {
			throw new ServiceException(e);
		}
		return false;
	}

	@Override
	public synchronized AtomicRequest getFirstUnprocessedRequestForCrawler(
			Query query, Crawler crawler) throws ServiceException {

		for (AtomicRequest atomReq : query.getAtomicRequests()) {
			if (!atomReq.getCrawler().equals(crawler))
				continue;
			if (atomReq.getResults() == null || atomReq.getResults().isEmpty())
				return atomReq;
		}

		return null;

	}

	private List<AtomicRequest> getUnprocessedAtomicRequests(Query query) {
		List<AtomicRequest> list = new LinkedList<AtomicRequest>();
		for (AtomicRequest atomReq : query.getAtomicRequests()) {
			if (atomReq.getResults() == null || atomReq.getResults().isEmpty())
				list.add(atomReq);
		}
		return list;
	}

	private int getMaxQueuePosition() throws ServiceException {
		// TODO (low) improve performance by using direct SQL max() query
		int max = 0;
		for (Query q : getAll()) {
			Integer pos = q.getQueuePosition();
			if (pos != null && pos > max)
				max = pos;
		}
		return max;
	}

	@Override
	public void setAllIdleOrFinished() throws ServiceException {
		for (Query q : getAll()) {
			update(q, setInitialStatus(q));
		}
	}

	private Query setStatus(Query q) {
		if (q.getStatus() != null)
			return q;
		if (!q.getAtomicRequests().isEmpty()
				&& getUnprocessedAtomicRequests(q).isEmpty())
			return new QueryBuilder(q).setStatus(QueryStatus.FINISHED)
					.getObject();
		return new QueryBuilder(q).setStatus(QueryStatus.IDLE).getObject();
	}

	private Query setInitialStatus(Query q) {
		if (q.getStatus() != null
				&& (q.getStatus() == QueryStatus.FINISHED || q.getStatus() == QueryStatus.FINISHED_WITH_ERROR)) {
			return new QueryBuilder(q).setStatus(q.getStatus()).getObject();
		}
		return new QueryBuilder(q).setStatus(QueryStatus.IDLE).getObject();
	}

}
