package com.github.bfour.fpliteraturecollector.domain;

import java.util.Date;

import org.springframework.data.neo4j.annotation.Indexed;

import com.github.bfour.fpjpersist.neo4j.model.Neo4JEntity;
import com.github.bfour.fpjsearch.fpjsearch.Searchable;

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

public class Author extends Neo4JEntity implements Searchable {

	protected String firstName;
	protected String middleName;
	protected String lastName;

	@Indexed
	protected String gScholarID;

	@Indexed
	protected String msAcademicID;
	
	@Indexed
	protected String acmID;
	
	@Indexed
	protected String pubmedID;
	
	@Indexed
	protected String ieeeID;

	public Author(Long ID, Date creationTime, Date lastChangeTime,
			String firstName, String middleName, String lastName,
			String gScholarID, String msAcademicID, String acmID,
			String pubmedID, String ieeeID) {
		super(ID, creationTime, lastChangeTime);
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.gScholarID = gScholarID;
		this.msAcademicID = msAcademicID;
		this.acmID = acmID;
		this.pubmedID = pubmedID;
		this.ieeeID = ieeeID;
	}

	public Author(String firstName, String middleName, String lastName,
			String gScholarID, String msAcademicID, String acmID,
			String pubmedID, String ieeeID) {
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.gScholarID = gScholarID;
		this.msAcademicID = msAcademicID;
		this.acmID = acmID;
		this.pubmedID = pubmedID;
		this.ieeeID = ieeeID;
	}

	public Author() {
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getgScholarID() {
		return gScholarID;
	}

	public String getMsAcademicID() {
		return msAcademicID;
	}

	public String getAcmID() {
		return acmID;
	}

	public String getPubmedID() {
		return pubmedID;
	}

	public String getIeeeID() {
		return ieeeID;
	}

	@Override
	public String toString() {
		return getFirstName() + " " + getLastName() + " (ID " + getID() + ")";
	}

}
