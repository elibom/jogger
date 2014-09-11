package com.elibom.jogger.exception;

/**
 * Signals that there is a conflict in the input data and that we should respond a 409 Conflict status with the specified
 * <code>message</code>.
 *
 * @author German Escobar
 */
public class ConflictException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	private static final int STATUS = 409;
	private static final String NAME = "Conflict";
	
	public ConflictException() {
		super(STATUS, NAME);
	}
	
	public ConflictException(String message) {
		super(STATUS, NAME, message);
	}

}
