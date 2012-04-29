package org.jogger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import javax.servlet.ServletConfig;

import org.jogger.config.Interceptors;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.router.RouteInstance;
import org.jogger.router.RouteNotFoundException;
import org.jogger.router.Routes;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class JoggerServletTest {
	
	@BeforeTest
	public void setUp() {
		System.setProperty("JOGGER_ENV", "test");
	}
	
	@AfterTest
	public void tearDown() {
		System.clearProperty("JOGGER_ENV");
	}
	
	@Test
	public void shouldCallController() throws Exception {
		
		MockController controller = mock(MockController.class);
		Method method = MockController.class.getMethod("show", Request.class, Response.class);
		
		Routes routes = mock(Routes.class);
		when(routes.find(any(String.class), any(String.class))).thenReturn(new RouteInstance(controller, method));
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPath()).thenReturn("/users");
		
		JoggerServlet joggerServlet = new JoggerServlet();
		joggerServlet.setRoutes(routes);
		joggerServlet.setInterceptors(mock(Interceptors.class));
		
		joggerServlet.service(request, mock(Response.class));
		
		verify(controller).show(any(Request.class), any(Response.class));
		
	}
	
	@Test
	public void shouldCallControllerWithInterceptors() throws Exception {
		
		MockController controller = mock(MockController.class);
		Method method = MockController.class.getMethod("show", Request.class, Response.class);
		
		Routes routes = mock(Routes.class);
		when(routes.find(any(String.class), any(String.class))).thenReturn(new RouteInstance(controller, method));
		
		final ProceedInterceptor interceptor1 = new ProceedInterceptor();
		final ProceedInterceptor interceptor2 = new ProceedInterceptor();
		Interceptors interceptors = new Interceptors() {

			@Override
			public void initialize(ServletConfig servletConfig) {
				add(interceptor1);
				add(interceptor2);
			}
			
		};
		interceptors.initialize(mock(ServletConfig.class));
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPath()).thenReturn("/users");
		
		JoggerServlet joggerServlet = new JoggerServlet();
		joggerServlet.setRoutes(routes);
		joggerServlet.setInterceptors(interceptors);
		
		joggerServlet.service(request, mock(Response.class));
		
		Assert.assertTrue(interceptor1.wasCalled());
		Assert.assertTrue(interceptor2.wasCalled());
		
		verify(controller).show(any(Request.class), any(Response.class));
		
	}
	
	@Test(expectedExceptions=RouteNotFoundException.class)
	public void shouldFailWithInvalidPath() throws Exception {
		
		Routes routes = mock(Routes.class);
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPath()).thenReturn("/users");
		
		JoggerServlet joggerServlet = new JoggerServlet();
		joggerServlet.setRoutes(routes);
		joggerServlet.setInterceptors(mock(Interceptors.class));
		
		joggerServlet.service(request, mock(Response.class));
		
	}
	
	abstract class MockController {
		
		public abstract void show(Request request, Response response);
		
	}
	
	class ProceedInterceptor implements Interceptor {
		
		boolean called = false;

		@Override
		public void intercept(Request request, Response response, InterceptorChain execution) throws Exception {
			
			execution.proceed();
			called = true;
			
		}
		
		public boolean wasCalled() {
			return called;
		}
		
	}
	
}
