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
import com.github.bfour.fpliteraturecollector.service.crawlers.Crawler;
import com.github.bfour.fpliteraturecollector.service.crawlers.CrawlerService;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBAtomicRequestDAO extends OrientDBEntityDAO<AtomicRequest>
		implements AtomicRequestDAO {

	private static class LazyAtomicRequest extends AtomicRequest {

		private Vertex vertex;
		private LazyGraphEntity entity;
		private OrientDBLiteratureDAO literatureDAO;

		public LazyAtomicRequest(Vertex vertex,
				OrientDBLiteratureDAO literatureDAO) {
			this.vertex = vertex;
			this.entity = new LazyGraphEntity(vertex);
			this.literatureDAO = literatureDAO;
		}

		@Override
		public Crawler getCrawler() {
			if (crawler == null)
				crawler = CrawlerService.getInstance().getCrawlerForIdentifier(
						(String) vertex.getProperty("searchEngine"));
			return crawler;
		}

		@Override
		public String getSearchString() {
			if (searchString == null)
				searchString = (String) vertex.getProperty("searchString");
			return searchString;
		}

		@Override
		public Integer getMaxPageTurns() {
			if (maxPageTurns == null)
				maxPageTurns = (Integer) vertex.getProperty("maxPageTurns");
			return maxPageTurns;
		}

		@Override
		public List<Literature> getResults() {
			try {
				if (results == null)
					results = GraphUtils.getCollectionFromVertexProperty(
							vertex, "results", literatureDAO);
			} catch (DatalayerException e) {
				// TODO (low) improve
				results = new ArrayList<Literature>(0);
			}
			return results;
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
			AuthorService authServ) {
		super(dbs, "atomicRequest");
		this.literatureDAO = OrientDBLiteratureDAO.getInstance(dbs,
				forceCreateNewInstance, authServ);
		this.litServ = litServ;
	}

	public static OrientDBAtomicRequestDAO getInstance(
			OrientDBGraphService dbs, boolean forceCreateNewInstance,
			LiteratureService litServ, AuthorService authServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBAtomicRequestDAO(dbs,
					forceCreateNewInstance, litServ, authServ);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(AtomicRequest entity, long ID,
			Vertex givenVertex) throws DatalayerException {

		Vertex entityVertex = super.entityToVertex(entity, ID, givenVertex);

		entityVertex.setProperty("searchEngine", CrawlerService.getInstance()
				.getIdentifierForCrawler(entity.getCrawler()));
		entityVertex.setProperty("searchString", entity.getSearchString());
		entityVertex.setProperty("maxPageTurns", entity.getSearchString());
		GraphUtils.setCollectionPropertyOnVertex(entityVertex, "results",
				entity.getResults(), literatureDAO, litServ, true);

		return entityVertex;

	}

	@Override
	public AtomicRequest vertexToEntity(Vertex vertex) {
		return new LazyAtomicRequest(vertex, literatureDAO);
	}

}
