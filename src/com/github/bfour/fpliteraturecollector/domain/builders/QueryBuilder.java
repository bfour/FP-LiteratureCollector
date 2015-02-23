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

public class QueryBuilder extends EntityBuilder<Query> implements
		Builder<Query> {

	private List<AtomicRequest> atomicRequests;
	
	public QueryBuilder() {
		super();
	}
	
	public QueryBuilder(Query q) {
		setID(q.getID());
		setCreationTime(q.getCreationTime());
		setLastChangeTime(q.getLastChangeTime());
		setAtomicRequests(q.getAtomicRequests());
	}
	
	@Override
	public Query getObject() {
		return new Query(getID(), getCreationTime(), getLastChangeTime(), getAtomicRequests());
	}

	public List<AtomicRequest> getAtomicRequests() {
		return atomicRequests;
	}

	public void setAtomicRequests(List<AtomicRequest> atomicRequests) {
		this.atomicRequests = atomicRequests;
	}

}
