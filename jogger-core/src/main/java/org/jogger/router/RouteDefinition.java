package org.jogger.router;

/**
 * Represents a row in the routes.config file. 
 * 
 * @author German Escobar
 */
public class RouteDefinition {

	/**
	 * The HTTP method that this routes is declaring.
	 */
	private String httpMethod;
	
	/**
	 * The path of the route
	 */
	private String path;
	
	/**
	 * The name of the controller.
	 */
	private String controllerName;
	
	/**
	 * The method that is going to be called for this route.
	 */
	private String controllerMethod;
	
	/**
	 * Constructor.
	 */
	public RouteDefinition() {
		
	}
	
	/**
	 * Constructor. Sets all the properties with the arguments.
	 * 
	 * @param httpMethod
	 * @param path
	 * @param beanName
	 * @param beanMethod
	 */
	public RouteDefinition(String httpMethod, String path, String beanName, String beanMethod) {
		this.httpMethod = httpMethod;
		this.path = path;
		this.controllerName = beanName;
		this.controllerMethod = beanMethod;
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

	public String getControllerName() {
		return controllerName;
	}

	public void setBeanName(String controllerName) {
		this.controllerName = controllerName;
	}

	public String getControllerMethod() {
		return controllerMethod;
	}

	public void setControllerMethod(String controllerMethod) {
		this.controllerMethod = controllerMethod;
	}
	
}
