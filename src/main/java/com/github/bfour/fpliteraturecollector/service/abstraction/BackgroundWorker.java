package com.github.bfour.fpliteraturecollector.service.abstraction;

import java.util.LinkedList;
import java.util.List;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.service.crawlers.InvalidStateTransitionException;

public abstract class BackgroundWorker {

	public static interface FinishListener {
		void receiveFinished(List<Literature> results);
	}

	public static interface ProgressListener {
		void receiveProgress(String progress);
	}

	public static interface ResultStreamListener {
		void receiveResult(Literature literature);
	}

	public static enum BackgroundWorkerState {

		/**
		 * BackgroundWorker has not yet been started (before first
		 * {@link BackgroundWorker#start()} call)
		 */
		NOT_STARTED,

		/**
		 * BackgroundWorker is currently crawling ({@link BackgroundWorker#start()} has been
		 * called)
		 */
		RUNNING,

		// /**
		// * BackgroundWorker has been suspended by calling {@link BackgroundWorker#suspend()}
		// */
		// SUSPENDED,

		/**
		 * crawling process has been aborted by calling {@link BackgroundWorker#abort()};
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
	private BackgroundWorkerState state = BackgroundWorkerState.NOT_STARTED;

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
	 * @see {@link BackgroundWorkerState}
	 */
	public final synchronized BackgroundWorkerState getState() {
		return state;
	}

	/**
	 * Sets the state in which the Crawler is currently in.
	 * 
	 * @param state
	 *            state to be set
	 */
	protected final synchronized void setState(BackgroundWorkerState state) {

		// check if valid state transition
		if (this.state == BackgroundWorkerState.NOT_STARTED
				&& (state == BackgroundWorkerState.ABORTED || state == BackgroundWorkerState.FINISHED))
			throw new InvalidStateTransitionException(
					"cannot have transition from NOT_STARTED to ABORTED or FINISHED");

		if ((this.state == BackgroundWorkerState.RUNNING
				|| this.state == BackgroundWorkerState.ABORTED || this.state == BackgroundWorkerState.FINISHED)
				&& state == BackgroundWorkerState.NOT_STARTED)
			throw new InvalidStateTransitionException(
					"cannot have transition from either RUNNING, ABORTED or FINISHED to NOT_STARTED");

		if (this.state == BackgroundWorkerState.ABORTED
				&& state == BackgroundWorkerState.FINISHED)
			throw new InvalidStateTransitionException(
					"cannot have transition from ABORTED to FINISHED");

		if (this.state == BackgroundWorkerState.FINISHED
				&& state == BackgroundWorkerState.ABORTED)
			throw new InvalidStateTransitionException(
					"cannot have transition from FINISHED to ABORTED");

		// set state
		this.state = state;

	}

	/**
	 * Get errors that occurred since the last call to
	 * {@link BackgroundWorker#start(String)} if any.
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

}
