package org.jogger.http;

import java.io.InputStream;
import java.util.Map;

/**
 * Represents an HTTP Request. 
 * 
 * @author German Escobar
 */
public interface Request {

	String getHost();
	
	/**
	 * Returns the full URL including scheme, host, port, path and query string.
	 * 
	 * @return a String object with the URL.
	 */
	String getUrl();
	
	/**
	 * Retrieves the path that was requested without the context path - if any. For example, if the URL is
	 * "http://localhost:8080/app/users/1", the path would be "/users/1".
	 * 
	 * @return a String object with the path of the request.
	 */
	String getPath();
	
	/**
	 * Retrieves the raw query string part of the URL.
	 * 
	 * @return a String object with the query string part of the URL. An empty String if there is no query string. 
	 */
	String getQueryString();
	
	/**
	 * Retrieves the request parameters.
	 * 
	 * @return a Map<String,Value> object with the request parameters.
	 */
	Map<String,Value> getParameters();
	
	/**
	 * Retrieves the value of a request parameter.
	 * 
	 * @param name the name of the parameter
	 * 
	 * @return a Value object. Null if it doesn't exists.
	 */
	Value getParameter(String name);
	
	/**
	 * Returns the HTTP method of the request. 
	 * 
	 * @return a String object with the HTTP method.
	 */
	String getMethod();
	
	String getRemoteAddress();
	
	String getContentType();
	
	int getPort();
	
	boolean isSecure();
	
	/**
	 * Tells whether the request was done using an AJAX call or not.
	 * 
	 * @return true if this was an AJAX call, false otherwise.
	 */
	boolean isAjax();
	
	Map<String,Cookie> getCookies();
	
	/**
	 * Retrieves the {@link Cookie} with the specified name.
	 * 
	 * @param name the name of the cookie to be retrieved.
	 * 
	 * @return itself for method chaining.
	 */
	Cookie getCookie(String name);
	
	Map<String,String> getHeaders();
	
	String getHeader(String name);
	
	/**
	 * Returns an object that will allow us to retrieve the body in multiple ways.
	 * 
	 * @return a {@link BodyParser} implementation.
	 */
	BodyParser getBody();
	
	/**
	 * This is what we returned when the {@link Request#getBody()} is called. Provides convenient methods to parse the
	 * request body.
	 * 
	 * @author German Escobar
	 */
	interface BodyParser {
		
		/**
		 * Returns the body of the request as a string.
		 * 
		 * @return a String object with the body.
		 */
		String asString();
		
		/**
		 * @return the body as an InputStream.
		 */
		InputStream asInputStream();
		
	}
	
}
