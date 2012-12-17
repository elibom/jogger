package org.jogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jogger.http.AbstractRequest;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Action;
import org.jogger.interceptor.Controller;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorEntry;
import org.jogger.interceptor.InterceptorExecution;
import org.jogger.routes.RoutesException;

public class RouteExecutor {
	
	private Jogger jogger;
	
	public RouteExecutor(Jogger jogger) {
		if (jogger == null) {
			throw new IllegalArgumentException("No jogger provided.");
		}
		
		this.jogger = jogger;
	}

	/**
	 * Service the request using our {@link Request} and {@link Response} objects. It's public so we can test it.
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws RouteNotFoundException
	 * @throws Exception
	 */
	public boolean route(String httpMethod, String path, AbstractRequest request, Response response) throws Exception {
		
		Route route = findRoute(httpMethod, path);
		if (route == null) {
			response.notFound();
			return false;
		}
		
		// set the route path to the request so we can match path variables
		request.setRoutePath(route.getPath());
		
		// load the interceptors of the request
		List<Interceptor> requestInterceptors = getInterceptors(request.getPath());

		// execute the controller
		ControllerExecutor controllerExecutor = new ControllerExecutor(route, request, response, requestInterceptors);
		controllerExecutor.proceed();

		// clean
		route = null;
		
		return true;
	}
	
	private Route findRoute(String httpMethod, String path) throws RoutesException {

		for (Route route : jogger.getRoutes()) {
			
			if (matchesPath(route.getPath(), path) && route.getHttpMethod().toString().equalsIgnoreCase(httpMethod)) {
				try {
					return route;
				} catch (Exception e) {
					throw new RoutesException(e);
				}
			}
			
		}

		return null;
	}

	private boolean matchesPath(String routePath, String pathToMatch) {
		routePath = routePath.replaceAll("\\{([^{}]+)\\}", "[^#/?]+");
		return pathToMatch.matches(routePath);
	}
	
	/**
	 * Returns a list of interceptors that match a path
	 * 
	 * @param path
	 * @return a list of {@link Interceptor} objects.
	 */
	private List<Interceptor> getInterceptors(String path) {

		List<Interceptor> ret = new ArrayList<Interceptor>();

		for (InterceptorEntry entry : jogger.getInterceptors()) {
			if (matches(path, entry.getPaths())) {
				ret.add(entry.getInterceptor());
			}
		}

		return ret;
	}
	
	private boolean matches(String path, String... paths) {

		if (paths.length == 0) {
			return true;
		}

		for (String p : paths) {

			/* TODO we should use the same mechanism servlets use to match paths */
			if (p.equalsIgnoreCase(path)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * This is a helper class that executes the interceptor chain and calls the controller. Notice that this class uses
	 * recursion to call each interceptor and the controller. It uses an index to keep track of the next interceptor to be
	 * executed. Finally, it calls the controller.
	 * 
	 * @author German Escobar
	 */
	private class ControllerExecutor implements InterceptorExecution {

		private Route route;

		private Request request;

		private Response response;

		private List<Interceptor> interceptors;

		private int index = 0;

		/**
		 * Constructor. Initializes the object with the specified parameters.
		 * 
		 * @param route holds the controller class and the action method.
		 * @param request an object that represents the current HTTP request.
		 * @param response an object that represents the current HTTP response.
		 * @param interceptors a list of interceptors that we need to execute before calling the action.
		 */
		public ControllerExecutor(Route route, Request request, Response response, List<Interceptor> interceptors) {
			this.route = route;
			this.request = request;
			this.response = response;
			this.interceptors = interceptors;
		}

		@Override
		public void proceed() throws Exception {

			// if we finished executing all the interceptors, call the controller method
			if (index == interceptors.size()) {

				Object controller = route.getController();
				Method method = route.getAction();

				method.invoke(controller, request, response);

				return;
			}

			// retrieve the interceptor and increase the index
			Interceptor interceptor = interceptors.get(index);
			index++;

			// execute the interceptor - notice that the interceptor can eventually call the proceed() method recursively.
			interceptor.intercept(request, response, this);

		}

		@Override
		public Controller getController() {

			// create and return a new instance of the Controller class
			return new Controller() {
				public <A extends Annotation> A getAnnotation(Class<A> annotation) {
					return route.getController().getClass().getAnnotation(annotation);
				}
			};

		}

		@Override
		public Action getAction() {

			// create and return a new instance of the Action class
			return new Action() {
				public <A extends Annotation> A getAnnotation(Class<A> annotation) {
					return route.getAction().getAnnotation(annotation);
				}
			};

		}

	}
}
