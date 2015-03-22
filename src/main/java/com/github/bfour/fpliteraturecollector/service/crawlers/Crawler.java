package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.epop.dataprovider.DataProvider;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.service.abstraction.BackgroundWorker;

public class Crawler extends BackgroundWorker {

	private class CrawlerWorker extends
			SwingWorker<List<Literature>, Literature> {

		private String htmlParams;
		private int maxPageTurns;
		private DataProvider provider;

		public CrawlerWorker(String htmlParams, int maxPageTurns,
				DataProvider provider) {
			this.htmlParams = htmlParams;
			this.maxPageTurns = maxPageTurns;
			this.provider = provider;
		}

		@Override
		protected List<Literature> doInBackground() throws Exception {
			List<Literature> literature = provider.runQuery(htmlParams,
					maxPageTurns);
			return literature;
		}

		@Override
		protected void done() {
			super.done();
			finish();
		}

	}

	private CrawlerWorker worker;
	private List<Literature> results;
	private DataProvider provider;
	private List<SupportedSearchEngine> engines;

	public Crawler(DataProvider provider, SupportedSearchEngine... engines) {
		this.provider = provider;
		this.engines = Arrays.asList(engines);
		setState(BackgroundWorkerState.NOT_STARTED);
	}

	public synchronized void start(String htmlParams, int maxPageTurns) {
		setState(BackgroundWorkerState.RUNNING);
		worker = new CrawlerWorker(htmlParams, maxPageTurns, provider);
		worker.execute();
	}

	@Override
	public synchronized void abort() {
		setState(BackgroundWorkerState.ABORTED);
		worker.cancel(true);
	}

	@Override
	protected synchronized void finish() {
		try {
			results = worker.get();
			setState(BackgroundWorkerState.FINISHED);
			for (FinishListener listener : finishListeners)
				listener.receiveFinished(results);
		} catch (InterruptedException | ExecutionException e) {
			setState(BackgroundWorkerState.ABORTED);
		}
	}

	@Override
	public synchronized List<Exception> getErrors() {
		// TODO Auto-generated method stub
		return new ArrayList<Exception>(0);
	}

	@Override
	public String toString() {
		return CrawlerService.getInstance().getIdentifierForCrawler(this);
	}
	
	/**
	 * Get the SupportedSearchEngines used by this Crawler. This information may
	 * be used by a scheduler to run crawlers in parallel.
	 * 
	 * @return SupportedSearchEngines used by this Crawler
	 */
	@Override
	public List<SupportedSearchEngine> getSearchEnginesBeingAccessed() {
		return engines;
	}

}
