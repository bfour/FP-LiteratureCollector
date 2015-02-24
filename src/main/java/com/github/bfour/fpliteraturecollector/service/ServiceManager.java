package com.github.bfour.fpliteraturecollector.service;

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

/*
 * =================================
 * FP-LiteratureCollector
 * =================================
 * Copyright (C) 2014 - 2015 Florian Pollak
 * =================================
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * *
 */

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpliteraturecollector.service.database.FPLCOrientDBGraphService;
import com.github.bfour.fpliteraturecollector.service.database.OrientDBGraphService;

/**
 * TODO add comments
 */
public class ServiceManager {

	public static enum ServiceManagerMode {
		DEFAULT, TEST;
	}

	private static ServiceManager instance;

	private ServiceManagerMode modeMemory;
	private OrientDBGraphService graphService;
	private PersonService personServ;

	private ServiceManager(ServiceManagerMode mode) throws ServiceException {
		modeMemory = mode;
		if (mode == ServiceManagerMode.DEFAULT) {
			graphService = FPLCOrientDBGraphService.getInstance();
			graphService.setLocalDatabase("devDatabase");
			this.personServ = DefaultPersonService.getInstance(graphService);
		} else if (mode == ServiceManagerMode.TEST) {
			graphService = FPLCOrientDBGraphService.getInstance();
			graphService.setLocalDatabase("junitTestDatabase");
			this.personServ = DefaultPersonService.getInstance(graphService);
		} else {
			throw new ServiceException("invalid service manager mode: " + mode);
		}
	}

	public static ServiceManager getInstance(ServiceManagerMode mode)
			throws ServiceException {
		if (instance == null)
			instance = new ServiceManager(mode);
		return instance;
	}

	/**
	 * Deletes all user data and re-initializes.
	 */
	public void resetAllData() throws ServiceException {
		graphService.getLastDB().drop();
		graphService.shutdown();
		instance = new ServiceManager(modeMemory);
	}

	public PersonService getPersonService() {
		return personServ;
	}

}
