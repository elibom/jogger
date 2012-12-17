package org.jogger;

/**
 * 
 * @author German Escobar
 */
public class JoggerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JoggerException() {
		super();
	}

	public JoggerException(String message, Throwable cause) {
		super(message, cause);
	}

	public JoggerException(String message) {
		super(message);
	}

	public JoggerException(Throwable cause) {
		super(cause);
	}

}
