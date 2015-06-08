package com.github.bfour.fpliteraturecollector.service;

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

/*
 * =================================
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
 * =================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * *
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JAtomicRequestDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JAuthorDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JLiteratureDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JQueryDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JTagDAO;


@Service
@Configurable
public class ServiceManager {

	public static enum ServiceManagerMode {
		DEFAULT, TEST, REMOTE_TEST;
	}

	private static ServiceManager instance;

	private ServiceManagerMode modeMemory;
	private AuthorService authServ;
	private TagService tagServ;
	private LiteratureService litServ;
	private AtomicRequestService atomReqServ;
	private QueryService queryServ;
	private CrawlerService crawlServ;
	
	@Autowired
	private Neo4JAuthorDAO authDAO;
	@Autowired
	private Neo4JTagDAO tagDAO;
	@Autowired
	private Neo4JLiteratureDAO literatureDAO;
	@Autowired
	private Neo4JAtomicRequestDAO atomReqDAO;
	@Autowired
	private Neo4JQueryDAO queryDAO;

	public ServiceManager() throws ServiceException {
		this(ServiceManagerMode.TEST);
	}
	
	private ServiceManager(ServiceManagerMode mode) throws ServiceException {
		initialize(mode);
	}

	public static ServiceManager getInstance(ServiceManagerMode mode)
			throws ServiceException {
		if (instance == null)
			instance = new ServiceManager(mode);
		return instance;
	}
	
	private void initialize(ServiceManagerMode mode) throws ServiceException {
		
		modeMemory = mode;
		
		if (mode == ServiceManagerMode.DEFAULT
				|| mode == ServiceManagerMode.TEST 
				|| mode == ServiceManagerMode.REMOTE_TEST) {

//			if (mode == ServiceManagerMode.DEFAULT) {
//				graphService.setLocalDatabase("database");
////				graphService.setRemoteDatabase("localhost", "litcoll", "meow", "meow");
//			} else if (mode == ServiceManagerMode.TEST) {
//				graphService.setLocalDatabase("junitTestDatabase");
//				graphService.dropCurrentDB();
//				graphService.setLocalDatabase("junitTestDatabase");
//			} else if (mode == ServiceManagerMode.REMOTE_TEST) {
//				graphService.setRemoteDatabase("localhost", "cat", "root", "meow");
//			}

			this.crawlServ = CrawlerService.getInstance();
			
		} else {
			throw new ServiceException("invalid service manager mode: " + mode);
		}
		
	}

	public AuthorService getAuthorService() {
		if (authServ == null) authServ = DefaultAuthorService.getInstance(authDAO, true);
		return authServ;
	}

	public TagService getTagService() {
		if (tagServ == null) tagServ = DefaultTagService.getInstance(tagDAO, true);
		return tagServ;
	}
	
	public LiteratureService getLiteratureService() {
		if (litServ == null) litServ = DefaultLiteratureService.getInstance(literatureDAO, true, getAuthorService(), getTagService());
		return litServ;
	}

	public AtomicRequestService getAtomicRequestService() {
		return atomReqServ;
	}
	
	public QueryService getQueryService() {
		if (queryServ == null) queryServ = DefaultQueryService.getInstance(queryDAO, true, getAtomicRequestService());
		return queryServ;
	}
	
	public CrawlerService getCrawlerService() {
		return crawlServ;
	}

	/**
	 * Deletes all user data and re-initializes.
	 */
	public void resetAllData() throws ServiceException {
//		graphService.deleteAllDataInCurrentDB();
	}
	
	public void dropAndReinitDatabase() throws ServiceException {
//		graphService.dropCurrentDB();
		initialize(modeMemory);
	}

	public void close() {
//		graphService.shutdown();
	}

}
