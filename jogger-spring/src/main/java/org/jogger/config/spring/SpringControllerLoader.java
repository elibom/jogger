package org.jogger.config.spring;

import javax.servlet.ServletConfig;

import org.jogger.Controller;
import org.jogger.config.ControllerLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringControllerLoader implements ControllerLoader {
	
	private WebApplicationContext applicationContext;
	
	@Override
	public void init(ServletConfig servletConfig) {
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
	}

	@Override
	public Controller load(String controllerName) throws Exception {
		
		boolean exists = applicationContext.containsBean(controllerName);
		if (!exists) {
			throw new Exception("No Spring bean '" + controllerName + "' was not found.");
		}
		
		boolean isPrototype = applicationContext.isPrototype(controllerName);
		if (!isPrototype) {
			throw new Exception("Spring bean '" + controllerName + "' exists but its scope is not 'prototype'");
		}
		
		return applicationContext.getBean(controllerName, Controller.class);
	}

}
