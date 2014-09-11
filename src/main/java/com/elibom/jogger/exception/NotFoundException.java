package com.elibom.jogger.exception;

/**
 * Signals that a status 404 should be returned to the user. If the <code>message</code> is not null, it will be written to the
 * body of the response.
 *
 * @author German Escobar
 */
public class NotFoundException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	private static final int STATUS = 404;
	private static final String NAME = "Not Found";
	
	public NotFoundException() {
		super(STATUS, NAME);
	}
	
	public NotFoundException(String message) {
		super(STATUS, NAME, message);
	}

}
