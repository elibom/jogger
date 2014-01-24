package org.jogger.middleware.router;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jogger.Middleware;
import org.jogger.MiddlewareChain;
import org.jogger.http.Path;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.middleware.router.Route.HttpMethod;
import org.jogger.middleware.router.interceptor.Action;
import org.jogger.middleware.router.interceptor.Controller;
import org.jogger.middleware.router.interceptor.Interceptor;
import org.jogger.middleware.router.interceptor.InterceptorEntry;
import org.jogger.middleware.router.interceptor.InterceptorExecution;
import org.jogger.util.Preconditions;

/**
 * This middleware routes requests through <em>interceptors</em> and <em>controller actions</em> using the <em>routes</em> 
 * provided by the user.
 * 
 * @author German Escobar
 */
public class RouterMiddleware implements Middleware {
	
	/**
	 * The list of routes.
	 */
	private List<Route> routes = new CopyOnWriteArrayList<Route>();

	/**
	 * The list of interceptors.
	 */
	private List<InterceptorEntry> interceptors = new CopyOnWriteArrayList<InterceptorEntry>();
	
	@Override
	public void handle(Request request, Response response, MiddlewareChain chain) throws Exception {
		Route route = getRoute(request.getMethod(), request.getPath());
		if (route == null) {
			chain.next();
			return;
		}
		
		request.setRoute(route);
		response.status(Response.OK);
		
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

		for (InterceptorEntry entry : getInterceptors()) {
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
	 * Retrieves the {@link Route} that matches the specified <code>httpMethod</code> and <code>path</code>.
	 *
	 * @param httpMethod the HTTP method to match. Should not be null or empty.
	 * @param path the path to match. Should not be null but can be empty (which is interpreted as /)
	 *
	 * @return a {@link Route} object that matches the arguments or null if no route matches.
	 */
	private Route getRoute(String httpMethod, String path) {
		Preconditions.notEmpty(httpMethod, "no httpMethod provided.");
		Preconditions.notNull(path, "no path provided.");

		String cleanPath = parsePath(path);

		for (Route route : routes) {
			if (matchesPath(route.getPath(), cleanPath) && route.getHttpMethod().toString().equalsIgnoreCase(httpMethod)) {
				return route;
			}
		}

		return null;
	}
	
	private String parsePath(String path) {
		path = Path.fixPath(path);

		try {
			URI uri = new URI(path);
			return uri.getPath();
		} catch (URISyntaxException e) {
			return null;
		}
	}

	/**
	 * Helper method. Tells if the the HTTP path matches the route path.
	 *
	 * @param routePath the path defined for the route.
	 * @param pathToMatch the path from the HTTP request.
	 *
	 * @return true if the path matches, false otherwise.
	 */
	private boolean matchesPath(String routePath, String pathToMatch) {
		routePath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
		return pathToMatch.matches("(?i)" + routePath);
	}
	
	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		Preconditions.notNull(routes, "no routes provided");
		this.routes = routes;
	}
	
	/**
	 * Adds a route to the list of routes using a {@link Route} object.
	 *
	 * @param route the route to be added.
	 */
	public void addRoute(Route route) {
		Preconditions.notNull(route, "no route provided");
		this.routes.add(route);
	}
	
	/**
	 * Creates a {@link Route} object from the received arguments and adds it to the list of routes.
	 *
	 * @param httpMethod the HTTP method to which this route is going to respond.
	 * @param path the path to which this route is going to respond.
	 * @param controller the object that will be invoked when this route matches.
	 * @param methodName the name of the method in the <code>controller</code> object that will be invoked when this
	 * route matches.
	 *
	 * @throws NoSuchMethodException if the <code>methodName</code> is not found or doesn't have the right signature.
	 */
	public void addRoute(HttpMethod httpMethod, String path, Object controller, String methodName) throws NoSuchMethodException {
		Preconditions.notNull(controller, "no controller provided");
		Method method = controller.getClass().getMethod(methodName, Request.class, Response.class);
		addRoute(httpMethod, path, controller, method);
	}
	
	/**
	 * Creates a {@link Route} object from the received arguments and adds it to the list of routes.
	 *
	 * @param httpMethod the HTTP method to which this route is going to respond.
	 * @param path the path to which this route is going to respond.
	 * @param controller the object that will be invoked when this route matches.
	 * @param method the Method that will be invoked when this route matches.
	 */
	public void addRoute(HttpMethod httpMethod, String path, Object controller, Method method) {
		// validate signature
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes.length != 2 || !paramTypes[0].equals(Request.class) || !paramTypes[1].equals(Response.class)) {
			throw new RoutesException("Expecting two params of type org.jogger.http.Request and org.jogger.http.Response "
					+ "respectively");
		}

		method.setAccessible(true); // to access methods from anonymous classes
		routes.add(new Route(httpMethod, path, controller, method));
	}
	
