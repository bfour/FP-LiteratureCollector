package com.github.bfour.fpliteraturecollector.test;

import java.util.ArrayList;
import java.util.List;

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
import com.github.bfour.fpliteraturecollector.service.AtomicRequestService;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.QueryService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;
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
		assert (atomReqServ.get().hasNext());
		assert (queryServ.get().hasNext());
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
	public void createAndRemoveLiteraturesAndTestDatabaseClean()
			throws ServiceException, DatalayerException {

		List<Literature> literatureList = TestDataCreator
				.createLiteratureList1(authServ);

		for (Literature tag : literatureList)
			literatureList
					.set(literatureList.indexOf(tag), litServ.create(tag));

		// check all literature created properly
		DataIterator<Literature> dbIterator = litServ.get();
		for (Literature tag : literatureList) {
			assert (dbIterator.next().equals(tag));
		}

		// create AtomicRequests
		List<AtomicRequest> atomReqs = new ArrayList<AtomicRequest>(3);
		atomReqs.add(new AtomicRequest(crawlServ.getAvailableCrawlers()
				.iterator().next(), "LDL", literatureList));
		atomReqs.add(new AtomicRequest(crawlServ.getAvailableCrawlers()
				.iterator().next(), "another test &%$$ öäüß ß é Á _:_::___' ",
				literatureList));
		atomReqs.add(new AtomicRequest(crawlServ.getAvailableCrawlers()
				.iterator().next(), ":-) 1861", new ArrayList<Literature>(0)));
		for (AtomicRequest atomReq : atomReqs)
			atomReqs.set(atomReqs.indexOf(atomReq), atomReqServ.create(atomReq));

		// query
		List<Query> queries = new ArrayList<>();
		queries.add(new Query("test query", atomReqs, 1));
		for (Query query : queries)
			queries.set(queries.indexOf(query), queryServ.create(query));

		// check all queries created properly
		DataIterator<Query> queryIterator = queryServ.get();
		for (Query query : queries)
			assert (queryIterator.next().equals(query));

		// delete all
		for (Query query : queries)
			queryServ.delete(query);

		// confirm delete
		assert (queryServ.getAll().isEmpty());

	}

}
