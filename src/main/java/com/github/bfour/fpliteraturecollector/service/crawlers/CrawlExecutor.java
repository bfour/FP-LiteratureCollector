package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.domain.builders.AtomicRequestBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.QueryService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker;

public class CrawlExecutor extends BackgroundWorker {

	private class CrawlerWorker extends SwingWorker<Void, Void> {

		private List<Exception> exceptions;
		private QueryService qServ;
		private List<Exception> errors;
		private Crawler crawler;

		public CrawlerWorker(ServiceManager servMan, Crawler crawler) {
			exceptions = new LinkedList<Exception>();
			this.qServ = servMan.getQueryService();
			this.errors = new ArrayList<Exception>();
			this.crawler = crawler;
		}

		@Override
		protected Void doInBackground() {
			Query topQuery;
			try {
				while ((topQuery = qServ.getFirstInQueueForCrawler(crawler)) != null) {

					// set query status to crawling
					QueryBuilder qBuilder = new QueryBuilder(topQuery);
					qBuilder.setStatus(QueryStatus.CRAWLING);
					topQuery = qServ.update(topQuery, qBuilder.getObject());

					// fill atomic request with results
					AtomicRequest undoneReq = qServ
							.getFirstUnprocessedRequestForCrawler(topQuery,
									crawler);
					List<Literature> results = crawler.process(undoneReq);
					AtomicRequestBuilder atomReqBuilder = new AtomicRequestBuilder(
							undoneReq);
					atomReqBuilder.setResults(results);

					// update query with finished atomic request
					qBuilder = new QueryBuilder(topQuery);
					List<AtomicRequest> atomReqList = topQuery
							.getAtomicRequests();
					atomReqList.set(atomReqList.indexOf(undoneReq),
							atomReqBuilder.getObject());
					qBuilder.setAtomicRequests(atomReqList);
					qServ.update(topQuery, qBuilder.getObject());

				}
			} catch (Exception e) {
				exceptions.add(e);
			}
			return null;
		}

		@Override
		protected void done() {
			super.done();
			finish();
		}

		public List<Exception> getErrors() {
			return errors;
		}

	}

	private static CrawlExecutor instance;
	private ServiceManager servMan;
	private List<CrawlerWorker> workers;

	private CrawlExecutor(ServiceManager servMan) {
		this.servMan = servMan;
		this.workers = new ArrayList<CrawlerWorker>();
	}

	public static CrawlExecutor getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new CrawlExecutor(servMan);
		return instance;
	}

	public synchronized void start() {
		try {
			setState(BackgroundWorkerState.RUNNING);
		} catch (InvalidStateTransitionException e) {
			// TODO Auto-generated catch block
			return;
		}
		// queue all queries
		try {
			servMan.getQueryService().queueAll();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			return;
		}
		// for each crawler, create a worker
		// all workers - each standing for a crawler - will then run in parallel
		for (Crawler crawler : CrawlerService.getInstance()
				.getAvailableCrawlers()) {
			CrawlerWorker worker = new CrawlerWorker(servMan, crawler);
			worker.execute();
			workers.add(worker);
		}
	}

	public synchronized void rerunAll() {
		// TODO
	}

	@Override
	public synchronized void abort() {
		try {
			setState(BackgroundWorkerState.ABORTED);
		} catch (InvalidStateTransitionException e) {
			// TODO Auto-generated catch block
			return;
		}
		for (CrawlerWorker worker : workers)
			worker.cancel(true);
	}

	@Override
	protected synchronized void finish() {
		try {
			setState(BackgroundWorkerState.FINISHED);
		} catch (InvalidStateTransitionException e) {
			// TODO Auto-generated catch block
			return;
		}
		for (FinishListener listener : finishListeners)
			listener.receiveFinished();
	}

	@Override
	public List<Exception> getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

}
