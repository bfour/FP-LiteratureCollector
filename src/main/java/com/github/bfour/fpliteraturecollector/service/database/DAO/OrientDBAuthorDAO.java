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
import java.util.Iterator;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBAuthorDAO extends OrientDBEntityDAO<Author> implements
		AuthorDAO {

	private static class LazyPerson extends Author {

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
		public String getgScholarID() {
			if (gScholarID == null)
				gScholarID = vertex.getProperty("gScholarID");
			return gScholarID;
		}

		@Override
		public String getMsAcademicID() {
			if (msAcademicID == null)
				msAcademicID = vertex.getProperty("msAcademicID");
			return msAcademicID;
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

	private static OrientDBAuthorDAO instance;

	protected OrientDBAuthorDAO(OrientDBGraphService dbs) {
		super(dbs, "person");
	}

	public static OrientDBAuthorDAO getInstance(OrientDBGraphService dbs,
			boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBAuthorDAO(dbs);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Author entity, long ID, Vertex givenVertex)
			throws DatalayerException {

		Vertex v = super.entityToVertex(entity, ID, givenVertex);

		GraphUtils.setProperty(v, "firstName", entity.getFirstName(), true);
		GraphUtils.setProperty(v, "lastName", entity.getLastName(), true);
		GraphUtils.setProperty(v, "gScholarID", entity.getgScholarID(), true);
		GraphUtils.setProperty(v, "msAcademicID", entity.getMsAcademicID(),
				true);

		return v;

	}

	@Override
	public Author vertexToEntity(Vertex vertex) {
		if (vertex == null)
			return null;
		return new LazyPerson(vertex);
	}

	public Author getByGScholarID(String gScholarID) throws ServiceException {
		Iterator<Vertex> iter = db.getVertices("person", new String[] { "gScholarID" },
				new Object[] { gScholarID }).iterator();
		if (!iter.hasNext())
			return null;
		return vertexToEntity(iter.next());
	}

	public Author getByMsAcademicID(String msAcademicID)
			throws ServiceException {
		Iterator<Vertex> iter = db.getVertices("person", new String[] { "msAcademicID" },
				new Object[] { msAcademicID }).iterator();
		if (!iter.hasNext())
			return null;
		return vertexToEntity(iter.next());
	}

}