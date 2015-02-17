package com.github.bfour.fpliteraturecollector.domain;

import java.util.Date;

public class Entity {

	private long ID;
	private Date creationTime;
	private Date lastChangeTime;
	
	public Entity(long iD, Date creationTime, Date lastChangeTime) {
		super();
		ID = iD;
		this.creationTime = creationTime;
		this.lastChangeTime = lastChangeTime;
	}
	
	public Entity(long iD) {
		Date now = new Date();
		this.ID = iD;
		this.creationTime = now;
		this.lastChangeTime = now;
	}

	public long getID() {
		return ID;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public Date getLastChangeTime() {
		return lastChangeTime;
	}
	
}
