package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.LinkedList;
import java.util.List;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;

public abstract class Crawler {

	public static interface FinishListener {
		void receiveFinished(List<Literature> results);
	}

	public static interface ProgressListener {
		void receiveProgress(String progress);
	}

	public static interface ResultStreamListener {
		void receiveResult(Literature literature);
	}

	public static enum CrawlerState {

		/**
		 * Crawler has not yet been started (before first
		 * {@link Crawler#start()} call)
		 */
		NOT_STARTED,

		/**
		 * Crawler is currently crawling ({@link Crawler#start()} has been
		 * called)
		 */
		RUNNING,

		// /**
		// * Crawler has been suspended by calling {@link Crawler#suspend()}
		// */
		// SUSPENDED,

		/**
		 * crawling process has been aborted by calling {@link Crawler#abort()};
		 * results may be incomplete
		 */
		ABORTED,

		/**
		 * crawling process has been finished
		 */
		FINISHED;

	}

	protected List<ProgressListener> progressListeners = new LinkedList<>();
	protected List<ResultStreamListener> resultListeners = new LinkedList<>();
	protected List<FinishListener> finishListeners = new LinkedList<>();
	private CrawlerState state = CrawlerState.NOT_STARTED;

	/**
	 * Start the crawling process.
	 * 
	 * @param htmlParams
	 *            instructions for the Crawler; syntax specific to Crawler
	 * @param maximum
	 *            page turns on website to be crawled
	 */
	public abstract void start(String htmlParams, int maxPageTurns);

	// /**
	// * Pause the crawling process if possible.
	// * @throws OperationNotSupportedException
	// */
	// public abstract void suspend() throws OperationNotSupportedException;

	/**
	 * Abort the crawling process. Won't do anything if the crawler hasn't been
	 * started yet or is finished.
	 */
	public abstract void abort();

	protected abstract void finish();
	
	/**
	 * Get the state in which the Crawler is currently in.
	 * 
	 * @return current state of the Crawler
	 * @see {@link CrawlerState}
	 */
	public final synchronized CrawlerState getState() {
		return state;
	}

	/**
	 * Sets the state in which the Crawler is currently in.
	 * 
	 * @param state
	 *            state to be set
	 */
	protected final synchronized void setState(CrawlerState state) {

		// check if valid state transition
		if (this.state == CrawlerState.NOT_STARTED
				&& (state == CrawlerState.ABORTED || state == CrawlerState.FINISHED))
			throw new InvalidStateTransitionException(
					"cannot have transition from NOT_STARTED to ABORTED or FINISHED");

		if ((this.state == CrawlerState.RUNNING
				|| this.state == CrawlerState.ABORTED || this.state == CrawlerState.FINISHED)
				&& state == CrawlerState.NOT_STARTED)
			throw new InvalidStateTransitionException(
					"cannot have transition from either RUNNING, ABORTED or FINISHED to NOT_STARTED");

		if (this.state == CrawlerState.ABORTED
				&& state == CrawlerState.FINISHED)
			throw new InvalidStateTransitionException(
					"cannot have transition from ABORTED to FINISHED");

		if (this.state == CrawlerState.FINISHED
				&& state == CrawlerState.ABORTED)
			throw new InvalidStateTransitionException(
					"cannot have transition from FINISHED to ABORTED");

		// set state
		this.state = state;

	}

	/**
	 * Get errors that occurred since the last call to
	 * {@link Crawler#start(String)} if any.
	 * 
	 * @return
	 */
	public abstract List<Exception> getErrors();

	/**
	 * Get the SupportedSearchEngines used by this Crawler. This information may
	 * be used by a scheduler to run crawlers in parallel.
	 * 
	 * @return SupportedSearchEngines used by this Crawler
	 */
	public abstract List<SupportedSearchEngine> getSearchEnginesBeingAccessed();

	public synchronized void registerResultStreamListener(
			ResultStreamListener listener) {
		resultListeners.add(listener);
	}

	public synchronized void registerFinishListener(FinishListener listener) {
		finishListeners.add(listener);
	}

	public synchronized void registerProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}
	
	@Override
	public String toString() {
		return CrawlerService.getInstance().getIdentifierForCrawler(this);
	}

}
