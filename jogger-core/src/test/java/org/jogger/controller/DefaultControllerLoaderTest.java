package org.jogger.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;

import org.jogger.Controller;
import org.jogger.config.DefaultControllerLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DefaultControllerLoaderTest {

	@Test
	public void shouldLoadController() throws Exception {
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		when(servletConfig.getInitParameter(DefaultControllerLoader.BASE_PACKAGE_INIT_PARAM_NAME))
			.thenReturn("org.jogger.controller");
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.init(servletConfig);
		
		Controller controller = controllerLoader.load("MockController");
		
		Assert.assertNotNull(controller);
	}
	
	@Test
	public void shouldLoadControllerWithoutBasePackage() throws Exception {
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.init(servletConfig);
		
		Controller controller = controllerLoader.load("MockController");
		
		Assert.assertNotNull(controller);
		
	}
	
	@Test(expectedExceptions=ClassNotFoundException.class)
	public void shouldFailLoadingNotExistingController() throws Exception {
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.init(servletConfig);
		
		controllerLoader.load("NotExistingController");
		
	}
	
}
