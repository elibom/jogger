package com.elibom.jogger.exception;

/**
 * Signals that a status 403 should be returned to the user. If the <code>message</code> is not null, it will be written to the
 * body of the response.
 *
 * @author German Escobar
 */
public class ForbiddenException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	private static final int STATUS = 403;
	private static final String NAME = "Forbidden";
	
	public ForbiddenException() {
		super(STATUS, NAME);
	}
	
	public ForbiddenException(String message) {
		super(STATUS, NAME, message);
	}

}
