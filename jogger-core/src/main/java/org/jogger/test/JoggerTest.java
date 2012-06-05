package org.jogger.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;

import org.jogger.config.ControllerLoader;
import org.jogger.config.DefaultControllerLoader;
import org.jogger.config.Interceptors;
import org.jogger.router.Routes;
import org.jogger.router.RoutesException;
import org.jogger.router.RoutesImpl;
import org.jogger.router.RoutesParser;
import org.jogger.router.RoutesParserImpl;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This is an utility class that you can extend when testing Jogger applications. It provides methods
 * to emulate requests some abstract methods that you need to implement.
 * 
 * @author German Escobar
 */
public abstract class JoggerTest {
	
	@BeforeSuite
	public void init() throws Exception {	
		System.setProperty("JOGGER_ENV", "test");
	}
	
	@AfterSuite
	public void destroy() throws Exception {
		System.clearProperty("JOGGER_ENV");
	}
	
	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'GET' as the HTTP method. Call the 
	 * {@link MockRequest#run()} method to execute the request. 
	 * 
	 * @param path the path of the GET request.
	 * 
	 * @return a {@link MockRequest} object.
	 */
	public MockRequest get(String path) throws Exception {
		return service("GET", path);
	}
	
	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'POST' as the HTTP method. Call the 
	 * {@link MockRequest#run()} method to execute the request. 
	 * 
	 * @param path the path of the POST request.
	 * 
	 * @return a {@link MockRequest} object.
	 */
	public MockRequest post(String path) throws Exception {
		return service("POST", path);
	}
	
	/**
	 * Helper method. Builds a {@link MockRequest} based on the received arguments.
	 * 
	 * @param httpMethod the HTTP method.
	 * @param path the requested path.
	 * 
	 * @return a {@link MockRequest} object.
	 * @throws Exception
	 */
	private MockRequest service(String httpMethod, String path) throws Exception {
		
		String url = "http://localhost" + path;
		
		MockRequest request = new MockRequest(httpMethod, url);
		request.setJoggerServlet( getJoggerServlet() );
		request.setRoutes( getRoutes() );
		
		return request;
	}

	/**
	 * Retrieves the mock servlet that we are going to use to process the request. It can be overridden by subclasses 
	 * to customize.
	 * 
	 * @return an initialized {@link MockJoggerServlet} object.
	 */
	protected MockJoggerServlet getJoggerServlet() {
		MockJoggerServlet joggerServlet = new MockJoggerServlet();
		joggerServlet.setInterceptors( getInterceptors() );
		
		return joggerServlet;
	}
	
	/**
	 * Helper method. Retrieves a {@link Routes} object with the routes loaded.
	 * 
	 * @return an initialized {@link Routes} object.
	 * @throws ParseException if there is a problem parsing the routes.config file.
	 * @throws RoutesException if there is a problem loading the routes.
	 */
	private Routes getRoutes() throws ParseException, RoutesException {
		
		ControllerLoader controllerLoader = getControllerLoader();
		RoutesParser routesParser = new RoutesParserImpl();
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		try {
			
			InputStream inputStream = new FileInputStream( getRoutesPath() );
			routes.load(inputStream);
			
		} catch (Exception e) {
			throw new RoutesException(e);
		}
		
		return routes;
		
	}
	
	/**
	 * This is the default routes.config file path. It can be overridden by subclasses to provide a different path.
	 * 
	 * @return a String object representing the path to the routes.config file.
	 */
	protected String getRoutesPath() {
		return "src/main/webapp/WEB-INF/routes.config";
	}
	
	/**
	 * Retrieves the default {@link ControllerLoader} implementation used to retrieve controllers. It can be 
	 * overridden to provide a different one.
	 * 
	 * @return a {@link ControllerLoader} implementation object.
	 */
	protected ControllerLoader getControllerLoader() {
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.setBasePackage( getBasePackage() );
		
		return controllerLoader;
		
	}
	
	/**
	 * Retrieves the base package name where your controllers are located.
	 * 
	 * @return a String object representing the base package.
	 */
	protected abstract String getBasePackage();
	
	/**
	 * Retrieves the {@link Interceptors} implementation that you use in your class.
	 * 
	 * @return a {@link Interceptors} implementation object.
	 */
	protected abstract Interceptors getInterceptors();
	
}
