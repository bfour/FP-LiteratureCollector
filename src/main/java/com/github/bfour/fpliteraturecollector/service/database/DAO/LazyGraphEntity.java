package com.github.bfour.fpliteraturecollector.service.database.DAO;

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
import com.tinkerpop.blueprints.Vertex;

public class LazyGraphEntity extends Entity {

	private Vertex vertex;

	public LazyGraphEntity(Vertex vertex) {
		this.vertex = vertex;
	}

	@Override
	public Long getID() {
		if (super.getID() == null)
			setID((Long) vertex.getProperty("ID"));
		return super.getID();
	}

	@Override
	public Date getCreationTime() {
		if (super.getCreationTime() == null)
			setCreationTime((Date) vertex.getProperty("creationTime"));
		return super.getCreationTime();
	}

	@Override
	public Date getLastChangeTime() {
		if (super.getLastChangeTime() == null)
			setLastChangeTime((Date) vertex.getProperty("lastChangeTime"));
		return super.getLastChangeTime();
	}

}
