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

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

// TODO improve procedure for detecting and handling initialization states
// TODO improve schema checking and repair
public abstract class AbstractOrientDBGraphService {

	private OrientGraph lastDB;
	private String lastURL;
	private String lastUser;
	private String lastPassword;

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

		String URL = "remote:" + host + "/" + dbName;

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

	protected abstract void setupSchema(String URL, String user, String password);

}
