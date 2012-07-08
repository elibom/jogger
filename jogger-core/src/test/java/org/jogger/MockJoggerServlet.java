package org.jogger;

import org.jogger.config.Interceptors;
import org.jogger.support.AbstractJoggerServlet;

import freemarker.template.Configuration;

public class MockJoggerServlet extends AbstractJoggerServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Configuration freeMarkerConfig;
	
	private Interceptors interceptors;

	@Override
	public Configuration buildFreeMarker() {
		return freeMarkerConfig;
	}

	@Override
	public Interceptors getInterceptors() {
		return interceptors;
	}

	public void setFreeMarkerConfig(Configuration freeMarkerConfig) {
		this.freeMarkerConfig = freeMarkerConfig;
	}

	public void setInterceptors(Interceptors interceptors) {
		this.interceptors = interceptors;
	}

}
