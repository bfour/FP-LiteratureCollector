package com.github.bfour.fpliteraturecollector.test;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
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


import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

public class LiteratureTest {

	private static ServiceManager servMan;
	private static LiteratureService litServ;
	private static AuthorService authServ;

	@BeforeClass
	public static void preClass() throws ServiceException {
		servMan = ServiceManager.getInstance(ServiceManagerMode.TEST);
		servMan.dropAndReinitDatabase();
		litServ = servMan.getLiteratureService();
		authServ = servMan.getAuthorService();
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
		assert (litServ.getAll().isEmpty());
	}

	@Test
	public void iteratorDoesNotHaveNextOnEmptyDB() throws ServiceException,
			DatalayerException {
		assert (litServ.getAllByStream().hasNext());
	}

	@Test
	public void deleteNonExistentLiteratureExpectNoChange()
			throws ServiceException {
		assert (litServ.getAll().isEmpty());
		litServ.delete(new Literature());
		assert (litServ.getAll().isEmpty());
	}

	@Test(expected = ServiceException.class)
	public void updateNonExistentLiteratureExpectFailure()
			throws ServiceException {
		assert (litServ.getAll().isEmpty());
		litServ.update(new Literature(), new Literature());
		assert (litServ.getAll().isEmpty());
	}

	@Test
	public void createAndRemoveLiteraturesAndTestDatabaseClean()
			throws ServiceException, DatalayerException {

		List<Literature> literatureList = TestDataCreator
				.createLiteratureSet1(authServ);

		for (Literature lit : literatureList)
			literatureList
					.set(literatureList.indexOf(lit), litServ.create(lit));

		// check all created properly
		DataIterator<Literature> dbIterator = litServ.getAllByStream();
		for (Literature tag : literatureList) {
			assert (dbIterator.next().equals(tag));
		}

		// delete all
		for (Literature literature : literatureList)
			litServ.delete(literature);

		// confirm delete
		assert (litServ.getAll().isEmpty());

	}

}
