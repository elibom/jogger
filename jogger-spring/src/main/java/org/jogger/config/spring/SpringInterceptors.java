package org.jogger.config.spring;

import javax.servlet.ServletConfig;

import org.jogger.config.ConfigurationException;
import org.jogger.config.Interceptors;
import org.jogger.interceptor.Interceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This is {@link Interceptors} implementation that exposes the {@link #add(String, String...)} method that allows
 * subclasses to add declared beans as interceptors.
 * 
 * @author German Escobar
 */
public abstract class SpringInterceptors extends Interceptors {
	
	/**
	 * This is the application context from which we are retrieving the interceptors beans.
	 */
	private ApplicationContext applicationContext;

	@Override
	public void initialize(ServletConfig servletConfig) {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		
		initialize();
	}
	
	/**
	 * Retrieves an interceptor from the Spring ApplicationContext and delegates the call to the 
	 * {@link #add(Interceptor, String...)} method.
	 * 
	 * @param beanName the name of the Spring bean that extends {@link Interceptor}.
	 * @param paths the paths to which this interceptor applies.
	 */
	public void add(String beanName, String ... paths) {
		
		Interceptor interceptor = (Interceptor) applicationContext.getBean(beanName);
		if (interceptor == null) {
			throw new ConfigurationException("Spring bean '" + beanName + "' not found.");
		}
		
		add(interceptor, paths);
		
	}
	
	/**
	 * This method is implemented by concrete SpringInterceptor classes calling the {@link #add(String, String...)}
	 * method to add interceptors.
	 */
	public abstract void initialize();

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
