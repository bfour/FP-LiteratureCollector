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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjcommons.services.CRUD.CRUDDAO;
import com.github.bfour.fpjcommons.services.CRUD.DataIterator;
import com.github.bfour.fpjcommons.services.CRUD.DataIteratorWrapper;
import com.github.bfour.fpliteraturecollector.domain.Entity;

public abstract class AbstractNeo4JDAO<T extends Entity> implements CRUDDAO<T> {

	public AbstractNeo4JDAO() {
	}

	protected abstract GraphRepository<T> getDelegate();

	protected abstract TransactionManager getTxManager();

	@Override
	public List<T> getAll() throws DatalayerException {
		// return getDelegate().findAll().as(List.class);
		List<T> tags = new ArrayList<T>();
		try {
			getTxManager().begin();
			Iterable<T> iter = getDelegate().findAll();
			for (T tag : iter)
				tags.add(tag);
			getTxManager().commit();
		} catch (IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException | SystemException | NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new DatalayerException(e);
		}
		return tags;
	}

	@Override
	public DataIterator<T> getAllByStream() throws DatalayerException {
		// return new
		// DataIteratorWrapper<T>(getDelegate().findAll().iterator());
		return new DataIteratorWrapper<T>(getAll().iterator()); // TODO (low) improve (issues with transactions)
	}

	@Override
	public T get(T obj) throws DatalayerException {
		if (obj.getID() == null)
			return null;
		return getDelegate().findOne(obj.getID());
	}

	@Override
	public void delete(T obj) throws DatalayerException {
		try {
			getTxManager().begin();
			getDelegate().delete(obj.getID());
			getTxManager().commit();
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				getTxManager().rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new DatalayerException(e);
			}
			throw new DatalayerException(e);
		}
	}

	@Override
	public T create(T obj) throws DatalayerException {
		try {
			getTxManager().begin();
			T newObj = getDelegate().save(obj);
			getTxManager().commit();
			return newObj;
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				getTxManager().rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new DatalayerException(e);
			}
			throw new DatalayerException(e);
		}
	}

	@Override
	public T update(T oldObj, T newObj) throws DatalayerException {
		try {
			getTxManager().begin();
			T updatedObj = getDelegate().save(newObj);
			getTxManager().commit();
			return updatedObj;
		} catch (NotSupportedException | SystemException
				| IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				getTxManager().rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				throw new DatalayerException(e);
			}
			throw new DatalayerException(e);
		}
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
		return (getDelegate().findOne(obj.getID()) != null);
	}

}
