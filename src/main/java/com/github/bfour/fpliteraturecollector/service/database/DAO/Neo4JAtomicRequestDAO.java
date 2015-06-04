package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;

public class Neo4JAtomicRequestDAO extends AbstractNeo4JDAO<AtomicRequest>
		implements AtomicRequestDAO {

	@Autowired
	static Neo4JAtomicRequestDAODelegate delegate;

	private interface Neo4JAtomicRequestDAODelegate extends
			GraphRepository<AtomicRequest> {
	}

	public Neo4JAtomicRequestDAO() {
		super(delegate);
	}

}
