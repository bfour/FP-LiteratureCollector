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

package com.github.bfour.fpliteraturecollector.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.builders.AtomicRequestBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.AtomicRequestService;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.QueryService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;

public class QueryTest {

	private static ServiceManager servMan;
	private static LiteratureService litServ;
	private static AuthorService authServ;
	private static AtomicRequestService atomReqServ;
	private static QueryService queryServ;
	private static CrawlerService crawlServ;

	@BeforeClass
	public static void preClass() throws ServiceException {
		servMan = ServiceManager.getInstance(ServiceManagerMode.TEST);
		servMan.dropAndReinitDatabase();
		litServ = servMan.getLiteratureService();
		authServ = servMan.getAuthorService();
		atomReqServ = servMan.getAtomicRequestService();
		queryServ = servMan.getQueryService();
		crawlServ = servMan.getCrawlerService();
	}

	@After
	public void post() throws ServiceException {
		servMan.resetAllData();
	}

	@AfterClass
	public static void postClass() throws ServiceException {
		servMan.close();
	}

	@Test
	public void returnEmptyListOnEmptyDB() throws ServiceException {
		assert (atomReqServ.getAll().isEmpty());
		assert (queryServ.getAll().isEmpty());
	}

	@Test
	public void iteratorDoesNotHaveNextOnEmptyDB() throws ServiceException,
			DatalayerException {
		assert (atomReqServ.getAllByStream().hasNext());
		assert (queryServ.getAllByStream().hasNext());
	}

	@Test
	public void deleteNonExistentLiteratureExpectNoChange()
			throws ServiceException {

		assert (atomReqServ.getAll().isEmpty());
		atomReqServ.delete(new AtomicRequest());
		assert (atomReqServ.getAll().isEmpty());

		assert (queryServ.getAll().isEmpty());
		queryServ.delete(new Query());
		assert (queryServ.getAll().isEmpty());

	}

	@Test(expected = ServiceException.class)
	public void updateNonExistentLiteratureExpectFailure()
			throws ServiceException {

		assert (atomReqServ.getAll().isEmpty());
		atomReqServ.update(new AtomicRequest(), new AtomicRequest());
		assert (atomReqServ.getAll().isEmpty());

		assert (queryServ.getAll().isEmpty());
		queryServ.update(new Query(), new Query());
		assert (queryServ.getAll().isEmpty());

	}

