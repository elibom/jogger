package org.jogger.router;

/**
 * Thrown when there is no route for a given http method and path.
 * 
 * @author German Escobar
 */
public class RouteNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public RouteNotFoundException() {
		super();
	}

	public RouteNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public RouteNotFoundException(String message) {
		super(message);
	}

	public RouteNotFoundException(Throwable cause) {
		super(cause);
	}

}
