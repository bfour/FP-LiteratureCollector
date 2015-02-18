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
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphAPIService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBPersonDAO extends AbstractOrientDBDAO<Person> implements
		PersonDAO {

	private static class LazyPerson extends Person {

		private Vertex vertex;

		public LazyPerson(Vertex personVertex) {
			this.vertex = personVertex;
		}

		@Override
		public String getFirstName() {
			return vertex.getProperty("firstName");
		}

		@Override
		public String getLastName() {
			return vertex.getProperty("lastName");
		}

		@Override
		public long getID() {
			return vertex.getProperty("ID");
		}

		@Override
		public Date getCreationTime() {
			return vertex.getProperty("creationTime");
		}

		@Override
		public Date getLastChangeTime() {
			return vertex.getProperty("lastChangeTime");
		}

	}

	private static OrientDBPersonDAO instance;

	protected OrientDBPersonDAO(OrientDBGraphAPIService dbs) {
		super(dbs, "person");
	}

	public static OrientDBPersonDAO getInstance(OrientDBGraphAPIService dbs) {
		if (instance == null)
			instance = new OrientDBPersonDAO(dbs);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Person entity, long ID, Vertex givenVertex)
			throws DatalayerException {

		Vertex v = givenVertex;
		if (v == null) {
			v = db.addVertex("class:person");
		} else {
			Vertex vertexInDB = getVertexForEntity(entity);
			if (entity.getCreationTime() == null)
				vertexInDB.removeProperty("creationTime");
			if (entity.getLastChangeTime() == null)
				vertexInDB.removeProperty("lastChangeTime");
			if (entity.getFirstName() == null)
				vertexInDB.removeProperty("firstName");
			if (entity.getLastName() == null)
				vertexInDB.removeProperty("lastName");
		}

		v.setProperty("ID", ID);
		v.setProperty("creationTime", entity.getCreationTime());
		v.setProperty("lastChangeTime", entity.getLastChangeTime());
		v.setProperty("firstName", entity.getFirstName());
		v.setProperty("lastName", entity.getLastName());

		return v;

	}

	@Override
	protected Person vertexToEntity(Vertex vertex) {
		if (vertex == null)
			return null;
		return new LazyPerson(vertex);
	}

}
