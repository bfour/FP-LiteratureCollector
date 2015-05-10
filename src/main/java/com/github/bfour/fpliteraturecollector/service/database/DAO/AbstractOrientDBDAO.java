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
		for (Vertex vertex : db.getVerticesOfClass(dbClassName)) {
			list.add(vertexToEntity(vertex));
		}
		return list;
	}

	@Override
	public DataIterator<T> getAllByStream() {

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
				Vertex v = vertexIterator.next();
				if (v == null)
					return null;
				return vertexToEntity(v);
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
	public T get(T entity) throws DatalayerException {
		Vertex v = getVertexForEntity(entity);
		if (v == null)
			return null;
		return vertexToEntity(v);
	}

	@Override
	public void delete(T entity) {
		delete(entity, true);
	}

	/**
	 * Delete the given entity.
	 * 
	 * @param entity
	 *            the entity to be deleted
	 * @param commit
	 *            whether to commit the transaction
	 */
	public void delete(T entity, boolean commit) {
		Vertex v = getVertexForEntity(entity);
		if (v != null) {
			db.removeVertex(v);
			if (commit)
				db.commit();
		}
	}

	@Override
	public T create(T entity) throws DatalayerException {
		return create(entity, true);
	}

	public T create(T entity, boolean commit) throws DatalayerException {
		Long ID = entity.getID();
		if (ID == null)
			ID = getNewEntityID();
		Vertex newEntity = entityToVertex(entity, ID);
		if (commit)
			db.commit();
		if (newEntity == null)
			return null;
		return vertexToEntity(newEntity);
	}

	@Override
	public T update(T oldEntity, T newEntity) throws DatalayerException {
		return update(oldEntity, newEntity, true);
	}

	public T update(T oldEntity, T newEntity, boolean commit)
			throws DatalayerException {
		Vertex v = getVertexForEntity(oldEntity);
		if (v == null)
			throw new DatalayerException(
					"failed to update old entity: old entity no longer exists");
		entityToVertex(newEntity, oldEntity.getID(), v);
		if (commit)
			db.commit();
		return vertexToEntity(v);
	}

	protected Vertex getVertexForEntity(T entity) {
		if (entity == null || entity.getID() == null)
			return null;

//		for (Vertex v : db.getVerticesOfClass(dbClassName)) {
//			if (v.getProperty("ID").equals(entity.getID()))
//				return v;
//		}
//
//		return null;

		 Iterator<Vertex> iter = db.getVertices(dbClassName + ".ID",
		 entity.getID()).iterator();
		 // Vertex v = iter.next();
		 if (!iter.hasNext())
		 return null;
		 return iter.next();

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

	@Override
	public boolean exists(T obj) {
		return getVertexForEntity(obj) != null;
	}

	/**
	 * Converts the given vertex to a corresponding entity.
	 * 
	 * @param vertex
	 *            vertex from database; must not be null
	 * @return an entity that corresponds to the given vertex
	 * @throws DatalayerException
	 *             if creation of the entity is not possible (eg. database
	 *             connection lost)
	 */
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
