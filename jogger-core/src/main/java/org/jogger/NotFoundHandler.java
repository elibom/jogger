package org.jogger;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * <p>Provides a mechanism in which users can handle the case in which a resource is not found (usually for 
 * rendering a custom 404 template).</p>
 * 
 * <p>To use a custom handler create a concrete implementation of this class and configure it using 
 * {@link Jogger#setNotFoundHandler(NotFoundHandler)}. If no handler is configured the {@link DefaultNotFoundHandler} 
 * is used.</p>
 * 
 * @author German Escobar
 */
public interface NotFoundHandler {

	/**
	 * Handles the not found request.
	 * 
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 */
	void handle(Request request, Response response);
	
}
