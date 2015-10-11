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

import java.util.Set;

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Link;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Literature.LiteratureType;
import com.github.bfour.fpliteraturecollector.domain.Tag;

public class LiteratureBuilder extends EntityBuilder<Literature> implements
		Builder<Literature> {

	private String title;
	private String abstractText;
	private LiteratureType type;
	private Set<Author> authors;
	private String DOI;
	private ISBN ISBN;
	private Integer year;
	private String publicationContext;
	private String publisher;
	private Set<Link> websiteURLs;
	private Set<Link> fulltextURLs;
	private Set<Link> fulltextFilePaths;
	private Integer gScholarNumCitations;
	private Integer msAcademicNumCitations;
	private Integer acmNumCitations;
	private Integer pubmedNumCitations;
	private Integer ieeeNumCitations;
	private Set<Tag> tags;

	public LiteratureBuilder() {
		super();
	}

	public LiteratureBuilder(Literature l) {

		setID(l.getID());
		setCreationTime(l.getCreationTime());
		setLastChangeTime(l.getLastChangeTime());

		setTitle(l.getTitle());
		setAbstractText(l.getAbstractText());
		setType(l.getType());
		setAuthors(l.getAuthors());
		setDOI(l.getDOI());
		setISBN(l.getISBN());
		setYear(l.getYear());
		setPublicationContext(l.getPublicationContext());
		setPublisher(l.getPublisher());
		setWebsiteURLs(l.getWebsiteURLs());
		setFulltextFilePaths(l.getFulltextFilePaths());
		setFulltextURLs(l.getFulltextURLs());

		setgScholarNumCitations(l.getgScholarNumCitations());
		setMsAcademicNumCitations(l.getMsAcademicNumCitations());
		setAcmNumCitations(l.getAcmNumCitations());
		setPubmedNumCitations(l.getPubmedNumCitations());
		setIeeeNumCitations(l.getIeeeNumCitations());

		setTags(l.getTags());

	}

	@Override
	public Literature getObject() {
		return new Literature(getID(), getCreationTime(), getLastChangeTime(),
				getTitle(), getAbstractText(), getType(), getAuthors(),
				getDOI(), getISBN(), getYear(), getPublicationContext(),
				getPublisher(), getWebsiteURLs(), getFulltextURLs(),
				getFulltextFilePaths(), getgScholarNumCitations(),
				getMsAcademicNumCitations(), getAcmNumCitations(),
				getPubmedNumCitations(), getIeeeNumCitations(), getTags());
	}

	public String getTitle() {
		return title;
	}

	public LiteratureBuilder setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	public LiteratureType getType() {
		return type;
	}

	public LiteratureBuilder setType(LiteratureType type) {
		this.type = type;
		return this;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public LiteratureBuilder setAuthors(Set<Author> authors) {
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

	public Set<Link> getWebsiteURLs() {
		return websiteURLs;
	}

	public void setWebsiteURLs(Set<Link> websiteURLs) {
		this.websiteURLs = websiteURLs;
	}

	public Set<Link> getFulltextURLs() {
		return fulltextURLs;
	}

	public void setFulltextURLs(Set<Link> fulltextURLs) {
		this.fulltextURLs = fulltextURLs;
	}

	public Set<Link> getFulltextFilePaths() {
		return fulltextFilePaths;
	}

	public void setFulltextFilePaths(Set<Link> fulltextFilePaths) {
		this.fulltextFilePaths = fulltextFilePaths;
	}

	public Integer getgScholarNumCitations() {
		return gScholarNumCitations;
	}

	public LiteratureBuilder setgScholarNumCitations(
			Integer gScholarNumCitations) {
		this.gScholarNumCitations = gScholarNumCitations;
		return this;
	}

	public Integer getMsAcademicNumCitations() {
		return msAcademicNumCitations;
	}

	public void setMsAcademicNumCitations(Integer msAcademicNumCitations) {
		this.msAcademicNumCitations = msAcademicNumCitations;
	}

	public Integer getAcmNumCitations() {
		return acmNumCitations;
	}

	public void setAcmNumCitations(Integer acmNumCitations) {
		this.acmNumCitations = acmNumCitations;
	}

	public Integer getPubmedNumCitations() {
		return pubmedNumCitations;
	}

	public void setPubmedNumCitations(Integer pubmedNumCitations) {
		this.pubmedNumCitations = pubmedNumCitations;
	}

	public Integer getIeeeNumCitations() {
		return ieeeNumCitations;
	}

	public void setIeeeNumCitations(Integer ieeeNumCitations) {
		this.ieeeNumCitations = ieeeNumCitations;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public LiteratureBuilder setTags(Set<Tag> tags) {
		this.tags = tags;
		return this;
	}

}
