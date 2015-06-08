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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpjcommons.services.CRUD.DataIteratorWrapper;
import com.github.bfour.fpliteraturecollector.domain.Query;

@Service
@Configurable
public class Neo4JQueryDAO implements QueryDAO {

	@Autowired
	private Neo4JQueryDAODelegate delegate;

	public Neo4JQueryDAO() {
	}

	public Neo4JQueryDAODelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(Neo4JQueryDAODelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public Query getByQueuePosition(int position) throws DatalayerException {
		return delegate.findByQueuePosition(position);
	}
	
	@Override
	public List<Query> getAll() throws DatalayerException {
		Iterable<Query> iter = delegate.findAll();
		List<Query> tags = new ArrayList<Query>();
		for (Query tag : iter)
			tags.add(tag);
		return tags;
	}

	@Override
	public DataIterator<Query> getAllByStream() throws DatalayerException {
		return new DataIteratorWrapper<Query>(delegate.findAll().iterator());
	}

	@Override
	public Query get(Query obj) throws DatalayerException {
		if (obj.getID() == null)
			return null;
		return delegate.findOne(obj.getID());
	}

	@Override
	public void delete(Query obj) throws DatalayerException {
		delegate.delete(obj.getID());
	}

	@Override
	public Query create(Query obj) throws DatalayerException {
		return delegate.save(obj);
	}

	@Override
	public Query update(Query oldObj, Query newObj) throws DatalayerException {
		return delegate.save(newObj);
	}

	@Override
	public long getNewID() throws DatalayerException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exists(Query obj) throws DatalayerException {
		if (obj.getID() == null)
			return false;
		return (delegate.findOne(obj.getID()) != null);
	}

}
