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

import java.nio.file.Path;
import java.util.List;

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Author;

public class LiteratureBuilder extends EntityBuilder<Literature> implements
		Builder<Literature> {

	private String title;
	private LiteratureType type;
	private List<Author> authors;
	private String DOI;
	private ISBN ISBN;
	private Integer year;
	private String publicationContext;
	private String publisher;
	private String websiteURL;
	private String fulltextURL;
	private Path fulltextFilePath;
	private Integer gScholarNumCitations;

	public LiteratureBuilder() {
		super();
	}

	public LiteratureBuilder(Literature l) {

		setID(l.getID());
		setCreationTime(l.getCreationTime());
		setLastChangeTime(l.getLastChangeTime());

		setTitle(l.getTitle());
		setType(l.getType());
		setAuthors(l.getAuthors());
		setDOI(l.getDOI());
		setISBN(l.getISBN());
		setYear(l.getYear());
		setPublicationContext(l.getPublicationContext());
		setPublisher(l.getPublisher());
		setWebsiteURL(l.getWebsiteURL());
		setFulltextFilePath(l.getFulltextFilePath());
		setFulltextURL(l.getFulltextURL());

		setgScholarNumCitations(l.getgScholarNumCitations());

	}

	@Override
	public Literature getObject() {
		return new Literature(getID(), getCreationTime(), getLastChangeTime(),
				getTitle(), getType(), getAuthors(), getDOI(), getISBN(),
				getYear(), getPublicationContext(), getPublisher(),
				getWebsiteURL(), getFulltextURL(), getFulltextFilePath(),
				getgScholarNumCitations());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LiteratureType getType() {
		return type;
	}

	public void setType(LiteratureType type) {
		this.type = type;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPublicationContext() {
		return publicationContext;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setPublicationContext(String publicationContext) {
		this.publicationContext = publicationContext;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	public String getFulltextURL() {
		return fulltextURL;
	}

	public void setFulltextURL(String fulltextURL) {
		this.fulltextURL = fulltextURL;
	}

	public Path getFulltextFilePath() {
		return fulltextFilePath;
	}

	public void setFulltextFilePath(Path fulltextFilePath) {
		this.fulltextFilePath = fulltextFilePath;
	}

	public Integer getgScholarNumCitations() {
		return gScholarNumCitations;
	}

	public void setgScholarNumCitations(Integer gScholarNumCitations) {
		this.gScholarNumCitations = gScholarNumCitations;
	}

}
