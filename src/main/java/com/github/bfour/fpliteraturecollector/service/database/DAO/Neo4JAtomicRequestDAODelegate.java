package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;

public interface Neo4JAtomicRequestDAODelegate extends
		GraphRepository<AtomicRequest> {
}