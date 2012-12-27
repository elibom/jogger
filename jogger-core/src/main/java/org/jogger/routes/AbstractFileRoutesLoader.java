package org.jogger.routes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jogger.Route;
import org.jogger.RoutesException;
import org.jogger.Route.HttpMethod;
import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * <p>Base class for classes that load routes from a file. Concrete implementations need only to 
 * implement the method {@link #loadController(String)}.</p>
 * 
 * <h3>The format of the file</h3>
 * 
 * <p>The file can have zero or more lines, which can be a <strong>route</strong>, a comment (starts with #) or a blank 
 * line. A <strong>route</strong> line has the following format:</p>
 * 
 * <pre><code>
 * 	Route		:= <em>HttpMethod</em> <em>Path</em> <em>Controller#Method</em>
 * 
 * 	HttpMethod	:= (GET | POST | PUT | DELETE)
 * 	Path		:= a valid path starting with /
 * 	Controller	:= a string representing the name of the controller
 * 	Method		:= a string representing the name of the method
 * </code></pre>
 * 
 * <p>For example:</p>
 * 
 * <pre><code>
 * 	# users
 * 	GET    /users         com.app.controller.Users#index
 * 	POST   /users         com.app.controller.Users#create
 *  
 * 	# orders
 * 	GET    /orders/{id}   com.app.controller.Orders#show
 * </code></pre>
 * 
 * <p><em>Note:</em> tokens can be separated by one or more tabs/spaces.</p>
 * 
 * @author German Escobar
 */
public abstract class AbstractFileRoutesLoader {

	/**
	 * Loads the routes from the specified <code>routesFilePath</code>. 
	 * 
	 * @param routesFilePath the path to the file that holds the routes.
	 * 
	 * @return a List of {@link Route} objects or an empty List if there are no routes.
	 * @throws ParseException if there is a problem parsing the file.
	 * @throws RoutesException if the file is not found or any other problem creating the routes.
	 */
	public List<Route> load(String routesFilePath) throws ParseException, RoutesException {
		return load(new File(routesFilePath));
	}
	
	/**
	 * Loads the routes from the specified <code>routesFile</code>.
	 * 
	 * @param routesFile the File that holds the routes.
	 * 
	 * @return a List of {@link Route} objects or an empty List if there are no routes.
	 * @throws ParseException if there is a problem parsing the file.
	 * @throws RoutesException if the file is not found or any other problem creating the routes.
	 */
	public List<Route> load(File routesFile) throws ParseException, RoutesException {
		try {
			return load(new FileInputStream(routesFile));
		} catch (FileNotFoundException e) {
			throw new RoutesException(e);
		}
	}
	
	/**
	 * Loads the routes from the specified <code>inputStream</code>.
	 * 
	 * @param inputStream the InputStream that holds the routes.
	 * 
	 * @return a List of {@link Route} objects or an empty List if there are no routes.
	 * @throws ParseException if there is a problem parsing the file.
	 * @throws RoutesException if the file is not found or any other problem creating the routes.
	 */
	public List<Route> load(InputStream inputStream) throws ParseException, RoutesException {
		int line = 0; // reset line positioning
		List<Route> routes = new ArrayList<Route>(); // this is what we will fill and return
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			
			String input;
			while ( (input = in.readLine()) != null ) {
				line++;
				
				input = input.trim();
				
				// only parse line if it is not empty and not a comment
				if (!input.equals("") && !input.startsWith("#")) {
					
					Route route = parse(input, line);
					routes.add(route);
					
				}
			}
			
		} catch (IOException e) {
			throw new RoutesException("Problem loading the routes.config file: " + e.getMessage(), e);
		}
		
		return routes;
	}
	
	/**
	 * Helper method. Creates a {@link Route} object from the input string.
	 * 
	 * @param input the string to parse.
	 * 
	 * @return a {@link Route} object.
	 * @throws ParseException if the line has an invalid format.
	 */
	private Route parse(String input, int line) throws ParseException {
		
		StringTokenizer st = new StringTokenizer(input, " \t");
		if (st.countTokens() != 3) {
			throw new ParseException("Unrecognized format", line);
		}
		
		// retrieve and validate the three arguments
		String httpMethod = validateHttpMethod( st.nextToken().trim(), line );
		String path = validatePath( st.nextToken().trim(), line );
		String controllerAndMethod = validateControllerAndMethod( st.nextToken().trim(), line );
		
		// retrieve controller name
		int hashPos = controllerAndMethod.indexOf('#');
		String controllerName = controllerAndMethod.substring(0, hashPos);
		
		// retrieve controller method
		String controllerMethod = controllerAndMethod.substring(hashPos + 1);
		
		return buildRoute(httpMethod, path, controllerName, controllerMethod);
		
	}
	
	/**
	 * Helper method. It validates if the HTTP method is valid (i.e. is a GET, POST, PUT or DELETE).
	 * 
	 * @param httpMethod the HTTP method to validate.
	 * 
	 * @return the same httpMethod that was received as an argument.
	 * @throws ParseException if the HTTP method is not recognized.
	 */
	private String validateHttpMethod(String httpMethod, int line) throws ParseException {
		
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
	private String validatePath(String path, int line) throws ParseException {
		if (!path.startsWith("/")) {
			throw new ParseException("Path must start with '/'", line);
		}
		
		boolean openedKey = false;
		for (int i=0; i < path.length(); i++) {
			
			boolean validChar = isValidCharForPath(path.charAt(i), openedKey);
			if (!validChar) {
				throw new ParseException(path, i);
			}
				
			if (path.charAt(i) == '{') {
				openedKey = true;
			}
			
			if (path.charAt(i) == '}') {
				openedKey = false;
			}
			
		}
		
		return path;
	}
	
	/**
	 * Helper method. Tells if a char is valid in a the path of a route line.
	 * 
	 * @param c the char that we are validating.
	 * @param openedKey if there is already an opened key ({) char before.
	 * 
	 * @return true if the char is valid, false otherwise.
	 */
	private boolean isValidCharForPath(char c, boolean openedKey) {
		
		char[] invalidChars = { '?', '#', ' ' };
		for (char invalidChar : invalidChars) {
			if (c == invalidChar) {
				return false;
			}
		}
		
		if (openedKey) {
			char[] moreInvalidChars = { '/', '{' };
			for (char invalidChar : moreInvalidChars) {
				if (c == invalidChar) {
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	/**
	 * Helper method. Validates that the format of the controller and method is valid (i.e. in the form of controller#method).
	 * 
	 * @param beanAndMethod the beanAndMethod string to be validated.
	 * 
	 * @return the same beanAndMethod that was received as an argument.
	 * @throws ParseException if the format of the controller and method is not valid.
	 */
	private String validateControllerAndMethod(String beanAndMethod, int line) throws ParseException {
		int hashPos = beanAndMethod.indexOf('#');
		if (hashPos == -1) {
			throw new ParseException("Unrecognized format for '" + beanAndMethod + "'", line);
		}
		
		return beanAndMethod;
	}
	
	/**
	 * Helper method. Builds a {@link Route} object from the received arguments instantiating the controller and the 
	 * method. It uses the {@link #loadController(String)} method that has to be defined by concrete implementations. 
	 * 
	 * @param httpMethod the HTTP method to which the route will respond.
	 * @param path the HTTP path to which the route will respond.
	 * @param controllerName the name of the controller that will handle this route.
	 * @param methodName the name of the method that will handle this route.
	 * 
	 * @return a {@link Route} object.
	 * @throws RoutesException if there is a problem loading the controller or the method.
	 */
	private Route buildRoute(String httpMethod, String path, String controllerName, String methodName) throws RoutesException {	
		Object controller = loadController(controllerName);
		Method method = getMethod(controller, methodName);
		
		return new Route(HttpMethod.valueOf(httpMethod.toUpperCase()), path, controller, method);
	}
	
	/**
	 * Helper method. Retrieves the method with the specified <code>methodName</code> and from the specified object.
	 * Notice that the method must received two parameters of types {@link Request} and {@link Response} respectively.
	 * 
	 * @param controller the object from which we will retrieve the method.
	 * @param methodName the name of the method to be retrieved.
	 * 
	 * @return a <code>java.lang.reflect.Method</code> object.
	 * @throws RoutesException if the method doesn't exists or there is a problem accessing the method.
	 */
	private Method getMethod(Object controller, String methodName) throws RoutesException {
		try {
			// try to retrieve the method and check if an exception is thrown
			return controller.getClass().getMethod(methodName, Request.class, Response.class);
		} catch (Exception e) {
			throw new RoutesException(e);
		}
	}
	
	/**
	 * Helper method. Loads the controller with the specified <code>controllerName</code>. It is up to the concrete 
	 * implementation to decide how to load the controller and what does the <code>controllerName</code> represents.
	 * 
	 * @param controllerName the name of the controller.
	 * 
	 * @return an Object that represents the controller.
	 * @throws RoutesException if the controller is not found or there is a problem instantiating it.
	 */
	protected abstract Object loadController(String controllerName) throws RoutesException;
	
}
