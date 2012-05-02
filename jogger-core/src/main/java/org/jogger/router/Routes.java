package org.jogger.router;

import java.io.InputStream;
import java.text.ParseException;

/**
 * Loads routes and exposes a method to find them.
 * 
 * @author German Escobar
 */
public interface Routes {
	
	/**
	 * Loads the routes 
	 * 
	 * @param inputStream the input stream from which we are going to load the route.
	 * 
	 * @throws ParseException if there is a parsing error while parsing the input stream.
	 * @throws RoutesException if there is a problem loading a controller.
	 */
	void load(InputStream inputStream) throws ParseException, RoutesException;
	
	/**
	 * Tries to find a route that matches the method and path, retrieves or instantiates the controller and
	 * retrieves the method to be invoked.
	 * 
	 * @param httpMethod the HTTP method that we are going to use to find the route.
	 * @param path the path for which we are searching a route.
	 * 
	 * @return an initialized {@link RouteInstance} if a route is found or null otherwise.
	 * @throws RoutesException if there is a problem loading the controller or the method.
	 */
	RouteInstance find(String httpMethod, String path) throws RoutesException;
}
