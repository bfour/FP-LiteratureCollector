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

import java.util.List;

import com.github.bfour.fpjcommons.lang.Tuple;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.BidirectionalCRUDService;
import com.github.bfour.fpliteraturecollector.domain.Literature;

public interface LiteratureService extends BidirectionalCRUDService<Literature> {

	void downloadFullTexts(Literature literature) throws ServiceException;

	/**
	 * Delete duplicates without user-interaction. Duplicates are detected based
	 * on these criteria: - DOI matches
	 * 
	 * @return list of deleted entries
	 * @throws ServiceException
	 */
	List<Literature> autoDeleteDuplicates() throws ServiceException;

	Tuple<Literature, Literature> getPossibleDuplicate()
			throws ServiceException;
	
	List<Tuple<Literature, Literature>> getPossibleDuplicates()
			throws ServiceException;

	void deleteCascadeIfMaxOneAdjacentAtomicRequest(Literature literature)
			throws ServiceException;

	/**
	 * Merges one literature (A) into another (B) and deletes the former (A).
	 * Merging means fields that are not defined in B but in A are copied from A
	 * to B. If a field is a collection, the entries of that collection that
	 * exist in A but not in B will be copied to the collection in B.
	 * 
	 * @param fromLit
	 * @param intoLit
	 * @throws ServiceException 
	 */
	void mergeInto(Literature fromLit, Literature intoLit) throws ServiceException;

}
