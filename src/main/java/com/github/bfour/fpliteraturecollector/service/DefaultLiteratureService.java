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
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.database.DAO.LiteratureDAO;

public class DefaultLiteratureService extends
		EventCreatingCRUDService<Literature> implements LiteratureService {

	private static DefaultLiteratureService instance;
	private AuthorService authServ;
	private LiteratureDAO DAO;

	private DefaultLiteratureService(LiteratureDAO DAO,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ) {
		super(DAO);
		this.DAO = DAO;
		this.authServ = authServ;
	}

	public static DefaultLiteratureService getInstance(LiteratureDAO DAO,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultLiteratureService(DAO,
					forceCreateNewInstance, authServ, tagServ);
		return instance;
	}

	@Override
	public void deleteCascadeIfMaxOneAdjacentAtomicRequest(Literature literature)
			throws ServiceException {
		if (DAO.hasMaxOneAdjacentAtomicRequest(literature)) {
			for (Author author : literature.getAuthors())
				authServ.deleteIfMaxOneAdjacentLiterature(author);
			super.delete(literature);
		}
	}

}
