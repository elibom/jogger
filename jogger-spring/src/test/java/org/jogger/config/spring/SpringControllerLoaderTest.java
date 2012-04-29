package org.jogger.config.spring;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.jogger.config.ConfigurationException;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

public class SpringControllerLoaderTest {

	@Test
	public void shouldLoadController() throws Exception {
		
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		when(applicationContext.containsBean("test")).thenReturn(true);
		when(applicationContext.getBean("test")).thenReturn(new Object());
		
		SpringControllerLoader controllerLoader = new SpringControllerLoader();
		controllerLoader.setApplicationContext(applicationContext);
		
		Object object = controllerLoader.load("test");
		Assert.assertNotNull(object);
		
	}
	
	@Test(expectedExceptions=ConfigurationException.class)
	public void shouldFailLoadingNonExistingController() throws Exception {
		
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		
		SpringControllerLoader controllerLoader = new SpringControllerLoader();
		controllerLoader.setApplicationContext(applicationContext);
		
		controllerLoader.load("test");
		
	}
}
