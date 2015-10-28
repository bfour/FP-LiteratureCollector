package com.github.bfour.fpliteraturecollector.domain;

import java.util.Date;

import com.github.bfour.fpjpersist.neo4j.model.Neo4JEntity;
import com.github.bfour.fpjsearch.fpjsearch.Searchable;

public class ProtocolEntry extends Neo4JEntity implements Searchable {

	private String string;
	
	public ProtocolEntry()  {
	}

	public ProtocolEntry(String string) {
		super();
		this.string = string;
	}

	public ProtocolEntry(Long ID, Date creationTime, Date lastChangeTime,
			String string) {
		super(ID, creationTime, lastChangeTime);
		this.string = string;
	}

	public String getString() {
		return string;
	}

}
