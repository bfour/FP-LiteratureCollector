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
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.jlib.commons.lang.Tuple;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.commons.services.CRUD.BidirectionalCRUDService;

public interface QueryService extends BidirectionalCRUDService<Query> {

	void deleteCascade(Query q) throws ServiceException;

	Query getByQueuePosition(int position) throws ServiceException;

	Tuple<Query, AtomicRequest> getFirstUnprocessedRequestInQueueForCrawler(Crawler crawler)
			throws ServiceException;

	boolean hasAnyUnprocessedRequest() throws ServiceException;

	Query queueUp(Query q) throws ServiceException;

	Query queueDown(Query q) throws ServiceException;

	Query queue(Query q) throws ServiceException;

	void queueAll() throws ServiceException;

	Query unqueue(Query query) throws ServiceException;

	void unqueueAll() throws ServiceException;

	void setAllIdleOrFinished() throws ServiceException;
	
	Query setCrawling(Query q) throws ServiceException;
	
	Query setNotCrawling(Query q) throws ServiceException;

}
