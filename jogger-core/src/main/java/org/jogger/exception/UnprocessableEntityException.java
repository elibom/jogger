package org.jogger.exception;

/**
 * Signals that a status 422 should be returned to the user. If the <code>message</code> is not null, it will be written to the
 * body of the response.
 *
 * @author German Escobar
 */
public class UnprocessableEntityException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	private static final int STATUS = 422;
	private static final String NAME = "Unprocessable Entity";

	public UnprocessableEntityException() {
		super(STATUS, NAME);
	}
	
	public UnprocessableEntityException(String message) {
		super(STATUS, NAME, message);
	}
}
