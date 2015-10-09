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
