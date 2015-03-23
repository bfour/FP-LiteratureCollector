/**
 * 
 */
package com.github.bfour.fpliteraturecollector.service.crawlers;

/**
 * @author FP
 *
 */
public class InvalidStateTransitionException extends Exception {

	private static final long serialVersionUID = -8987118614354748953L;

	/**
	 * 
	 */
	public InvalidStateTransitionException() {
	}

	/**
	 * @param arg0
	 */
	public InvalidStateTransitionException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public InvalidStateTransitionException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidStateTransitionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public InvalidStateTransitionException(String arg0, Throwable arg1,
			boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
