package org.jogger;

import java.lang.reflect.Method;

/**
 * This is a wrapper class that has a controller object and a {@link Method}. It is returned from the 
 * {@link Routes#find(String, String)} method.
 * 
 * @author German Escobar
 */
public class Route {
	
	public enum HttpMethod {
		GET,
		POST
	}
	
	private final HttpMethod httpMethod;
	
	private final String path;
	
	private final Object controller;
	
	private final Method action;
	
	public Route(HttpMethod httpMethod, String path, Object controller, Method action) {
		if (httpMethod == null) {
			throw new IllegalArgumentException("No httpMethod provided");
		}
		if (path == null) {
			throw new IllegalArgumentException("No path provided");
		}
		if (controller == null) {
			throw new IllegalArgumentException("No controller provided");
		}
		if (action == null) {
			throw new IllegalArgumentException("No action provided");
		}
		
		this.httpMethod = httpMethod;
		this.path = fixPath(path);
		this.controller = controller;
		this.action = action;
	}
	
	private String fixPath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		
		return path;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getPath() {
		return path;
	}

	public Object getController() {
		return controller;
	}

	public Method getAction() {
		return action;
	}
	
}
