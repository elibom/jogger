package org.jogger;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the default {@link ExceptionHandler} implementation that is used if no one is specified in the 
 * {@link Jogger} instance or if the specified one throws an exception.
 * 
 * @author German Escobar
 */
public class DefaultExceptionHandler implements ExceptionHandler {
	
	private Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@Override
	public void handle(Exception exception, Request request, Response response) {
		log.error("Exception processing request (" + request.getMethod() + " " + request.getPath() + "): " + exception.getMessage(), exception);
		
		response.status(Response.INTERNAL_ERROR);
		renderException(exception, request, response);
	}
	
	/**
	 * Helper method. Renders the response in case of an exception.
	 * 
	 * @param exception
	 * @param request
	 * @param response
	 */
	private void renderException(Exception exception, Request request, Response response) {
		
		String accept = request.getHeader("Accept");
		
		if (accept.toLowerCase().contains("html")) {
			response.write("<h1>Server Error</h1><p>" + exception.getMessage() + "</p>" + getStackTrace(exception));
		} else if (accept.toLowerCase().contains("json")) {
			response.write("{ \"message\": \"" + exception.getMessage() + "\" }");
		} else {
			response.write(exception.getMessage() + "\n\n" + getStackTrace(exception));
		}
	}
	
	private String getStackTrace(Exception exception) {
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

}
