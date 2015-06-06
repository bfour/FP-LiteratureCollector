package com.github.bfour.fpliteraturecollector.application;

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


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
// @Import(RepositoryRestMvcConfiguration.class)
@EnableScheduling
// @EnableAutoConfiguration
@ComponentScan(basePackages = { "com.github.bfour.fpliteraturecollector.service" })
@Configuration
@EnableNeo4jRepositories(basePackages = "com.github.bfour.fpliteraturecollector.service")
public class MyNeo4jConfiguration extends Neo4jConfiguration {
	public MyNeo4jConfiguration() {
		setBasePackage("com.github.bfour.fpliteraturecollector.domain");
	}

	@Bean
	public GraphDatabaseService graphDatabaseService() {
		return new GraphDatabaseFactory().newEmbeddedDatabase("testDB");
	}

//	@Override
//	public TypeRepresentationStrategy<Relationship> relationshipTypeRepresentationStrategy()
//			throws Exception {
//		return new NoopRelationshipTypeRepresentationStrategy();
//	}
	
}