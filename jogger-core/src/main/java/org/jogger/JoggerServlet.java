package org.jogger;

import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.ServiceLoader;

import javax.servlet.ServletConfig;

import org.jogger.config.ConfigurationException;
import org.jogger.config.ControllerLoader;
import org.jogger.config.DefaultControllerLoader;
import org.jogger.config.Interceptors;
import org.jogger.router.Routes;
import org.jogger.router.RoutesException;
import org.jogger.router.RoutesImpl;
import org.jogger.router.RoutesParser;
import org.jogger.router.RoutesParserImpl;

import freemarker.template.Configuration;

/**
 * <p>This Servlet must be configured in the web.xml of your application to work. It acts as a front controller 
 * that receives all the HTTP requests and maps them to your controllers.</p>
 * 
 * 
 * 
 * @author German Escobar
 */
public class JoggerServlet extends AbstractJoggerServlet {

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
	protected Routes getRoutes() throws ParseException, RoutesException {
		
		if (routes == null) {
			
			routes = createRoutes();
			loadRoutes(routes);
			
		} else if ( isDeveloperMode() ) {
			
			loadRoutes(routes);
			
		}
		
		return routes;
		
	}
	
	/**
	 * Helper method. Creates, but it doesn't initializes, the {@link Routes} implementation. 
	 * 
	 * @return a {@link Routes} implementation.
	 */
	private Routes createRoutes() {
		
		ControllerLoader controllerLoader = getControllerLoader();
		RoutesParser routesParser = new RoutesParserImpl();
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		return routes;
	}
	
	/**
	 * Tells if we are working in development environment. Remember that environments are set using the 
	 * <em>BROADCAST_ENV</em> environment variable or system property. 
	 * 
	 * @return true if we are working in development mode, false otherwise.
	 */
	private boolean isDeveloperMode() {
		
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

	@Override
	protected Configuration getFreeMarkerConfig() throws ConfigurationException {
		
		if (freemarker != null) {
			return freemarker;
		}
		
		freemarker = new Configuration();
		
		String templatesLocation = getServletConfig().getInitParameter("templatesLocation");
		if (templatesLocation == null) {
			templatesLocation = "/WEB-INF/freemarker";
		}

		freemarker.setServletContextForTemplateLoading(getServletContext(), templatesLocation);
		
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
