package org.jogger;

import java.text.ParseException;

import org.jogger.config.Interceptors;
import org.jogger.router.Routes;
import org.jogger.router.RoutesException;

import freemarker.template.Configuration;

public class MockJoggerServlet extends AbstractJoggerServlet {
	
	private static final long serialVersionUID = 1L;

	private Routes routes;
	
	private Configuration freeMarkerConfig;
	
	private Interceptors interceptors;

	@Override
	public Routes getRoutes() throws ParseException, RoutesException {
		return routes;
	}

	@Override
	public Configuration getFreeMarkerConfig() {
		return freeMarkerConfig;
	}

	@Override
	public Interceptors getInterceptors() {
		return interceptors;
	}

	public void setRoutes(Routes routes) {
		this.routes = routes;
	}

	public void setFreeMarkerConfig(Configuration freeMarkerConfig) {
		this.freeMarkerConfig = freeMarkerConfig;
	}

	public void setInterceptors(Interceptors interceptors) {
		this.interceptors = interceptors;
	}

}
