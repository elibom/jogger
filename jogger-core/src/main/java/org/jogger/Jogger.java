package org.jogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.http.servlet.ServletRequest;
import org.jogger.http.servlet.ServletResponse;
import org.jogger.template.FreemarkerTemplateEngine;
import org.jogger.template.TemplateEngine;
import org.jogger.util.Preconditions;

/**
 * A server that handles HTTP requests using the provided middleware list.
 *
 * @author German Escobar
 */
public class Jogger {

	private static final int DEFAULT_PORT = 5000;

	/**
	 * The Jetty server instance.
	 */
	private Server server;

	/**
	 * The port in which the server will respond.
	 */
	private int port = DEFAULT_PORT;
	
	/**
	 * A list of middlewares that will handle HTTP requests.
	 */
	private List<Middleware> middlewareList = new CopyOnWriteArrayList<Middleware>();
	
	private TemplateEngine templateEngine = new FreemarkerTemplateEngine();
	
	/**
	 * Constructor. Initializes a new instance without middlewares.
	 */
	public Jogger() {}
	
	/**
	 * Constructor. Initializes a new instance with the supplied middleware list. The order is important because they will be 
	 * called in that same order.
	 * 
	 * @param middlewareList an array of middlewares that will be executed on each request.
	 */
	public Jogger(Middleware...middlewareList) {
		this.middlewareList.addAll(Arrays.asList(middlewareList));
	}
	
	/**
	 * Handles an HTTP request by delgating the call to the middlewares.
	 * 
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 * @throws Exception
	 */
	public void handle(Request request, Response response) throws Exception {
		handle(request, response, new ArrayList<Middleware>(middlewareList));
	}
	
	/**
	 * Helper method. Creates a {@link MiddlewareChain} implementation to recursively call the middlewares.
	 * 
	 * @param request
	 * @param response
	 * @param middleware
	 * @throws Exception
	 */
	private void handle(final Request request, final Response response, final List<Middleware> middleware) throws Exception {
		if (middleware.isEmpty()) {
			return;
		}
		
		Middleware current = middleware.remove(0);
		current.handle(request, response, new MiddlewareChain() {
			@Override
			public void next() throws Exception {
				// recursive call
				handle(request, response, middleware);
			}
		});
	}

	/**
	 * Starts the HTTP server listening in the configured <code>port</code> attribute.
	 *
	 * @return itself for method chaining.
	 */
	public Jogger listen() {
		return listen(port);
	}

	/**
	 * Starts the HTTP server listening in the specified <code>port</code>
	 *
	 * @param port the port in which the HTTP server will listen.
	 *
	 * @return itself for method chaining.
	 */
	public Jogger listen(int port) {
		this.port = port;

		// configure the Jetty server
		server = new Server(port);
		server.setHandler(new JoggerHandler());

		// start the Jetty server
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	/**
	 * Joins to the server thread preventing the program to finish.
	 *
	 * @return itself for method chaining.
	 * @throws InterruptedException if the thread is interrupted.
	 */
	public Jogger join() throws InterruptedException {
		server.join();
		return this;
	}

	/**
	 * Stops the HTTP server.
	 *
	 * @return itself for method chaining.
	 */
	public Jogger stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<Middleware> getMiddlewareList() {
		return middlewareList;
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		Preconditions.notNull(templateEngine, "no templateEngine provided");
		this.templateEngine = templateEngine;
	}

	/**
	 * The Jetty handler that will handle HTTP requests.
	 *
	 * @author German Escobar
	 */
	private class JoggerHandler extends AbstractHandler {

		@Override
		public void handle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest servletRequest,
				HttpServletResponse servletResponse) throws IOException, ServletException {
			try {
				// wrap Jetty's request and response in Jogger objects
				Request request = new ServletRequest(servletRequest);
				Response response = new ServletResponse(servletResponse, templateEngine);
				
				Jogger.this.handle(request, response);
			} catch (Exception e) {
				throw new ServletException(e);
			} finally {
				baseRequest.setHandled(true);
			}
		}
	}
}
