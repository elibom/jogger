package org.jogger.config.spring;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import junit.framework.Assert;

import org.jogger.interceptor.Interceptor;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

public class SpringInterceptorsTest {

	@Test
	public void shouldAddInterceptors() throws Exception {
		
		Interceptor interceptor = mock(Interceptor.class);
		
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		when(applicationContext.getBean( any(String.class) )).thenReturn(interceptor);
		
		SpringInterceptors interceptors = new SpringInterceptors() {

			@Override
			public void initialize() {
				add("test-1");
				add("test-2");
			}
			
		};
		interceptors.setApplicationContext(applicationContext);
		
		interceptors.initialize();
		
		List<Interceptor> requestInterceptors = interceptors.getInterceptors("/");
		Assert.assertNotNull(requestInterceptors);
		Assert.assertEquals(requestInterceptors.size(), 2);
		
	}
	
}
