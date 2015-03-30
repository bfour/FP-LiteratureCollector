package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;
import com.github.bfour.fpliteraturecollector.domain.builders.AtomicRequestBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.QueryService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker;

public class CrawlExecutor extends BackgroundWorker implements FeedbackProvider {

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
					try {
						// set query status to crawling
						QueryBuilder qBuilder = new QueryBuilder(topQuery);
						qBuilder.setStatus(QueryStatus.CRAWLING);
						topQuery = qServ.update(topQuery, qBuilder.getObject());

						// fill atomic request with results
						AtomicRequest undoneReq = qServ
								.getFirstUnprocessedRequestForCrawler(topQuery,
										crawler);
						List<Literature> results = crawler.process(undoneReq);

						// update query with finished atomic request
						List<AtomicRequest> atomReqList = topQuery
								.getAtomicRequests();
						atomReqList.set(
								atomReqList.indexOf(undoneReq),
								new AtomicRequestBuilder(undoneReq).setResults(
										results).getObject());
						qBuilder = new QueryBuilder(topQuery);
						qBuilder.setAtomicRequests(atomReqList);
						qBuilder.setStatus(QueryStatus.FINISHED);
						topQuery = qServ.update(topQuery, qBuilder.getObject());

					} catch (Exception e) {
						exceptions.add(e);
						qServ.update(topQuery, new QueryBuilder(topQuery)
								.setStatus(QueryStatus.FINISHED_WITH_ERROR)
								.getObject());
					}
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
	private FeedbackProviderProxy feedbackProxy;
	private ServiceManager servMan;
	private List<CrawlerWorker> workers;

	private CrawlExecutor(ServiceManager servMan) {
		this.feedbackProxy = new FeedbackProviderProxy();
		this.servMan = servMan;
		this.workers = new ArrayList<CrawlerWorker>();
	}

	public static CrawlExecutor getInstance(ServiceManager servMan) {
		if (instance == null)
			instance = new CrawlExecutor(servMan);
		return instance;
	}

	/**
	 * 
	 * @return whether the crawler has been started
	 */
	public synchronized boolean start() {

		try {
			if (!servMan.getQueryService().hasAnyUnprocessedRequest()) {
				feedbackProxy
						.fireFeedback(new Feedback(
								null,
								"<html>Did not start crawling, because there are <b>no queries that could be run</b>.</html>",
								FeedbackType.INFO));
				return false;
			}
		} catch (ServiceException e1) {
			feedbackProxy
					.fireFeedback(new Feedback(
							null,
							"<html>Did not start crawling, because I could not determine <br/> whether there are queries that could be run.</html>",
							e1.getMessage(), FeedbackType.INFO));
			return false;
		}

		try {
			setState(BackgroundWorkerState.RUNNING);
		} catch (InvalidStateTransitionException e) {
			feedbackProxy
					.fireFeedback(new Feedback(
							null,
							"Sorry, failed to set Crawler to running, state transition invalid.",
							e.getMessage(), FeedbackType.WARN));
			return false;
		}

		// queue all queries
		try {
			servMan.getQueryService().queueAll();
		} catch (ServiceException e) {
			feedbackProxy
					.fireFeedback(new Feedback(
							null,
							"Sorry, failed to queue queries, therefore will not start crawling.",
							e.getMessage(), FeedbackType.WARN));
			return false;
		}

		// for each crawler, create a worker
		// all workers - each standing for a crawler - will then run in parallel
		for (Crawler crawler : CrawlerService.getInstance()
				.getAvailableCrawlers()) {
			CrawlerWorker worker = new CrawlerWorker(servMan, crawler);
			worker.execute();
			workers.add(worker);
		}
		
		return true;

	}

	public synchronized void rerunAll() {
		// TODO
	}

	@Override
	public synchronized void abort() {
		try {
			setState(BackgroundWorkerState.ABORTED);
			feedbackProxy.fireFeedback(new Feedback(null,
					"Crawling has been aborted.", FeedbackType.INFO));
		} catch (InvalidStateTransitionException e) {
			feedbackProxy
					.fireFeedback(new Feedback(
							null,
							"Sorry, failed to set Crawler to aborted, state transition invalid.",
							e.getMessage(), FeedbackType.WARN));
		}
		for (CrawlerWorker worker : workers)
			worker.cancel(true);
	}

	@Override
	protected synchronized void finish() {
		try {
			setState(BackgroundWorkerState.FINISHED);
			feedbackProxy.fireFeedback(new Feedback(null,
					"Crawling has been finished.", FeedbackType.SUCCESS));
		} catch (InvalidStateTransitionException e) {
			feedbackProxy
					.fireFeedback(new Feedback(
							null,
							"Sorry, failed to set Crawler to finished, state transition invalid.",
							e.getMessage(), FeedbackType.WARN));
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

	@Override
	public void addFeedbackListener(FeedbackListener arg0) {
		feedbackProxy.addFeedbackListener(arg0);
	}

	@Override
	public void removeFeedbackListener(FeedbackListener arg0) {
		feedbackProxy.removeFeedbackListener(arg0);
	}

}
