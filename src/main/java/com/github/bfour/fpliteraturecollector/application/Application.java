package com.github.bfour.fpliteraturecollector.application;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
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

import java.net.URLEncoder;

import javax.swing.JOptionPane;

import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.Utils;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;
import com.github.bfour.fpliteraturecollector.service.crawlers.epop.EpopScholarCrawler;

// TODO import mit einfacher text-file
// TODO evtl. request-generator tool (Kombinations-Tool)

// TODO letzter Schritt: Output nur distinct; CSV generieren

public class Application {

	public static void main(String[] args) {

		try {
//			ServiceManager servMan = ServiceManager
//					.getInstance(ServiceManagerMode.DEFAULT);
			
			new EpopScholarCrawler().start("q="+URLEncoder.encode("e-health wearable", "UTF-8"));
			
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							null,
							"Sorry, the application cannot continue and will terminate.\n\n"
									+ "This might be because the application is not configured properly or the database is unavailable.\n"
									+ "Reinstalling the application might solve this problem.\n"
									+ "Please report this to the developer at https://github.com/bfour/FP-LiteratureCollector/issues.\n"
									+ "Details: " + e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
		}

	}

}
