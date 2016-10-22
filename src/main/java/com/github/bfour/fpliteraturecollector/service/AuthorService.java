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

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.BidirectionalCRUDService;
import com.github.bfour.fpliteraturecollector.domain.Author;

public interface AuthorService extends BidirectionalCRUDService<Author> {

	public Author getByGScholarID(String gScholarID) throws ServiceException;

	public Author getByMsAcademicID(String msAcademicID)
			throws ServiceException;

	void deleteIfMaxOneAdjacentLiterature(Author author)
			throws ServiceException;

}
