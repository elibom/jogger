package org.jogger.http;

import java.util.Map;

/**
 * Represents an HTTP Response.
 * 
 * @author German Escobar
 */
public interface Response {
	
	int OK = 200;
    int CREATED = 201;
    int ACCEPTED = 202;
    int PARTIAL_INFO = 203;
    int NO_RESPONSE = 204;
    int MOVED = 301;
    int FOUND = 302;
    int METHOD = 303;
    int NOT_MODIFIED = 304;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int PAYMENT_REQUIRED = 402;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int CONFLICT = 409;
    int INTERNAL_ERROR = 500;
    int NOT_IMPLEMENTED = 501;
    int OVERLOADED = 502;
    int GATEWAY_TIMEOUT = 503;

	int getStatus();
	
	/**
	 * Sets the status of the response to the received argument.
	 * 
	 * @param status the status to set to the response.
	 * 
	 * @return itself for method chaining.
	 */
	Response status(int status);
	
	/**
	 * Sets the status of the response to 400 - Bad Request.
	 * 
	 * @return itself for method chaining.
	 */
	Response badRequest();
	
	/**
	 * Sets the status of the response to 401 - Unauthorized.
	 * 
	 * @return itself for method chaining.
	 */
	Response unauthorized();
	
	/**
	 * Sets the status of the response to 404 - Not Found.
	 * 
	 * @return itself for method chaining.
	 */
	Response notFound();
	
	/**
	 * Sets the status of the response to 409 - Conflict
	 * 
	 * @return itself for method chaining.
	 */
	Response conflict();
	
	String getContentType();
	
	Response contentType(String contentType);
	
	String getHeader(String name);
	
	Response setHeader(String name, String value);
	
	/**
	 * Sets a cookie.
	 * 
	 * @param cookie the {@link Cookie} to be set.
	 * 
	 * @return itself for method chaining.
	 */
	Response setCookie(Cookie cookie);
	
	/**
	 * Removes a cookie.
	 * 
	 * @param cookie the name of the cookie to be removed.
	 * 
	 * @return itself for method chaining.
	 */
	Response removeCookie(Cookie cookie);
	
	Map<String,Object> getAttributes();
	
	Response setAttribute(String name, Object object);
	
	/**
	 * Writes an HTML string into the response.
	 * 
	 * @param html the HTML code to write in the response.
	 * 
	 * @return itself for method chaining.
	 */
	Response print(String html);
	
	/**
	 * Renders the specified template with no additional attributes (besides those already in the response)
	 * 
	 * @param templateName the name of the template to be rendered.
	 * 
	 * @return itself for method chaining.
	 */
	Response render(String templateName);
	
	/**
	 * Renders the specified template with the specified attributes (and those already in the response). If an
	 * attribute with the same name already exists in the response it will be overriden.
	 * 
	 * @param templateName the name of the template to be rendered.
	 * @param attributes a map of attributes to be passed to the view.
	 * 
	 * @return itself for method chaining.
	 */
	Response render(String templateName, Map<String,Object> attributes);
	
	/**
	 * Sends a redirect.
	 * 
	 * @param path the path to which the request is redirected.
	 */
	void redirect(String path);
	
}
