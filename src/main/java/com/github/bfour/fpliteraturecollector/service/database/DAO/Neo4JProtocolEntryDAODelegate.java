package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;

public interface Neo4JProtocolEntryDAODelegate extends GraphRepository<ProtocolEntry> {
}