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

import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import com.github.bfour.fpjcommons.model.Entity;
import com.github.bfour.fpliteraturecollector.gui.design.Icons;

public class Query extends Entity {

	public static enum QueryStatus {
		CRAWLING("crawling", com.github.bfour.fpjgui.design.Icons.BUSY_16
				.getIcon()), FINISHED("finished", Icons.FINISHED_20.getIcon()), QUEUED(
				"queued", Icons.QUEUED_20.getIcon()), IDLE("idle", Icons.IDLE_20.getIcon());

		private String tellingName;
		private ImageIcon icon;

		QueryStatus(String tellingName, ImageIcon icon) {
			this.tellingName = tellingName;
			this.icon = icon;
		}

		public String getTellingName() {
			return tellingName;
		}

		public ImageIcon getIcon() {
			return icon;
		}

	}

	protected String name;
	protected List<AtomicRequest> atomicRequests;
	protected Integer queuePosition;
	protected QueryStatus status;

	public Query(Long iD, Date creationTime, Date lastChangeTime, String name,
			List<AtomicRequest> atomicRequests, Integer queuePosition, QueryStatus status) {
		super(iD, creationTime, lastChangeTime);
		this.name = name;
		this.atomicRequests = atomicRequests;
		this.queuePosition = queuePosition;
		this.status = status;
	}

	public Query(String name, List<AtomicRequest> atomicRequests,
			Integer queuePosition, QueryStatus status) {
		super();
		this.name = name;
		this.atomicRequests = atomicRequests;
		this.queuePosition = queuePosition;
		this.status = status;
	}

	public Query() {
		super();
	}

	public String getName() {
		return name;
	}

	public List<AtomicRequest> getAtomicRequests() {
		return atomicRequests;
	}

	public Integer getQueuePosition() {
		return queuePosition;
	}

	public QueryStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return name;
	}

}
