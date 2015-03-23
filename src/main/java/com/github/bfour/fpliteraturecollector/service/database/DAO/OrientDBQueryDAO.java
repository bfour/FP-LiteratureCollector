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
import java.util.Iterator;
import java.util.List;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.service.AtomicRequestService;
import com.github.bfour.fpliteraturecollector.service.AuthorService;
import com.github.bfour.fpliteraturecollector.service.LiteratureService;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public class OrientDBQueryDAO extends OrientDBEntityDAO<Query> implements
		QueryDAO {

	private static class LazyQuery extends Query {

		private Vertex vertex;
		private LazyGraphEntity entity;
		private OrientDBAtomicRequestDAO atomicRequestDAO;

		public LazyQuery(Vertex vertex,
				OrientDBAtomicRequestDAO atomicRequestDAO) {
			this.vertex = vertex;
			this.entity = new LazyGraphEntity(vertex);
			this.atomicRequestDAO = atomicRequestDAO;
		}

		@Override
		public String getName() {
			if (name == null)
				name = vertex.getProperty("name");
			return name;
		}

		@Override
		public List<AtomicRequest> getAtomicRequests() {
			try {
				if (atomicRequests == null)
					atomicRequests = GraphUtils
							.getCollectionFromVertexProperty(vertex,
									"atomicRequests", atomicRequestDAO);
			} catch (DatalayerException e) {
				// TODO (low) improve
				atomicRequests = new ArrayList<AtomicRequest>(0);
			}
			return atomicRequests;
		}

		@Override
		public Integer getQueuePosition() {
			if (queuePosition == null)
				queuePosition = vertex.getProperty("queuePosition");
			return queuePosition;
		}

		@Override
		public QueryStatus getStatus() {
			if (status == null)
				status = QueryStatus.IDLE;
			return status;
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
	private OrientDBAtomicRequestDAO atomicRequestDAO;
	private AtomicRequestService atomReqServ;

	protected OrientDBQueryDAO(OrientDBGraphService dbs,
			boolean forceCreateNewInstance, AtomicRequestService atomReqServ,
			LiteratureService litServ, AuthorService authServ) {
		super(dbs, "query");
		this.atomicRequestDAO = OrientDBAtomicRequestDAO.getInstance(dbs,
				forceCreateNewInstance, litServ, authServ);
		this.atomReqServ = atomReqServ;
	}

	public static OrientDBQueryDAO getInstance(OrientDBGraphService dbs,
			boolean forceCreateNewInstance, AtomicRequestService atomReqServ,
			LiteratureService litServ, AuthorService authServ) {
		if (instance == null || forceCreateNewInstance)
			instance = new OrientDBQueryDAO(dbs, forceCreateNewInstance,
					atomReqServ, litServ, authServ);
		return instance;
	}

	@Override
	protected Vertex entityToVertex(Query entity, long ID, Vertex givenVertex)
			throws DatalayerException {

		Vertex v = super.entityToVertex(entity, ID, givenVertex);

		GraphUtils.setProperty(v, "name", entity.getName(), false);
		GraphUtils
				.setCollectionPropertyOnVertex(v, "atomicRequests",
						entity.getAtomicRequests(), atomicRequestDAO,
						atomReqServ, true);
		GraphUtils.setProperty(v, "queuePosition", entity.getQueuePosition(),
				true);

		return v;

	}

	@Override
	public Query vertexToEntity(Vertex vertex) throws DatalayerException {
		return new LazyQuery(vertex, atomicRequestDAO);
	}

	@Override
	public Query getByQueuePosition(int position) throws DatalayerException {
		Iterator<Vertex> iter = db.getVertices(dbClassName + ".queuePosition",
				position).iterator();
		if (!iter.hasNext())
			return null;
		return vertexToEntity(iter.next());
	}

}
