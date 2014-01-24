package org.jogger.middleware.router.loader;

import java.text.ParseException;
import java.util.List;

import org.jogger.middleware.router.Route;
import org.jogger.middleware.router.RoutesException;

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
