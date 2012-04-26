package org.jogger.router;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

/**
 * A routes parser.
 * 
 * @author German Escobar
 */
public interface RoutesParser {

	/**
	 * Creates a list of routes from the received InputStream.
	 * 
	 * @param inputStream
	 * 
	 * @return a list of {@link Route} objects.
	 * @throws ParseException
	 * @throws RoutesException
	 */
	List<Route> parse(InputStream inputStream) throws ParseException, RoutesException;
	
}
