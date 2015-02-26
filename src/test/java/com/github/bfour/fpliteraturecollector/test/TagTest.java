package com.github.bfour.fpliteraturecollector.test;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.TagService;
import com.github.bfour.fpliteraturecollector.service.ServiceManager;
import com.github.bfour.fpliteraturecollector.service.ServiceManager.ServiceManagerMode;

public class TagTest {

	private static ServiceManager servMan;
	private static TagService tagServ;

	@BeforeClass
	public static void preClass() throws ServiceException {
		servMan = ServiceManager.getInstance(ServiceManagerMode.TEST);
		servMan.dropAndReinitDatabase();
		tagServ = servMan.getTagService();
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
		assert(tagServ.getAll().isEmpty());
	}
	
	@Test
	public void iteratorDoesNotHaveNextOnEmptyDB() throws ServiceException, DatalayerException {
		assert(tagServ.get().hasNext());
	}
	
	@Test
	public void deleteNonExistentTagExpectNoChange() throws ServiceException {
		assert(tagServ.getAll().isEmpty());
		tagServ.delete(new Tag("red tag", Color.RED));
		assert(tagServ.getAll().isEmpty());
	}
	
	@Test (expected = ServiceException.class)
	public void updateNonExistentTagExpectFailure() throws ServiceException {
		assert(tagServ.getAll().isEmpty());
		tagServ.update(new Tag("unknown tag", new Color(1, 1, 1)), new Tag("new tag", Color.GREEN));
		assert(tagServ.getAll().isEmpty());
	}
	
	@Test
	public void createTags()
			throws ServiceException, DatalayerException {

		List<Tag> tagList = new LinkedList<Tag>();
		tagList.add(new Tag("red tag", Color.GREEN));
		tagList.add(new Tag("blå", Color.BLUE));
		tagList.add(new Tag("transparent tag öäüßÖÄÜ?&%$§", Color.RED));

		for (Tag tag : tagList)
			tagList.set(tagList.indexOf(tag), tagServ.create(tag));
		
		// check all created properly
		DataIterator<Tag> dbIterator = tagServ.get();
		for (Tag tag : tagList) {
			assert (dbIterator.next().equals(tag));
		}		
		
	}
	
	@Test
	public void createAndRemoveTagsAndTestDatabaseClean()
			throws ServiceException, DatalayerException {

		List<Tag> tagList = new LinkedList<Tag>();
		tagList.add(new Tag("red tag", Color.GREEN));
		tagList.add(new Tag("blå", Color.BLUE));
		tagList.add(new Tag("transparent tag", Color.RED));

		for (Tag tag : tagList)
			tagList.set(tagList.indexOf(tag), tagServ.create(tag));

		// check all created properly
		DataIterator<Tag> dbIterator = tagServ.get();
		for (Tag tag : tagList) {
			assert (dbIterator.next().equals(tag));
		}

		// delete all
		for (Tag Tag : tagList)
			tagServ.delete(Tag);
		
		// confirm delete
		assert(tagServ.getAll().isEmpty());

	}	
	
}
