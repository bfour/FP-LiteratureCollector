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
