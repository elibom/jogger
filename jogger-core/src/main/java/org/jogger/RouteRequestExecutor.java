package org.jogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Action;
import org.jogger.interceptor.Controller;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorEntry;
import org.jogger.interceptor.InterceptorExecution;
import org.jogger.util.Preconditions;

/**
 * This class is responsible of executing the <em>interceptors</em> and <em>controller actions</em> when there is a 
 * matching route for the HTTP request.
 * 
 * @author German Escobar
 */
public class RouteRequestExecutor {
	
	private Jogger jogger;
	
	/**
	 * Constructor.
	 * 
	 * @param jogger
	 */
	public RouteRequestExecutor(Jogger jogger) {
		Preconditions.notNull(jogger, "no jogger provided");

		this.jogger = jogger;
	}

	/**
	 * Calls the <em>interceptors</em> and the <em>controller action</em> for the specified route. Notice that the 
	 * method {@link Request#getRoute()} must not be null.
	 * 
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 * 
	 * @throws Exception
	 */
	public void execute(Route route, Request request, Response response) throws Exception {
		
		if (route == null) {
			throw new IllegalStateException("There is no route for this request");
		}
		
		// load the interceptors of the request
		List<Interceptor> requestInterceptors = getInterceptors(request.getPath());

		// execute the controller
		ControllerExecutor controllerExecutor = new ControllerExecutor(route, request, response, requestInterceptors);
		controllerExecutor.proceed();
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
	
	/**
	 * Helper method. Checks if the <code>path</code> is in the array of <code>paths</code>. 
	 * 
	 * @param path the path we want to check.
	 * @param paths the paths to match.
	 * 
	 * @return true if the <code>path</code> matches the <code>paths</code> (at least one), false otherwise.
	 */
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
		 * @param route
		 * @param request the Jogger HTTP request.
		 * @param response the Jogger HTTP response.
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
				
				try {
					method.invoke(controller, request, response);
				} catch (InvocationTargetException e) {
					throw (Exception) e.getCause();
				}

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
