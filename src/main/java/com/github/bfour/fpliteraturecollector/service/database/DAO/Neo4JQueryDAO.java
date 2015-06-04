package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Controller;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpliteraturecollector.domain.Query;

@Controller
public class Neo4JQueryDAO extends AbstractNeo4JDAO<Query> implements QueryDAO {

	@Autowired
	static Neo4JQueryDAODelegate delegate;

	private interface Neo4JQueryDAODelegate extends GraphRepository<Query> {
		Query findByQueuePosition(Integer queuePosition);
	}

	public Neo4JQueryDAO() {
		super(delegate);
	}

	@Override
	public Query getByQueuePosition(int position) throws DatalayerException {
		return delegate.findByQueuePosition(position);
	}

}
