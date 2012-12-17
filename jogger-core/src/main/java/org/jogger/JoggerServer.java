package org.jogger;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jogger.asset.Asset;
import org.jogger.http.Response;
import org.jogger.http.servlet.ServletRequest;
import org.jogger.http.servlet.ServletResponse;

public class JoggerServer {
	
	private static final int DEFAULT_PORT = 5000;
	
	private Jogger jogger;

	private Server server;
	
	private int port = DEFAULT_PORT;
	
	public JoggerServer(Jogger jogger) {
		if (jogger == null) {
			throw new IllegalArgumentException("No jogger provided.");
		}
		
		this.jogger = jogger;
	}
	
	public Jogger getJogger() {
		return jogger;
	}

	public void setJogger(Jogger jogger) {
		this.jogger = jogger;
	}
	
	public JoggerServer listen() {
		return listen(port);
	}

	public JoggerServer listen(int port) {
		this.port = port;

		// configure the Jetty server
		server = new Server(port);
		server.setHandler(new JoggerHandler(jogger));

		// start the Jetty server
		try {
			server.start();
		} catch (Exception e) {
			throw new JoggerException(e);
		}
		
		return this;
	}

	public JoggerServer join() throws InterruptedException {
		server.join();
		return this;
	}

	public JoggerServer stop() {
		try {
			server.stop();
		} catch (Exception e) {
			throw new JoggerException(e);
		}
		
		return this;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private class JoggerHandler extends AbstractHandler {
		
		private RouteExecutor routeExecutor;
		
		public JoggerHandler(Jogger jogger) {
			routeExecutor = new RouteExecutor(jogger);
		}

		@Override
		public void handle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest servletRequest,
				HttpServletResponse servletResponse) throws IOException, ServletException {

			// build the Jogger request/response objects
			ServletRequest request = null;
			ServletResponse response = null;
			try {
				request = new ServletRequest(servletRequest);
				response = new ServletResponse(servletResponse, jogger.getTemplateEngine());
			} catch (Exception e) {
				servletResponse.setStatus(Response.INTERNAL_ERROR);
				e.printStackTrace(servletResponse.getWriter());
			}
			
			try {
				String httpMethod = servletRequest.getMethod();
				String path = request.getPath(); // the ServletRequest object fixes the path
				boolean foundRoute = routeExecutor.route(httpMethod, path, request, response);
				if (!foundRoute && jogger.getAssetLoader() != null) {
					Asset asset = jogger.getAssetLoader().load(URLDecoder.decode(path, "UTF-8"));
					if (asset != null) {
						response.status(Response.OK);
						response.write(asset);
					} else {
						response.notFound();
					}
				}
				
			} catch (Exception e) {
				// call exception handler
				e.printStackTrace();
				response.status(Response.INTERNAL_ERROR);
			} finally {
				baseRequest.setHandled(true);
			}
		}
	}
}
