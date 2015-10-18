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
import java.util.HashSet;
import java.util.Set;

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
	public synchronized void deleteCascadeIfMaxOneAdjacentAtomicRequest(
			Literature literature) throws ServiceException {
		if (DAO.hasMaxOneAdjacentAtomicRequest(literature)) {
			for (Author author : literature.getAuthors())
				authServ.deleteIfMaxOneAdjacentLiterature(author);
			super.delete(literature);
		}
	}

}
