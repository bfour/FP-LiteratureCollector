package com.github.bfour.fpliteraturecollector.service.database;

/*
 * -\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
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




import java.io.IOException;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class OrientDBGraphAPIService {

	private static OrientDBGraphAPIService instance;
	private OrientGraph lastDB;
	private String lastURL;
	private String lastUser;
	private String lastPassword;

	private OrientDBGraphAPIService() {
	}

	public static OrientDBGraphAPIService getInstance() {
		if (instance == null)
			instance = new OrientDBGraphAPIService();
		return instance;
	}

	public void shutdown() {
		if (lastDB != null)
			lastDB.shutdown();
	}

	public OrientGraph getLastDB() {
		return new OrientGraph(lastURL, lastUser, lastPassword);
	}

	private void setLastDB(OrientGraph db, String URL, String user,
			String password) {
		this.lastDB = db;
		this.lastURL = URL;
		this.lastUser = user;
		this.lastPassword = password;
	}

	/**
	 * 
	 * @param location
	 *            URL of the location in the file system, not starting with a
	 *            prefix (eg. plocal)
	 * @param user
	 * @param password
	 * @return
	 */
	public void initializeAndSetLocalDatabase(String location) {

		if (lastDB != null)
			lastDB.shutdown();

		String URL = "plocal:" + location;
		String user = "admin"; // this is a local database, so we don't use user
								// and password
		String password = "admin";

		setupSchema(URL, user, password);
		OrientGraph db = new OrientGraph(URL, user, password);

		setLastDB(db, URL, user, password);

	}

	public void initializeAndSetRemoteDatabase(String host, String dbName,
			String user, String password) throws IOException {

		if (lastDB != null)
			lastDB.shutdown();

		String URL = "remote:"+host+"/"+dbName;
		
		setupSchema(URL, user, password);
		OrientGraph db = new OrientGraph(URL, user, password);

		setLastDB(db, URL, user, password);

	}

	public void setDatabase(String URL, String user, String password) {
		if (lastDB != null)
			lastDB.shutdown();
		OrientGraph db = new OrientGraph(URL, user, password);
		setLastDB(db, URL, user, password);
	}

	private void setupSchema(String URL, String user, String password) {

		// TODO finish

		// open as non-transactional
		OrientGraphNoTx db = new OrientGraphNoTx(URL, user, password);
		
		// setup counters as workaround for sequence
		OrientVertexType counterClass = db.createVertexType("counters");
		counterClass.createProperty("name", OType.STRING);
		counterClass.createProperty("value", OType.LONG);

		db.createKeyIndex("name", Vertex.class, new Parameter<>("type",
				"UNIQUE"), new Parameter<>("class", "counters"));		
		
		// setup literature
		OrientVertexType literatureClass = db.createVertexType("literature");
		literatureClass.createProperty("ID", OType.LONG);
		literatureClass.createProperty("title", OType.STRING);
		literatureClass.createProperty("persons", OType.LINKLIST);
		literatureClass.createProperty("registrationTime", OType.DATETIME);
		literatureClass.createProperty("lastChangeTime", OType.DATETIME);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"literature"));
		db.addVertex("class:counters", "name", "literature", "value", 0);

		// setup persons
		OrientVertexType personClass = db.createVertexType("person");
		personClass.createProperty("ID", OType.LONG);
		personClass.createProperty("firstName", OType.STRING);
		personClass.createProperty("lastName", OType.STRING);

		db.createKeyIndex("ID", Vertex.class,
				new Parameter<>("type", "UNIQUE"), new Parameter<>("class",
						"person"));		
		db.addVertex("class:counters", "name", "person", "value", 0);		
		
		// end
		db.shutdown();

	}

}
