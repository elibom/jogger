package org.jogger.config;

import java.util.List;

import javax.servlet.ServletConfig;

import org.jogger.Interceptor;
import org.jogger.InterceptorChain;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InterceptorsTest {

	@Test
	public void shouldAddGlobalInterceptor() throws Exception {
		
		Interceptors config = new Interceptors() {

			@Override
			public void initialize(ServletConfig servletConfig) {
				add(new MockInterceptor());
			}
			
		};
		config.initialize(null);
		
		List<Interceptor> interceptors = config.getInterceptors("/");
		Assert.assertNotNull(interceptors);
		Assert.assertEquals(interceptors.size(), 1);
		
		interceptors = config.getInterceptors("/some/internal/path");
		Assert.assertNotNull(interceptors);
		Assert.assertEquals(interceptors.size(), 1);
		
	}
	
	@Test
	public void shouldAddInterceptorsWithPaths() throws Exception {
		
		Interceptors config = new Interceptors() {

			@Override
			public void initialize(ServletConfig servletConfig) {
				add(new MockInterceptor(), "/", "/path", "/path/to/route");
			}
			
		};
		config.initialize(null);
		
		List<Interceptor> interceptors = config.getInterceptors("/");
		Assert.assertNotNull(interceptors);
		Assert.assertEquals(interceptors.size(), 1);
		
		interceptors = config.getInterceptors("/path");
		Assert.assertNotNull(interceptors);
		Assert.assertEquals(interceptors.size(), 1);
		
		interceptors = config.getInterceptors("/path/to/route");
		Assert.assertNotNull(interceptors);
		Assert.assertEquals(interceptors.size(), 1);
		
		interceptors = config.getInterceptors("/nonexistent");
		Assert.assertNotNull(interceptors);
		Assert.assertEquals(interceptors.size(), 0);
		
	}
	
	private class MockInterceptor implements Interceptor {

		@Override
		public void intercept(Request request, Response response, InterceptorChain execution) throws Exception {
			
			execution.proceed();
			
		}
		
	}
}
