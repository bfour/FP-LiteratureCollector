package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.Author;

public interface Neo4JAuthorDAODelegate extends GraphRepository<Author> {
	Author findByGScholarID(String gScholarID);
	Author findByMsAcademicID(String msAcademicID);
}