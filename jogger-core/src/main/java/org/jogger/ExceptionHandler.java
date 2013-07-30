package org.jogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jogger.http.Request;
import org.jogger.http.Response;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Used by the {@link JoggerServer} when an exception is caught in a request. It renders a template showing the
 * exception message and stack trace.
 *
 * @author German Escobar
 */
public class ExceptionHandler {

	private Configuration freemarker;

	/**
	 * Constructor. Initializes the freemarker configuration object.
	 */
	public ExceptionHandler() {
		this.freemarker = new Configuration();
		this.freemarker.setClassForTemplateLoading(Jogger.class, "/templates/");
	}

	public void handle(Exception exception, Request request, Response response) throws TemplateException, IOException {
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
