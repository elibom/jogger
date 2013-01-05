package org.jogger.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.jogger.asset.Asset;
import org.jogger.http.Cookie;
import org.jogger.http.Response;
import org.jogger.template.TemplateEngine;
import org.jogger.template.TemplateException;

/**
 * <p>This is a {@link Response} implementation that stores the response state in attributes. Useful for testing Jogger 
 * without a Servlet Container.</p>
 * 
 * @author German Escobar
 */
public class MockResponse implements Response {

	private TemplateEngine templateEngine;
	
	private Map<String,String> headers = new HashMap<String,String>();
	
	private Map<String,Cookie> addedCookies = new HashMap<String,Cookie>();
	
	private Map<String,Cookie> removedCookies = new HashMap<String,Cookie>(); 
	
	private Map<String,Object> attributes = new HashMap<String,Object>();
	
	private String contentType = "text/html";
	
	private int status = Response.OK;
	
	private String output;
	
	private Asset renderedAsset;
	
	private String renderedTemplate;
	
	public MockResponse(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
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
	public Response write(String html) {
		this.output = html;
		return this;
	}

	@Override
	public Response write(Asset asset) {
		this.renderedAsset = asset;
		return this;
	}

	@Override
	public Response render(String templateName) throws TemplateException {
		return render(templateName, new HashMap<String,Object>());
	}

	@Override
	public Response render(String templateName, Map<String, Object> atts) throws TemplateException {
		// merge the user attributes with the controller attributes
		attributes.putAll(atts);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(out);
		
		// retrieve and process the template
		templateEngine.render(templateName, attributes, writer);
			
		try {
			output = out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new TemplateException(e);
		}
			
		this.renderedTemplate = templateName;
			
		return this;
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
	
	public Asset getRenderedAsset() {
		return renderedAsset;
	}
	
}
