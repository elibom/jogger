package org.jogger.test;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;

import org.jogger.config.ConfigurationException;
import org.jogger.config.Interceptors;
import org.jogger.support.AbstractJoggerServlet;

import freemarker.template.Configuration;

/**
 * This class is used to test Jogger without a Servlet Container. It emulates the Servlet environment.
 * It exposes new methods to set the templates path and the interceptors.
 * 
 * @author German Escobar
 */
public class MockJoggerServlet extends AbstractJoggerServlet {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The path where the FreeMarker templates are located.
	 */
	private String templatesPath = "src/main/webapp/WEB-INF/freemarker/";
	
	/**
	 * The {@link Interceptors} implementation that we are going to use.
	 */
	private Interceptors interceptors = new Interceptors() {

		@Override
		public void initialize(ServletConfig servletConfig) {}
		
	};

	@Override
	public Configuration buildFreeMarker() throws ConfigurationException {
		
		try {
		
			Configuration freemarker = new Configuration();
			freemarker.setDirectoryForTemplateLoading(new File(templatesPath));
		
			return freemarker;
			
		} catch (IOException e) {
			throw new ConfigurationException(e);
		}
		
	}

	@Override
	public Interceptors getInterceptors() throws ConfigurationException {
		return interceptors;
	} 
	
	public void setTemplatesPath(String templatesPath) {
		this.templatesPath = templatesPath;
	}
	
	public void setInterceptors(Interceptors interceptors) {
		this.interceptors = interceptors;
	}

}
