package com.github.bfour.fpliteraturecollector.domain;

import java.util.Date;

public class Person extends Entity {

	private String firstName;
	private String lastName;

	public Person(long iD, Date creationTime, Date lastChangeTime,
			String firstName, String lastName) {
		super(iD, creationTime, lastChangeTime);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

}
