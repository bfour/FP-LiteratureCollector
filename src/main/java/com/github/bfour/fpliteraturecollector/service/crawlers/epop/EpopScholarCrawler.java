package com.github.bfour.fpliteraturecollector.service.crawlers.epop;

import java.util.List;

import javax.swing.SwingWorker;

import org.epop.dataprovider.googlescholar.GoogleScholarProvider;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class EpopScholarCrawler extends Crawler {

	private static class EpopScholarCrawlerWorker extends
			SwingWorker<List<Literature>, Literature> {

		String htmlParams;
		
		public EpopScholarCrawlerWorker(String htmlParams) {
			this.htmlParams = htmlParams;
		}

		@Override
		protected List<Literature> doInBackground() throws Exception {
			GoogleScholarProvider provider = new GoogleScholarProvider();
			List<Literature> literature = provider.runQuery(htmlParams, 1);
//			interpreter.execfile(scriptname);
			// interpreter.setOut(new );
			return literature;
		}

	}

	public EpopScholarCrawler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(String htmlParams) {
		new EpopScholarCrawlerWorker(htmlParams).execute();
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public Exception getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SupportedSearchEngine> getSearchEnginesBeingAccessed() {
		// TODO Auto-generated method stub
		return null;
	}

}