	@Test
	public void testBasicPersistence() throws ServiceException {

		// q1
		long ID = 1;
		Date creationTime = new Date();
		Date lastChangeTime = new Date();
		String name = "test query 1";
		int queuePos = 1;

		Set<AtomicRequest> atomReqs = new HashSet<AtomicRequest>();
		Crawler crawler = CrawlerService.getInstance().getAvailableCrawlers()
				.iterator().next();
		String searchString = "q=meow";
		int maxPageTurns = 2;
		atomReqs.add(new AtomicRequestBuilder().setCrawler(crawler)
				.setSearchString(searchString).setMaxPageTurns(maxPageTurns)
				.getObject());

		Query q1 = new QueryBuilder().setName(name).setQueuePosition(queuePos)
				.setAtomicRequests(atomReqs).setID(ID)
				.setCreationTime(creationTime)
				.setLastChangeTime(lastChangeTime).getObject();

		queryServ.create(q1);

		Query createdQuery = queryServ.getAll().get(0);
		assert (createdQuery.getID().equals(ID));
		assert (createdQuery.getCreationTime().equals(creationTime));
		assert (createdQuery.getName().equals(name));
		assert (createdQuery.getQueuePosition().equals(queuePos));

		Set<AtomicRequest> createdRequests = createdQuery.getAtomicRequests();
		Iterator<AtomicRequest> iter = createdRequests.iterator();
		assert (createdRequests.size() == atomReqs.size());
		for (AtomicRequest atomReq : atomReqs) {
			assert (iter.next().getCrawler().equals(atomReq.getCrawler()));
			assert (iter.next().getSearchString().equals(atomReq
					.getSearchString()));
			assert (iter.next().getMaxPageTurns().equals(atomReq
					.getMaxPageTurns()));
		}

		// q2
		long ID2 = 2;
		Date creationTime2 = new Date();
		Date lastChangeTime2 = new Date();
		String name2 = "test query 2";
		int queuePos2 = 1;

		Set<AtomicRequest> atomReqs2 = new HashSet<AtomicRequest>();
		Crawler crawler2 = CrawlerService.getInstance().getAvailableCrawlers()
				.iterator().next();
		String searchString2 = "q=oink";
		int maxPageTurns2 = 2;
		atomReqs2.add(new AtomicRequestBuilder().setCrawler(crawler2)
				.setSearchString(searchString2).setMaxPageTurns(maxPageTurns2)
				.getObject());

		Query q2 = new QueryBuilder().setName(name2)
				.setQueuePosition(queuePos2).setAtomicRequests(atomReqs2)
				.setID(ID2).setCreationTime(creationTime2)
				.setLastChangeTime(lastChangeTime2).getObject();

		queryServ.create(q2);

		Query createdQuery2 = queryServ.getAll().get(1);
		assert (createdQuery2.getID().equals(ID2));
		assert (createdQuery2.getCreationTime().equals(creationTime2));
		assert (createdQuery2.getName().equals(name2));
		assert (createdQuery2.getQueuePosition().equals(queuePos2));

		Set<AtomicRequest> createdRequests2 = createdQuery2
				.getAtomicRequests();
		Iterator<AtomicRequest> iter2 = createdRequests2.iterator();
		assert (createdRequests2.size() == atomReqs2.size());
		for (AtomicRequest atomReq2 : atomReqs2) {
			assert (iter2.next().getCrawler().equals(atomReq2.getCrawler()));
			assert (iter2.next().getSearchString().equals(atomReq2
					.getSearchString()));
			assert (iter2.next().getMaxPageTurns().equals(atomReq2
					.getMaxPageTurns()));
		}

		queryServ.delete(createdQuery);
		queryServ.delete(createdQuery2);
		assert (queryServ.getAll().isEmpty());

	}

	@Test
	public void createAndRemoveLiteraturesAndTestDatabaseClean()
			throws ServiceException, DatalayerException {

		List<Literature> literatureList = TestDataCreator
				.createLiteratureList1(authServ);

		for (Literature tag : literatureList)
			literatureList
					.set(literatureList.indexOf(tag), litServ.create(tag));

		// check all literature created properly
		DataIterator<Literature> dbIterator = litServ.getAllByStream();
		for (Literature tag : literatureList) {
			assert (dbIterator.next().equals(tag));
		}

		// create AtomicRequests
		Set<Literature> literatureSet = new HashSet<>(literatureList);
		List<AtomicRequest> atomReqs = new ArrayList<AtomicRequest>(3);
		atomReqs.add(new AtomicRequest(crawlServ.getAvailableCrawlers()
				.iterator().next(), "LDL", 2, literatureSet, true, null));
		atomReqs.add(new AtomicRequest(crawlServ.getAvailableCrawlers()
				.iterator().next(), "another test &%$$ öäüß ß é Á _:_::___' ",
				1, literatureSet, true, null));
		atomReqs.add(new AtomicRequest(crawlServ.getAvailableCrawlers()
				.iterator().next(), ":-) 1861", 0,
				new HashSet<Literature>(0), true, null));
		for (AtomicRequest atomReq : atomReqs)
			atomReqs.set(atomReqs.indexOf(atomReq), atomReqServ.create(atomReq));

		// query
		List<Query> queries = new ArrayList<>();
		queries.add(new Query("test query", new HashSet<>(atomReqs), 1, null));
		for (Query query : queries)
			queries.set(queries.indexOf(query), queryServ.create(query));

		// check all queries created properly
		DataIterator<Query> queryIterator = queryServ.getAllByStream();
		for (Query query : queries)
			assert (queryIterator.next().equals(query));

		// delete all
		for (Query query : queries)
			queryServ.delete(query);

		// confirm delete
		assert (queryServ.getAll().isEmpty());

	}

}
