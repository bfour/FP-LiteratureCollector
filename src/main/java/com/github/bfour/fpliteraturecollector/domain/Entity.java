package com.github.bfour.fpliteraturecollector.domain;

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

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Entity extends com.github.bfour.fpjcommons.model.Entity {

	@GraphId
	private Long ID;
	private Date creationTime;
	private Date lastChangeTime;

	public Entity() {
	}

	public Entity(Long ID, Date creationTime, Date lastChangeTime) {
		this.ID = ID;
		this.creationTime = creationTime;
		this.lastChangeTime = lastChangeTime;
	}

	@Override
	public Long getID() {
		return ID;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public Date getLastChangeTime() {
		return lastChangeTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getID() == null) ? 0 : getID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getID() == null)
			return false;
		if (obj == null)
			return false;
		if (!(obj instanceof Entity))
			return false;
		Entity other = (Entity) obj;
		if (!getID().equals(other.getID()))
			return false;
		return true;
	}

}
