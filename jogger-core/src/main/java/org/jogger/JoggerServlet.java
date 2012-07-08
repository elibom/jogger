package org.jogger;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.ServiceLoader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jogger.config.ConfigurationException;
import org.jogger.config.ControllerLoader;
import org.jogger.config.DefaultControllerLoader;
import org.jogger.config.Interceptors;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.http.servlet.ServletRequest;
import org.jogger.http.servlet.ServletResponse;
import org.jogger.router.Route;
import org.jogger.router.Routes;
import org.jogger.router.RoutesException;
import org.jogger.router.RoutesImpl;
import org.jogger.router.RoutesParser;
import org.jogger.router.RoutesParserImpl;
import org.jogger.support.AbstractJoggerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;

/**
 * <p>This Servlet must be configured in the web.xml of your application to work. It acts as a front controller 
 * that receives all the HTTP requests and maps them to your controllers.</p>
 * 
 * @author German Escobar
 */
public class JoggerServlet extends AbstractJoggerServlet {

	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(JoggerServlet.class);
	
	/**
	 * Contains the interceptors that we must check on each request.
	 */
	private Interceptors interceptors;
	
	/**
	 * Used to load and retrieve the routes.
	 */
	private Routes routes;
	
	/**
	 * Loads the routes on initialization.
	 */
	@Override
	public void init() throws ServletException {
		
		doInit();
		
		try {
			getRoutes(); // validate the routes on initialization
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	}
	
	/**
	 * Extended classes can override this method to add functionality on initialization of the Servlet. This method is
	 * called before loading the routes.
	 */
	protected void doInit() {}
	
	/**
	 * This is the entry point of a request (through the Servlet API). It creates the Jogger {@link Request} and 
	 * {@link Response} objects, tries to find the route and delegates the call to 
	 * {@link AbstractJoggerServlet#service(Route, Request, Response)} method.
	 */
	@Override
	protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
		
		// build the Jogger request/response objects
		ServletRequest request = new ServletRequest(servletRequest);
		ServletResponse response = new ServletResponse(servletResponse, getFreeMarker());
		
		try {
			
			// try to find the route 
			Route route = getRoutes().find( request.getMethod(), request.getPath() );
			if (route == null) {
			
				// if no route found, forward to the servlet container dispatcher
				servletResponse.setCharacterEncoding("UTF-8");
				forward(servletRequest, servletResponse);
				
				return;
				
			}
			
			// set the route path to the request so we can match path variables
			request.setRoutePath( route.getPath() );

			// delegate call using the request/response wrappers
			service(route, request, response);
			
		} catch (Exception e) {
			/* TODO create a mechanism to handle exceptions, it could be a default template for 500 errors */
			servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(servletResponse.getWriter());
			
			log.error("Exception processing request: " + e.getMessage(), e);
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
	 * Helper method. Loads the routes.
	 * 
	 * @return
	 * @throws ParseException
	 * @throws RoutesException
	 */
	private Routes getRoutes() throws ParseException, RoutesException {
		
		if (routes == null) { // create the routes object if first time
			routes = buildRoutesObject();
			loadRoutes(routes);
		} else if ( Jogger.isDevEnv() ) { // reload routes if development mode
			loadRoutes(routes);
		}
		
		return routes;
		
	}
	
	/**
	 * Helper method. Builds the {@link Routes} implementation. It doesn't loads the routes.
	 * 
	 * @return a {@link Routes} implementation.
	 */
	private Routes buildRoutesObject() {
		
		ControllerLoader controllerLoader = getControllerLoader();
		RoutesParser routesParser = new RoutesParserImpl();
		
		RoutesImpl ret = new RoutesImpl();
		ret.setControllerLoader(controllerLoader);
		ret.setRoutesParser(routesParser);
		
		return ret;
	}
	
	/**
	 * Helper method. Loads the controller loader using the Java Service Loader mechanism.
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
	 * Helper method. Loads the routes from the routes.config file. Routes are stored in the {@link Routes} object.
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
			
			// try to get the file from the servlet context
			InputStream inputStream = getServletContext().getResourceAsStream(routesConfigLocation);
			
			if (inputStream == null) {
				// try in the classpath
				inputStream =JoggerServlet.class.getClassLoader().getResourceAsStream(routesConfigLocation);
			}
			
			routes.load(inputStream);
			
		}
		
	}

	@Override
	protected Configuration buildFreeMarker() throws ConfigurationException {

		Configuration freemarker = new Configuration();
		
		String templatesLocation = getServletConfig().getInitParameter("templatesLocation");
		if (templatesLocation == null) {
			templatesLocation = "/WEB-INF/freemarker";
		}

		freemarker.setServletContextForTemplateLoading(getServletContext(), templatesLocation);
		if (Jogger.isDevEnv()) {
			freemarker.setCacheStorage(new NullCacheStorage());
		}
		
		return freemarker;
		
	}

	@Override
	protected Interceptors getInterceptors() throws ConfigurationException {
		
		if (interceptors != null) {
			return interceptors;
		}
		
		// check if the user has provided a class with the list of interceptors
		String interceptorsClass = getServletConfig().getInitParameter("interceptorsClass");
		
		// if no class has been provided, return a default empty interceptors config
		if (interceptorsClass == null) {
			
			interceptors = new Interceptors() {

				@Override
				public void initialize(ServletConfig servletConfig) {}
				
			};
			
		} else {
		
			try {
				// load the class and return a new instance
				Class<? extends Interceptors> clazz = Class.forName(interceptorsClass).asSubclass(Interceptors.class); 
				interceptors = clazz.newInstance();
				
				// initialize the interceptors
				interceptors.initialize(getServletConfig());
				
			} catch (Exception e) {
				
				// wrap the exception and rethrow it
				throw new ConfigurationException(e);
			} 
			
		}
		
		return interceptors;
	}

}
