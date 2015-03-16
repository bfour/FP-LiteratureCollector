package com.github.bfour.fpliteraturecollector.domain;

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

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import com.github.bfour.fpjcommons.model.Entity;

public class Literature extends Entity {

	public static enum LiteratureType {
		UNKNOWN, BOOK, DISSERTATION, JOURNAL_PAPER, CONFERENCE_PAPER,
	}

	protected String title;
	protected LiteratureType type;
	protected List<Author> authors;
	protected String DOI;
	protected ISBN ISBN;
	protected Integer year;
	protected String fulltextURL;
	protected Path fulltextFilePath;

	// TODO: also important: type of publication (journal, proceeding
	// (Konferenzband), book chapter), Verlag, Datum (Jahr)

	public Literature(long iD, Date creationTime, Date lastChangeTime,
			String title, List<Author> authors, String DOI, ISBN ISBN,
			Integer year) {
		super(iD, creationTime, lastChangeTime);
		this.title = title;
		this.authors = authors;
		this.DOI = DOI;
		this.ISBN = ISBN;
		this.year = year;
	}

	public Literature(String title, List<Author> authors, String DOI, ISBN ISBN) {
		this.title = title;
		this.authors = authors;
		this.DOI = DOI;
		this.ISBN = ISBN;
	}

	public Literature() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public LiteratureType getType() {
		return type;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public String getDOI() {
		return DOI;
	}

	public ISBN getISBN() {
		return ISBN;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public String getFulltextURL() {
		return fulltextURL;
	}

	public Path getFulltextFilePath() {
		return fulltextFilePath;
	}

}
