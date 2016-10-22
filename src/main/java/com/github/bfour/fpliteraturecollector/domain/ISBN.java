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

import java.security.InvalidParameterException;

import org.apache.commons.validator.routines.ISBNValidator;

public class ISBN {

	String v13String;

	public ISBN(String v10OrV13String) {
		
		ISBNValidator validator = ISBNValidator.getInstance();
		String normalizedString = getNormalizedString(v10OrV13String);
		
		if (validator.isValidISBN13(normalizedString))
			this.v13String = normalizedString;
		else if (validator.isValidISBN10(normalizedString))
			this.v13String = validator.convertToISBN13(normalizedString);
		else
			throw new InvalidParameterException("String passed as ISBN is not a valid v10 or v13 ISBN");
		
	}

	private String getNormalizedString(String v10OrV13String) {
		v10OrV13String = v10OrV13String.trim();
		v10OrV13String = v10OrV13String.replace(" ", "");
		v10OrV13String = v10OrV13String.replace("-", "");
		v10OrV13String = v10OrV13String.replace(".", "");
		v10OrV13String = v10OrV13String.replace("/", "");
		return v10OrV13String;
	}

	public String getV13String() {
		return v13String;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((v13String == null) ? 0 : v13String.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ISBN other = (ISBN) obj;
		if (v13String == null) {
			if (other.v13String != null)
				return false;
		} else if (!v13String.equals(other.v13String))
			return false;
		return true;
	}

}
