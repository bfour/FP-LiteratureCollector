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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBQueryDAO extends OrientDBEntityDAO<Query> implements QueryDAO {

	private static class LazyQuery extends Query {

		private Vertex vertex;
		private LazyGraphEntity entity;

		public LazyQuery(Vertex vertex) {
			this.vertex = vertex;
			this.entity = new LazyGraphEntity(vertex);
		}

		@Override
		public List<AtomicRequest> getAtomicRequests() {
			// TODO
			return new ArrayList<AtomicRequest>();
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
	
	private static OrientDBQueryDAO instance;

	protected OrientDBQueryDAO(OrientDBGraphService dbs) {
		super(dbs, "query");
	}

	public static OrientDBQueryDAO getInstance(OrientDBGraphService dbs,
			boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBQueryDAO(dbs);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Query entity, long ID, Vertex givenVertex)
			throws DatalayerException {
		Vertex entityVertex = super.entityToVertex(entity, ID, givenVertex);
		entityVertex.setProperty("atomicRequests", entity.getAtomicRequests());
		return entityVertex;
	}

	@Override
	public Query vertexToEntity(Vertex vertex) throws DatalayerException {
		return new LazyQuery(vertex);
	}

}
