package com.elibom.jogger.exception;

/**
 * Signals that a status 401 should be returned to the user. If the <code>message</code> is not null, it will be written to the
 * body of the response.
 *
 * @author German Escobar
 */
public class UnAuthorizedException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	private static final int STATUS = 401;
	private static final String NAME = "Unauthorized";
	
	public UnAuthorizedException() {
		super(STATUS, NAME);
	}
	
	public UnAuthorizedException(String message) {
		super(STATUS, NAME, message);
	}

}
