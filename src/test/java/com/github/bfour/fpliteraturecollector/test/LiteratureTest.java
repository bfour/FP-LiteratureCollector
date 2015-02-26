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
import com.github.bfour.fpliteraturecollector.domain.Person;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.PersonService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

public class LiteratureTest {

	private static ServiceManager servMan;
	private static LiteratureService litServ;
	private static PersonService persServ;

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

		List<Person> authorList1 = new ArrayList<>(2);
		authorList1.add(new Person("Bob", "Sponge"));
		authorList1.add(new Person("Patrick", "Star"));
		for (Person person : authorList1)
			authorList1.set(authorList1.indexOf(person), persServ.create(person));

		List<Person> authorList2 = new ArrayList<>(1);
		authorList2.add(new Person("Mariela", "Castro Espín"));
		for (Person person : authorList2)
			authorList2.set(authorList2.indexOf(person), persServ.create(person));

		List<Person> authorList3 = new ArrayList<>(5);
		authorList3.add(new Person("Mariela", "Castro Espín"));
		authorList3.add(new Person("Raúl", "Castro"));
		authorList3.add(new Person("Fidel", "Castro"));
		authorList3.add(new Person("José Alberto", "Mujica Cordano"));
		authorList3.add(new Person("Cristina Fernández", "de Kirchner"));
		for (Person person : authorList3)
			authorList3.set(authorList3.indexOf(person), persServ.create(person));

		List<Person> authorList4 = new ArrayList<>(0);
		for (Person person : authorList4)
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
