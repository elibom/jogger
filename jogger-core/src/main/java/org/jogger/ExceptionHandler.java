package org.jogger;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * An implementation of this interface (i.e. {@link DefaultExceptionHandler}) is used by {@link Jogger} to handle the 
 * exceptions of a request. If you want to provide your own exception handler, however, we encourage you to use a 
 * {@link Middleware} instead. 
 * 
 * @author German Escobar
 */
public interface ExceptionHandler {

	void handle(Exception e, Request request, Response response);
	
}
