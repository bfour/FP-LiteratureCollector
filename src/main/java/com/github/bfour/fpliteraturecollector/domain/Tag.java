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

import java.awt.Color;
import java.util.Date;

import com.github.bfour.fpjpersist.neo4j.model.Neo4JEntity;
import com.github.bfour.fpjsearch.fpjsearch.Searchable;

public class Tag extends Neo4JEntity implements
		com.github.bfour.fpjguiextended.tagging.Tag, Searchable {

	private String name;
	private Color colour;

	public Tag(Long ID, Date creationTime, Date lastChangeTime, String name,
			Color colour) {
		super(ID, creationTime, lastChangeTime);
		this.name = name;
		this.colour = colour;
	}

	public Tag(String name, Color colour) {
		super();
		this.name = name;
		this.colour = colour;
	}

	public Tag() {

	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Color getColour() {
		return colour;
	}

	@Override
	public String toString() {
		return name;
	}

}
