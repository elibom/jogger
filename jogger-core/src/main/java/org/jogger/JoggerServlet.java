package org.jogger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jogger.config.ControllerLoader;
import org.jogger.config.DefaultControllerLoader;
import org.jogger.config.Interceptors;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.http.servlet.ServletRequest;
import org.jogger.http.servlet.ServletResponse;
import org.jogger.router.RouteInstance;
import org.jogger.router.RouteNotFoundException;
import org.jogger.router.Routes;
import org.jogger.router.RoutesException;
import org.jogger.router.RoutesImpl;
import org.jogger.router.RoutesParser;
import org.jogger.router.RoutesParserImpl;

import freemarker.template.Configuration;

/**
 * This Servlet acts as a front controller and must be installed on each application to route the requests. 
 * 
 * @author German Escobar
 */
public class JoggerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A routes store that exposes methods to load and find routes.
	 */
	private Routes routes;
	
	/**
	 * Contains the interceptors that we must check on each request.
	 */
	private Interceptors interceptors;
	
	/**
	 * The FreeMarker configuration
	 */
	private Configuration freemarker;
	
	@Override
	public void init() throws ServletException {
		
		try {
			routes = createRoutes();
			
			freemarker = new Configuration();
			freemarker.setServletContextForTemplateLoading(getServletContext(), getTemplatesLocation());
			
			interceptors = createInterceptors();
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	}
	
	/**
	 * Creates the {@link Routes} implementation and loads the routes.
	 * 
	 * @return an initialized {@link Routes} implementation. 
	 * 
	 * @throws ParseException if there is a parsing problem while loading the routes.
	 * @throws RoutesException if there is a problem loading a controller.
	 */
	private Routes createRoutes() throws ParseException, RoutesException {
		
		ControllerLoader controllerLoader = getControllerLoader();
		RoutesParser routesParser = new RoutesParserImpl();
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		loadRoutes(routes);
		
		return routes;
	}
	
	/**
	 * Loads the routes from the routes.config file. Routes are stored in the {@link Routes} object.
	 * 
	 * @param routes we are actually going to use this object to load the routes.
	 * 
	 * @throws ParseException if there is a parsing error while loading the routes.
	 * @throws RoutesException if there is a problem loading a controller
	 */
	private void loadRoutes(Routes routes) throws ParseException, RoutesException {
		
		// get the routes.config location
		String routesConfigLocation = getServletConfig().getInitParameter("routesConfigLocation");
		if (routesConfigLocation == null) {
			routesConfigLocation = "/WEB-INF/routes.config";
		}
		
		// we don't want two or more threads loading routes at the same time although this can only happen in
		// development mode.
		synchronized(routes) {
			
			InputStream inputStream = getServletContext().getResourceAsStream(routesConfigLocation);; 
			routes.load(inputStream);
			
		}
		
	}
	
	/**
	 * Loads the controller loader using the Java Service Loader mechanism.
	 * 
	 * @return the {@link ControllerLoader} implementation to be used.
	 * @throws IllegalStateException if there is more than one controller loader in classpath
	 */
	private ControllerLoader getControllerLoader() throws IllegalStateException {
		
		// this is what we will actually return - set the default one
		ControllerLoader controllerLoader = new DefaultControllerLoader();
		
		// use the ServiceLoader mechanism to load the class
		ServiceLoader<ControllerLoader> serviceLoader = ServiceLoader.load(ControllerLoader.class);
		
		// retrieve the first one
		Iterator<ControllerLoader> iterator = serviceLoader.iterator();
		if (iterator.hasNext()) {
			controllerLoader = iterator.next();
		}
		
		// check if we have more than one controller loader
		if (iterator.hasNext()) {
			throw new IllegalStateException("There is more than one Jogger loader in classpath");
		}
		
		controllerLoader.init(getServletConfig());
		
		return controllerLoader;
	}
	
	/**
	 * Returns the configuration of the interceptors. 
	 * 
	 * @return a {@link Interceptors} object with the list of interceptors that we need to execute on each request.
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private Interceptors createInterceptors() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		
		// check if the user has provided a class with the list of interceptors
		String interceptorsClass = getServletConfig().getInitParameter("interceptorsClass");
		
		// if no class has been provided, return a default empty interceptors config
		if (interceptorsClass == null) {
			
			Interceptors interceptors = new Interceptors() {

				@Override
				public void initialize(ServletConfig servletConfig) {}
				
			};
			
			return interceptors;
			
		}
		
		// load the class and return a new instance
		Class<? extends Interceptors> clazz = Class.forName(interceptorsClass).asSubclass(Interceptors.class); 
		Interceptors interceptors = clazz.newInstance();
		
		// initialize the interceptors
		interceptors.initialize(getServletConfig());
		
		return interceptors;
		
	}

	@Override
	protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
		
		Request request = new ServletRequest(servletRequest);
		Response response = new ServletResponse(servletResponse, freemarker);
		
		try {
			
			// delegate call using the request/response wrappers
			service(request, response);
			
		} catch (RouteNotFoundException e) {
			forward(servletRequest, servletResponse);
			return;
		} catch (Exception e) {
			/* TODO create a mechanism to handle exceptions, it could be a default template for 500 errors */
			servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(servletResponse.getWriter());
		}
		
	}
	
	/**
	 * Helper method. Forwards a request to the default dispatcher.
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forward(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
		rd.forward(request, response);
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
	public void service(Request request, Response response) throws RouteNotFoundException, Exception {
		
		// reload routes if we are working on development mode
		if (isDevelopmentEnv()) {
			loadRoutes(routes);
		}
		
		RouteInstance routeInstance = routes.find(request.getMethod(), request.getPath());
		
		// if no route found, use the default dispatcher
		if (routeInstance == null) {
			throw new RouteNotFoundException("No route found for method '" + request.getMethod() + "' and path '" 
					+ request.getPath() + "'");
		}
			
		// load the interceptors of the request
		List<Interceptor> requestInterceptors = interceptors.getInterceptors(request.getPath());
			
		// execute the controller
		ControllerExecutor controllerExecutor = new ControllerExecutor(routeInstance, request, response, 
				requestInterceptors);   
		controllerExecutor.proceed();
			
		// clean
		routeInstance = null;
	}
	
	/**
	 * Helper method. Searches for the templates folder path in the <em>init params</em> of the servlet. If not found
	 * it returns a default location 
	 * 
	 * @return a string with the folder where the templates are located.
	 */
	private String getTemplatesLocation() {
		
		String templatesLocation = getServletConfig().getInitParameter("templatesLocation");
		if (templatesLocation == null) {
			templatesLocation = "/WEB-INF/freemarker";
		}
		
		return templatesLocation;
		
	}
	
	/**
	 * Tells if we are working in development environment. Remember that environments are set using the 
	 * <em>BROADCAST_ENV</em> environment variable or system property. 
	 * 
	 * @return true if we are working in development mode, false otherwise.
	 */
	private boolean isDevelopmentEnv() {
		
		String env = System.getProperty("JOGGER_ENV");
		if (env == null) {
			env = System.getenv("JOGGER_ENV");
		}
		
		if (env == null || "dev".equalsIgnoreCase(env)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * This is a helper class that executes the controller and the interceptors. Notice that this class uses recursion
	 * to call each interceptor and the controller. It uses an index to keep track of the next interceptor to be
	 * executed. Finally, it calls the controller.
	 * 
	 * @author German Escobar
	 */
	private class ControllerExecutor implements InterceptorChain {
		
		private RouteInstance routeInstance;
		
		private Request request;
		
		private Response response;
		
		private List<Interceptor> interceptors;
		
		private int index = 0;
		
		public ControllerExecutor(RouteInstance routeInstance, Request request, Response response, List<Interceptor> interceptors) {
			this.routeInstance = routeInstance;
			this.request = request;
			this.response = response;
			this.interceptors = interceptors;
		}

		@Override
		public void proceed() throws Exception {
			
			// if we finished executing all the interceptors, call the controller method
			if (index == interceptors.size()) {
				
				Object controller = routeInstance.getController();
				Method method = routeInstance.getMethod();
				
				method.invoke(controller, request, response);
				
				return;
			}
			
			// retrieve the interceptor and increase the index 
			Interceptor interceptor = interceptors.get(index);
			index++;
			
			// execute the interceptor - notice that the interceptor will call this same method
			interceptor.intercept(request, response, this);
			
		}
		
	}

	public void setRoutes(Routes routes) {
		this.routes = routes;
	}

	public void setInterceptors(Interceptors interceptors) {
		this.interceptors = interceptors;
	}

	public void setFreemarker(Configuration freemarker) {
		this.freemarker = freemarker;
	}
	
}
