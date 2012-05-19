package org.jogger.http.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jogger.http.Cookie;
import org.jogger.http.HttpException;
import org.jogger.http.Response;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * A {@link Response} implementation based on the Servlet API.
 * 
 * @author German Escobar
 */
public class ServletResponse implements Response {
	
	/**
	 * The underlying Servlet Response.
	 */
	private HttpServletResponse response;
	
	/**
	 * The FreeMarker configuration object.
	 */
	private Configuration freemarker;
	
	/**
	 * The attributes that we are passing to the view.
	 */
	private Map<String,Object> attributes = new HashMap<String,Object>(); 
	
	/**
	 * Constructor. Initializes the object with the underlying Servlet Response and the FreeMarker configuration.
	 * 
	 * @param response the Servlet response object.
	 * @param freemarker the FreeMarker configuration object.
	 */
	public ServletResponse(HttpServletResponse response, Configuration freemarker) {
		this.response = response;
		this.freemarker = freemarker;
	}

	@Override
	public int getStatus() {
		return response.getStatus();
	}

	@Override
	public Response status(int status) {
		response.setStatus(status);
		
		return this;
	}

	@Override
	public Response badRequest() {
		response.setStatus(Response.BAD_REQUEST);
		
		return this;
	}

	@Override
	public Response unauthorized() {
		response.setStatus(Response.UNAUTHORIZED);
		
		return this;
	}

	@Override
	public Response notFound() {
		response.setStatus(Response.NOT_FOUND);
		
		return this;
	}
	
	

	@Override
	public Response conflict() {
		response.setStatus(Response.CONFLICT);
		
		return this;
	}

	@Override
	public String getContentType() {
		return response.getContentType();
	}

	@Override
	public Response contentType(String contentType) {
		response.setContentType(contentType);
		
		return this;
	}

	@Override
	public String getHeader(String name) {
		return response.getHeader(name);
	}

	@Override
	public Response setHeader(String name, String value) {
		response.setHeader(name, value);
		
		return this;
	}

	@Override
	public Response setCookie(Cookie cookie) {
		response.addCookie(map(cookie));
		
		return this;
	}

	@Override
	public Response removeCookie(Cookie cookie) {
		cookie.setMaxAge(0);
		
		setCookie(cookie);
		
		return this;
	}
	
	javax.servlet.http.Cookie map(Cookie cookie) {
		javax.servlet.http.Cookie servletCookie = new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
		servletCookie.setMaxAge(cookie.getMaxAge());
		
		if (cookie.getPath() != null) {
			servletCookie.setPath(cookie.getPath());
		}
		
		if (cookie.getDomain() != null) {
			servletCookie.setDomain(cookie.getDomain());
		}
		
		servletCookie.setHttpOnly(cookie.isHttpOnly());
		
		return servletCookie;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Response setAttribute(String name, Object object) {
		if (name == null || "".equals(name)) {
			throw new IllegalArgumentException("No name specified");
		}
		
		if (object == null) {
			throw new IllegalArgumentException("No object specified");
		}
		
		attributes.put(name, object);
		
		return this;
	}

	@Override
	public Response print(String html) throws HttpException {
		try {
			response.getWriter().print(html);
			return this;
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}

	@Override
	public Response render(String templateName) {
		return render(templateName, new HashMap<String,Object>());
	}

	@Override
	public Response render(String templateName, Map<String, Object> atts) throws HttpException {
		// merge the user attributes with the controller attributes
		attributes.putAll(atts);
		
		try {
			// retrieve and process the template
			Template template = freemarker.getTemplate(templateName);
			template.process(attributes, response.getWriter());
			
			return this;
		} catch (Exception e) {
			throw new HttpException(e);
		}
	}

	@Override
	public void redirect(String path) throws HttpException {
		try {
			response.sendRedirect(path);
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}

}
