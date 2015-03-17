package com.github.bfour.fpliteraturecollector.service.crawlers.epop;

import java.util.List;

import javax.swing.SwingWorker;

import org.epop.dataprovider.googlescholar.GoogleScholarProvider;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class EpopScholarCrawler extends Crawler {

	private static class EpopScholarCrawlerWorker extends
			SwingWorker<List<Literature>, Literature> {

		private String htmlParams;
		private int maxPageTurns;
		private AuthorService authServ;

		public EpopScholarCrawlerWorker(String htmlParams, int maxPageTurns, 
				ServiceManager servMan) {
			this.htmlParams = htmlParams;
			this.maxPageTurns = maxPageTurns;
			this.authServ = servMan.getAuthorService();
		}

		@Override
		protected List<Literature> doInBackground() throws Exception {
			GoogleScholarProvider provider = new GoogleScholarProvider();
			List<Literature> literature = provider.runQuery(htmlParams, maxPageTurns,
					authServ);
			return literature;
		}

	}

	private ServiceManager servMan;
	
	public EpopScholarCrawler(ServiceManager servMan) {
		this.servMan = servMan;
	}

	@Override
	public void start(String htmlParams, int maxPageTurns) {
		new EpopScholarCrawlerWorker(htmlParams, maxPageTurns, servMan).execute();
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
