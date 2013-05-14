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
 * Used by the {@link JoggerServer} when the route or static file doesn't exist.
 * 
 * @author German Escobar
 */
public class NotFoundHandler {
	
	private Logger log = LoggerFactory.getLogger(NotFoundHandler.class);
	
	private Configuration freemarker;
	
	/**
	 * Constructor. Initializes the freemarker configuration object.
	 */
	public NotFoundHandler() {
		this.freemarker = new Configuration();
		this.freemarker.setClassForTemplateLoading(Jogger.class, "/templates/");
	}

	public void handle(Request request, Response response) {
		
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", "404 - Not Found");
		root.put("message", "The page/resource doesn't exists.");
		
		try {
			StringWriter writer = new StringWriter();
			freemarker.getTemplate("404.ftl").process(root, writer);
			response.contentType("text/plain; charset=UTF-8").write(writer.toString());
		} catch (Exception e) {
			log.error("Exception while rendering default status 404 template: " + e.getMessage(), e);
		}
		
	}
	
}
