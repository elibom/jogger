package org.jogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * A middleware that intercepts exceptions and shows a 500 Internal Server Error HTML page.
 * 
 * @author German Escobar
 */
public class ShowExceptionsMiddleware implements Middleware {
	
	private Logger log = LoggerFactory.getLogger(ShowExceptionsMiddleware.class);
	
	private Configuration freemarker;
	
	public ShowExceptionsMiddleware() {
		this.freemarker = new Configuration();
		this.freemarker.setClassForTemplateLoading(Jogger.class, "/templates/");
	}

	@Override
	public void handle(Request request, Response response, MiddlewareChain chain) throws Exception {
		try {
			chain.next();
		} catch (Exception e) {
			handleException(e, request, response);
		}
	}
	
	private void handleException(Exception exception, Request request, Response response) throws TemplateException, IOException {
		log.error(request.getMethod() + " " + request.getPath() + " - Exception processing request: " 
				+ exception.getMessage(), exception);

		response.status(Response.INTERNAL_ERROR);

		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", "Internal Server Error");
		root.put("message", exception.getMessage());
		root.put("stackTrace", getStackTrace(exception));

		StringWriter writer = new StringWriter();
		freemarker.getTemplate("500.ftl").process(root, writer);
		response.contentType("text/html; charset=UTF-8");
		response.write(writer.toString());
	}
	
	private String getStackTrace(Exception exception) {
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
}

}
