package com.github.bfour.fpliteraturecollector.domain.builders;

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

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Person;

public class LiteratureBuilder extends EntityBuilder<Literature> implements
		Builder<Literature> {

	private String title;
	private List<Person> authors;
	private String DOI;
	private ISBN ISBN;

	public LiteratureBuilder() {
		super();
	}

	public LiteratureBuilder(Literature l) {
		setID(l.getID());
		setCreationTime(l.getCreationTime());
		setLastChangeTime(l.getLastChangeTime());
		setTitle(l.getTitle());
		setAuthors(l.getAuthors());
		setDOI(l.getDOI());
		setISBN(l.getISBN());
	}

	@Override
	public Literature getObject() {
		return new Literature(getID(), getCreationTime(), getLastChangeTime(),
				getTitle(), getAuthors(), getDOI(), getISBN());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Person> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Person> authors) {
		this.authors = authors;
	}

	public String getDOI() {
		return DOI;
	}

	public void setDOI(String dOI) {
		DOI = dOI;
	}

	public ISBN getISBN() {
		return ISBN;
	}

	public void setISBN(ISBN iSBN) {
		ISBN = iSBN;
	}

}
