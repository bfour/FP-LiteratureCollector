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
import com.github.bfour.fpliteraturecollector.domain.SupportedSearchEngine;
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
		public SupportedSearchEngine getSearchEngine() {
			if (getSearchEngine() == null)
				setSearchEngine(SupportedSearchEngine.valueOf((String) vertex
						.getProperty("searchEngine")));
			return getSearchEngine();
		}

		@Override
		public String getSearchString() {
			if (getSearchString() == null)
				setSearchString((String) vertex.getProperty("searchString"));
			return getSearchString();
		}

		@Override
		public List<Literature> getResults() {
			try {
				if (getResults() == null)
					setResults(GraphUtils.getCollectionFromVertexProperty(
							vertex, "results", literatureDAO));
			} catch (DatalayerException e) {
				// TODO (low) improve
				setResults(new ArrayList<Literature>(0));
			}
			return getResults();
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

	private OrientDBAtomicRequestDAO(OrientDBGraphService dbs,
			boolean forceCreateNewInstance) {
		super(dbs, "atomicRequest");
		this.literatureDAO = OrientDBLiteratureDAO.getInstance(dbs,
				forceCreateNewInstance);
	}

	public static OrientDBAtomicRequestDAO getInstance(
			OrientDBGraphService dbs, boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBAtomicRequestDAO(dbs, forceCreateNewInstance);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(AtomicRequest entity, long ID,
			Vertex givenVertex) throws DatalayerException {

		Vertex entityVertex = super.entityToVertex(entity, ID, givenVertex);

		entityVertex.setProperty("searchEngine", entity.getSearchEngine().name());
		entityVertex.setProperty("searchString", entity.getSearchString());
		GraphUtils.setCollectionPropertyOnVertex(entityVertex, "results",
				entity.getResults(), literatureDAO);
		
		return entityVertex;

	}

	@Override
	public AtomicRequest vertexToEntity(Vertex vertex) {
		return new LazyAtomicRequest(vertex, literatureDAO);
	}

}
