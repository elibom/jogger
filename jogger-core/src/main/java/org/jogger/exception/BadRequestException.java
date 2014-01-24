package org.jogger.exception;

/**
 * Signals that a request has invalid input data and that we should respond a 400 Bad Request with the specified
 * <code>message</code>.
 *
 * @author German Escobar
 */
public class BadRequestException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	private static final int STATUS = 400;
	private static final String NAME = "Bad Request";
	
	public BadRequestException() {
		super(STATUS, NAME);
	}
	
	public BadRequestException(String message) {
		super(STATUS, NAME, message);
	}

}
