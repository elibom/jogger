package com.elibom.jogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.elibom.jogger.exception.WebApplicationException;
import com.elibom.jogger.http.Http.Headers;
import com.elibom.jogger.http.Request;
import com.elibom.jogger.http.Response;

import freemarker.template.Configuration;

/**
 * This is the default exception handler used internally by {@link Jogger}, triggered when no other middleware handles an 
 * exception.
 * 
 * @author German Escobar
 */
public class DefaultExceptionHandler implements ExceptionHandler {
	
	private Configuration freemarker;
	
	public DefaultExceptionHandler() {
		this.freemarker = new Configuration();
		this.freemarker.setClassForTemplateLoading(Jogger.class, "/templates/");
	}

	public void handle(Exception e, Request request, Response response) {
		if (WebApplicationException.class.isInstance(e)) {
			handleWebApplicationException((WebApplicationException) e, request, response);
		} else {
			handleException(e, request, response);
		}
	}
	
	private void handleWebApplicationException(WebApplicationException wae, Request request, Response response) {
		response.status(wae.getStatus());
		
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", wae.getStatus() + " " + wae.getName());
		root.put("message", wae.getMessage());
		if (wae.getCause() != null) {
			root.put("stackTrace", getStackTrace(wae.getCause()));
		}
		
		if (wae.getStatus() == Response.INTERNAL_ERROR) {
			render(request, response, root, "500.ftl");
		} else {
			render(request, response, root, "4xx.ftl");
		}
	}
	
	private void handleException(Exception e, Request request, Response response) {
		response.status(Response.INTERNAL_ERROR);
		
		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", Response.INTERNAL_ERROR + " " + "Internal Server Error");
		root.put("message", e.getMessage());
		root.put("stackTrace", getStackTrace(e));

		render(request, response, root, "500.ftl");
	}
	
	private void render(Request request, Response response, Map<String,Object> root, String template) {
		if (isHtmlRequest(request)) {
			StringWriter writer = new StringWriter();
			try {
				freemarker.getTemplate(template).process(root, writer);
				response.contentType("text/html; charset=UTF-8");
				response.write(writer.toString());
			} catch (Exception e) {
				response.write("<h1>" + root.get("title") + "</h1><p>" + root.get("message") + "</p>");
			}
		}
	}
	
	private boolean isHtmlRequest(Request request) {
		String accepts = request.getHeader(Headers.ACCEPT);
		if (accepts != null && accepts.contains("html")) {
			return true;
		}

		return false;
	}
	
	private String getStackTrace(Throwable exception) {
		StringWriter errors = new StringWriter();
		exception.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
