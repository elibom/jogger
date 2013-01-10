package org.jogger;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jogger.Route.HttpMethod;
import org.jogger.asset.AssetLoader;
import org.jogger.asset.FileAssetLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorEntry;
import org.jogger.template.FreemarkerTemplateEngine;
import org.jogger.template.TemplateEngine;
import org.jogger.util.Preconditions;

/**
 * <p>Use this class to configure your application's routes, interceptors, template engine, exception handlers, etc.</p>
 * 
 * <h3>Adding routes</h3>
 * There are multiple ways in which you can add routes:
 * 
 * <ol>
 * 	<li>Using the <code>get</code>, <code>post</code>, etc. methods:
 * 		<pre>
 * Jogger app = new Jogger();
 * app.get("/", new RouteHandler() {
 * 	&#064Override
 * 	public void handle(Request request, Response response) {
 * 	
 * 	}
 * });
 * 		</pre>
 * 	</li>
 * 	<li>Using the <code>addRoute</code> methods. In this case, the class <code>MyController</code> with a method 
 * <code>root</code> (that receives a {@link Request} and {@link Response}) must exists.
 * 		<pre>
 * Jogger app = new Jogger();
 * app.addRoute(HttpMethod.GET, "/", new MyController(), "root");
 * 		</pre>
 * 	</li>
 * 	<li>Setting the routes list directly with the method {@link #setRoutes(List)}.
 * 		<pre>
 * Jogger app = new Jogger();
 * List<Route> routes = ...; // retreive the routes somehow
 * app.setRoutes(routes);
 * 		</pre>
 * 	</li>
 * </ol> 
 * To learn how to load routes from a file or using annotations click here.
 * 
 * <h3>Default configuration</h3>
 * When you instantiate this class it comes with the following default configuration:
 * 
 * <ul>
 * 	<li>An empty route list (i.e. no routes).</li>
 * 	<li>An empty interceptor list (i.e. no interceptors).</li>
 * 	<li>A {@link FileAssetLoader} for loading static files.</li>
 * 	<li>A {@link FreemarkerTemplateEngine} for loading and rendering templates.</li>
 * </ul>
 * 
 * You can then add routes, interceptors, change the {@link AssetLoader} and {@link TemplateEngine} 
 * implementations, etc.
 * 
 * @author German Escobar
 */
public class Jogger {

	/**
	 * The list of routes.
	 */
	private List<Route> routes = new CopyOnWriteArrayList<Route>();

	/**
	 * The list of interceptors.
	 */
	private List<InterceptorEntry> interceptors = new CopyOnWriteArrayList<InterceptorEntry>();

	/**
	 * Used to load the static assets.
	 */
	private AssetLoader assetLoader = new FileAssetLoader();

	/**
	 * Used to load and render templates.
	 */
	private TemplateEngine templateEngine = new FreemarkerTemplateEngine();
	
	/**
	 * Called when the request throws an exception.
	 */
	private ExceptionHandler exceptionHandler;
	
	/**
	 * Called when the resource is not found. 
	 */
	private NotFoundHandler notFoundHandler;
	
	/**
	 * Constructor. Initializes the object with the default configuration.
	 */
	public Jogger() {
		
	}
	
	/**
	 * Retrieves the {@link Route} that matches the specified <code>httpMethod</code> and <code>path</code>.
	 * 
	 * @param httpMethod the HTTP method to match. Should not be null or empty.
	 * @param path the path to match. Should not be null but can be empty (which is interpreted as /)
	 * 
	 * @return a {@link Route} object that matches the arguments or null if no route matches.
	 */
	public Route getRoute(String httpMethod, String path) {

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

		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		URI uri = null;
		try {
			uri = new URI(path);
		} catch (URISyntaxException e) {
			return null;
		}

		return uri.getPath();
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
		routePath = routePath.replaceAll("\\{([^{}]+)\\}", "[^#/?]+");
		return pathToMatch.matches(routePath);
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

	public AssetLoader getAssetLoader() {
		return assetLoader;
	}

	public void setAssetLoader(AssetLoader assetLoader) {
		Preconditions.notNull(assetLoader, "no assetLoader provided");
		this.assetLoader = assetLoader;
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		Preconditions.notNull(templateEngine, "no templateEngine provided");
		this.templateEngine = templateEngine;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public NotFoundHandler getNotFoundHandler() {
		return notFoundHandler;
	}

	public void setNotFoundHandler(NotFoundHandler notFoundHandler) {
		this.notFoundHandler = notFoundHandler;
	}

}
