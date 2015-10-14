package com.github.bfour.fpliteraturecollector.domain;

import java.net.URI;
import java.net.URISyntaxException;

public class Link {

	private String name;
	private URI uri;
	/**
	 * custom reference string, eg. if a link to an online resources is
	 * persisted and this link links to the offline local/downloaded resource,
	 * the reference could have the URI of the online resource
	 */
	private String reference;

	public Link(String name, String uriString) throws URISyntaxException {
		URI uri = new URI(uriString);
		if (name == null)
			name = uri.getHost();
		this.name = name;
		this.uri = uri;
	}

	public Link(String name, URI uri) {
		if (name == null)
			name = uri.getHost();
		this.name = name;
		this.uri = uri;
	}

	public Link(String name, URI uri, String reference) {
		this(name, uri);
		this.reference = reference;
	}

	public String getName() {
		return name;
	}

	public URI getUri() {
		return uri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Link))
			return false;
		Link other = (Link) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
