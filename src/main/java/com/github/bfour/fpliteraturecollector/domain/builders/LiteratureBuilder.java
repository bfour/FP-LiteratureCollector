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
import java.util.Set;

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Tag;

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
	private Set<Tag> tags;

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

		setTags(l.getTags());
		
	}

	@Override
	public Literature getObject() {
		return new Literature(getID(), getCreationTime(), getLastChangeTime(),
				getTitle(), getType(), getAuthors(), getDOI(), getISBN(),
				getYear(), getPublicationContext(), getPublisher(),
				getWebsiteURL(), getFulltextURL(), getFulltextFilePath(),
				getgScholarNumCitations(), getTags());
	}

	public String getTitle() {
		return title;
	}

	public LiteratureBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public LiteratureType getType() {
		return type;
	}

	public LiteratureBuilder setType(LiteratureType type) {
		this.type = type;
		return this;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public LiteratureBuilder setAuthors(List<Author> authors) {
		this.authors = authors;
		return this;
	}

	public String getDOI() {
		return DOI;
	}

	public LiteratureBuilder setDOI(String dOI) {
		DOI = dOI;
		return this;
	}

	public ISBN getISBN() {
		return ISBN;
	}

	public LiteratureBuilder setISBN(ISBN iSBN) {
		ISBN = iSBN;
		return this;
	}

	public Integer getYear() {
		return year;
	}

	public LiteratureBuilder setYear(Integer year) {
		this.year = year;
		return this;
	}

	public String getPublicationContext() {
		return publicationContext;
	}

	public String getPublisher() {
		return publisher;
	}

	public LiteratureBuilder setPublisher(String publisher) {
		this.publisher = publisher;
		return this;
	}

	public LiteratureBuilder setPublicationContext(String publicationContext) {
		this.publicationContext = publicationContext;
		return this;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public LiteratureBuilder setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
		return this;
	}

	public String getFulltextURL() {
		return fulltextURL;
	}

	public LiteratureBuilder setFulltextURL(String fulltextURL) {
		this.fulltextURL = fulltextURL;
		return this;
	}

	public Path getFulltextFilePath() {
		return fulltextFilePath;
	}

	public LiteratureBuilder setFulltextFilePath(Path fulltextFilePath) {
		this.fulltextFilePath = fulltextFilePath;
		return this;
	}

	public Integer getgScholarNumCitations() {
		return gScholarNumCitations;
	}

	public LiteratureBuilder setgScholarNumCitations(Integer gScholarNumCitations) {
		this.gScholarNumCitations = gScholarNumCitations;
		return this;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public LiteratureBuilder setTags(Set<Tag> tags) {
		this.tags = tags;
		return this;
	}

}
