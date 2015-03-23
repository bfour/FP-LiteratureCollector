package com.github.bfour.fpliteraturecollector.domain.builders;

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

import java.util.List;

import com.github.bfour.fpjcommons.lang.Builder;
import com.github.bfour.fpjcommons.model.EntityBuilder;
import com.github.bfour.fpliteraturecollector.domain.AtomicRequest;
import com.github.bfour.fpliteraturecollector.domain.Query;
import com.github.bfour.fpliteraturecollector.domain.Query.QueryStatus;

public class QueryBuilder extends EntityBuilder<Query> implements
		Builder<Query> {

	private String name;
	private List<AtomicRequest> atomicRequests;
	private Integer queuePosition;
	private QueryStatus status;

	public QueryBuilder() {
		super();
	}

	public QueryBuilder(Query q) {

		setID(q.getID());
		setCreationTime(q.getCreationTime());
		setLastChangeTime(q.getLastChangeTime());

		setName(q.getName());
		setAtomicRequests(q.getAtomicRequests());
		setQueuePosition(q.getQueuePosition());
		setStatus(q.getStatus());

	}

	@Override
	public Query getObject() {
		return new Query(getID(), getCreationTime(), getLastChangeTime(),
				getName(), getAtomicRequests(), getQueuePosition(), getStatus());
	}

	public String getName() {
		return name;
	}

	public QueryBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public List<AtomicRequest> getAtomicRequests() {
		return atomicRequests;
	}

	public QueryBuilder setAtomicRequests(List<AtomicRequest> atomicRequests) {
		this.atomicRequests = atomicRequests;
		return this;
	}

	public Integer getQueuePosition() {
		return queuePosition;
	}

	public QueryBuilder setQueuePosition(Integer queuePosition) {
		this.queuePosition = queuePosition;
		return this;
	}

	public QueryStatus getStatus() {
		return status;
	}

	public QueryBuilder setStatus(QueryStatus status) {
		this.status = status;
		return this;
	}

}
