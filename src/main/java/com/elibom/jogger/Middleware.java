package com.elibom.jogger;

import com.elibom.jogger.http.Request;
import com.elibom.jogger.http.Response;

/**
 * A middleware provides functionality to the life cycle of the request. Examples of what middlewares could do include: 
 * serving static files, handling exceptions, routing requests or provide session management.
 * 
 * @author German Escobar
 */
public interface Middleware {

	/**
	 * This method is called by {@link Jogger} when a request arrives. Notice that the implementation has to call 
	 * {@link MiddlewareChain#next} to execute the next middlewares.
	 * 
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 * @param chain an object used to call the next middleware.
	 * @throws Exception
	 */
	void handle(Request request, Response response, MiddlewareChain chain) throws Exception;
	
}
