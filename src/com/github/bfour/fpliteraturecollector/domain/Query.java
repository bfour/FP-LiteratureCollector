package com.github.bfour.fpliteraturecollector.domain;

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




import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.bfour.fpjcommons.model.Entity;

public class Query extends Entity {

	private Map<AtomicRequest, List<Literature>> requestsLiteratureMap;

	public Query(long iD, List<AtomicRequest> requests) {
		super(iD);
		this.requestsLiteratureMap = new HashMap<AtomicRequest, List<Literature>>(
				requests.size());
		for (AtomicRequest request : requests) {
			requestsLiteratureMap.put(request, new LinkedList<Literature>());
		}
	}

	public Map<AtomicRequest, List<Literature>> getRequestsLiteratureMap() {
		return requestsLiteratureMap;
	}

}
