package com.github.bfour.fpliteraturecollector.test;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

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
		persServ.delete(new Author("Nombre", "Inconnu", null, null));
		assert (persServ.getAll().isEmpty());
	}

	@Test(expected = ServiceException.class)
	public void updateNonExistentPersonExpectFailure() throws ServiceException {
		assert (persServ.getAll().isEmpty());
		persServ.update(new Author("Nombre", "Inconnu", null, null),
				new Author("Persona", "Nueva", null, null));
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
