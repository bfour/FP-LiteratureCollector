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
import java.util.List;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.CRUD.CRUDDAO;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpjcommons.services.CRUD.DataIteratorWrapper;
import com.github.bfour.fpliteraturecollector.domain.Entity;

public abstract class AbstractNeo4JDAO<T extends Entity> implements CRUDDAO<T> {

	private GraphRepository<T> delegate;

	public AbstractNeo4JDAO(GraphRepository<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public List<T> getAll() throws DatalayerException {
		Iterable<T> iter = delegate.findAll();
		List<T> tags = new ArrayList<T>();
		for (T tag : iter)
			tags.add(tag);
		return tags;
	}

	@Override
	public DataIterator<T> getAllByStream() throws DatalayerException {
		return new DataIteratorWrapper<T>(delegate.findAll().iterator());
	}

	@Override
	public T get(T obj) throws DatalayerException {
		if (obj.getID() == null)
			return null;
		return delegate.findOne(obj.getID());
	}

	@Override
	public void delete(T obj) throws DatalayerException {
		delegate.delete(obj.getID());
	}

	@Override
	public T create(T obj) throws DatalayerException {
		return delegate.save(obj);
	}

	@Override
	public T update(T oldObj, T newObj) throws DatalayerException {
		return delegate.save(newObj);
	}

	@Override
	public long getNewID() throws DatalayerException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exists(T obj) throws DatalayerException {
		if (obj.getID() == null)
			return false;
		return (delegate.findOne(obj.getID()) != null);
	}

}
