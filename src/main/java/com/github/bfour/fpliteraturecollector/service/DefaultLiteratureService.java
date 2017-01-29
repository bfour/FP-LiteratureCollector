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

package com.github.bfour.fpliteraturecollector.service;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.domain.builders.LiteratureBuilder;
import com.github.bfour.fpliteraturecollector.service.database.DAO.LiteratureDAO;
import com.github.bfour.jlib.commons.lang.Tuple;
import com.github.bfour.jlib.commons.services.ServiceException;
import com.github.bfour.jlib.commons.services.CRUD.EventCreatingCRUDService;
import com.github.bfour.jlib.commons.utils.Getter;

public class DefaultLiteratureService extends
		EventCreatingCRUDService<Literature> implements LiteratureService {

	private static DefaultLiteratureService instance;
	private LiteratureDAO DAO;
	private AuthorService authServ;
	private FileStorageService fileServ;
	private ProtocolEntryService protocolServ;

	private DefaultLiteratureService(LiteratureDAO DAO,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ, FileStorageService fileServ,
			ProtocolEntryService protocolServ) {
		super(DAO);
		this.DAO = DAO;
		this.authServ = authServ;
		this.fileServ = fileServ;
		this.protocolServ = protocolServ;
	}

	public static DefaultLiteratureService getInstance(LiteratureDAO DAO,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ, FileStorageService fileServ,
			ProtocolEntryService protocolServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultLiteratureService(DAO,
					forceCreateNewInstance, authServ, tagServ, fileServ,
					protocolServ);
		return instance;
	}

	@Override
	public Literature create(Literature entity) throws ServiceException {
		Literature created = super.create(entity);
		protocolServ.create(new ProtocolEntry("created literature "
				+ created.getID() + " " + created.getTitle()));
		return created;
	}

	@Override
	public void delete(Literature entity) throws ServiceException {
		super.delete(entity);
		protocolServ.create(new ProtocolEntry("deleted literature "
				+ entity.getID() + " " + entity.getTitle()));
	}

	@Override
	public Literature update(Literature oldEntity, Literature newEntity)
			throws ServiceException {
		Literature updated = super.update(oldEntity, newEntity);
		protocolServ.create(new ProtocolEntry("updated literature "
				+ updated.getID() + " " + updated.getTitle()));
		return updated;
	}

	@Override
	public synchronized void downloadFullTexts(Literature literature)
			throws ServiceException {

		if (literature.getFulltextURLs() == null)
			return;
		
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
			} catch (IOException | IllegalArgumentException e) {
				// TODO Auto-generated catch block
				System.err.println(fullTextURL);
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
					protocolServ.create(new ProtocolEntry(focusedLit.getID()
							+ " and " + compareLit.getID()
							+ " are certain duplicates ("
							+ focusedLit.getTitle() + " | "
							+ compareLit.getTitle() + ")"));
					// if we have a duplicate, remove focusedLit (at i)
					litList.remove(focusedLit);
					mergeInto(focusedLit, compareLit);
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
	public Tuple<Literature, Literature> getPossibleDuplicate()
			throws ServiceException {

		List<Literature> litList = getAll();

		// go through list in reverse order starting with last element
		for (int i = litList.size() - 1; i >= 0; i--) {
			Literature focusedLit = litList.get(i);
			// go through all preceding elements and compare
			for (int j = i - 1; j >= 0; j--) {
				Literature compareLit = litList.get(j);
				if (isProbableDuplicate(focusedLit, compareLit)) {
					return new Tuple<Literature, Literature>(focusedLit,
							compareLit);
				}
			}
		}

		return null;

	}

	@Override
	public List<Tuple<Literature, Literature>> getPossibleDuplicates()
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

		int matches = 0;

		if (litA.getDOI() != null && litB.getDOI() != null
				&& !litA.getDOI().isEmpty()
				&& litA.getDOI().equals(litB.getDOI())) {
			try {
				protocolServ.create(new ProtocolEntry(
						"certain duplicate determined based on DOI "
								+ litA.getDOI()));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			matches++;
		}

		if (litA.getgScholarID() != null && litB.getgScholarID() != null
				&& !litA.getgScholarID().isEmpty()
				&& litA.getgScholarID().equals(litB.getgScholarID())) {
			try {
				protocolServ.create(new ProtocolEntry(
						"certain duplicate determined based on gScholarID "
								+ litA.getgScholarID()));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			matches++;
		}

		if (litA.getMsAcademicID() != null && litB.getMsAcademicID() != null
				&& !litA.getMsAcademicID().isEmpty()
				&& litA.getMsAcademicID().equals(litB.getMsAcademicID())) {
			try {
				protocolServ.create(new ProtocolEntry(
						"certain duplicate determined based on MsAcademicID "
								+ litA.getMsAcademicID()));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			matches++;
		}

		if (litA.getPubmedID() != null && litB.getPubmedID() != null
				&& !litA.getPubmedID().isEmpty()
				&& litA.getPubmedID().equals(litB.getPubmedID())) {
			try {
				protocolServ.create(new ProtocolEntry(
						"certain duplicate determined based on PubmedID "
								+ litA.getPubmedID()));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			matches++;
		}

		if (litA.getAcmID() != null && litB.getAcmID() != null
				&& !litA.getAcmID().isEmpty()
				&& litA.getAcmID().equals(litB.getAcmID())) {
			try {
				protocolServ.create(new ProtocolEntry(
						"certain duplicate determined based on AcmID "
								+ litA.getAcmID()));
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			matches++;
		}

		if (matches > 0) {
			if (!litA.getTitle().equals(litB.getTitle())) {
				try {
					protocolServ.create(new ProtocolEntry(
							"certain duplicate despite mismatch of titles "
									+ litA.getID() + ":" + litB.getID() + " ("
									+ litA.getTitle() + " | " + litB.getTitle()
									+ ")"));
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			return true;
		}

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
				Normalizer.normalize(litA.getTitle(), Normalizer.Form.NFD)
						.toLowerCase(),
				Normalizer.normalize(litB.getTitle(), Normalizer.Form.NFD)
						.toLowerCase()) <= (litA.getTitle().length() / 14))
			return true;

		if (litA.getISBN() != null && litB.getISBN() != null
				&& litA.getISBN().equals(litB.getISBN()))
			return true;

		return false;

	}

	@Override
	public synchronized void mergeInto(Literature fromLit, Literature intoLit)
			throws ServiceException {

		LiteratureBuilder intoBuilder = new LiteratureBuilder(intoLit);

		if (fromLit.getAbstractText() != null
				&& !fromLit.getAbstractText().isEmpty()
				&& (intoLit.getAbstractText() == null
						|| intoLit.getAbstractText().isEmpty() || fromLit
						.getAbstractText().length() > intoLit.getAbstractText()
						.length()))
			intoBuilder.setAbstractText(fromLit.getAbstractText());

		if (fromLit.getType() != null
				&& (intoLit.getType() == null || intoLit.getType() == LiteratureType.UNKNOWN))
			intoBuilder.setType(fromLit.getType());

		// TODO (low) we'd first have to find a way to determine equality of
		// authors
		if (fromLit.getAuthors() != null
				&& (intoLit.getAuthors() == null || intoLit.getAuthors().size() < fromLit
						.getAuthors().size()))
			intoBuilder.setAuthors(fromLit.getAuthors());

		intoBuilder
				.setISBN(getMergeValue(fromLit.getISBN(), intoLit.getISBN()));

		intoBuilder.setDOI(getMergeValue(fromLit.getDOI(), intoLit.getDOI()));
		intoBuilder.setgScholarID(getMergeValue(fromLit.getgScholarID(),
				intoLit.getgScholarID()));
		intoBuilder.setMsAcademicID(getMergeValue(fromLit.getMsAcademicID(),
				intoLit.getMsAcademicID()));
		intoBuilder.setPubmedID(getMergeValue(fromLit.getPubmedID(),
				intoLit.getPubmedID()));
		intoBuilder.setAcmID(getMergeValue(fromLit.getAcmID(),
				intoLit.getAcmID()));

		intoBuilder
				.setYear(getMergeValue(fromLit.getYear(), intoLit.getYear()));
		intoBuilder.setPublicationContext(getMergeValue(
				fromLit.getPublicationContext(),
				intoLit.getPublicationContext()));
		intoBuilder.setPublisher(getMergeValue(fromLit.getPublisher(),
				intoLit.getPublisher()));

		intoBuilder.setWebsiteURLs(getMergeValue(fromLit, intoLit,
				new Getter<Literature, Set<Link>>() {
					@Override
					public Set<Link> get(Literature input) {
						return input.getWebsiteURLs();
					}
				}));
		intoBuilder.setFulltextURLs(getMergeValue(fromLit, intoLit,
				new Getter<Literature, Set<Link>>() {
					@Override
					public Set<Link> get(Literature input) {
						return input.getFulltextURLs();
					}
				}));
		intoBuilder.setFulltextFilePaths(getMergeValue(fromLit, intoLit,
				new Getter<Literature, Set<Link>>() {
					@Override
					public Set<Link> get(Literature input) {
						return input.getFulltextFilePaths();
					}
				}));

		intoBuilder.setgScholarNumCitations(getMergeValue(
				fromLit.getgScholarNumCitations(),
				intoLit.getgScholarNumCitations()));
		intoBuilder.setMsAcademicNumCitations(getMergeValue(
				fromLit.getMsAcademicNumCitations(),
				intoLit.getMsAcademicNumCitations()));
		intoBuilder.setAcmNumCitations(getMergeValue(
				fromLit.getAcmNumCitations(), intoLit.getAcmNumCitations()));
		intoBuilder.setPubmedNumCitations(getMergeValue(
				fromLit.getPubmedNumCitations(),
				intoLit.getPubmedNumCitations()));
		intoBuilder.setIeeeNumCitations(getMergeValue(
				fromLit.getIeeeNumCitations(), intoLit.getIeeeNumCitations()));

		intoBuilder.setTags(getMergeValue(fromLit, intoLit,
				new Getter<Literature, Set<Tag>>() {
					@Override
					public Set<Tag> get(Literature input) {
						return input.getTags();
					}
				}));

		intoBuilder.setNotes(getMergeValue(fromLit.getNotes(),
				intoLit.getNotes()));

		update(intoLit, intoBuilder.getObject());
		delete(fromLit);
		protocolServ.create(new ProtocolEntry("merged literature "
				+ fromLit.getID() + " into " + intoLit.getID()));

	}

	private String getMergeValue(String from, String to) {
		if (from == null || from.isEmpty())
			return to;
		if (to == null || to.isEmpty())
			return from;
		return to;
	}

	private <X> X getMergeValue(X from, X to) {
		if (from == null)
			return to;
		if (to == null)
			return from;
		return to;
	}

	private <X> Set<X> getMergeValue(Literature from, Literature to,
			Getter<Literature, Set<X>> getter) {

		Set<X> toSet = getter.get(to);

		// from set is 0
		if (getter.get(from) == null || getter.get(from).isEmpty())
			return toSet;

		// from set is not 0
		if (toSet == null)
			toSet = new HashSet<X>();

		for (X x : getter.get(from)) {
			if (!toSet.contains(x))
				toSet.add(x);
		}

		return toSet;

	}

}
