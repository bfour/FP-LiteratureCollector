package com.github.bfour.fpliteraturecollector.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
