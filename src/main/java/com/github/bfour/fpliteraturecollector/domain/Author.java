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

import com.github.bfour.fpjcommons.model.Entity;

public class Author extends Entity {

	protected String firstName;
	protected String lastName;
	protected String gScholarID;
	protected String msAcademicID;

	public Author(long iD, Date creationTime, Date lastChangeTime,
			String firstName, String lastName, String gScholarID,
			String msAcademicID) {
		super(iD, creationTime, lastChangeTime);
		this.firstName = firstName;
		this.lastName = lastName;
		this.gScholarID = gScholarID;
		this.msAcademicID = msAcademicID;
	}

	public Author(String firstName, String lastName, String gScholarID,
			String msAcademicID) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.gScholarID = gScholarID;
		this.msAcademicID = msAcademicID;
	}

	public Author() {
		super();
	}

	public String getFirstName() {
		return firstName;
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

	@Override
	public String toString() {
		return getFirstName() + " " + getLastName() + " (ID " + getID() + ")";
	}

}
