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
	private String title;

	private String abstractText;

	private LiteratureType type;

	@Fetch
	@RelatedTo(type = "AUTHORS", direction = Direction.OUTGOING)
	private Set<Author> authors;

	private ISBN ISBN;
	private String DOI;
	private String gScholarID;
	private String msAcademicID;
	private String pubmedID;
	private String acmID;

	private Integer year;

	/**
	 * eg. name of journal, name of conference ...
	 */
	@Indexed(indexType = IndexType.FULLTEXT, indexName = "publicationContext")
	private String publicationContext;
	@Indexed(indexType = IndexType.FULLTEXT, indexName = "publisher")
	private String publisher;

	private Set<Link> websiteURLs;
	private Set<Link> fulltextURLs;
	private Set<Link> fulltextFilePaths;

	private Integer gScholarNumCitations;
	private Integer msAcademicNumCitations;
	private Integer acmNumCitations;
	private Integer pubmedNumCitations;
	private Integer ieeeNumCitations;

	@Fetch
	@RelatedTo(type = "TAGS", direction = Direction.OUTGOING)
	private Set<Tag> tags;
	private String notes;

	public Literature() {
	}

	public Literature(Long ID, Date creationTime, Date lastChangeTime,
			String title, String abstractText, LiteratureType type,
			Set<Author> authors, String dOI,
			com.github.bfour.fpliteraturecollector.domain.ISBN iSBN,
			String gScholarID, String msAcademicID, String pubmedID,
			String acmID, Integer year, String publicationContext,
			String publisher, Set<Link> websiteURLs, Set<Link> fulltextURLs,
			Set<Link> fulltextFilePaths, Integer gScholarNumCitations,
			Integer msAcademicNumCitations, Integer acmNumCitations,
			Integer pubmedNumCitations, Integer ieeeNumCitations,
			Set<Tag> tags, String notes) {
		super(ID, creationTime, lastChangeTime);
		this.title = title;
		this.abstractText = abstractText;
		this.type = type;
		this.authors = authors;
		DOI = dOI;
		ISBN = iSBN;
		this.gScholarID = gScholarID;
		this.msAcademicID = msAcademicID;
		this.pubmedID = pubmedID;
		this.acmID = acmID;
		this.year = year;
		this.publicationContext = publicationContext;
		this.publisher = publisher;
		this.websiteURLs = websiteURLs;
		this.fulltextURLs = fulltextURLs;
		this.fulltextFilePaths = fulltextFilePaths;
		this.gScholarNumCitations = gScholarNumCitations;
		this.msAcademicNumCitations = msAcademicNumCitations;
		this.acmNumCitations = acmNumCitations;
		this.pubmedNumCitations = pubmedNumCitations;
		this.ieeeNumCitations = ieeeNumCitations;
		this.tags = tags;
		this.notes = notes;
	}



	public String getTitle() {
		return title;
	}

	public String getAbstractText() {
		return abstractText;
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

	public String getgScholarID() {
		return gScholarID;
	}

	public String getMsAcademicID() {
		return msAcademicID;
	}

	public String getPubmedID() {
		return pubmedID;
	}

	public String getAcmID() {
		return acmID;
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

	public Set<Link> getWebsiteURLs() {
		return websiteURLs;
	}

	public Set<Link> getFulltextURLs() {
		return fulltextURLs;
	}

	public Set<Link> getFulltextFilePaths() {
		return fulltextFilePaths;
	}

	public Integer getgScholarNumCitations() {
		return gScholarNumCitations;
	}

	public Integer getMsAcademicNumCitations() {
		return msAcademicNumCitations;
	}

	public Integer getAcmNumCitations() {
		return acmNumCitations;
	}

	public Integer getPubmedNumCitations() {
		return pubmedNumCitations;
	}

	public Integer getIeeeNumCitations() {
		return ieeeNumCitations;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public String getNotes() {
		return notes;
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
