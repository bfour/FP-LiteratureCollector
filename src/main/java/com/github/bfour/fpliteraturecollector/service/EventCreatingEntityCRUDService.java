package com.github.bfour.fpliteraturecollector.service;

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


import java.util.Date;

import com.github.bfour.fpjcommons.model.Entity;
import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.CRUDDAO;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingCRUDService;

public class EventCreatingEntityCRUDService<E extends Entity> extends
		EventCreatingCRUDService<E> {

	public EventCreatingEntityCRUDService(CRUDDAO<E> DAO) {
		super(DAO);
	}

	@Override
	public E create(E entity) throws ServiceException {
		setDefaultsIfRequired(entity);
		checkIntegrity(entity);
		return super.create(entity);
	}

	@Override
	public E update(E oldEntity, E newEntity) throws ServiceException {
		setDefaultsIfRequired(newEntity);
		checkIntegrity(newEntity);
		return super.update(oldEntity, newEntity);
	}

	private void setDefaultsIfRequired(E entity) {
		if (entity.getCreationTime() == null)
			entity.setCreationTime(new Date());
		if (entity.getLastChangeTime() == null) 
			entity.setLastChangeTime(new Date());
	}
	
	private void checkIntegrity(E entity) throws ServiceException {
		if (entity.getCreationTime() == null)
			throw new ServiceException("creation time must be specified");
		if (entity.getLastChangeTime() == null) 
			throw new ServiceException("last change time must be specified");
		if (entity.getCreationTime().after(entity.getLastChangeTime()))
			throw new ServiceException("creation time must not be after last change time");
	}

}
