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

package com.github.bfour.fpliteraturecollector.test;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;
import com.github.bfour.jlib.commons.services.DatalayerException;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.commons.services.CRUD.DataIterator;

public class PersonTest {

	private static ServiceManager servMan;
	private static AuthorService persServ;

	@BeforeClass
	public static void preClass() throws ServiceException {
		servMan = ServiceManager.getInstance(ServiceManagerMode.TEST);
		servMan.dropAndReinitDatabase();
		persServ = servMan.getAuthorService();
	}

	@After
	public void post() throws ServiceException {
		servMan.resetAllData();
	}

	@AfterClass
	public static void postClass() throws ServiceException {
		servMan.close();
	}

	@Test
	public void returnEmptyListOnEmptyDB() throws ServiceException {
		assert (persServ.getAll().isEmpty());
	}

	@Test
	public void iteratorDoesNotHaveNextOnEmptyDB() throws ServiceException,
			DatalayerException {
		assert (persServ.getAllByStream().hasNext());
	}

	@Test
	public void deleteNonExistentPersonExpectNoChange() throws ServiceException {
		assert (persServ.getAll().isEmpty());
		persServ.delete(new Author("Nombre", "Q.", "Inconnu", null, null, null,
				null, null));
		assert (persServ.getAll().isEmpty());
	}

	@Test(expected = ServiceException.class)
	public void updateNonExistentPersonExpectFailure() throws ServiceException {
		assert (persServ.getAll().isEmpty());
		persServ.update(new Author("Nombre", "A.", "Inconnu", null, null, null,
				null, null), new Author("Persona", "M.", "Nueva", null, null,
				null, null, null));
		assert (persServ.getAll().isEmpty());
	}

	@Test
	public void createAndRemovePersonsAndTestDatabaseClean()
			throws ServiceException, DatalayerException {

		List<Author> personList = TestDataCreator.createAuthorList1();

		for (Author person : personList)
			personList.set(personList.indexOf(person), persServ.create(person));

		// check all created properly
		DataIterator<Author> dbIterator = persServ.getAllByStream();
		for (Author person : personList) {
			assert (dbIterator.next().equals(person));
		}

		// delete all
		for (Author person : personList)
			persServ.delete(person);

		// confirm delete
		assert (persServ.getAll().isEmpty());

	}

}
