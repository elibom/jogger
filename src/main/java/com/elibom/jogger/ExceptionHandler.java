package com.elibom.jogger;

import com.elibom.jogger.http.Request;
import com.elibom.jogger.http.Response;

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
