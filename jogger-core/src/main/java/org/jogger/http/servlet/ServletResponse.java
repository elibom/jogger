package org.jogger.http.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jogger.asset.Asset;
import org.jogger.http.Cookie;
import org.jogger.http.HttpException;
import org.jogger.http.Response;
import org.jogger.template.TemplateEngine;
import org.jogger.template.TemplateException;
import org.jogger.util.Preconditions;

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

	private TemplateEngine templateEngine;
	
	/**
	 * The attributes that we are passing to the view.
	 */
	private Map<String,Object> attributes = new HashMap<String,Object>(); 
	
	/**
	 * Constructor. Initializes the object with the underlying Servlet Response and the FreeMarker configuration.
	 * 
	 * @param response the Servlet response object.
	 * @param templateEngine the {@link TemplateEngine} implementation to use.
	 */
	public ServletResponse(HttpServletResponse response, TemplateEngine templateEngine) {
		Preconditions.notNull(response, "no response provided.");
		Preconditions.notNull(templateEngine, "no templateEngine provided.");
		
		this.response = response;
		response.setStatus(Response.OK);
		
		this.templateEngine = templateEngine;
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
		Preconditions.notEmpty(name, "no name provided.");
		Preconditions.notNull(object, "no object specified");
		
		attributes.put(name, object);
		
		return this;
	}

	@Override
	public Response write(String html) throws HttpException {
		try {
			response.getWriter().print(html);
			return this;
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}

	@Override
	public Response write(Asset asset) {
		
		response.setBufferSize(10240);
		response.setContentType(asset.getContentType());
		response.setHeader("Content-Length", String.valueOf(asset.getLength()));
		response.setHeader("Content-Disposition", "inline; filename=\"" + asset.getName() + "\"");

		// prepare streams
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		try {
			// open streams
			input = new BufferedInputStream(asset.getInputStream(), 10240);
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			// Write file contents to response.
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} catch (IOException e) {
			throw new HttpException(e);
		} finally {
			close(output);
			close(input);
		}

		return this;
	}

	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {

			}
		}
	}

	@Override
	public Response render(String templateName) throws TemplateException {
		return render(templateName, new HashMap<String,Object>());
	}

	@Override
	public Response render(String templateName, Map<String, Object> atts) throws TemplateException {
		
		// merge the user attributes with the controller attributes
		attributes.putAll(atts);
		
		try {
			templateEngine.render(templateName, attributes, response.getWriter());
		} catch (IOException e) {
			throw new TemplateException(e);
		}
		return this;
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
