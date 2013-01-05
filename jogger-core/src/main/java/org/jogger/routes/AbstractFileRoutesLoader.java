package org.jogger.routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jogger.Route;
import org.jogger.Route.HttpMethod;
import org.jogger.RoutesException;
import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * <p>Base class for classes that load routes from a file. Concrete implementations need only to 
 * implement the method {@link #getInputStream()}.</p>
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
public abstract class AbstractFileRoutesLoader implements RoutesLoader {
	
	private ControllerLoader controllerLoader = new ClassPathControllerLoader();

	@Override
	public List<Route> load() throws ParseException, RoutesException {
		InputStream inputStream = null;
		try {
			inputStream = getInputStream();
		} catch (Exception e) {
			throw new RoutesException("Problem loading the routes.config file: " + e.getMessage(), e);
		}
		
		try {
			return load(inputStream);
		} catch (IOException e) {
			throw new RoutesException("Problem loading the routes.config file: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Helper method. Loads the routes from the <code>inputStream</code>.
	 * 
	 * @param inputStream
	 * 
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	private List<Route> load(InputStream inputStream) throws ParseException, IOException {
		int line = 0; // reset line positioning
		List<Route> routes = new ArrayList<Route>(); // this is what we will fill and return
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			
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
			
		} finally {
			closeResource(in);
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
		Object controller = controllerLoader.load(controllerName);
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
	
	private void closeResource(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Used to retrieve the InputStream 
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract InputStream getInputStream() throws Exception;
	
	/**
	 * Sets the <code>basePackage</code> to use when loading controllers (i.e you don't need to specified all the 
	 * package of all controllers in the routes files). This method will set a {@link ClassPathControllerLoader} as the
	 * default mechanism to load controllers with the specified <code>basePackage</code>.
	 * 
	 * @param basePackage the base package of all controllers.
	 */
	public void setBasePackage(String basePackage) {
		this.controllerLoader = new ClassPathControllerLoader(basePackage);
	}

	public ControllerLoader getControllerLoader() {
		return controllerLoader;
	}

	public void setControllerLoader(ControllerLoader controllerLoader) {
		this.controllerLoader = controllerLoader;
	}
	
}
