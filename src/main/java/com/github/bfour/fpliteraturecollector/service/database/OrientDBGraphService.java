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

import com.github.bfour.fpjcommons.services.ServiceException;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

// TODO improve procedure for detecting and handling initialization states
// TODO improve schema checking and repair
public abstract class OrientDBGraphService {

	private OrientGraph currentDB;
	private String currentURL;
	private String currentUser;
	private String currentPassword;

	public void shutdown() {
		if (currentDB != null) {
			if (!currentDB.isClosed())
				currentDB.shutdown();
			currentDB = null;
		}
	}

	public OrientGraph getCurrentDB() {
		return new OrientGraph(currentURL, currentUser, currentPassword);
	}

	private void setCurrentDB(OrientGraph db, String URL, String user,
			String password) {
		this.currentDB = db;
		this.currentURL = URL;
		this.currentUser = user;
		this.currentPassword = password;
	}

	/**
	 * 
	 * @param location
	 *            URL of the location in the file system, not starting with a
	 *            prefix (eg. plocal)
	 * @param user
	 * @param password
	 * @return
	 * @throws ServiceException
	 */
	private void initializeLocalDatabase(String location)
			throws ServiceException {

		// OGlobalConfiguration.DB_MVCC.setValue(false);
		OGlobalConfiguration.CACHE_LEVEL1_ENABLED.setValue(false);
		// OGlobalConfiguration.CACHE_LEVEL2_ENABLED.setValue(false);

		if (currentDB != null && !currentDB.isClosed())
			currentDB.shutdown();

		String URL = "plocal:" + location;
		String user = "admin"; // this is a local database, so we don't use user
								// and password
		String password = "admin";

		// check db already exists
		if (databaseExists(URL, user, password))
			throw new ServiceException(
					"failed to initialize local database at " + location
							+ ": database already exists");

		setupSchema(URL, user, password);

	}

	private void initializeRemoteDatabase(String host, String dbName,
			String user, String password) throws ServiceException {

		// OGlobalConfiguration.DB_MVCC.setValue(false);
		OGlobalConfiguration.CACHE_LEVEL1_ENABLED.setValue(false);
		// OGlobalConfiguration.CACHE_LEVEL2_ENABLED.setValue(false);

		if (currentDB != null && !currentDB.isClosed())
			currentDB.shutdown();

		String URL = "remote:" + host + "/" + dbName;

		// check db already exists
		if (databaseExists(URL, user, password))
			throw new ServiceException(
					"failed to initialize remote database at " + host + "/"
							+ dbName + ": database already exists");

		setupSchema(URL, user, password);

	}

	private void setDatabase(String URL, String user, String password) {
		if (currentDB != null)
			currentDB.shutdown();
		OrientGraph db = new OrientGraph(URL, user, password);
		setCurrentDB(db, URL, user, password);
	}

	public void setRemoteDatabase(String host, String dbName, String user,
			String password) throws ServiceException {
		String URL = "remote:" + host + "/" + dbName;
		if (!databaseExists(URL, user, password))
			initializeRemoteDatabase(host, dbName, user, password);
		setDatabase(URL, user, password);
	}

	public void setLocalDatabase(String location) throws ServiceException {
		String URL = "plocal:" + location;
		String user = "admin";
		String password = "admin";
		if (!databaseExists(URL, user, password))
			initializeLocalDatabase(location);
		setDatabase(URL, user, password);
	}

	public boolean databaseExists(String URL, String user, String password) {
		// TODO (low) improve
		OrientGraph db = new OrientGraph(URL, user, password);
		long verticeCount = db.countVertices();
		db.shutdown();
		return verticeCount > 0;
	}

	protected abstract void setupSchema(String URL, String user, String password);

	public void deleteAllDataInCurrentDB() throws ServiceException {
		OrientGraph db = getCurrentDB();
		if (db == null)
			throw new ServiceException(
					"failed to delete all data: no database set as current database");
		for (Vertex v : db.getVertices())
			db.removeVertex(v);
	}

	public void dropCurrentDB() throws ServiceException {
		getCurrentDB().drop();
		setCurrentDB(null, null, null, null);
	}

}
