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

import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
import com.github.bfour.fpliteraturecollector.service.database.DAO.AtomicRequestDAO;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.commons.services.CRUD.EventCreatingCRUDService;

public class DefaultAtomicRequestService extends
		EventCreatingCRUDService<AtomicRequest> implements AtomicRequestService {

	private static DefaultAtomicRequestService instance;
	private LiteratureService litServ;
	private ProtocolEntryService protocolServ;

	private DefaultAtomicRequestService(AtomicRequestDAO DAO,
			boolean forceCreateNewInstance, LiteratureService litServ,
			ProtocolEntryService protocolService) {
		super(DAO);
		this.litServ = litServ;
		this.protocolServ = protocolService;
	}

	public static DefaultAtomicRequestService getInstance(AtomicRequestDAO DAO,
			boolean forceCreateNewInstance, LiteratureService litServ,
			ProtocolEntryService protocolService) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultAtomicRequestService(DAO,
					forceCreateNewInstance, litServ, protocolService);
		return instance;
	}

	@Override
	public AtomicRequest create(AtomicRequest entity) throws ServiceException {
		checkIntegrity(entity);
		AtomicRequest created = super.create(entity);
		protocolServ.create(new ProtocolEntry("created atomic request " + created.getID()
				+ " " + created.getCrawler() + " " + created.getSearchString()));
		return created;
	}

	@Override
	public AtomicRequest update(AtomicRequest oldEntity, AtomicRequest newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		AtomicRequest updated = super.update(oldEntity, newEntity);
		protocolServ.create(new ProtocolEntry("updated atomic request " + updated.getID()
				+ " " + updated.getCrawler() + " " + updated.getSearchString()));
		return updated;
	}

	@Override
	public void delete(AtomicRequest entity) throws ServiceException {
		super.delete(entity);
		protocolServ.create(new ProtocolEntry("deleted atomic request " + entity.getID()
				+ " " + entity.getCrawler() + " " + entity.getSearchString()));
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
