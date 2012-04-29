package org.jogger.config.spring;

import javax.servlet.ServletConfig;

import org.jogger.config.ConfigurationException;
import org.jogger.config.ControllerLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This is a {@link ControllerLoader} implementation based on the Spring Framework. It loads the controller from the 
 * Spring ApplicationContext. 
 * 
 * @author German Escobar
 */
public class SpringControllerLoader implements ControllerLoader {
	
	/**
	 * This is the application context from which we are retrieving the controllers.
	 */
	private ApplicationContext applicationContext;
	
	@Override
	public void init(ServletConfig servletConfig) {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
	}

	@Override
	public Object load(String controllerName) throws ConfigurationException {
		
		boolean exists = applicationContext.containsBean(controllerName);
		if (!exists) {
			throw new ConfigurationException("No Spring bean '" + controllerName + "' was not found.");
		}
		
		return applicationContext.getBean(controllerName);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
