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
import com.github.bfour.fpliteraturecollector.domain.Person;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.DAO.OrientDBPersonDAO;

public class DefaultPersonService extends EventCreatingEntityCRUDService<Person>
		implements PersonService {

	private static DefaultPersonService instance;

	private DefaultPersonService(OrientDBGraphService graphService) {
		super(OrientDBPersonDAO.getInstance(graphService));
	}

	public static DefaultPersonService getInstance(
			OrientDBGraphService graphService) {
		if (instance == null)
			instance = new DefaultPersonService(graphService);
		return instance;
	}

	@Override
	public Person create(Person entity) throws ServiceException {
		checkIntegrity(entity);
		return super.create(entity);
	}

	@Override
	public Person update(Person oldEntity, Person newEntity)
			throws ServiceException {
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	private void checkIntegrity(Person entity) throws ServiceException {
		if (entity.getFirstName() == null)
			throw new ServiceException(
					"first name of person must be specified (can be empty)");
		if (entity.getLastName() == null)
			throw new ServiceException(
					"last name of person must be specified (can be empty)");
	}

}
