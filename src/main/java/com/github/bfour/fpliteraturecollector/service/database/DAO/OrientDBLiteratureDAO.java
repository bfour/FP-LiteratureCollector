package com.github.bfour.fpliteraturecollector.service.database.DAO;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Person;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBLiteratureDAO extends OrientDBEntityDAO<Literature>
		implements LiteratureDAO {

	private static class LazyLiterature extends Literature {

		private Vertex vertex;
		private OrientDBPersonDAO personDAO;
		private LazyGraphEntity entity;

		public LazyLiterature(Vertex vertex, OrientDBPersonDAO personDAO) {
			this.vertex = vertex;
			this.entity = new LazyGraphEntity(vertex);
			this.personDAO = personDAO;
		}

		@Override
		public String getTitle() {
			if (title == null)
				title = vertex.getProperty("title");
			return title;
		}

		@Override
		public List<Person> getAuthors() {
			if (authors == null) {
				try {
					authors = GraphUtils.getCollectionFromVertexProperty(
							vertex, "authors", personDAO);
				} catch (DatalayerException e) {
					authors = new ArrayList<Person>(0);
					// TODO (low) improve
				}
			}
			return this.authors;
		}

		@Override
		public String getDOI() {
			if (DOI == null)
				DOI = vertex.getProperty("DOI");
			return DOI;
		}

		@Override
		public ISBN getISBN() {
			if (ISBN == null)
				ISBN = new ISBN((String) vertex.getProperty("ISBN"));
			return ISBN;
		}

		@Override
		public File getFulltext() {
			// TODO Auto-generated method stub
			return super.getFulltext();
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

	private static OrientDBLiteratureDAO instance;
	private OrientDBPersonDAO personDAO;

	private OrientDBLiteratureDAO(OrientDBGraphService dbs,
			boolean forceCreateNewInstance) {
		super(dbs, "literature");
		this.personDAO = OrientDBPersonDAO.getInstance(dbs,
				forceCreateNewInstance);
	}

	public static OrientDBLiteratureDAO getInstance(OrientDBGraphService dbs,
			boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBLiteratureDAO(dbs, forceCreateNewInstance);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Literature entity, long ID,
			Vertex givenVertex) throws DatalayerException {

		Vertex entityVertex = super.entityToVertex(entity, ID, givenVertex);

		// title
		entityVertex.setProperty("title", entity.getTitle());

		// authors
		GraphUtils.setCollectionPropertyOnVertex(entityVertex, "authors",
				entity.getAuthors(), personDAO);

		// DOI ISBN
		if (entity.getDOI() == null)
			entityVertex.removeProperty("DOI");
		else
			entityVertex.setProperty("DOI", entity.getDOI());
		if (entity.getISBN() == null)
			entityVertex.removeProperty("ISBN");
		else
			entityVertex.setProperty("ISBN", entity.getISBN().getV13String());

		return entityVertex;

	}

	@Override
	public Literature vertexToEntity(Vertex vertex) {
		return new LazyLiterature(vertex, personDAO);
	}

}
