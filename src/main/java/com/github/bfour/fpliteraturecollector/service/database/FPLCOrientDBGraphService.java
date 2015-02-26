package com.github.bfour.fpliteraturecollector.service.database;

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

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class FPLCOrientDBGraphService extends OrientDBGraphService {

	private static FPLCOrientDBGraphService instance;

	private FPLCOrientDBGraphService() {
	}

	public static FPLCOrientDBGraphService getInstance() {
		if (instance == null)
			instance = new FPLCOrientDBGraphService();
		return instance;
	}

	@Override
	protected void setupSchema(String URL, String user, String password) {

		// TODO finish

		// open as non-transactional
		OrientGraphNoTx db = new OrientGraphNoTx(URL, user, password);

		// setup counters as workaround for sequence
		OrientVertexType counterClass = db.createVertexType("counters");
		counterClass.createProperty("name", OType.STRING);
		counterClass.createProperty("value", OType.LONG);

		db.createKeyIndex("name", Vertex.class, new Parameter<>("type",
				"UNIQUE"), new Parameter<>("class", "counters"));

		// setup persons
		OrientVertexType personClass = db.createVertexType("person");
		personClass.createProperty("ID", OType.LONG);
		personClass.createProperty("registrationTime", OType.DATETIME);
		personClass.createProperty("lastChangeTime", OType.DATETIME);	
		personClass.createProperty("firstName", OType.STRING);
		personClass.createProperty("lastName", OType.STRING);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"person"));
		db.addVertex("class:counters", "name", "person", "value", 0);
		
		// setup tags
		OrientVertexType tagClass = db.createVertexType("tag");
		tagClass.createProperty("ID", OType.LONG);
		tagClass.createProperty("registrationTime", OType.DATETIME);
		tagClass.createProperty("lastChangeTime", OType.DATETIME);		
		tagClass.createProperty("name", OType.STRING);
		tagClass.createProperty("colour", OType.BINARY); // Color
		
		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"tag"));
		db.addVertex("class:counters", "name", "tag", "value", 0);
		
		// setup literature
		OrientVertexType literatureClass = db.createVertexType("literature");
		literatureClass.createProperty("ID", OType.LONG);
		literatureClass.createProperty("registrationTime", OType.DATETIME);
		literatureClass.createProperty("lastChangeTime", OType.DATETIME);
		literatureClass.createProperty("title", OType.STRING);
		literatureClass.createProperty("authors", OType.LINKLIST);
		literatureClass.createProperty("DOI", OType.STRING);
		literatureClass.createProperty("ISBN", OType.STRING);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"literature"));
		db.addVertex("class:counters", "name", "literature", "value", 0);
		
		// setup atomic request
		OrientVertexType atomicRequestClass = db.createVertexType("atomicRequest");
		atomicRequestClass.createProperty("ID", OType.LONG);
		atomicRequestClass.createProperty("registrationTime", OType.DATETIME);
		atomicRequestClass.createProperty("lastChangeTime", OType.DATETIME);
		atomicRequestClass.createProperty("searchEngine", OType.STRING);
		atomicRequestClass.createProperty("searchString", OType.STRING);
		atomicRequestClass.createProperty("results", OType.LINKLIST);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"atomicRequest"));
		db.addVertex("class:counters", "name", "atomicRequest", "value", 0);
		
		// setup query
		OrientVertexType queryClass = db.createVertexType("query");
		queryClass.createProperty("ID", OType.LONG);
		queryClass.createProperty("registrationTime", OType.DATETIME);
		queryClass.createProperty("lastChangeTime", OType.DATETIME);
		queryClass.createProperty("atomicRequests", OType.LINKLIST);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"query"));
		db.addVertex("class:counters", "name", "query", "value", 0);				

		// end
		db.shutdown();

	}

}
