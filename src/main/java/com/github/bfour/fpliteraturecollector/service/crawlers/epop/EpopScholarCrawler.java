package com.github.bfour.fpliteraturecollector.service.crawlers.epop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.epop.dataprovider.googlescholar.GoogleScholarProvider;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class EpopScholarCrawler extends Crawler {

	private class EpopScholarCrawlerWorker extends
			SwingWorker<List<Literature>, Literature> {

		private String htmlParams;
		private int maxPageTurns;

		public EpopScholarCrawlerWorker(String htmlParams, int maxPageTurns) {
			this.htmlParams = htmlParams;
			this.maxPageTurns = maxPageTurns;
		}

		@Override
		protected List<Literature> doInBackground() throws Exception {
			GoogleScholarProvider provider = new GoogleScholarProvider();
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

	private List<SupportedSearchEngine> engines;
	private EpopScholarCrawlerWorker worker;
	private List<Literature> results;

	public EpopScholarCrawler() {
		this.engines = new ArrayList<SupportedSearchEngine>(1);
		this.engines.add(SupportedSearchEngine.GOOGLE_SCHOLAR);
		setState(CrawlerState.NOT_STARTED);
	}

	@Override
	public synchronized void start(String htmlParams, int maxPageTurns) {
		setState(CrawlerState.RUNNING);
		worker = new EpopScholarCrawlerWorker(htmlParams, maxPageTurns);
		worker.execute();
	}

	@Override
	public synchronized void abort() {
		setState(CrawlerState.ABORTED);
		worker.cancel(true);
	}

	@Override
	protected synchronized void finish() {
		try {
			results = worker.get();
			setState(CrawlerState.FINISHED);
			for (FinishListener listener : finishListeners)
				listener.receiveFinished(results);
		} catch (InterruptedException | ExecutionException e) {
			setState(CrawlerState.ABORTED);
		}
	}

	@Override
	public synchronized List<Exception> getErrors() {
		// TODO Auto-generated method stub
		return new ArrayList<Exception>(0);
	}

	@Override
	public List<SupportedSearchEngine> getSearchEnginesBeingAccessed() {
		return engines;
	}

}
