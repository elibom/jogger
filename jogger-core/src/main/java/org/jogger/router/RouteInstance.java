package org.jogger.router;

import java.lang.reflect.Method;

/**
 * This is a wrapper class that has a controller object and a {@link Method}. It is returned from the 
 * {@link Routes#find(String, String)} method.
 * 
 * @author German Escobar
 */
public class RouteInstance {
	
	private Object controller;
	
	private Method method;
	
	public RouteInstance() {
		this(null, null);
	}
	
	public RouteInstance(Object controller, Method method) {
		this.controller = controller;
		this.method = method;
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
}
