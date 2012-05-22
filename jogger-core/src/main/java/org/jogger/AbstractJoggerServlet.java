package org.jogger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jogger.config.ConfigurationException;
import org.jogger.config.Interceptors;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.http.servlet.ServletRequest;
import org.jogger.http.servlet.ServletResponse;
import org.jogger.router.RouteInstance;
import org.jogger.router.RouteNotFoundException;
import org.jogger.router.Routes;
import org.jogger.router.RoutesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;

/**
 * This is a base class for the {@link JoggerServlet} and the {@link org.jogger.test.MockJoggerServlet}. It basically 
 * provides all the logic to service requests using the Servlet API. 
 * 
 * @author German Escobar
 */
public abstract class AbstractJoggerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(AbstractJoggerServlet.class);
	
	@Override
	public void init() throws ServletException {
		
		try {
			getRoutes(); // validate the routes on initialization
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	}
	
	@Override
	protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
		
		Request request = new ServletRequest(servletRequest);
		Response response = new ServletResponse(servletResponse, getFreeMarkerConfig());
		
		try {
			
			// delegate call using the request/response wrappers
			service(request, response);
			
		} catch (RouteNotFoundException e) {
			servletResponse.setCharacterEncoding("UTF-8");
			
			forward(servletRequest, servletResponse);
			return;
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
	 * Service the request using our {@link Request} and {@link Response} objects. It's public so we can test it.
	 * 
	 * @param request
	 * @param response
	 * 
	 * @throws RouteNotFoundException
	 * @throws Exception
	 */
	public void service(Request request, Response response) throws RouteNotFoundException, Exception {
		
		RouteInstance routeInstance = getRoutes().find(request.getMethod(), request.getPath());
		
		// if no route found, throw exception
		if (routeInstance == null) {
			throw new RouteNotFoundException("No route found for method '" + request.getMethod() + "' and path '" 
					+ request.getPath() + "'");
		}
			
		// load the interceptors of the request
		List<Interceptor> requestInterceptors = getInterceptors().getInterceptors(request.getPath());
			
		// execute the controller
		ControllerExecutor controllerExecutor = new ControllerExecutor(routeInstance, request, response, 
				requestInterceptors);   
		controllerExecutor.proceed();
			
		// clean
		routeInstance = null;
	}
	
	/**
	 * This is a helper class that executes the interceptor chain and calls the controller. Notice that this class 
	 * uses recursion to call each interceptor and the controller. It uses an index to keep track of the next 
	 * interceptor to be executed. Finally, it calls the controller.
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
	
	/**
	 * Must return the routes that the Servlet will use to map requests.
	 * 
	 * @return a ready to use {@link Routes} implementation. 
	 * 
	 * @throws ParseException if there is a problem parsing the routes file.
	 * @throws RoutesException if there is any other problem loading the routes.
	 */
	protected abstract Routes getRoutes() throws ParseException, RoutesException;
	
	/**
	 * Must return the FreeMarker configuration that the Servlet will use to render templates.
	 * 
	 * @return the FreeMarker {@link Configuration} object.
	 * 
	 * @throws ConfigurationException if there is a problem creating the {@link Configuration} object.
	 */
	protected abstract Configuration getFreeMarkerConfig() throws ConfigurationException;
	
	/**
	 * Must return the interceptors that the Servlet will use to intercept requests.
	 * 
	 * @return an initialized {@link Interceptors} implementation. 
	 * 
	 * @throws ConfigurationException if there is a problem creating the {@link Interceptors} implementation.
	 */
	protected abstract Interceptors getInterceptors() throws ConfigurationException;
	
}
