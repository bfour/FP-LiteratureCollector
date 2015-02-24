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
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

// TODO improve procedure for detecting and handling initialization states
// TODO improve schema checking and repair
public abstract class OrientDBGraphService {

	private OrientGraph lastDB;
	private String lastURL;
	private String lastUser;
	private String lastPassword;

	public void shutdown() {
		if (lastDB != null) {
			if (!lastDB.isClosed())
				lastDB.shutdown();
			lastDB = null;
		}
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
	 * @throws ServiceException
	 */
	private void initializeLocalDatabase(String location)
			throws ServiceException {

		if (lastDB != null && !lastDB.isClosed())
			lastDB.shutdown();

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

		if (lastDB != null && !lastDB.isClosed())
			lastDB.shutdown();

		String URL = "remote:" + host + "/" + dbName;

		// check db already exists
		if (databaseExists(URL, user, password))
			throw new ServiceException(
					"failed to initialize remote database at " + host + "/"
							+ dbName + ": database already exists");

		setupSchema(URL, user, password);

	}

	private void setDatabase(String URL, String user, String password) {
		if (lastDB != null)
			lastDB.shutdown();
		OrientGraph db = new OrientGraph(URL, user, password);
		setLastDB(db, URL, user, password);
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

}
