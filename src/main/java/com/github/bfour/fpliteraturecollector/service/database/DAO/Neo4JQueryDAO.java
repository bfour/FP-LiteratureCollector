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


import javax.transaction.TransactionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.github.bfour.fpjcommons.services.DatalayerException;
import com.github.bfour.fpjpersist.neo4j.service.AbstractNeo4JDAO;
import com.github.bfour.fpliteraturecollector.domain.Query;

@Service
@Configurable
public class Neo4JQueryDAO extends AbstractNeo4JDAO<Query> implements QueryDAO {

	@Autowired
	private Neo4JQueryDAODelegate delegate;
	
	@Autowired
	private Neo4jTemplate neoTemplate;

	public Neo4JQueryDAO() {
	}

	@Override
	public Query getByQueuePosition(int position) throws DatalayerException {
		return delegate.findByQueuePosition(position);
	}

	@Override
	protected GraphRepository<Query> getDelegate() {
		return delegate;
	}
	
	@Override
	protected TransactionManager getTxManager() {
		return neoTemplate.getGraphDatabase().getTransactionManager();
	}

}
