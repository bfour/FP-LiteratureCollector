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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.Author;
import com.github.bfour.fpliteraturecollector.domain.ISBN;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.domain.Tag;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.TagService;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientDBLiteratureDAO extends OrientDBEntityDAO<Literature>
		implements LiteratureDAO {

	// TODO if values in DB are null -> remember somehow if already accessed
	private static class LazyLiterature extends Literature {

		private Object vertexID;
		private OrientGraph db;
		private OrientDBAuthorDAO personDAO;
		private OrientDBTagDAO tagDAO;
		private LazyGraphEntity entity;

		public LazyLiterature(Object vertexID, OrientGraph db,
				OrientDBAuthorDAO personDAO, OrientDBTagDAO tagDAO) {
			this.vertexID = vertexID;
			this.db = db;
			this.entity = new LazyGraphEntity(vertexID, db);
			this.personDAO = personDAO;
			this.tagDAO = tagDAO;
		}

		@Override
		public String getTitle() {
			if (title == null)
				title = db.getVertex(vertexID).getProperty("title");
			return title;
		}

		@Override
		public List<Author> getAuthors() {
			if (authors == null) {
				try {
					authors = GraphUtils.getCollectionFromVertexProperty(
							db.getVertex(vertexID), "authors", personDAO);
				} catch (DatalayerException e) {
					authors = new ArrayList<Author>(0);
					// TODO (low) improve
				}
			}
			return this.authors;
		}

		@Override
		public String getDOI() {
			if (DOI == null)
				DOI = db.getVertex(vertexID).getProperty("DOI");
			return DOI;
		}

		@Override
		public ISBN getISBN() {
			if (ISBN == null) {
				String ISBNString = db.getVertex(vertexID).getProperty("ISBN");
				if (ISBNString != null)
					ISBN = new ISBN(ISBNString);
			}
			return ISBN;
		}

		@Override
		public LiteratureType getType() {
			if (type == null)
				type = LiteratureType.valueOf((String) db.getVertex(vertexID)
						.getProperty("type"));
			return type;
		}

		@Override
		public Integer getYear() {
			if (year == null)
				year = (Integer) db.getVertex(vertexID).getProperty("year");
			return year;
		}

		@Override
		public String getPublicationContext() {
			if (publicationContext == null)
				publicationContext = db.getVertex(vertexID).getProperty(
						"publicationContext");
			return publicationContext;
		}

		@Override
		public String getPublisher() {
			if (publisher == null)
				publisher = db.getVertex(vertexID).getProperty("publisher");
			return publisher;
		}

		@Override
		public String getWebsiteURL() {
			if (websiteURL == null)
				websiteURL = db.getVertex(vertexID).getProperty("websiteURL");
			return websiteURL;
		}

		@Override
		public Path getFulltextFilePath() {
			if (fulltextFilePath == null) {
				String pathString = db.getVertex(vertexID).getProperty(
						"fulltextFilePath");
				if (pathString != null)
					fulltextFilePath = Paths.get(pathString);
			}
			return fulltextFilePath;
		}

		@Override
		public String getFulltextURL() {
			if (fulltextURL == null)
				fulltextURL = (String) db.getVertex(vertexID).getProperty(
						"fulltextURL");
			return fulltextURL;
		}

		@Override
		public Integer getgScholarNumCitations() {
			if (gScholarNumCitations == null)
				gScholarNumCitations = (Integer) db.getVertex(vertexID)
						.getProperty("gScholarNumCitations");
			return gScholarNumCitations;
		}

		@Override
		public Set<Tag> getTags() {
			if (tags == null) {
				try {
					tags = new HashSet<Tag>(
							GraphUtils.getCollectionFromVertexProperty(
									db.getVertex(vertexID), "tags", tagDAO));
				} catch (DatalayerException e) {
					// tags = new HashSet<Tag>();
					// TODO (low) improve
				}
			}
			return this.tags;
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
	private OrientDBAuthorDAO authorDAO;
	private OrientDBTagDAO tagDAO;
	private AuthorService authServ;
	private TagService tagServ;

	private OrientDBLiteratureDAO(OrientDBGraphService dbs,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ) {
		super(dbs, "literature");
		this.authorDAO = OrientDBAuthorDAO.getInstance(dbs,
				forceCreateNewInstance);
		this.tagDAO = OrientDBTagDAO.getInstance(dbs, forceCreateNewInstance);
		this.authServ = authServ;
		this.tagServ = tagServ;
	}

	public static OrientDBLiteratureDAO getInstance(OrientDBGraphService dbs,
			boolean forceCreateNewInstance, AuthorService authServ,
			TagService tagServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBLiteratureDAO(dbs, forceCreateNewInstance,
					authServ, tagServ);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Literature entity, long ID,
			Vertex givenVertex) throws DatalayerException {

		Vertex v = super.entityToVertex(entity, ID, givenVertex);

		// title
		GraphUtils.setProperty(v, "title", entity.getTitle(), false);

		// type
		GraphUtils.setProperty(v, "type", (entity.getType() == null ? null
				: entity.getType().name()), true);

		// authors
		GraphUtils.setCollectionPropertyOnVertex(v, "authors",
				entity.getAuthors(), authorDAO, authServ, true);

		// DOI
		GraphUtils.setProperty(v, "DOI", entity.getDOI(), true);

		// ISBN
		GraphUtils.setProperty(v, "ISBN", (entity.getISBN() == null ? null
				: entity.getISBN().getV13String()), true);

		// year
		GraphUtils.setProperty(v, "year", entity.getYear(), true);

		// publicationContext
		GraphUtils.setProperty(v, "publicationContext",
				entity.getPublicationContext(), true);

		// publisher
		GraphUtils.setProperty(v, "publisher", entity.getPublisher(), true);

		// website URL
		GraphUtils.setProperty(v, "websiteURL", entity.getWebsiteURL(), true);

		// fulltext URL
		GraphUtils.setProperty(v, "fulltextURL", entity.getFulltextURL(), true);

		// fulltext file path
		GraphUtils.setProperty(v, "fulltextFilePath",
				entity.getFulltextFilePath(), true);

		// gScholarNumCitations
		GraphUtils.setProperty(v, "gScholarNumCitations",
				entity.getgScholarNumCitations(), true);

		// tags
		GraphUtils.setCollectionPropertyOnVertex(v, "tags", entity.getTags(),
				tagDAO, tagServ, true);

		return v;

	}

	@Override
	public Literature vertexToEntity(Vertex vertex) {
		return new LazyLiterature(vertex.getId(), db, authorDAO, tagDAO);
	}

	public boolean hasMaxOneAdjacentAtomicRequest(Literature lit) {
		Vertex v = getVertexForEntity(lit);
		Iterator<Edge> iter = v.getEdges(Direction.BOTH, "results").iterator();
		if (!iter.hasNext())
			return true;
		iter.next();
		return !iter.hasNext();
	}

}
