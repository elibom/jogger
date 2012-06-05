package org.jogger.http.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jogger.http.Cookie;
import org.jogger.http.HttpException;
import org.jogger.http.Request;
import org.jogger.http.Value;
import org.jogger.support.AbstractRequest;

/**
 * A {@link Request} implementation based on the Servlet API.
 * 
 * @author German Escobar
 */
public class ServletRequest extends AbstractRequest {
	
	/**
	 * The underlying Servlet Request.
	 */
	private HttpServletRequest request;
	
	/**
	 * Constructor. Initialized the underlying Servlet request with a null route path.
	 * 
	 * @param request the Servlet request object.
	 */
	public ServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String getHost() {
		return request.getServerName();
	}
	
	@Override
	public String getUrl() {
		return request.getRequestURL().toString();
	}

	@Override
	public String getPath() {
		String contextPath = request.getContextPath();
		String path = request.getRequestURI();
		
		if (path.startsWith(contextPath)) {
			return path.substring(contextPath.length());
		}
		
		return path;
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public Map<String,Value> getParameters() {
		
		Map<String,Value> ret = new HashMap<String,Value>();
		
		Set<String> names = request.getParameterMap().keySet();
		for (String name : names) {
			ret.put( name, getParameter(name) );
		}
		
		return ret;
		
	}

	@Override
	public Value getParameter(String name) {
		
		Map<String,String[]> params = request.getParameterMap();
		
		String[] paramValues = params.get(name);
		if (paramValues == null) {
			return null;
		}
		
		String ret = "";
		for (String p : paramValues) {
			ret += "," + p;
		}
		
		return new Value( ret.substring(1) );
		
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public String getRemoteAddress() {
		return request.getRemoteAddr();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public int getPort() {
		return request.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public boolean isAjax() {
		if (request.getHeader("x-requested-with") == null) {
            return false;
        }
        return "XMLHttpRequest".equals(request.getHeader("x-requested-with"));
	}

	@Override
	public Map<String,Cookie> getCookies() {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		
		Map<String,Cookie> cookies = new HashMap<String,Cookie>();
		for (javax.servlet.http.Cookie c : servletCookies) {
			cookies.put( c.getName(), map(c) );
		}
		
		return Collections.unmodifiableMap(cookies);
	}

	@Override
	public Cookie getCookie(String name) {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		
		if (servletCookies == null) {
			return null;
		}
		
		for (javax.servlet.http.Cookie c : servletCookies) {
			if (c.getName().equals(name)) {
				return map(c);
			}
		}
		
		return null;
	}
	
	private Cookie map(javax.servlet.http.Cookie servletCookie) {
		Cookie cookie = new Cookie(servletCookie.getName(), servletCookie.getValue(), servletCookie.getMaxAge(), servletCookie.isHttpOnly());
		
		cookie.setPath(servletCookie.getPath());
		cookie.setDomain(servletCookie.getDomain());
		cookie.setSecure(servletCookie.getSecure());
		
		return cookie;
	}

	@Override
	public Map<String, String> getHeaders() {
		Enumeration<String> servletHeaders = request.getHeaderNames();
		
		Map<String,String> headers = new HashMap<String,String>();
		
		while(servletHeaders.hasMoreElements()) {
			String headerName = servletHeaders.nextElement();
			
			headers.put(headerName, request.getHeader(headerName));
		}
		
		return headers;
	}

	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public BodyParser getBody() throws HttpException {
		
		BodyParser bodyParser = new BodyParser() {

			@Override
			public String asString() throws HttpException {
				try {
					BufferedReader reader = request.getReader();
				    StringBuilder sb = new StringBuilder();
				    String line = reader.readLine();
				    while (line != null) {
				        sb.append(line + "\n");
				        line = reader.readLine();
				    }
				    reader.close();
				    String data = sb.toString();
				    
				    return data; 
				} catch (IOException e) {
					throw new HttpException(e);
				}
			}

			@Override
			public InputStream asInputStream() throws HttpException {
				try {
					return request.getInputStream();
				} catch (IOException e) {
					throw new HttpException(e);
				}
			}
			
		};
		
		return bodyParser;
	}

	/**
	 * Sets the path variables from the holders in the route path.
	 * 
	 * @param routePath the original route path with holders, if any.
	 */
	public void setRoutePath(String routePath) {
		initPathVariables( routePath );
	}
	
}
