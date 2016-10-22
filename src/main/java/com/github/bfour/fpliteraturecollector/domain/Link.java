/*
 * Copyright 2016 Florian Pollak
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

	public Link(URI uri) {
		this.name = uri.getHost();
		this.uri = uri;
	}
	
	public Link(String uriString) throws URISyntaxException {
		this.uri = new URI(uriString);
		this.name = this.uri.getHost();
	}	
	
	public Link(String name, String uriString) throws URISyntaxException {
		this(name, new URI(uriString));
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

	public String getReference() {
		return reference;
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

	@Override
	public String toString() {
		return "Link [name=" + name + ", uri=" + uri + "]";
	}

}
