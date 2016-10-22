/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.bfour.fpliteraturecollector.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.github.bfour.fpjcommons.services.ServiceException;
import com.github.bfour.fpjcommons.services.CRUD.BidirectionalCRUDService;
import com.github.bfour.fpjcommons.services.CRUD.CRUDDAO;
import com.github.bfour.fpjcommons.services.CRUD.EventCreatingCRUDService;
import com.github.bfour.fpjcommons.utils.Getter;

public class DependencyAwareEventCreatingCRUDService<E> extends
		EventCreatingCRUDService<E> {

	public static class DependencyHandler<ENTITY_TYPE, DEPENDENT_TYPE> {

		private BidirectionalCRUDService<DEPENDENT_TYPE> service;
		private Getter<ENTITY_TYPE, Collection<DEPENDENT_TYPE>> getter;

		public DependencyHandler(
				BidirectionalCRUDService<DEPENDENT_TYPE> service,
				Getter<ENTITY_TYPE, Collection<DEPENDENT_TYPE>> getter) {
			this.service = service;
			this.getter = getter;
		}

		public void handleCreate(ENTITY_TYPE createdEntity) {
			// TODO
		}

		public void handleDelete(ENTITY_TYPE deletedEntity) {
			// TODO
		}

		public void handleUpdate(ENTITY_TYPE oldE, ENTITY_TYPE newE) {

			Collection<DEPENDENT_TYPE> oldCollection = getter.get(oldE);
			Collection<DEPENDENT_TYPE> newCollection = getter.get(newE);

			// notify created (in new entity and not in old)
			@SuppressWarnings("unchecked")
			Collection<DEPENDENT_TYPE> created = CollectionUtils.subtract(
					newCollection, oldCollection);
			for (DEPENDENT_TYPE DEPENDENT_TYPE : created)
				service.receiveCreate(DEPENDENT_TYPE);

			// notify deleted (in old entity but not in new)
			@SuppressWarnings("unchecked")
			Collection<DEPENDENT_TYPE> deleted = CollectionUtils.subtract(
					oldCollection, newCollection);
			for (DEPENDENT_TYPE DEPENDENT_TYPE : deleted)
				service.receiveDelete(DEPENDENT_TYPE);

			// notify updated
			@SuppressWarnings("unchecked")
			Collection<DEPENDENT_TYPE> shared = CollectionUtils.intersection(
					oldCollection, newCollection);
			for (DEPENDENT_TYPE DEPENDENT_TYPE : shared) {
				ArrayList<DEPENDENT_TYPE> oldList = new ArrayList<DEPENDENT_TYPE>(
						oldCollection);
				ArrayList<DEPENDENT_TYPE> newList = new ArrayList<DEPENDENT_TYPE>(
						newCollection);
				DEPENDENT_TYPE oldEntity = oldList.get(oldList
						.indexOf(DEPENDENT_TYPE));
				DEPENDENT_TYPE newEntity = newList.get(newList
						.indexOf(DEPENDENT_TYPE));
				if (!EqualsBuilder.reflectionEquals(oldEntity, newEntity)) // TODO
																			// (low)
																			// maybe
																			// change
																			// for
																			// different
																			// approach
																			// "This will fail under a security manager, unless the appropriate permissions are set up correctly. It is also slower than testing explicitly."
					service.receiveUpdate(oldEntity, newEntity);

			}

		}

	}

	private Set<DependencyHandler<E, ?>> dependencyHandlers;

	public DependencyAwareEventCreatingCRUDService(CRUDDAO<E> DAO,
			Set<DependencyHandler<E, ?>> dependencyMappings) {
		super(DAO);
		this.dependencyHandlers = dependencyMappings;
	}

	public DependencyAwareEventCreatingCRUDService(CRUDDAO<E> DAO,
			DependencyHandler<E, ?> dependencyMapping) {
		super(DAO);
		this.dependencyHandlers = new HashSet<DependencyHandler<E, ?>>(1);
		this.dependencyHandlers.add(dependencyMapping);
	}

	@Override
	public E create(E entity) throws ServiceException {
		E createdEntity = super.create(entity);
		for (DependencyHandler<E, ?> handler : dependencyHandlers)
			handler.handleCreate(createdEntity);
		return createdEntity;
	}

	@Override
	public void delete(E entity) throws ServiceException {
		super.delete(entity);
		for (DependencyHandler<E, ?> handler : dependencyHandlers)
			handler.handleDelete(entity);
	}

	@Override
	public E update(E oldEntity, E newEntity) throws ServiceException {
		E updatedEntity = super.update(oldEntity, newEntity);
		for (DependencyHandler<E, ?> handler : dependencyHandlers)
			handler.handleUpdate(oldEntity, updatedEntity);
		return updatedEntity;
	}
}
