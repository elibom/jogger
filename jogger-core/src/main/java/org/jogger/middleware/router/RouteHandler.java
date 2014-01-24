package org.jogger.middleware.router;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * This interface is used in the {@link RouterMiddleware} to create routes easily using anonymous classes. For example:
 *
 * <pre><code>
 * 	Jogger jogger = new Jogger();
 * 	jogger.get("/", new RouteHandler() {
 * 		public void handle(Request request, Response response) {
 * 			// do something here
 * 		}
 * 	});
 * </code></pre>
 *
 * Using this mechanism there is no need to create another class for the controller providing a quick way of handling
 * routes for small applications.
 *
 * @author German Escobar
 */
public interface RouteHandler {

	/**
	 * This method is called when an HTTP request matches the route for which this route handler is configured.
	 *
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 */
	void handle(Request request, Response response);

}
