package org.jogger;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;

/**
 * This is the default {@link NotFoundHandler} implementation that is used is no one is specified in the 
 * {@link Jogger} instance or if the specified one throws an exception.
 * 
 * @author German Escobar
 */
public class DefaultNotFoundHandler implements NotFoundHandler {
	
	private Logger log = LoggerFactory.getLogger(DefaultNotFoundHandler.class);
	
	private Configuration freemarker;
	
	/**
	 * Constructor. Initializes the freemarker configuration object.
	 */
	public DefaultNotFoundHandler() {
		this.freemarker = new Configuration();
		this.freemarker.setClassForTemplateLoading(Jogger.class, "/templates/");
	}

	@Override
	public void handle(Request request, Response response) {
		
		String accept = request.getHeader("Accept");
		if (accept == null) {
			accept = ""; // this avoids having to checking for null below
		}
		
		if (accept.toLowerCase().contains("html")) {
			renderHtml(response);
		} else if (accept.toLowerCase().contains("json")) {
			response.write("{ \"message\": \"The resource doesn't exists\" }");
		} else {
			response.write("Not Found - The page/resource doesn't exists");
		}
	}
	
	/**
	 * Helper method. Renders the HTML template.
	 * 
	 * @param response the Jogger HTTP response.
	 */
	private void renderHtml(Response response) {
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", "404 - Not Found");
		root.put("message", "The page doesn't exists.");
		
		try {
			StringWriter writer = new StringWriter();
			freemarker.getTemplate("404.ftl").process(root, writer);
			response.write(writer.toString());
		} catch (Exception e) {
			log.error("Exception while rendering default status 404 template: " + e.getMessage(), e);
		}
	}
}
