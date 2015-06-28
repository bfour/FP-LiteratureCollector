package com.github.bfour.fpliteraturecollector.service.crawlers;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback;
import com.github.bfour.fpjgui.abstraction.feedback.Feedback.FeedbackType;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackListener;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProvider;
import com.github.bfour.fpjgui.abstraction.feedback.FeedbackProviderProxy;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.builders.AtomicRequestBuilder;
import com.github.bfour.fpliteraturecollector.domain.builders.QueryBuilder;
import com.github.bfour.fpliteraturecollector.service.AtomicRequestService;
import com.github.bfour.fpliteraturecollector.service.QueryService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker;

public class CrawlExecutor extends BackgroundWorker implements FeedbackProvider {

	private class CrawlerWorker extends SwingWorker<Void, Void> {

		private final Logger LOGGER = Logger.getLogger(CrawlerWorker.class);

		private QueryService qServ;
		private AtomicRequestService atomReqServ;
		private List<Exception> errors;
		private Crawler crawler;

		public CrawlerWorker(ServiceManager servMan, Crawler crawler) {
			this.qServ = servMan.getQueryService();
			this.atomReqServ = servMan.getAtomicRequestService();
			this.errors = new ArrayList<Exception>();
			this.crawler = crawler;
		}

		@Override
		protected Void doInBackground() {
			Tuple<Query, AtomicRequest> topRequest;
			try {
				// get the first atomic request with the corresponding query in
				// queue for the given crawler
				// (search engine)
				while ((topRequest = qServ
						.getFirstUnprocessedRequestInQueueForCrawler(crawler)) != null) {
					try {

						// set crawling
						qServ.setCrawling(topRequest.getA());

						// get results and update
						List<Literature> results = crawler.process(topRequest
								.getB());
						AtomicRequest newAtomReq = new AtomicRequestBuilder(
								topRequest.getB()).setProcessed(true)
								.setResults(new HashSet<>(results)).getObject();
						atomReqServ.update(topRequest.getB(), newAtomReq);

						List<AtomicRequest> atomReqs = new ArrayList<>(
								topRequest.getA().getAtomicRequests());
						atomReqs.set(atomReqs.indexOf(topRequest.getB()),
								newAtomReq);

						qServ.setNotCrawling(new QueryBuilder(topRequest.getA())
								.setAtomicRequests(new HashSet<>(atomReqs))
								.getObject());

					} catch (Exception e) {
						e.printStackTrace();
						LOGGER.error(e);
						List<AtomicRequest> atomReqs = new ArrayList<>(
								topRequest.getA().getAtomicRequests());
						atomReqs.set(
								atomReqs.indexOf(topRequest.getB()),
								new AtomicRequestBuilder(topRequest.getB())
										.setProcessed(true)
										.setProcessingError(e.getMessage())
										.getObject());
						qServ.update(
								topRequest.getA(),
								new QueryBuilder(topRequest.getA())
										.setAtomicRequests(
												new HashSet<>(atomReqs))
										.getObject());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e);
				errors.add(e);
			}
			return null;
		}

		@Override
		protected void done() {
			if (!isCancelled())
				finish(this);
		}

		public List<Exception> getErrors() {
			return errors;
		}

	}

	private static CrawlExecutor instance;
	private FeedbackProviderProxy feedbackProxy;
	private ServiceManager servMan;
	private List<CrawlerWorker> workers;
	private Feedback mainStatusFeedback;

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

	public void initialize() {
		try {
			setState(BackgroundWorkerState.NOT_STARTED);
		} catch (InvalidStateTransitionException e) {
			feedbackProxy.feedbackBroadcasted(new Feedback(null,
					"Sorry, failed to set initial status of crawler.", e
							.getMessage(), FeedbackType.ERROR));
		}
		try {
			servMan.getQueryService().setAllIdleOrFinished();
			servMan.getQueryService().unqueueAll();
		} catch (ServiceException e1) {
			feedbackProxy.feedbackBroadcasted(new Feedback(null,
					"Sorry, failed to set initial query status.", e1
							.getMessage(), FeedbackType.ERROR));
		}
	}

	/**
	 * 
	 * @return whether the crawler has been started
	 */
	public synchronized boolean start() {

		try {
			if (!servMan.getQueryService().hasAnyUnprocessedRequest()) {
				feedbackProxy
						.feedbackBroadcasted(new Feedback(
								null,
								"<html>Did not start crawling, because there are <b>no queries that could be run</b>.</html>",
								FeedbackType.INFO));
				return false;
			}
		} catch (ServiceException e1) {
			feedbackProxy
					.feedbackBroadcasted(new Feedback(
							null,
							"<html>Did not start crawling, because I could not determine <br/> whether there are queries that could be run.</html>",
							e1.getMessage(), FeedbackType.INFO));
			return false;
		}

		try {
			setState(BackgroundWorkerState.RUNNING);
		} catch (InvalidStateTransitionException e) {
			feedbackProxy
					.feedbackBroadcasted(new Feedback(
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
					.feedbackBroadcasted(new Feedback(
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

		// feedback
		mainStatusFeedback = new Feedback(
				null,
				"Crawling",
				null,
				FeedbackType.PROGRESS.getColor(),
				com.github.bfour.fpliteraturecollector.gui.design.Icons.CRAWLING_32
						.getIcon(), FeedbackType.PROGRESS, true);
		feedbackProxy.feedbackBroadcasted(mainStatusFeedback);

		return true;

	}

	public synchronized void rerunAll() {
		// TODO
	}

	public synchronized void abort() {
		feedbackProxy.feedbackRevoked(mainStatusFeedback);
		try {
			setState(BackgroundWorkerState.ABORTED);
			for (CrawlerWorker worker : workers)
				worker.cancel(true);
			servMan.getQueryService().setAllIdleOrFinished();
			servMan.getQueryService().unqueueAll();
			feedbackProxy.feedbackBroadcasted(new Feedback(null,
					"Crawling has been aborted.", FeedbackType.INFO));
		} catch (InvalidStateTransitionException e) {
			feedbackProxy
					.feedbackBroadcasted(new Feedback(
							null,
							"Sorry, failed to set Crawler to aborted, state transition invalid.",
							e.getMessage(), FeedbackType.WARN));
		} catch (ServiceException e) {
			feedbackProxy.feedbackBroadcasted(new Feedback(null,
					"Sorry, failed to set reset query status after abort.", e
							.getMessage(), FeedbackType.WARN));
		}
	}

	protected synchronized void finish(CrawlerWorker crawlerWorker) {
		feedbackProxy.feedbackRevoked(mainStatusFeedback);
		workers.remove(crawlerWorker);
		if (!workers.isEmpty())
			return;
		try {
			setState(BackgroundWorkerState.FINISHED);
			feedbackProxy.feedbackBroadcasted(new Feedback(null,
					"Crawling has been finished.", FeedbackType.SUCCESS));
		} catch (InvalidStateTransitionException e) {
			feedbackProxy
					.feedbackBroadcasted(new Feedback(
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
