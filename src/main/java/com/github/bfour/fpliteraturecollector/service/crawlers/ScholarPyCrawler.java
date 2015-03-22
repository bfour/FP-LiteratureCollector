package com.github.bfour.fpliteraturecollector.service.crawlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import org.python.util.PythonInterpreter;

import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;

public class ScholarPyCrawler extends Crawler {

	private static class ScholarPyWorker extends
			SwingWorker<List<Literature>, Literature> {

		private PythonInterpreter interpreter;
		private String scriptname;

		public ScholarPyWorker() {
			interpreter = new PythonInterpreter();
			PythonInterpreter.initialize(System.getProperties(),
					System.getProperties(), new String[] { "-c 1",
							"--author \"albert einstein\"",
							"--phrase \"quantum theory\"" });
			scriptname = "scholar.py-master/scholar.py";
		}

		@Override
		protected List<Literature> doInBackground() throws Exception {
			List<Literature> list = new LinkedList<Literature>();
			interpreter.execfile(scriptname);
			// interpreter.setOut(new );
			return list;
		}

	}

	private List<SupportedSearchEngine> engines;

	public ScholarPyCrawler() {
		this.engines = new ArrayList<SupportedSearchEngine>(1);
		this.engines.add(SupportedSearchEngine.GOOGLE_SCHOLAR);
	}

	@Override
	public synchronized void start(String searchString, int maxPageTurns) {

	}

	@Override
	public synchronized void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SupportedSearchEngine> getSearchEnginesBeingAccessed() {
		return engines;
	}

	@Override
	public Exception getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

}
