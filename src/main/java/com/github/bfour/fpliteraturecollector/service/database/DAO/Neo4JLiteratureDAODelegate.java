package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.Literature;

public interface Neo4JLiteratureDAODelegate extends
		GraphRepository<Literature> {
}