package org.jogger.interceptor;

import org.jogger.http.Request;
import org.jogger.http.Response;


/**
 * Performs tasks before or after the controller is called. Use cases include:
 * 
 * <ul>
 * 	<li>Check authentication and authorization of the request.</li>
 * 	<li>Log the time taken by the request.</li>
 * 	<li>Open a database connection before the controller is called and then close it.</li>
 * </ul>
 * 
 * Interceptors <em>must be thread-safe</em> as Jogger only uses one instance for all the requests.
 * 
 * @author German Escobar
 */
public interface Interceptor {

	/**
	 * This method is called when a matching request is received. Implementations of this method are responsible of
	 * calling the {@link InterceptorExecution#proceed()} method to continue with the execution of the request unless it
	 * actually wants to stop the execution (in which case it should set the response in the controller)
	 * 
	 * @param request
	 * @param response
	 * @param execution
	 * 
	 * @throws Exception
	 */
	void intercept(Request request, Response response, InterceptorExecution execution) throws Exception;
	
}
