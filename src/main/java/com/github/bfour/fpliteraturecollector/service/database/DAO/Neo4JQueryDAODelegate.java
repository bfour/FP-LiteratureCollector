package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.Query;

public interface Neo4JQueryDAODelegate extends GraphRepository<Query> {
	Query findByQueuePosition(Integer queuePosition);
}