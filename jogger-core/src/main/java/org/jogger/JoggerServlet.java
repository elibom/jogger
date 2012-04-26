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
import org.jogger.router.Route;
import org.jogger.router.RoutesException;
import org.jogger.router.RoutesParserImpl;

import freemarker.template.Configuration;

/**
 * This Servlet acts as a front controller and must be installed on each application to route the requests. 
 * 
 * @author German Escobar
 */
public class JoggerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private RoutesParserImpl routesParser;
	
	private ControllerLoader controllerLoader;
	
	private Interceptors interceptorsConfig;
	
	private Configuration freemarker;
	
	private List<Route> routes;

	@Override
	public void init() throws ServletException {
		
		try {
			
			controllerLoader = getControllerLoader();
			controllerLoader.init(getServletConfig());
			
			routesParser = new RoutesParserImpl();
			routesParser.setControllerLoader(controllerLoader);
			
			freemarker = new Configuration();
			freemarker.setServletContextForTemplateLoading(getServletContext(), getTemplatesLocation());
			
			interceptorsConfig = getInterceptorsConfig();
			interceptorsConfig.initialize(getServletConfig());
			
			loadRoutes();
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	}
	
	/**
	 * Loads the controller loader using the Java Service Loader mechanism.
	 * 
	 * @return the {@link ControllerLoader} implementation to be used.
	 * @throws Exception if there is more than one controller loader in classpath
	 */
	private ControllerLoader getControllerLoader() throws Exception {
		
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
			throw new Exception("There is more than one Jogger loader in classpath");
		}
		
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
	private Interceptors getInterceptorsConfig() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		
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
		return clazz.newInstance();
		
	}

	@Override
	protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
		
		// reload routes if we are working on development mode
		if (isDevelopmentEnv()) {
			try {
				loadRoutes();
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
		
		Request request = new ServletRequest(servletRequest);
		Response response = new ServletResponse(servletResponse, freemarker);
		
		final Route route = findRoute(request.getMethod(), request.getPath());
		
		// if no route found, use the default dispatcher
		if (route == null) {
			forward(servletRequest, servletResponse);
			return;
		}
		
		try {
			
			// load the controller
			Controller controller = controllerLoader.load(route.getControllerName());
			controller.init(request, response);
			
			// load the interceptors of the request
			List<Interceptor> interceptors = interceptorsConfig.getInterceptors(request.getPath());
			
			// execute the controller
			ControllerExecutor controllerExecutor = new ControllerExecutor(route, controller, request, response, 
					interceptors);   
			controllerExecutor.proceed();
			
			// clean
			controller = null;
			
		} catch (Exception e) {
			
			/* TODO create a mechanism to handle exceptions, it could be a default template for 500 errors */
			servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(servletResponse.getWriter());
		}
		
	}
	
	/**
	 * Helper method. Searches for a route that matches the HTTP method and the path.
	 * 
	 * @param httpMethod the HTTP method of the request.
	 * @param path the path of the request.
	 * 
	 * @return a {@link Route} that matches the httpMethod and path; or null if no route matches.
	 */
	private Route findRoute(String httpMethod, String path) {
		
		for (Route r : routes) {
			if (r.getPath().equalsIgnoreCase(path) && r.getHttpMethod().equalsIgnoreCase(httpMethod)) {
				return r;
			}
		}
		
		return null;
	}
	
	/**
	 * Helper method. Forwards a request to the default dispatcher.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void forward(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
		rd.forward(request, response);
	}
	
	/**
	 * Helper method. Loads the routes from the routes.config file into the <em>routes attribute</em>. It is called
	 * when the Servlet initializes and when a request is received in development mode.
	 * 
	 * @throws ParseException
	 * @throws RoutesException
	 */
	private synchronized void loadRoutes() throws ParseException, RoutesException {
		
		// load the routes.config file as an InputStream
		String routesConfigLocation = getRoutesConfigLocation();
		InputStream inputStream = getServletContext().getResourceAsStream(routesConfigLocation);
		
		// parse the input stream and set the routes attribute
		routes = routesParser.parse(inputStream);
		
	}
	
	/**
	 * Helper method. Searches for the routes configuration file path in the <em>init params</em> of the Servlet. If 
	 * not found, it returns a default location. 
	 * 
	 * @return a string with the path of the routes configuration file.
	 */
	private String getRoutesConfigLocation() {
		
		String routesConfigLocation = getServletConfig().getInitParameter("routesConfigLocation");
		if (routesConfigLocation == null) {
			routesConfigLocation = "/WEB-INF/routes.config";
		}
		
		return routesConfigLocation;
		
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
		
		String env = System.getProperty("BROADCAST_ENV");
		if (env == null) {
			env = System.getenv("BROADCAST_ENV");
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
		
		private Route route;
		
		private Controller controller;
		
		private Request request;
		
		private Response response;
		
		private List<Interceptor> interceptors;
		
		private int index = 0;
		
		public ControllerExecutor(Route route, Controller controller, Request request, Response response, List<Interceptor> interceptors) {
			this.route = route;
			this.controller = controller;
			this.request = request;
			this.response = response;
			this.interceptors = interceptors;
		}

		@Override
		public void proceed() throws Exception {
			
			// if we finished executing all the interceptors, call the controller method
			if (index == interceptors.size()) {
				Method method = controller.getClass().getMethod(route.getControllerMethod());
				method.invoke(controller);
				
				return;
			}
			
			// retrieve the interceptor and increase the index 
			Interceptor interceptor = interceptors.get(index);
			index++;
			
			// execute the interceptor - notice that the interceptor will call this same method
			interceptor.intercept(request, response, this);
			
		}
		
		
		
	}
}
