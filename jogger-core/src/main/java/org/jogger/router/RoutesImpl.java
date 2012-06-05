package org.jogger.router;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

import org.jogger.config.ControllerLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * Default implementation of the {@link Routes} interface.
 * 
 * @author German Escobar
 */
public class RoutesImpl implements Routes {
	
	/**
	 * Used to parse the input stream
	 */
	private RoutesParser routesParser;
	
	/**
	 * Used to load the controllers.
	 */
	private ControllerLoader controllerLoader;
	
	/**
	 * Here we are storing the loaded routes.
	 */
	private List<RouteDefinition> routes;

	@Override
	public void load(InputStream inputStream) throws ParseException, RoutesException {
		
		List<RouteDefinition> routes = routesParser.parse(inputStream);
		
		// validate routes
		for (RouteDefinition route : routes) {
			Object controller = validateController(route.getControllerName());
			validateMethod(controller, route.getControllerMethod());
		}
		
		this.routes = routes;
	}
	
	/**
	 * Checks if the controller is valid (i.e. exists and can be loaded).
	 * 
	 * @param controllerName the name of the controller to be validated.
	 * 
	 * @return a instantiated controller.
	 * @throws RoutesException if the controller can't be instantiated.
	 */
	private Object validateController(String controllerName) throws RoutesException {
		
		Object controller = null;
		
		try {
			controller = controllerLoader.load(controllerName);
		} catch (Exception e) {
			throw new RoutesException("Exception loading the controller '" + controllerName + "': " 
					+ e.getMessage(), e);
		}
		
		if (controller == null) {
			throw new RoutesException("Controller '" + controllerName + "' was not found.");
		}
		
		return controller;
	}
	
	/**
	 * Checks if the controller method exists and it's accessible.
	 * 
	 * @param controller the controller in which we are going to search for the method.
	 * @param controllerMethod the method we are searching for.
	 * 
	 * @throws RoutesException if the method doesn't exists or can't be accessed.
	 */
	private void validateMethod(Object controller, String controllerMethod) throws RoutesException {
		
		try {
			
			// try to retrieve the method and check if an exception is thrown
			controller.getClass().getMethod(controllerMethod, Request.class, Response.class);
			
		} catch (Exception e) {
			
			throw new RoutesException("Method '" + controllerMethod + "' couldn't be accessed on controller '" + 
					controller.getClass().getName() + "': " + e.getMessage(), e);
			
		} 
	}

	@Override
	public Route find(String httpMethod, String path) throws RoutesException {
		
		for (RouteDefinition route : routes) {
			
			if (matchesPath(route.getPath(), path) && route.getHttpMethod().equalsIgnoreCase(httpMethod)) {
				
				try {
					return createInstance(route);
				} catch (Exception e) {
					throw new RoutesException(e);
				}
				
			}
		}
		
		return null;
	}
	
	private boolean matchesPath(String routePath, String pathToMatch) {
		
		routePath = routePath.replaceAll( "\\{([^{}]+)\\}", "[^#/?]+" );
		return pathToMatch.matches( routePath );
		
	}
	
	/**
	 * Helper method. Given a {@link RouteDefinition} create a {@link Route}.
	 * 
	 * @param route the route from which we are creating the route instance. 
	 * 
	 * @return a initialized {@link Route}.
	 * @throws Exception if something goes wrong.
	 */
	private Route createInstance(RouteDefinition routeDef) throws Exception {
		
		Object controller = controllerLoader.load( routeDef.getControllerName() );
		Method method = controller.getClass().getMethod( routeDef.getControllerMethod(), Request.class, Response.class );
		
		return new Route(routeDef.getHttpMethod(), routeDef.getPath(), controller, method);
		
	}

	public void setRoutesParser(RoutesParser routesParser) {
		this.routesParser = routesParser;
	}

	public void setControllerLoader(ControllerLoader controllerLoader) {
		this.controllerLoader = controllerLoader;
	}

}
