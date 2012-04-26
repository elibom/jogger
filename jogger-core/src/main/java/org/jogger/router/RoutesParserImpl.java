package org.jogger.router;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jogger.Controller;
import org.jogger.config.ControllerLoader;

/**
 * Default routes parser implementation. This class is not thread-safe.
 * 
 * @author German Escobar
 */
public class RoutesParserImpl implements RoutesParser {
	
	/**
	 * Used to validate the controller.
	 */
	private ControllerLoader controllerLoader;
	
	/**
	 * Keeps track of the line we are parsing so we don't have to keep passing it through the methods.
	 */
	private int line = 0;

	/**
	 * Expects 0 or more lines representing routes where each line is in the form of:
	 * 
	 * <pre><code>
	 * 	Route		:= HttpMethod Path Controller#Method
	 * 	HttpMethod	:= (GET | POST | PUT | DELETE)
	 * 	Path		:= a valid path starting with /
	 * 	Controller	:= a string representing the name of the controller
	 * 	Method		:= a string representing the name of the method
	 * </code></pre>
	 */
	@Override
	public List<Route> parse(InputStream inputStream) throws ParseException, RoutesException {
		
		// check that the controller loader is set
		if (controllerLoader == null) {
			throw new IllegalStateException("No ControllerLoader specified.");
		}
		
		line = 0; // reset line positioning
		List<Route> routes = new ArrayList<Route>(); // this is what we will fill and return
		
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			String input;
			while ( (input = in.readLine()) != null ) {
				line++;
				
				input = input.trim();
				
				// only parse line if it is not empty and not a comment
				if (!input.equals("") && !input.startsWith("#")) {
					
					Route route = parse(input);
					routes.add(route);
					
				}
			}
			
		} catch (IOException e) {
			throw new RoutesException("Problem loading the routes.config file: " + e.getMessage(), e);
		}
		
		return routes;
	}
	
	/**
	 * Helper method. Parses the line and returns a route checking that the controller and controller method are valid.
	 * 
	 * @param input the string to parse.
	 * 
	 * @return a filled {@link Route} object.
	 * @throws ParseException if the line contains a parsing error.
	 * @throws RoutesException if another error occurs while creating the route.
	 */
	private Route parse(String input) throws ParseException, RoutesException {
		
		// create the Route object from the line checking for parsing errors
		Route route = rawParse(input);
		
		// validate controller and method
		Controller controller = validateController(route.getControllerName());
		validateMethod(controller, route.getControllerMethod());
		
		return route;
	}
	
	/**
	 * Helper method. Creates a {@link Route} object from the input string.
	 * 
	 * @param input the string to parse.
	 * 
	 * @return an 
	 * @throws ParseException
	 */
	private Route rawParse(String input) throws ParseException {
		
		StringTokenizer st = new StringTokenizer(input, " \t");
		if (st.countTokens() != 3) {
			throw new ParseException("Unrecognized format", line);
		}
		
		// retrieve and validate the three arguments
		String httpMethod = validateHttpMethod( st.nextToken().trim() );
		String path = validatePath( st.nextToken().trim() );
		String controllerAndMethod = validateControllerAndMethod( st.nextToken().trim() );
		
		// retrieve controller name
		int hashPos = controllerAndMethod.indexOf("#");
		String controllerName = controllerAndMethod.substring(0, hashPos);
		
		// retrieve controller method
		String controllerMethod = controllerAndMethod.substring(hashPos + 1);
		
		return new Route(httpMethod, path, controllerName, controllerMethod);
		
	}
	
	/**
	 * Helper method. It validates if the HTTP method is valid (i.e. is a GET, POST, PUT or DELETE).
	 * 
	 * @param httpMethod the HTTP method to validate.
	 * 
	 * @return the same httpMethod that was received as an argument.
	 * @throws ParseException if the HTTP method is not recognized.
	 */
	private String validateHttpMethod(String httpMethod) throws ParseException {
		
		if (!httpMethod.equalsIgnoreCase("GET") && 
				!httpMethod.equalsIgnoreCase("POST") &&
				!httpMethod.equalsIgnoreCase("PUT") &&
				!httpMethod.equalsIgnoreCase("DELETE")) {
			
			throw new ParseException("Unrecognized HTTP method: " + httpMethod, line);
		}
		
		return httpMethod;
	}
	
	/**
	 * Helper method. It validates if the path is valid.
	 * 
	 * @param path the path to be validated
	 * 
	 * @return the same path that was received as an argument.
	 * @throws ParseException if the path is not valid.
	 */
	private String validatePath(String path) throws ParseException {
		if (!path.startsWith("/")) {
			throw new ParseException("Path must start with '/'", line);
		}
		
		return path;
	}
	
	/**
	 * Helper method. Validates that the format of the controller and method is valid (i.e. in the form of controller#method).
	 * 
	 * @param beanAndMethod the beanAndMethod string to be validated.
	 * 
	 * @return the same beanAndMethod that was received as an argument.
	 * @throws ParseException if the format of the controller and method is not valid.
	 */
	private String validateControllerAndMethod(String beanAndMethod) throws ParseException {
		int hashPos = beanAndMethod.indexOf("#");
		if (hashPos == -1) {
			throw new ParseException("Unrecognized format for '" + beanAndMethod + "'", line);
		}
		
		return beanAndMethod;
	}
	
	/**
	 * Checks if the controller is valid (i.e. exists and can be loaded).
	 * 
	 * @param controllerName the name of the controller to be validated.
	 * 
	 * @return a instantiated controller.
	 * @throws RoutesException if the controller can't be instantiated.
	 */
	private Controller validateController(String controllerName) throws RoutesException {
		
		Controller controller = null;
		
		try {
			controller = controllerLoader.load(controllerName);
		} catch (Exception e) {
			throw new RoutesException("Exception loading the controller '" + controllerName + "': " + e.getMessage(), e, line);
		}
		
		if (controller == null) {
			throw new RoutesException("Controller '" + controllerName + "' was not found.", line);
		}
		
		return controller;
	}
	
	/**
	 * Checks if the controller method exists and it's accessible.
	 * 
	 * @param controller the controller in which we are going to search for the method.
	 * @param controllerMethod the method we are searching for.
	 * 
	 * @throws RoutesException if the method doesn't exists or can't be accessed.
	 */
	private void validateMethod(Controller controller, String controllerMethod) throws RoutesException {
		try {
			controller.getClass().getMethod(controllerMethod);
		} catch (Exception e) {
			throw new RoutesException("Method '" + controllerMethod + " couldn't be accessed: " + e.getMessage(), e, line);
		} 
	}

	public void setControllerLoader(ControllerLoader controllerLoader) {
		this.controllerLoader = controllerLoader;
	}

}
