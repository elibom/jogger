package org.jogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;

/**
 * This is the default {@link ExceptionHandler} implementation that is used if no one is specified in the 
 * {@link Jogger} instance or if the specified one throws an exception.
 * 
 * @author German Escobar
 */
public class DefaultExceptionHandler implements ExceptionHandler {
	
	private Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);
	
	private Configuration freemarker;
	
	/**
	 * Constructor. Initializes the freemarker configuration object.
	 */
	public DefaultExceptionHandler() {
		this.freemarker = new Configuration();
		this.freemarker.setClassForTemplateLoading(Jogger.class, "/templates/");
	}

	@Override
	public void handle(Exception exception, Request request, Response response) {
		
		String accept = request.getHeader("Accept");
		if (accept == null) {
			accept = ""; // this avoids having to checking for null below
		}
		
		if (accept.toLowerCase().contains("html")) {
			renderHtml(exception, response);
		} else if (accept.toLowerCase().contains("json")) {
			response.write("{ \"message\": \"" + exception.getMessage() + "\" }");
		} else {
			response.write(exception.getMessage() + "\n\n" + getStackTrace(exception));
		}
		
	}
	
	/**
	 * Helper method. Renders the HTML template.
	 * 
	 * @param exception the exception that caused this handler to trigger.
	 * @param response the Jogger HTTP response.
	 */
	private void renderHtml(Exception exception, Response response) {
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", "Internal Server Error");
		root.put("message", exception.getMessage());
		root.put("stackTrace", getStackTrace(exception));
		
		try {
			StringWriter writer = new StringWriter();
			freemarker.getTemplate("500.ftl").process(root, writer);
			response.write(writer.toString());
		} catch (Exception e) {
			log.error("Exception while rendering default status 500 template: " + e.getMessage(), e);
		}
	}
	
	private String getStackTrace(Exception exception) {
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

}
