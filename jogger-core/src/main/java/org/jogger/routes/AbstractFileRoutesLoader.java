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
import org.jogger.Route.HttpMethod;
import org.jogger.http.Request;
import org.jogger.http.Response;

public abstract class AbstractFileRoutesLoader {

	public List<Route> load(String routesFilePath) throws ParseException, RoutesException {
		return load(new File(routesFilePath));
	}
	
	public List<Route> load(File routesFile) throws ParseException, RoutesException {
		try {
			return load(new FileInputStream(routesFile));
		} catch (FileNotFoundException e) {
			throw new RoutesException(e);
		}
	}
	
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
	 * @return an 
	 * @throws ParseException
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
			
			validateChar(path, i, openedKey);
				
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
	 * Helper method. 
	 * 
	 * @param path
	 * @param index
	 * @param openedKey
	 * @throws ParseException
	 */
	private void validateChar(String path, int index, boolean openedKey) throws ParseException {
		
		char pathChar = path.charAt(index);
		
		char[] invalidChars = { '?', '#', ' ' };
		for (char invalidChar : invalidChars) {
			if (pathChar == invalidChar) {
				throw new ParseException(path, index);
			}
		}
		
		if (openedKey) {
			char[] moreInvalidChars = { '/', '{' };
			for (char invalidChar : moreInvalidChars) {
				if (pathChar == invalidChar) {
					throw new ParseException(path, index);
				}
			}
		}
		
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
	
	private Route buildRoute(String httpMethod, String path, String controllerName, String methodName) throws RoutesException {	
		Object controller = loadController(controllerName);
		Method method = getMethod(controller, methodName);
		
		return new Route(HttpMethod.valueOf(httpMethod.toUpperCase()), path, controller, method);
	}
	
	protected abstract Object loadController(String controllerName) throws RoutesException;
	
	private Method getMethod(Object controller, String controllerMethod) throws RoutesException {
		try {
			// try to retrieve the method and check if an exception is thrown
			return controller.getClass().getMethod(controllerMethod, Request.class, Response.class);
		} catch (Exception e) {
			throw new RoutesException(e);
		}
	}
	
}
