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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Literature;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.TagService;
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class OrientDBAtomicRequestDAO extends OrientDBEntityDAO<AtomicRequest>
		implements AtomicRequestDAO {

	private static class LazyAtomicRequest extends AtomicRequest {

		private OrientGraph db;
		private Object vertexID;
		private LazyGraphEntity entity;
		private OrientDBLiteratureDAO literatureDAO;

		public LazyAtomicRequest(Object vertexID, OrientGraph db,
				OrientDBLiteratureDAO literatureDAO) {
			this.vertexID = vertexID;
			this.db = db;
			this.entity = new LazyGraphEntity(vertexID, db);
			this.literatureDAO = literatureDAO;
		}

		@Override
		public Crawler getCrawler() {
			if (crawler == null)
				crawler = CrawlerService.getInstance().getCrawlerForIdentifier(
						(String) db.getVertex(vertexID).getProperty(
								"searchEngine"));
			return crawler;
		}

		@Override
		public String getSearchString() {
			if (searchString == null)
				searchString = (String) db.getVertex(vertexID).getProperty(
						"searchString");
			return searchString;
		}

		@Override
		public Integer getMaxPageTurns() {
			if (maxPageTurns == null)
				maxPageTurns = (Integer) db.getVertex(vertexID).getProperty(
						"maxPageTurns");
			return maxPageTurns;
		}

		@Override
		public List<Literature> getResults() {
			try {
				if (results == null)
					results = GraphUtils.getCollectionFromVertexProperty(
							db.getVertex(vertexID), "results", literatureDAO);
			} catch (DatalayerException e) {
				// TODO (low) improve
				results = new ArrayList<Literature>(0);
			}
			return results;
		}

		@Override
		public boolean isProcessed() {
			Boolean processed = db.getVertex(vertexID).getProperty("processed");
			return (processed == null ? false : processed);
		}

		@Override
		public String getProcessingError() {
			if (processingError == null)
				processingError = db.getVertex(vertexID).getProperty(
						"processingError");
			return processingError;
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

	private static OrientDBAtomicRequestDAO instance;
	private OrientDBLiteratureDAO literatureDAO;
	private LiteratureService litServ;

	private OrientDBAtomicRequestDAO(OrientDBGraphService dbs,
			boolean forceCreateNewInstance, LiteratureService litServ,
			AuthorService authServ, TagService tagServ) {
		super(dbs, "atomicRequest");
		this.literatureDAO = OrientDBLiteratureDAO.getInstance(dbs,
				forceCreateNewInstance, authServ, tagServ);
		this.litServ = litServ;
	}

	public static OrientDBAtomicRequestDAO getInstance(
			OrientDBGraphService dbs, boolean forceCreateNewInstance,
			LiteratureService litServ, AuthorService authServ,
			TagService tagServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBAtomicRequestDAO(dbs,
					forceCreateNewInstance, litServ, authServ, tagServ);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(AtomicRequest entity, long ID,
			Vertex givenVertex) throws DatalayerException {

		Vertex entityVertex = super.entityToVertex(entity, ID, givenVertex);

		entityVertex.setProperty("searchEngine", CrawlerService.getInstance()
				.getIdentifierForCrawler(entity.getCrawler()));
		entityVertex.setProperty("searchString", entity.getSearchString());
		entityVertex.setProperty("maxPageTurns", entity.getMaxPageTurns());
		GraphUtils.setCollectionPropertyOnVertex(entityVertex, "results",
				entity.getResults(), literatureDAO, litServ, true);
		GraphUtils.setProperty(entityVertex, "processed",
				entity.isProcessed(), false);
		GraphUtils.setProperty(entityVertex, "processingError",
				entity.getProcessingError(), true);

		return entityVertex;

	}

	@Override
	public AtomicRequest vertexToEntity(Vertex vertex) {
		return new LazyAtomicRequest(vertex.getId(), db, literatureDAO);
	}

}
