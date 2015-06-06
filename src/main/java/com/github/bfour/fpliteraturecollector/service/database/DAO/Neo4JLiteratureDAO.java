package com.github.bfour.fpliteraturecollector.service.database.DAO;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2015 Florian Pollak
 * =================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -///////////////////////////////-
 */


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