	/**
	 * Creates a {@link Route} object and adds it to the routes list. It will respond to the GET HTTP method and the
	 * specified <code>path</code> invoking the {@link RouteHandler} object.
	 *
	 * @param path the path to which this route will respond.
	 * @param handler the object that will be invoked when the route matches.
	 */
	public void get(String path, RouteHandler handler) {
		try {
			addRoute(HttpMethod.GET, path, handler, "handle");
		} catch (NoSuchMethodException e) {
			// shouldn't happen unless we change the name of the method in RouteHandler
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a {@link Route} object and adds it to the routes list. It will respond to the POST HTTP method and the
	 * specified <code>path</code> invoking the {@link RouteHandler} object.
	 *
	 * @param path the path to which this route will respond.
	 * @param handler the object that will be invoked when the route matches.
	 */
	public void post(String path, RouteHandler handler) {
		try {
			addRoute(HttpMethod.POST, path, handler, "handle");
		} catch (NoSuchMethodException e) {
			// shouldn't happen unless we change the name of the method in RouteHandler
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a {@link Route} object and adds it to the routes list. It will respond to the PUT HTTP method and the
	 * specified <code>path</code> invoking the {@link RouteHandler} object.
	 *
	 * @param path the path to which this route will respond.
	 * @param handler the object that will be invoked when the route matches.
	 */
	public void put(String path, RouteHandler handler) {
		try {
			addRoute(HttpMethod.PUT, path, handler, "handle");
		} catch (NoSuchMethodException e) {
			// shouldn't happen unless we change the name of the method in RouteHandler
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a {@link Route} object and adds it to the routes list. It will respond to the DELETE HTTP method and the
	 * specified <code>path</code> invoking the {@link RouteHandler} object.
	 *
	 * @param path the path to which this route will respond.
	 * @param handler the object that will be invoked when the route matches.
	 */
	public void delete(String path, RouteHandler handler) {
		try {
			addRoute(HttpMethod.DELETE, path, handler, "handle");
		} catch (NoSuchMethodException e) {
			// shouldn't happen unless we change the name of the method in RouteHandler
			throw new RuntimeException(e);
		}
	}
	
	public List<InterceptorEntry> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<InterceptorEntry> interceptors) {
		Preconditions.notNull(interceptors, "no interceptors provided");
		this.interceptors = interceptors;
	}

	/**
	 * Adds the <code>interceptor</code> to the list of interceptors that will matches the specified
	 * <code>paths</code>.
	 *
	 * @param interceptor the interceptor object to be added.
	 * @param paths the paths in which this interceptor will be invoked, an empty array to respond to all paths.
	 */
	public void addInterceptor(Interceptor interceptor, String... paths) {
		Preconditions.notNull(interceptor, "no interceptor provided");
		interceptors.add(new InterceptorEntry(interceptor, paths));
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
				public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
					return findAnnotation(route.getAction(), annotationClass);
				}

				/**
				 * Helper method. Tries to find the annotation in the method or its super methods. Annotations on
				 * methods are not inherited by default, so we need to handle this explicitly.
				 *
				 * @param method the method from which we want to find the annotation.
				 * @param annotationClass the class of the annotation we are searching for.
				 *
				 * @return the annotation or null if not found.
				 */
				private <A extends Annotation> A findAnnotation(Method method, Class<A> annotationClass) {
					A annotation = method.getAnnotation(annotationClass);
					if (annotation != null) {
						return annotation;
					}

					Method superMethod = getSuperMethod(method);
					if (superMethod != null) {
						return findAnnotation(superMethod, annotationClass);
					}

					return null;
				}

				private Method getSuperMethod(Method method) {
					Class<?> superClass = method.getDeclaringClass().getSuperclass();
					try {
						return superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
					} catch (NoSuchMethodException e) {
						return null;
					}
				}
			};
		}
	}
}