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

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingCRUDService;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.database.DAO.LiteratureDAO;

public class DefaultLiteratureService extends
		EventCreatingCRUDService<Literature> implements LiteratureService {

	private static DefaultLiteratureService instance;
	private AuthorService authServ;
	private FileStorageService fileServ;
	private LiteratureDAO DAO;

	private DefaultLiteratureService(LiteratureDAO DAO,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ, FileStorageService fileServ) {
		super(DAO);
		this.DAO = DAO;
		this.authServ = authServ;
		this.fileServ = fileServ;
	}

	public static DefaultLiteratureService getInstance(LiteratureDAO DAO,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ, FileStorageService fileServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultLiteratureService(DAO,
					forceCreateNewInstance, authServ, tagServ, fileServ);
		return instance;
	}

	@Override
	public synchronized void downloadFullTexts(Literature literature)
			throws ServiceException {

		outerloop: for (Link fullTextURL : literature.getFulltextURLs()) {

			if (literature.getFulltextFilePaths() != null)
				// check if already exists
				for (Link alreadyExistingFiles : literature
						.getFulltextFilePaths()) {
					if (alreadyExistingFiles.getReference().equals(
							fullTextURL.getUri().toString())) {
						// check if file actually exists
						File file = new File(alreadyExistingFiles.getUri());
						if (file.exists())
							continue outerloop;
						else {
							Set<Link> newFullTextPaths = literature
									.getFulltextFilePaths();
							newFullTextPaths.remove(alreadyExistingFiles);
							update(literature,
									new LiteratureBuilder(literature)
											.setFulltextFilePaths(
													newFullTextPaths)
											.getObject());
						}
					}
				}

			try {
				Link fullTextFileLink = fileServ.persist(fullTextURL.getUri()
						.toURL(), literature);
				Set<Link> newFileLinks = new HashSet<>();
				if (literature.getFulltextFilePaths() != null)
					newFileLinks.addAll(literature.getFulltextFilePaths());
				newFileLinks.add(fullTextFileLink);
				update(literature, new LiteratureBuilder(literature)
						.setFulltextFilePaths(newFileLinks).getObject());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ServiceException(e);
			}

		}

	}

	@Override
	public List<Literature> autoDeleteDuplicates() throws ServiceException {

		List<Literature> litList = getAll();
		List<Literature> deleted = new LinkedList<>();

		// go through list in reverse order starting with last element
		for (int i = litList.size() - 1; i >= 0; i--) {
			Literature focusedLit = litList.get(i);
			// go through all preceding elements and compare
			for (int j = i - 1; j >= 0; j--) {
				Literature compareLit = litList.get(j);
				if (isCertainDuplicate(focusedLit, compareLit)) {
					// if we have a duplicate, remove focusedLit (at i)
					litList.remove(focusedLit);
					delete(focusedLit);
					deleted.add(focusedLit);
					break;
				}
			}
		}

		return deleted;

		// try {
		// DAO.beginTx();
		// } catch (DatalayerException e1) {
		// e1.printStackTrace();
		// throw new ServiceException(e1);
		// }
		// int offset = 0;
		// List<Literature> deleted = new ArrayList<>();
		// while (true) {
		// DataIterator<Literature> iter = getAllByStream();
		// try {
		// Literature focusedLit = null;
		// for (int i = 0; i <= offset; i++) {
		// if (!iter.hasNext()) {
		// DAO.commitTx();
		// return deleted;
		// } else
		// focusedLit = iter.next();
		// }
		// offset++;
		// // go through all subsequent elements and delete matches
		// // NB: previous elements have already been checked
		// while (iter.hasNext()) {
		// Literature compareLit = iter.next();
		// if (isCertainDuplicate(focusedLit, compareLit)) {
		// iter.remove();
		// delete(compareLit);
		// deleted.add(compareLit);
		// }
		// }
		// } catch (DatalayerException e) {
		// try {
		// DAO.rollbackTx();
		// } catch (DatalayerException e1) {
		// e1.printStackTrace();
		// throw new ServiceException(e);
		// }
		// e.printStackTrace();
		// throw new ServiceException(e);
		// }
		// }
	}

	@Override
	public List<Tuple<Literature, Literature>> getPossibleDuplicate()
			throws ServiceException {

		List<Literature> litList = getAll();
		List<Tuple<Literature, Literature>> dups = new LinkedList<>();

		// go through list in reverse order starting with last element
		for (int i = litList.size() - 1; i >= 0; i--) {
			Literature focusedLit = litList.get(i);
			// go through all preceding elements and compare
			for (int j = i - 1; j >= 0; j--) {
				Literature compareLit = litList.get(j);
				if (isProbableDuplicate(focusedLit, compareLit)) {
					dups.add(new Tuple<Literature, Literature>(focusedLit,
							compareLit));
				}
			}
		}

		return dups;

		// DataIterator<Literature> iter = getAllByStream();
		// try {
		// if (!iter.hasNext())
		// return null;
		// Literature focusedLit = iter.next();
		// while (iter.hasNext()) {
		// Literature compareLit = iter.next();
		// if (isProbableDuplicate(focusedLit, compareLit))
		// return new Tuple<Literature, Literature>(focusedLit,
		// compareLit);
		// }
		// return null;
		// } catch (DatalayerException e) {
		// e.printStackTrace();
		// throw new ServiceException(e);
		// }
	}

	@Override
	public synchronized void deleteCascadeIfMaxOneAdjacentAtomicRequest(
			Literature literature) throws ServiceException {
		if (DAO.hasMaxOneAdjacentAtomicRequest(literature)) {
			for (Author author : literature.getAuthors())
				authServ.deleteIfMaxOneAdjacentLiterature(author);
			super.delete(literature);
		}
	}

	/**
	 * Determines whether a pair of two Literatures certainly is a duplicate
	 * pair. Employs comparative measures to ensure only actual duplicates are
	 * detected as such (eg. by using identifiers).
	 * 
	 * @param litA
	 * @param litB
	 * @return
	 */
	private boolean isCertainDuplicate(Literature litA, Literature litB) {
		if (litA.getDOI() != null && litB.getDOI() != null
				&& litA.getDOI().equals(litB.getDOI()))
			return true;
		if (litA.getgScholarID() != null && litB.getgScholarID() != null
				&& litA.getgScholarID().equals(litB.getgScholarID()))
			return true;
		if (litA.getMsAcademicID() != null && litB.getMsAcademicID() != null
				&& litA.getMsAcademicID().equals(litB.getMsAcademicID()))
			return true;
		if (litA.getPubmedID() != null && litB.getPubmedID() != null
				&& litA.getPubmedID().equals(litB.getPubmedID()))
			return true;
		if (litA.getAcmID() != null && litB.getAcmID() != null
				&& litA.getAcmID().equals(litB.getAcmID()))
			return true;
		return false;
	}

	/**
	 * Determines whether a pair of two Literatures maybe is a duplicate pair.
	 * Employs comparative measures that might lead to false positives.
	 * 
	 * @param litA
	 * @param litB
	 * @return
	 */
	private boolean isProbableDuplicate(Literature litA, Literature litB) {

		if (isCertainDuplicate(litA, litB))
			return true;

		// 1 character different for every 14 characters
		if (StringUtils.getLevenshteinDistance(
				Normalizer.normalize(litA.getTitle(), Normalizer.Form.NFD),
				Normalizer.normalize(litB.getTitle(), Normalizer.Form.NFD)) <= (litA
				.getTitle().length() / 14))
			return true;

		if (litA.getISBN() != null && litB.getISBN() != null
				&& litA.getISBN().equals(litB.getISBN()))
			return true;

		return false;

	}
}
