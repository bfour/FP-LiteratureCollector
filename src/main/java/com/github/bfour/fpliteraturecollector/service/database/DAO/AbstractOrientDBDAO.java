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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.bfour.fpjcommons.model.Entity;
import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.CRUD.CRUDDAO;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public abstract class AbstractOrientDBDAO<T extends Entity> implements
		CRUDDAO<T> {

	protected OrientDBGraphService dbs;
	protected OrientGraph db;
	String dbClassName;

	protected AbstractOrientDBDAO(OrientDBGraphService dbs, String dbClassName) {
		this.dbs = dbs;
		this.db = dbs.getCurrentDB();
		this.dbClassName = dbClassName;
	}

	@Override
	public List<T> getAll() throws DatalayerException {
		long count = db.countVertices(dbClassName);
		if (count > Integer.MAX_VALUE)
			count = Integer.MAX_VALUE;
		List<T> list = new ArrayList<>((int) count);
		for (Vertex person : db.getVerticesOfClass(dbClassName)) {
			list.add(vertexToEntity(person));
		}
		return list;
	}

	@Override
	public DataIterator<T> get() {

		class VertexToEntityConvertingIterator implements DataIterator<T> {

			private Iterator<Vertex> vertexIterator;

			public VertexToEntityConvertingIterator(
					Iterator<Vertex> vertexIterator) {
				this.vertexIterator = vertexIterator;
			}

			@Override
			public boolean hasNext() {
				return vertexIterator.hasNext();
			}

			@Override
			public T next() throws DatalayerException {
				return vertexToEntity(vertexIterator.next());
			}

			@Override
			public void remove() {
				vertexIterator.remove();
			}

		}

		return new VertexToEntityConvertingIterator(db.getVerticesOfClass(
				dbClassName).iterator());

	}

	@Override
	public void delete(T entity) {
		Vertex v = getVertexForEntity(entity);
		if (v != null) {
			db.removeVertex(v);
			db.commit();
		}
	}

	@Override
	public T create(T entity) throws DatalayerException {
		Long ID = entity.getID();
		if (ID == null)
			ID = getNewEntityID();
		Vertex newEntity = entityToVertex(entity, ID);
		db.commit();
		return vertexToEntity(newEntity);
	}

	@Override
	public T update(T oldEntity, T newEntity) throws DatalayerException {
		Vertex v = getVertexForEntity(oldEntity);
		if (v == null)
			throw new DatalayerException(
					"failed to update old entity: old entity no longer exists");
		entityToVertex(newEntity, oldEntity.getID(), v);
		db.commit();
		return vertexToEntity(v);
	}

	protected Vertex getVertexForEntity(T entity) {
		if (entity == null)
			return null;
		Iterator<Vertex> iter = db.getVertices(dbClassName + ".ID",
				entity.getID()).iterator();
		if (!iter.hasNext())
			return null;
		Vertex v = iter.next();
		return v;
	}

	/**
	 * Generates a new, unique ID on the database.
	 * 
	 * @return the new ID
	 * @throws DatalayerException
	 */
	protected long getNewEntityID() throws DatalayerException {

		// increment by 1
		OCommandSQL incrCommand = new OCommandSQL(
				"UPDATE counters INCREMENT value = 1 WHERE name='"
						+ dbClassName + "'");
		db.command(incrCommand).execute();

		// get current value
		Iterator<Vertex> result = db.getVertices("counters.name", dbClassName)
				.iterator();
		if (!result.hasNext())
			throw new DatalayerException(
					"invalid size of result set for sequence-workaround query");
		long ID = result.next().getProperty("value");
		return ID;

	}

	@Override
	public long getNewID() throws DatalayerException {
		return getNewEntityID();
	}

	public abstract T vertexToEntity(Vertex vertex) throws DatalayerException;

	private Vertex entityToVertex(T entity, long ID) throws DatalayerException {
		return entityToVertex(entity, ID, null);
	}

	protected abstract Vertex entityToVertex(T entity, long ID,
			Vertex givenVertex) throws DatalayerException;

	public synchronized OrientDBGraphService getDBService() {
		return dbs;
	}

}
