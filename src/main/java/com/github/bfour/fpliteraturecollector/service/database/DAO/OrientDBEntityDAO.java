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

import java.util.Date;

import com.github.bfour.fpjcommons.model.Entity;
import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.tinkerpop.blueprints.Vertex;

public abstract class OrientDBEntityDAO<T extends Entity> extends
		AbstractOrientDBDAO<T> {

	protected OrientDBEntityDAO(OrientDBGraphService dbs, String dbClassName) {
		super(dbs, dbClassName);
	}

	public Entity vertexToRawEntity(Vertex vertex) throws DatalayerException {
		long ID = vertex.getProperty("ID");
		Date creationTime = vertex.getProperty("creationTime");
		Date lastChangeTime = vertex.getProperty("lastChangeTime");
		return new Entity(ID, creationTime, lastChangeTime);
	}

	@Override
	public abstract T vertexToEntity(Vertex vertex) throws DatalayerException;

	@Override
	protected Vertex entityToVertex(T entity, long ID, Vertex v)
			throws DatalayerException {

		if (v == null)
			v = db.addVertex("class:" + this.dbClassName);

		GraphUtils.setProperty(v, "ID", ID, false);
		GraphUtils.setProperty(
				v,
				"creationTime",
				(entity.getCreationTime() == null ? new Date() : entity
						.getCreationTime()), false);
		GraphUtils.setProperty(
				v,
				"lastChangeTime",
				(entity.getLastChangeTime() == null ? new Date() : entity
						.getLastChangeTime()), false);

		return v;

	}
}
