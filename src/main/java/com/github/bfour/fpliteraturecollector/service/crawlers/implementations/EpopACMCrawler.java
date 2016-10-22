/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.service.crawlers.implementations;

import org.epop.dataprovider.acmdigital.ACMDigitalLibrarySearch;

import com.github.bfour.fpliteraturecollector.domain.SearchEngine;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;

public class EpopACMCrawler extends Crawler {

	public EpopACMCrawler() {
		super(new ACMDigitalLibrarySearch(), SearchEngine.ACM_DIGITAL_LIBRARY);
	}

}
