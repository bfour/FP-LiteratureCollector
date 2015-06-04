package com.github.bfour.fpliteraturecollector.service.database.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.github.bfour.fpliteraturecollector.domain.Author;

public class Neo4JAuthorDAO extends AbstractNeo4JDAO<Author> implements
		AuthorDAO {

	@Autowired
	static Neo4JAuthorDAODelegate delegate;

	private interface Neo4JAuthorDAODelegate extends GraphRepository<Author> {
		Author findByGScholarID(String gScholarID);
		Author findByMsAcademicID(String msAcademicID);
	}

	public Neo4JAuthorDAO() {
		super(delegate);
	}

	@Override
	public Author getByGScholarID(String gScholarID) {
		return delegate.findByGScholarID(gScholarID);
	}

	@Override
	public Author getByMsAcademicID(String msAcademicID) {
		return delegate.findByMsAcademicID(msAcademicID);
	}

	@Override
	public boolean hasMaxOneAdjacentLiterature(Author author) {
		// TODO Auto-generated method stub
		return false;
	}

}
