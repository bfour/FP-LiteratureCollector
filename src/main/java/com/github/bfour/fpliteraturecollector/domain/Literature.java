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
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.support.index.IndexType;

import com.github.bfour.fpjpersist.neo4j.model.Neo4JEntity;
import com.github.bfour.fpjsearch.fpjsearch.Searchable;

public class Literature extends Neo4JEntity implements Searchable {

	public static enum LiteratureType implements Searchable {
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

	@Fetch
	@RelatedTo(type = "AUTHORS", direction = Direction.OUTGOING)
	protected Set<Author> authors;

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
	protected Integer msAcademicNumCitations;

	@Fetch
	@RelatedTo(type = "TAGS", direction = Direction.OUTGOING)
	protected Set<Tag> tags;

	public Literature() {
	}
	
	public Literature(Long ID, Date creationTime, Date lastChangeTime,
			String title, LiteratureType type, Set<Author> authors, String dOI,
			com.github.bfour.fpliteraturecollector.domain.ISBN iSBN,
			Integer year, String publicationContext, String publisher,
			String websiteURL, String fulltextURL, Path fulltextFilePath,
			Integer gScholarNumCitations, Integer msAcademicNumCitations,
			Set<Tag> tags) {
		super(ID, creationTime, lastChangeTime);
		this.title = title;
		this.type = type;
		this.authors = authors;
		DOI = dOI;
		ISBN = iSBN;
		this.year = year;
		this.publicationContext = publicationContext;
		this.publisher = publisher;
		this.websiteURL = websiteURL;
		this.fulltextURL = fulltextURL;
		this.fulltextFilePath = fulltextFilePath;
		this.gScholarNumCitations = gScholarNumCitations;
		this.msAcademicNumCitations = msAcademicNumCitations;
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public LiteratureType getType() {
		return type;
	}

	public Set<Author> getAuthors() {
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

	public Integer getMsAcademicNumCitations() {
		return msAcademicNumCitations;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
