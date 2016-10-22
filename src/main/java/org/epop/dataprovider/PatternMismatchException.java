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

package org.epop.dataprovider;

public class PatternMismatchException extends Exception {

	private static final long serialVersionUID = -7105742760088946995L;

	public PatternMismatchException() {
	}

	public PatternMismatchException(String message) {
		super(message);
	}

	public PatternMismatchException(Throwable cause) {
		super(cause);
	}

	public PatternMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public PatternMismatchException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
