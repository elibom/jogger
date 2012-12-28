package org.jogger;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * <p>Provides a mechanism by which users can handle exceptions thrown in a request (e.g. for showing a custom 500 
 * page, sending an email to support, etc.). Notice that when this exception handler is called the exception has 
 * already been logged and the response already has the status 500 (Internal Error).</p>
 * 
 * <p>To use a custom handler create a concrete implementation of this class and configure it using 
 * {@link Jogger#setExceptionHandler(ExceptionHandler)}. If no handler is configured the 
 * {@link DefaultExceptionHandler} is used.
 * 
 * @author German Escobar
 */
public interface ExceptionHandler {

	/**
	 * <p>Handles the exception.</p>
	 * 
	 * <p><strong>Note:</strong> Don't log the exception or change the status of the response. This has already been 
	 * done for you.</p> 
	 * 
	 * @param exception the exception that was thrown.
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 */
	void handle(Exception exception, Request request, Response response);
	
}
