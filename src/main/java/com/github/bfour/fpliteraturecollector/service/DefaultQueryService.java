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

package com.github.bfour.fpliteraturecollector.service;

import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.database.DAO.QueryDAO;
import com.github.bfour.jlib.commons.lang.Tuple;
import com.github.bfour.jlib.commons.services.DatalayerException;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.commons.services.CRUD.DataIterator;
import com.github.bfour.jlib.commons.services.CRUD.EventCreatingCRUDService;

public class DefaultQueryService extends EventCreatingCRUDService<Query>
		implements QueryService {

	private static DefaultQueryService instance;
	private AtomicRequestService atomReqServ;
	private ProtocolEntryService protocolServ;
	private QueryDAO DAO;

	private DefaultQueryService(QueryDAO DAO, boolean forceCreateNewInstance,
			AtomicRequestService atomReqServ, ProtocolEntryService protocolServ) {
		super(DAO);
		this.DAO = DAO;
		this.atomReqServ = atomReqServ;
		this.protocolServ = protocolServ;
	}

	public static DefaultQueryService getInstance(QueryDAO DAO,
			boolean forceCreateNewInstance, AtomicRequestService atomReqServ,
			ProtocolEntryService protocolServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultQueryService(DAO, forceCreateNewInstance,
					atomReqServ, protocolServ);
		return instance;
	}

	@Override
	public synchronized Query create(Query entity) throws ServiceException {
		entity = setStatus(entity);
		checkIntegrity(entity);
		Query created = super.create(entity);
		protocolServ.create(new ProtocolEntry("created query " + created.getID()
				+ " " + created.getName()));
		return created;
	}

	@Override
	public synchronized Query update(Query oldEntity, Query newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		Query updated = super.update(oldEntity, setStatus(newEntity));
		protocolServ.create(new ProtocolEntry("updated query " + updated.getID()
				+ " " + updated.getName()));
		return updated;
	}

	@Override
	public void delete(Query entity) throws ServiceException {
		if (entity.getAtomicRequests() != null)
			for (AtomicRequest ar : entity.getAtomicRequests())
				atomReqServ.delete(ar);
		super.delete(entity);
		protocolServ.create(new ProtocolEntry("deleted query " + entity.getID()
				+ " " + entity.getName()));
	}

	@Override
	public synchronized void deleteCascade(Query q) throws ServiceException {
		if (q.getAtomicRequests() != null)
			for (AtomicRequest ar : q.getAtomicRequests())
				atomReqServ.deleteCascade(ar);
		super.delete(q);
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
			Query q = DAO.getByQueuePosition(position);
			if (q == null)
				return null;
			return q;
		} catch (DatalayerException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public synchronized Query queueUp(Query query) throws ServiceException {

		if (query.getStatus() == QueryStatus.FINISHED
				|| query.getStatus() == QueryStatus.FINISHED_WITH_ERROR)
			return query;

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

		// TODO (high) wrap in transaction
		update(predecessor, predBuilder.getObject());
		return update(query, qBuilder.getObject());

	}

	@Override
	public synchronized Query queueDown(Query query) throws ServiceException {

		if (query.getStatus() == QueryStatus.FINISHED
				|| query.getStatus() == QueryStatus.FINISHED_WITH_ERROR)
			return query;

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

		if (query.getStatus() == QueryStatus.FINISHED
				|| query.getStatus() == QueryStatus.FINISHED_WITH_ERROR)
			return query;

		Query newQuery = new QueryBuilder(query)
				.setQueuePosition(getMaxQueuePosition() + 1)
				.setStatus(QueryStatus.QUEUED).getObject();
		return update(query, newQuery);

	}

	@Override
	public synchronized void queueAll() throws ServiceException {
		for (Query q : getAll())
			queue(q);
	}

	@Override
	public synchronized Query unqueue(Query query) throws ServiceException {
		Query newQuery = new QueryBuilder(query).setQueuePosition(null)
				.getObject();
		return update(query, newQuery);
	}

	@Override
	public synchronized void unqueueAll() throws ServiceException {
		for (Query q : getAll())
			unqueue(q);
	}

	@Override
	public boolean hasAnyUnprocessedRequest() throws ServiceException {
		DataIterator<Query> iter = getAllByStream();
		try {
			while (iter.hasNext()) {
				Query query;
				query = iter.next();
				for (AtomicRequest atomReq : query.getAtomicRequests()) {
					if (!atomReq.isProcessed())
						return true;
				}
			}
		} catch (DatalayerException e) {
			throw new ServiceException(e);
		}
		return false;
	}

	@Override
	public synchronized Tuple<Query, AtomicRequest> getFirstUnprocessedRequestInQueueForCrawler(
			Crawler crawler) throws ServiceException {

		// TODO (low) improve performance by using direct SQL max() query
		int pos = 1;
		Query q;
		while ((q = getByQueuePosition(pos)) != null) {
			pos++;
			for (AtomicRequest atomReq : q.getAtomicRequests()) {
				if (!atomReq.getCrawler().equals(crawler))
					continue;
				if (!atomReq.isProcessed())
					return new Tuple<>(q, atomReq);
			}
		}
		return null;

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
			if (q.getStatus() == QueryStatus.FINISHED
					|| q.getStatus() == QueryStatus.FINISHED_WITH_ERROR)
				continue;
			update(q, new QueryBuilder(q).setStatus(QueryStatus.IDLE)
					.getObject());
		}
	}

	private Query setStatus(Query q) {

		if (q.getStatus() == QueryStatus.CRAWLING)
			return q;

		QueryStatus status = null;

		boolean hasError = false;
		boolean hasUnprocessed = false;
		for (AtomicRequest atomReq : q.getAtomicRequests()) {
			if (!atomReq.isProcessed())
				hasUnprocessed = true;
			if (atomReq.getProcessingError() != null)
				hasError = true;
		}

		if (hasUnprocessed && q.getQueuePosition() == null)
			status = QueryStatus.IDLE;
		else if (hasUnprocessed && q.getQueuePosition() != null)
			status = QueryStatus.QUEUED;
		else if (hasError)
			status = QueryStatus.FINISHED_WITH_ERROR;
		else
			status = QueryStatus.FINISHED;

		return new QueryBuilder(q).setStatus(status).getObject();

	}

	@Override
	public Query setCrawling(Query q) throws ServiceException {
		return update(q, new QueryBuilder(q).setStatus(QueryStatus.CRAWLING)
				.getObject());
	}

	@Override
	public Query setNotCrawling(Query q) throws ServiceException {
		return update(q, new QueryBuilder(q).setStatus(QueryStatus.IDLE)
				.getObject());
	}

}
