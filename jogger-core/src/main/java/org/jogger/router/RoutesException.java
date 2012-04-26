package org.jogger.router;

/**
 * Generally thrown when a problem occurs loading the routes (different from a parsing error)
 * 
 * @author German Escobar
 */
public class RoutesException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private int offset = -1;
	
	public RoutesException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoutesException(String message, Throwable throwable, int offset) {
		super(message, throwable);
		this.offset = offset;
	}

	public RoutesException(String message, int offset) {
		super(message);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}


}
