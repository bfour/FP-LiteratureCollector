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
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.OrientDBAuthorDAO;

public class DefaultAuthorService extends
		EventCreatingCRUDService<Author, OrientDBAuthorDAO> implements
		AuthorService {

	private static DefaultAuthorService instance;

	private DefaultAuthorService(OrientDBGraphService graphService,
			boolean forceCreateNewInstance) {
		super(OrientDBAuthorDAO.getInstance(graphService,
				forceCreateNewInstance));
	}

	public static DefaultAuthorService getInstance(
			OrientDBGraphService graphService, boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultAuthorService(graphService,
					forceCreateNewInstance);
		return instance;
	}

	@Override
	public Author create(Author entity) throws ServiceException {
		checkIntegrity(entity);
		return super.create(entity);
	}

	@Override
	public Author update(Author oldEntity, Author newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	private void checkIntegrity(Author entity) throws ServiceException {
		if (entity.getFirstName() == null)
			throw new ServiceException(
					"first name of person must be specified (can be empty)");
		if (entity.getLastName() == null)
			throw new ServiceException(
					"last name of person must be specified (can be empty)");
	}

	@Override
	public Author getByGScholarID(String gScholarID) throws ServiceException {
		return getDAO().getByGScholarID(gScholarID);
	}

	@Override
	public Author getByMsAcademicID(String msAcademicID)
			throws ServiceException {
		return getDAO().getByMsAcademicID(msAcademicID);
	}

	@Override
	public void deleteIfMaxOneAdjacentLiterature(Author author)
			throws ServiceException {
		if (getDAO().hasMaxOneAdjacentLiterature(author)) {
			super.delete(author);
		}
	}

}
