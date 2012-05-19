package org.jogger.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jogger.http.Cookie;
import org.jogger.http.HttpException;
import org.jogger.http.Response;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * <p>This is a {@link Response} implementation that stores the response state in attributes. Useful for testing Jogger 
 * without a Servlet Container.</p>
 * 
 * @author German Escobar
 */
public class MockResponse implements Response {

	private Configuration freemarker;
	
	private Map<String,String> headers = new HashMap<String,String>();
	
	private Map<String,Cookie> addedCookies = new HashMap<String,Cookie>();
	
	private Map<String,Cookie> removedCookies = new HashMap<String,Cookie>(); 
	
	private Map<String,Object> attributes = new HashMap<String,Object>();
	
	private String contentType = "text/html";
	
	private int status = Response.OK;
	
	private String output;
	
	private String renderedTemplate;
	
	public MockResponse(Configuration freemarker) {
		this.freemarker = freemarker;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public Response status(int status) {
		this.status = status;
		return this;
	}

	@Override
	public Response badRequest() {
		this.status = Response.BAD_REQUEST;
		return this;
	}

	@Override
	public Response unauthorized() {
		this.status = Response.UNAUTHORIZED;
		return this;
	}

	@Override
	public Response notFound() {
		this.status = Response.NOT_FOUND;
		return this;
	}

	@Override
	public Response conflict() {
		this.status = Response.CONFLICT;
		return this;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public Response contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Response setHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	@Override
	public Response setCookie(Cookie cookie) {
		addedCookies.put(cookie.getName(), cookie);
		return this;
	}

	@Override
	public Response removeCookie(Cookie cookie) {
		removedCookies.put(cookie.getName(), cookie);
		return this;
	}
	
	public Map<String,Cookie> getAddedCookies() {
		return addedCookies;
	}
	
	public Map<String,Cookie> getRemovedCookies() {
		return removedCookies;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Response setAttribute(String name, Object object) {
		attributes.put(name, object);
		return this;
	}

	@Override
	public Response print(String html) {
		this.output = html;
		return this;
	}

	@Override
	public Response render(String templateName) {
		return render(templateName, new HashMap<String,Object>());
	}

	@Override
	public Response render(String templateName, Map<String, Object> atts) {
		// merge the user attributes with the controller attributes
		attributes.putAll(atts);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);
		
		try {
			// retrieve and process the template
			Template template = freemarker.getTemplate(templateName);
			template.process(attributes, writer);
			
			output = out.toString("UTF-8");
			
			this.renderedTemplate = templateName;
			
			return this;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}

	@Override
	public void redirect(String path) {
		this.status = Response.FOUND;
		headers.put("Location", path);
	}
	
	public String getOutputAsString() {
		return output;
	}

	public String getRenderedTemplate() {
		return renderedTemplate;
	}
	
}
