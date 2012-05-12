package org.jogger.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.servlet.ServletConfig;

import org.jogger.AbstractJoggerServlet;
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
 * This class is used to test Jogger without a Servlet Container. It emulates the Servlet environment.
 * It exposes new methods to set the {@link ControllerLoader}, the routes file path, the templates path and the 
 * interceptors.
 * 
 * @author German Escobar
 */
public class MockJoggerServlet extends AbstractJoggerServlet {
	
	private static final long serialVersionUID = 1L;
	
	private String basePackage = "";

	/**
	 * The path to the routes.config file.
	 */
	private String routesPath = "src/main/webapp/WEB-INF/routes.config";
	
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
	public Routes getRoutes() throws ParseException, RoutesException {
		
		ControllerLoader controllerLoader = getControllerLoader();
		RoutesParser routesParser = new RoutesParserImpl();
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		try {
			
			InputStream inputStream = new FileInputStream(routesPath);
			routes.load(inputStream);
			
		} catch (Exception e) {
			throw new RoutesException(e);
		}
		
		return routes;
		
	}
	
	protected ControllerLoader getControllerLoader() {
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.setBasePackage(basePackage);
		
		return controllerLoader;
		
	}

	@Override
	public Configuration getFreeMarkerConfig() throws ConfigurationException {
		
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
	
	public void setBasePackage(String basePackage) {
		
		if (basePackage == null) {
			throw new IllegalArgumentException("No basePackage specified");
		}
		
		if ( !basePackage.endsWith(".") ) {
			basePackage += ".";
		}
		
		this.basePackage = basePackage;
	}

	public void setRoutesPath(String routesPath) {
		this.routesPath = routesPath;
	}
	
	public void setTemplatesPath(String templatesPath) {
		this.templatesPath = templatesPath;
	}
	
	public void setInterceptors(Interceptors interceptors) {
		this.interceptors = interceptors;
	}

}
