package com.elibom.jogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.elibom.jogger.exception.NotFoundException;
import com.elibom.jogger.http.Request;
import com.elibom.jogger.http.Response;
import com.elibom.jogger.http.servlet.ServletRequest;
import com.elibom.jogger.http.servlet.ServletResponse;
import com.elibom.jogger.template.FreemarkerTemplateEngine;
import com.elibom.jogger.template.TemplateEngine;
import com.elibom.jogger.util.Preconditions;

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
	 * The factory used to create the middleware list.
	 */
	private MiddlewaresFactory middlewareFactory;
	
	/**
	 * The cached version of the middlewares
	 */
	private Middleware[] middlewares;
	
	private TemplateEngine templateEngine = new FreemarkerTemplateEngine();
	
	private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();
	
	/**
	 * Constructor. Initializes a new instance without middlewares.
	 */
	public Jogger() {
		this(new MiddlewaresFactory() {
			@Override
			public Middleware[] create() {
				return new Middleware[0];
			}
		});
	}
	
	/**
	 * Constructor. Initializes a new instance with the supplied middleware list. The order is important because they will be 
	 * called in that same order.
	 * 
	 * @param middlewares an array of middlewares that will be executed on each request.
	 */
	public Jogger(final Middleware...middlewares) {
		Preconditions.notNull(middlewares, "no middlewares provided.");
		this.middlewareFactory = new MiddlewaresFactory() {
			@Override
			public Middleware[] create() {
				return middlewares;
			}
		};
		this.middlewares = middlewares;
	}
	
	/**
	 * Constructor. Initializes a new instance with the supplied {@link MiddlewaresFactory}.
	 * 
	 * @param middlewareFactory the factory from which we are going to retrieve the array of middlewares.
	 */
	public Jogger(final MiddlewaresFactory middlewareFactory) {
		Preconditions.notNull(middlewareFactory, "no middlewareFactory provided.");
		this.middlewareFactory = middlewareFactory;
		try {
			this.middlewares = middlewareFactory.create();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Handles an HTTP request by delgating the call to the middlewares.
	 * 
	 * @param request the Jogger HTTP request.
	 * @param response the Jogger HTTP response.
	 * @throws Exception
	 */
	public void handle(Request request, Response response) throws Exception {
		if (Environment.isDevelopment()) {
			this.middlewares = this.middlewareFactory.create();
		}
		
		try {
			handle(request, response, new ArrayList<Middleware>(Arrays.asList(middlewares)));
		} catch (Exception e) {
			if (exceptionHandler != null) {
				exceptionHandler.handle(e, request, response);
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Helper method. Creates a {@link MiddlewareChain} implementation to recursively call the middlewares.
	 * 
	 * @param request
	 * @param response
	 * @param middlewares
	 * @throws Exception
	 */
	private void handle(final Request request, final Response response, final List<Middleware> middlewares) throws Exception {
		if (middlewares.isEmpty()) {
			throw new NotFoundException();
		}
		
		Middleware current = middlewares.remove(0);
		current.handle(request, response, new MiddlewareChain() {
			@Override
			public void next() throws Exception {
				// recursive call
				handle(request, response, middlewares);
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

	public Middleware[] getMiddlewareList() {
		return middlewares;
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setTemplateEngine(TemplateEngine templateEngine) {
		Preconditions.notNull(templateEngine, "no templateEngine provided");
		this.templateEngine = templateEngine;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
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
