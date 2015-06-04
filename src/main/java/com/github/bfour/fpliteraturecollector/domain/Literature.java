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
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

public class Literature extends Entity {

	public static enum LiteratureType {
		UNKNOWN("unknown"), BOOK("book"), DISSERTATION("dissertation"), JOURNAL_PAPER(
				"journal paper"), CONFERENCE_PAPER("conference paper"), PATENT(
				"patent"), BOOK_CHAPTER("book chapter"), WORKING_PAPER(
				"working paper");

		private String tellingName;

		LiteratureType(String tellingName) {
			this.tellingName = tellingName;
		}

		public String getTellingName() {
			return tellingName;
		}

		@Override
		public String toString() {
			return tellingName;
		}

	}
	
	@Indexed(indexType = IndexType.FULLTEXT, indexName = "literatureTitle")
	protected String title;
	
	protected LiteratureType type;
	
	@RelatedTo(type="AUTHORS", direction = Direction.OUTGOING)
	protected List<Author> authors;
	
	protected String DOI;
	
	protected ISBN ISBN;
	
	protected Integer year;

	/**
	 * eg. name of journal, name of conference ...
	 */
	@Indexed(indexType = IndexType.FULLTEXT, indexName = "publicationContext")
	protected String publicationContext;
	@Indexed(indexType = IndexType.FULLTEXT, indexName = "publisher")
	protected String publisher;

	protected String websiteURL;
	protected String fulltextURL;
	protected Path fulltextFilePath;

	protected Integer gScholarNumCitations;

	@RelatedTo(type="TAGS", direction = Direction.OUTGOING)
	protected Set<Tag> tags;

	public Literature(Long iD, Date creationTime, Date lastChangeTime,
			String title, LiteratureType type, List<Author> authors,
			String DOI, ISBN ISBN, Integer year, String publicationContext,
			String publisher, String websiteURL, String fulltextURL,
			Path fulltextFilePath, Integer gScholarNumCitations, Set<Tag> tags) {
		super(iD, creationTime, lastChangeTime);
		this.title = title;
		this.type = type;
		this.authors = authors;
		this.DOI = DOI;
		this.ISBN = ISBN;
		this.year = year;
		this.publicationContext = publicationContext;
		this.publisher = publisher;
		this.websiteURL = websiteURL;
		this.fulltextURL = fulltextURL;
		this.fulltextFilePath = fulltextFilePath;
		this.gScholarNumCitations = gScholarNumCitations;
		this.tags = tags;
	}
	
	public Literature(String title, LiteratureType type, List<Author> authors,
			String DOI, ISBN ISBN, Integer year, String publicationContext,
			String publisher, String websiteURL, String fulltextURL,
			Path fulltextFilePath, Integer gScholarNumCitations, Set<Tag> tags) {
		super();
		this.title = title;
		this.type = type;
		this.authors = authors;
		this.DOI = DOI;
		this.ISBN = ISBN;
		this.year = year;
		this.publicationContext = publicationContext;
		this.publisher = publisher;
		this.websiteURL = websiteURL;
		this.fulltextURL = fulltextURL;
		this.fulltextFilePath = fulltextFilePath;
		this.gScholarNumCitations = gScholarNumCitations;
		this.tags = tags;
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

	public String getPublicationContext() {
		return publicationContext;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public String getFulltextURL() {
		return fulltextURL;
	}

	public Path getFulltextFilePath() {
		return fulltextFilePath;
	}

	public Integer getgScholarNumCitations() {
		return gScholarNumCitations;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
