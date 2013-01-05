package org.jogger.routes;

import java.text.ParseException;
import java.util.List;

import org.jogger.Route;
import org.jogger.RoutesException;

/**
 * 
 * @author German Escobar
 */
public interface RoutesLoader {

	/**
	 * 
	 * @return a List of {@link Route} objects or an empty List if there are no routes.
	 * @throws ParseException if there is a problem parsing the file.
	 * @throws RoutesException if the file is not found or any other problem creating the routes.
	 */
	List<Route> load() throws ParseException, RoutesException;
	
}
