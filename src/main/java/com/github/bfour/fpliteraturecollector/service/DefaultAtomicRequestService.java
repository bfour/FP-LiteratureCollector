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

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingCRUDService;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.database.DAO.AtomicRequestDAO;

public class DefaultAtomicRequestService extends
		EventCreatingCRUDService<AtomicRequest> implements
		AtomicRequestService {

	private static DefaultAtomicRequestService instance;
	private LiteratureService litServ;

	private DefaultAtomicRequestService(AtomicRequestDAO DAO,
			boolean forceCreateNewInstance, LiteratureService litServ,
			AuthorService authServ, TagService tagServ) {
		super(DAO);
		this.litServ = litServ;
	}

	public static DefaultAtomicRequestService getInstance(AtomicRequestDAO DAO,
			boolean forceCreateNewInstance, LiteratureService litServ,
			AuthorService authServ, TagService tagServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultAtomicRequestService(DAO,
					forceCreateNewInstance, litServ, authServ, tagServ);
		return instance;
	}
	
	@Override
	public AtomicRequest create(AtomicRequest entity) throws ServiceException {
		checkIntegrity(entity);
		return super.create(entity);
	}

	@Override
	public AtomicRequest update(AtomicRequest oldEntity, AtomicRequest newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	@Override
	public void deleteCascade(AtomicRequest atomReq) throws ServiceException {
		for (Literature literature : atomReq.getResults())
			litServ.deleteCascadeIfMaxOneAdjacentAtomicRequest(literature);
		super.delete(atomReq);
	}

	private void checkIntegrity(AtomicRequest entity) throws ServiceException {
		if (entity.getCrawler() == null)
			throw new ServiceException("crawler must not be null");
		if (entity.getSearchString() == null)
			throw new ServiceException("search string must not be null");
		if (entity.getSearchString().isEmpty())
			throw new ServiceException("search string must not be empty");
		// if (entity.getResults() == null)
		// throw new ServiceException(
		// "results list must not be null (may be empty)");
	}

}
