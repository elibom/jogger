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
	
	String getPath();
	
	String getQueryString();
	
	/**
	 * @return the full URL with host, port, path and query string
	 */
	String getUrl();
	
	String getMethod();
	
	String getRemoteAddress();
	
	String getContentType();
	
	int getPort();
	
	boolean isSecure();
	
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
	
	BodyParser getBody();
	
	/**
	 * This is what we returned when the {@link Request#getBody()} is called. Provides convenient methods to parse the
	 * request body.
	 * 
	 * @author German Escobar
	 */
	interface BodyParser {
		
		/**
		 * @return the body as a string.
		 */
		String asString();
		
		/**
		 * @return the body as an InputStream.
		 */
		InputStream asInputStream();
		
	}
	
}
