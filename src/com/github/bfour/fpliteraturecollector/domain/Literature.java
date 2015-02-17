package com.github.bfour.fpliteraturecollector.domain;

import java.util.List;

public class Literature extends Entity {

	private String title;
	private List<Person> authors;
	
	public Literature(long iD, String title, List<Person> authors) {
		super(iD);
		this.title = title;
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public List<Person> getAuthors() {
		return authors;
	}
	
}
