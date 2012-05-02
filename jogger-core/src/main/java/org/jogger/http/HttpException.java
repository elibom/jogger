package org.jogger.http;

/**
 * It usually wraps any underlying exception thrown from the methods in {@link Request} and {@link Response} implementations.
 * 
 * @author German Escobar
 */
public class HttpException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public HttpException() {
		super();
	}

	public HttpException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Throwable throwable) {
		super(throwable);
	}

}
