package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import org.epop.dataprovider.Paper;
import org.epop.dataprovider.Query;
import org.epop.dataprovider.googlescholar.GoogleScholarProvider;
import org.python.util.PythonInterpreter;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;

public class EpopScholarCrawler extends Crawler {

	private static class EpopScholarCrawlerWorker extends
			SwingWorker<List<Literature>, Literature> {

		String query;
		
		public EpopScholarCrawlerWorker(String query) {
			this.query = query;
		}

		@Override
		protected List<Literature> doInBackground() throws Exception {
			GoogleScholarProvider provider = new GoogleScholarProvider();
			List<Paper> papers = provider.runQuery(new Query(query), 1);
			for (Paper paper : papers) {
				System.out.println(paper.getTitle());
				LiteratureBuilder builder = new LiteratureBuilder();
				builder.setTitle(paper.getTitle());
//				paper.getAuthors()
//				builder.setAuthors(authors);
			}
			List<Literature> list = new LinkedList<Literature>();
//			interpreter.execfile(scriptname);
			// interpreter.setOut(new );
			return list;
		}

	}

	public EpopScholarCrawler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(String searchString) {
		new EpopScholarCrawlerWorker(searchString).execute();
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
