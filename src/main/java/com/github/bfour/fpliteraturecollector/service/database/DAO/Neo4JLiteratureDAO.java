package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.Literature;

public class Neo4JLiteratureDAO extends AbstractNeo4JDAO<Literature> implements
		LiteratureDAO {

	@Autowired
	static Neo4JLiteratureDAODelegate delegate;

	private interface Neo4JLiteratureDAODelegate extends
			GraphRepository<Literature> {
		
	}

	public Neo4JLiteratureDAO() {
		super(delegate);
	}

	@Override
	public boolean hasMaxOneAdjacentAtomicRequest(Literature lit) {
		// TODO (high) implement
		return false;
	}

}
