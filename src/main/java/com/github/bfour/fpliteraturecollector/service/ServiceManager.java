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

package com.github.bfour.fpliteraturecollector.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import retrofit2.Retrofit;

import com.gimranov.libzotero.ZoteroService;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JAtomicRequestDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JAuthorDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JLiteratureDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JProtocolEntryDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JQueryDAO;
import com.github.bfour.fpliteraturecollector.service.database.DAO.Neo4JTagDAO;
import com.github.bfour.jlib.commons.services.ServiceException;

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
	private ProtocolEntryService protocolServ;
	private CrawlerService crawlServ;
	private ReportService reportServ;
	private FileStorageService fileServ;
	private ZoteroService zoteroServ;

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
	@Autowired
	private Neo4JProtocolEntryDAO protocolEntryDAO;

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

			// if (mode == ServiceManagerMode.DEFAULT) {
			// graphService.setLocalDatabase("database");
			// // graphService.setRemoteDatabase("localhost", "litcoll", "meow",
			// "meow");
			// } else if (mode == ServiceManagerMode.TEST) {
			// graphService.setLocalDatabase("junitTestDatabase");
			// graphService.dropCurrentDB();
			// graphService.setLocalDatabase("junitTestDatabase");
			// } else if (mode == ServiceManagerMode.REMOTE_TEST) {
			// graphService.setRemoteDatabase("localhost", "cat", "root",
			// "meow");
			// }

			this.crawlServ = CrawlerService.getInstance();
			this.reportServ = ReportService.getInstance(this);
			this.fileServ = FileStorageService.getInstance();

			Retrofit retrofit = new Retrofit.Builder()
					.baseUrl("http://zotero.org/")
					.build();
			this.zoteroServ = retrofit.create(ZoteroService.class);

		} else {
			throw new ServiceException("invalid service manager mode: " + mode);
		}

	}

	public AuthorService getAuthorService() {
		if (authServ == null)
			authServ = DefaultAuthorService.getInstance(authDAO, true,
					getProtocolEntryService());
		return authServ;
	}

	public TagService getTagService() {
		if (tagServ == null)
			tagServ = DefaultTagService.getInstance(tagDAO, true);
		return tagServ;
	}

	public LiteratureService getLiteratureService() {
		if (litServ == null)
			litServ = DefaultLiteratureService.getInstance(literatureDAO, true,
					getAuthorService(), getTagService(), getFileServ(),
					getProtocolEntryService());
		return litServ;
	}

	public AtomicRequestService getAtomicRequestService() {
		if (atomReqServ == null)
			atomReqServ = DefaultAtomicRequestService.getInstance(atomReqDAO,
					true, getLiteratureService(), getProtocolEntryService());
		return atomReqServ;
	}

	public QueryService getQueryService() {
		if (queryServ == null)
			queryServ = DefaultQueryService.getInstance(queryDAO, true,
					getAtomicRequestService(), getProtocolEntryService());
		return queryServ;
	}

	public ProtocolEntryService getProtocolEntryService() {
		if (protocolServ == null)
			protocolServ = DefaultProtocolEntryService.getInstance(
					protocolEntryDAO, true);
		return protocolServ;
	}

	public CrawlerService getCrawlerService() {
		return crawlServ;
	}

	public ReportService getReportService() {
		return reportServ;
	}

	public FileStorageService getFileServ() {
		return fileServ;
	}

	public ZoteroService getZoteroServ() {
		return zoteroServ;
	}

	/**
	 * Deletes all user data and re-initializes.
	 */
	public void resetAllData() throws ServiceException {
		// graphService.deleteAllDataInCurrentDB();
	}

	public void dropAndReinitDatabase() throws ServiceException {
		// graphService.dropCurrentDB();
		initialize(modeMemory);
	}

	public void close() {
		// graphService.shutdown();
	}

}
