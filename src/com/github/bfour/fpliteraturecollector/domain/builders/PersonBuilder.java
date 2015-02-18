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


import java.util.Date;

import com.github.bfour.fpjgui.abstraction.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.Person;
import com.tinkerpop.frames.Property;

public class PersonBuilder extends Person implements EntityBuilder<Person> {

	public PersonBuilder() {
		this.ID = -1;
		this.creationTime = new Date();
		this.lastChangeTime = new Date();
	}
	
	public PersonBuilder(Person person) {
		this.ID = person.getID();
		this.creationTime = person.getCreationTime();
		this.lastChangeTime = person.getLastChangeTime();
		this.firstName = person.getFirstName();
		this.lastName = person.getLastName();
	}

	@Override
	public Person getEntity() {
		return new Person(ID, creationTime, lastChangeTime, firstName, lastName);
	}
	
	@Property("ID")
	public void setID(long ID) {
		this.ID = ID;
	}
	
	@Property("creationTime")
	public void setCreationTime(Date time) {
		this.creationTime = time;
	}
	
	@Property("lastChangeTime")
	public void setLastChangeTime(Date time) {
		this.lastChangeTime = time;
	}
	
	@Property("firstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@Property("lastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
