package org.jogger.router;

import java.lang.reflect.Method;

/**
 * This is a wrapper class that has a controller object and a {@link Method}. It is returned from the 
 * {@link Routes#find(String, String)} method.
 * 
 * @author German Escobar
 */
public class Route {
	
	private String httpMethod;
	
	private String path;
	
	private Object controller;
	
	private Method action;
	
	public Route() {
		this(null, null, null, null);
	}
	
	public Route(String httpMethod, String path, Object controller, Method action) {
		this.httpMethod = httpMethod;
		this.path = path;
		this.controller = controller;
		this.action = action;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getAction() {
		return action;
	}

	public void setAction(Method action) {
		this.action = action;
	}
	
}
