package com.github.bfour.fpliteraturecollector.domain.builders;

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

/*
 * =================================
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
 * =================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * *
 */

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.Author;

public class AuthorBuilder extends EntityBuilder<Author> implements
		Builder<Author> {

	private String firstName;
	private String middleName;
	private String lastName;
	private String gScholarID;
	private String msAcademicID;
	private String acmID;
	private String pubmedID;
	private String ieeeID;	

	public AuthorBuilder() {
		super();
	}

	public AuthorBuilder(Author person) {
		setID(person.getID());
		setCreationTime(person.getCreationTime());
		setLastChangeTime(person.getLastChangeTime());
		setFirstName(person.getFirstName());
		setMiddleName(person.getMiddleName());
		setLastName(person.getLastName());
		setgScholarID(person.getgScholarID());
		setMsAcademicID(person.getMsAcademicID());
		setAcmID(person.getAcmID());
		setPubmedID(person.getPubmedID());
		setIeeeID(person.getIeeeID());
	}

	@Override
	public Author getObject() {
		return new Author(getID(), getCreationTime(), getLastChangeTime(),
				getFirstName(), getMiddleName(), getLastName(), getgScholarID(),
				getMsAcademicID(), getAcmID(), getPubmedID(), getIeeeID());
	}

	public String getFirstName() {
		return firstName;
	}

	public AuthorBuilder setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public AuthorBuilder setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getgScholarID() {
		return gScholarID;
	}

	public AuthorBuilder setgScholarID(String gScholarID) {
		this.gScholarID = gScholarID;
		return this;
	}

	public String getMsAcademicID() {
		return msAcademicID;
	}

	public AuthorBuilder setMsAcademicID(String msAcademicID) {
		this.msAcademicID = msAcademicID;
		return this;
	}

	public String getAcmID() {
		return acmID;
	}

	public void setAcmID(String acmID) {
		this.acmID = acmID;
	}

	public String getPubmedID() {
		return pubmedID;
	}

	public void setPubmedID(String pubmedID) {
		this.pubmedID = pubmedID;
	}

	public String getIeeeID() {
		return ieeeID;
	}

	public void setIeeeID(String ieeeID) {
		this.ieeeID = ieeeID;
	}

}
