package com.github.bfour.fpliteraturecollector.service.database.DAO;

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
