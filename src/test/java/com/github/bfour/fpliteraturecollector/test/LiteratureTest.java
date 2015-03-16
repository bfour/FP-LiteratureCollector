package com.github.bfour.fpliteraturecollector.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

public class LiteratureTest {

	private static ServiceManager servMan;
	private static LiteratureService litServ;
	private static AuthorService persServ;

	@BeforeClass
	public static void preClass() throws ServiceException {
		servMan = ServiceManager.getInstance(ServiceManagerMode.TEST);
		servMan.dropAndReinitDatabase();
		litServ = servMan.getLiteratureService();
		persServ = servMan.getPersonService();
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
		assert (litServ.get().hasNext());
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

		List<Author> authorList1 = new ArrayList<>(2);
		authorList1.add(new Author("Bob", "Sponge"));
		authorList1.add(new Author("Patrick", "Star"));
		for (Author person : authorList1)
			authorList1.set(authorList1.indexOf(person), persServ.create(person));

		List<Author> authorList2 = new ArrayList<>(1);
		authorList2.add(new Author("Mariela", "Castro Espín"));
		for (Author person : authorList2)
			authorList2.set(authorList2.indexOf(person), persServ.create(person));

		List<Author> authorList3 = new ArrayList<>(5);
		authorList3.add(new Author("Mariela", "Castro Espín"));
		authorList3.add(new Author("Raúl", "Castro"));
		authorList3.add(new Author("Fidel", "Castro"));
		authorList3.add(new Author("José Alberto", "Mujica Cordano"));
		authorList3.add(new Author("Cristina Fernández", "de Kirchner"));
		for (Author person : authorList3)
			authorList3.set(authorList3.indexOf(person), persServ.create(person));

		List<Author> authorList4 = new ArrayList<>(0);
		for (Author person : authorList4)
			authorList4.set(authorList4.indexOf(person), persServ.create(person));

		List<Literature> literatureList = new LinkedList<Literature>();
		literatureList.add(new Literature(
				"Psychological Disorders in Bikini Bottom", authorList1, null,
				null));
		literatureList.add(new Literature("Mariette Pathy Allen: Transcuba",
				authorList2, null, new ISBN("978-0988983137")));
		literatureList
				.add(new Literature(
						"LDL Receptor-Related Protein 5 (LRP5) Affects Bone Accrual and Eye Development",
						authorList3, "10.1016/S0092-8674(01)00571-2", null));

		for (Literature tag : literatureList)
			literatureList
					.set(literatureList.indexOf(tag), litServ.create(tag));

		// check all created properly
		DataIterator<Literature> dbIterator = litServ.get();
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
