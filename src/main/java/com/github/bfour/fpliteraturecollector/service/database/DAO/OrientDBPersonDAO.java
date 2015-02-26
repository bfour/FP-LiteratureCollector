package com.github.bfour.fpliteraturecollector.service.database.DAO;

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

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.Person;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBPersonDAO extends OrientDBEntityDAO<Person> implements
		PersonDAO {

	private static class LazyPerson extends Person {

		private Vertex vertex;
		private LazyGraphEntity entity;

		public LazyPerson(Vertex vertex) {
			this.vertex = vertex;
			this.entity = new LazyGraphEntity(vertex);
		}

		@Override
		public String getFirstName() {
			if (firstName == null)
				firstName = vertex.getProperty("firstName");
			return firstName;
		}

		@Override
		public String getLastName() {
			if (lastName == null)
				lastName = vertex.getProperty("lastName");
			return lastName;
		}

		@Override
		public Long getID() {
			return entity.getID();
		}

		@Override
		public Date getCreationTime() {
			return entity.getCreationTime();
		}

		@Override
		public Date getLastChangeTime() {
			return entity.getLastChangeTime();
		}

	}

	private static OrientDBPersonDAO instance;

	protected OrientDBPersonDAO(OrientDBGraphService dbs) {
		super(dbs, "person");
	}

	public static OrientDBPersonDAO getInstance(OrientDBGraphService dbs,
			boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBPersonDAO(dbs);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Person entity, long ID, Vertex givenVertex)
			throws DatalayerException {

		Vertex entityVertex = super.entityToVertex(entity, ID, givenVertex);

		if (entity.getFirstName() == null)
			entityVertex.removeProperty("firstName");
		if (entity.getLastName() == null)
			entityVertex.removeProperty("lastName");

		entityVertex.setProperty("firstName", entity.getFirstName());
		entityVertex.setProperty("lastName", entity.getLastName());

		return entityVertex;

	}

	@Override
	public Person vertexToEntity(Vertex vertex) {
		if (vertex == null)
			return null;
		return new LazyPerson(vertex);
	}

}
