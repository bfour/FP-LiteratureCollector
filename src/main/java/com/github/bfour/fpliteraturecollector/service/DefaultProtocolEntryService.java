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

import com.github.bfour.fpjcommons.services.CRUD.EventCreatingCRUDService;
import com.github.bfour.fpliteraturecollector.domain.ProtocolEntry;
import com.github.bfour.fpliteraturecollector.service.database.DAO.ProtocolEntryDAO;

public class DefaultProtocolEntryService extends
		EventCreatingCRUDService<ProtocolEntry> implements
		ProtocolEntryService {

	private static DefaultProtocolEntryService instance;

	private DefaultProtocolEntryService(ProtocolEntryDAO DAO,
			boolean forceCreateNewInstance) {
		super(DAO);
	}

	public static DefaultProtocolEntryService getInstance(ProtocolEntryDAO DAO,
			boolean forceCreateNewInstance) {
		if (instance == null || forceCreateNewInstance)
			instance = new DefaultProtocolEntryService(DAO,
					forceCreateNewInstance);
		return instance;
	}

}
