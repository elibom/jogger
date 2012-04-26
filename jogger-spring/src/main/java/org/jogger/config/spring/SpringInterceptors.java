package org.jogger.config.spring;

import javax.servlet.ServletConfig;

import org.jogger.Interceptor;
import org.jogger.config.Interceptors;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class SpringInterceptors extends Interceptors {
	
	private ApplicationContext applicationContext;

	@Override
	public void initialize(ServletConfig servletConfig) {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
		
		initialize();
	}
	
	public void add(String beanName, String ... paths) {
		
		Interceptor interceptor = applicationContext.getBean(beanName, Interceptor.class);
		add(interceptor, paths);
		
	}
	
	public abstract void initialize();

}
