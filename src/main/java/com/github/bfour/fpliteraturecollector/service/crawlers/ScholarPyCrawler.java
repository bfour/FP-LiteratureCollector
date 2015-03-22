package com.github.bfour.fpliteraturecollector.service.crawlers;

import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;

public class ScholarPyCrawler extends Crawler {

//	private static class ScholarPyWorker extends
//			SwingWorker<List<Literature>, Literature> {
//
//		private PythonInterpreter interpreter;
//		private String scriptname;
//
//		public ScholarPyWorker() {
//			interpreter = new PythonInterpreter();
//			PythonInterpreter.initialize(System.getProperties(),
//					System.getProperties(), new String[] { "-c 1",
//							"--author \"albert einstein\"",
//							"--phrase \"quantum theory\"" });
//			scriptname = "scholar.py-master/scholar.py";
//		}
//
//		@Override
//		protected List<Literature> doInBackground() throws Exception {
//			List<Literature> list = new LinkedList<Literature>();
//			interpreter.execfile(scriptname);
//			// interpreter.setOut(new );
//			return list;
//		}
//
//	}

	public ScholarPyCrawler() {
		super(null, SupportedSearchEngine.GOOGLE_SCHOLAR); // TODO
	}

}
