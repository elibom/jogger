package org.jogger.router;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * A routes parser. Receives an {@link InputStream} and parse it into a list of {@link RouteDefinition} objects. Implementations 
 * should only check that the syntax is valid, not if the controller or the method exists and can be loaded.
 * 
 * @author German Escobar
 */
public interface RoutesParser {

	/**
	 * Creates a list of routes from the received InputStream.
	 * 
	 * @param inputStream
	 * 
	 * @return a list of {@link RouteDefinition} objects.
	 * @throws ParseException
	 * @throws RoutesException
	 */
	List<RouteDefinition> parse(InputStream inputStream) throws ParseException, RoutesException;
	
}
