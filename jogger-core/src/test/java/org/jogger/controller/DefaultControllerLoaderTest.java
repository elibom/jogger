package org.jogger.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;

import org.jogger.config.ConfigurationException;
import org.jogger.config.DefaultControllerLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DefaultControllerLoaderTest {

	@Test
	public void shouldLoadController() throws Exception {
		
		ClassLoader classLoader = mockClassLoader("org.jogger.controller.MockController");
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		when(servletConfig.getInitParameter(DefaultControllerLoader.BASE_PACKAGE_INIT_PARAM_NAME))
			.thenReturn("org.jogger.controller");
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.setClassLoader(classLoader);
		controllerLoader.init(servletConfig);
		
		Object controller = controllerLoader.load("MockController");
		
		Assert.assertNotNull(controller);
	}
	
	@Test
	public void shouldLoadControllerWithoutBasePackage() throws Exception {
		
		ClassLoader classLoader = mockClassLoader("MockController");
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.setClassLoader(classLoader);
		controllerLoader.init(servletConfig);
		
		Object controller = controllerLoader.load("MockController");
		
		Assert.assertNotNull(controller);
		
	}
	
	@Test(expectedExceptions=ConfigurationException.class)
	public void shouldFailLoadingNotExistingController() throws Exception {
		
		ServletConfig servletConfig = mock(ServletConfig.class);
		
		DefaultControllerLoader controllerLoader = new DefaultControllerLoader();
		controllerLoader.init(servletConfig);
		
		controllerLoader.load("NotExistingController");
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ClassLoader mockClassLoader(String className) throws ClassNotFoundException {
		
		ClassLoader classLoader = mock(ClassLoader.class);
		when(classLoader.loadClass(className)).thenReturn((Class) MockController.class);
		
		return classLoader;
		
	}
	
}
