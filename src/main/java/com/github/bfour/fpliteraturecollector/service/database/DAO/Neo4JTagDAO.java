package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.Tag;

@Configurable
public class Neo4JTagDAO extends AbstractNeo4JDAO<Tag> implements TagDAO {

	@Autowired
	static Neo4JTagDAODelegate delegate;

	private interface Neo4JTagDAODelegate extends GraphRepository<Tag> {
	}

	public Neo4JTagDAO() {
		super(delegate);
	}

}
