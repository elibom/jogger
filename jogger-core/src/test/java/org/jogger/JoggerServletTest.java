package org.jogger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import javax.servlet.ServletConfig;

import org.jogger.config.Interceptors;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorExecution;
import org.jogger.router.Route;
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
		
		Route route = new Route("GET", "/", controller, method);
		
		MockJoggerServlet joggerServlet = new MockJoggerServlet();
		joggerServlet.setInterceptors(mock(Interceptors.class));
		
		joggerServlet.service(route, mock(Request.class), mock(Response.class));
		
		verify(controller).show(any(Request.class), any(Response.class));
		
	}
	
	@Test
	public void shouldCallControllerWithInterceptors() throws Exception {
		
		MockController controller = mock(MockController.class);
		Method method = MockController.class.getMethod("show", Request.class, Response.class);
		
		Route route = new Route("GET", "/", controller, method);
		
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
		
		MockJoggerServlet joggerServlet = new MockJoggerServlet();
		joggerServlet.setInterceptors(interceptors);
		
		joggerServlet.service( route, mock(Request.class), mock(Response.class) );
		
		Assert.assertTrue( interceptor1.wasCalled() );
		Assert.assertFalse( interceptor1.getControllerHasAnnotation() );
		Assert.assertFalse( interceptor1.getActionHasAnnotation() );
		Assert.assertTrue( interceptor2.wasCalled() );
		
		verify(controller).show(any(Request.class), any(Response.class));
		
	}
	
	@Test
	public void shouldRetrieveControllerAnnotation() throws Exception {
		
		AnnotatedMockController controller = new AnnotatedMockController();
		Method method = AnnotatedMockController.class.getMethod("action", Request.class, Response.class);
		
		Route route = new Route("GET", "/", controller, method);
		final ProceedInterceptor interceptor = new ProceedInterceptor();
		Interceptors interceptors = new Interceptors() {

			@Override
			public void initialize(ServletConfig servletConfig) {
				add(interceptor);
			}
			
		};
		interceptors.initialize(mock(ServletConfig.class));
		
		MockJoggerServlet joggerServlet = new MockJoggerServlet();
		joggerServlet.setInterceptors(interceptors);
		
		joggerServlet.service( route, mock(Request.class), mock(Response.class) );
		
		Assert.assertTrue( interceptor.getControllerHasAnnotation() );
		Assert.assertFalse( interceptor.getActionHasAnnotation() );
		
	}
	
	@Test
	public void shouldRetrieveActionAnnotation() throws Exception {
		
		AnnotatedActionMockController controller = new AnnotatedActionMockController();
		Method method = AnnotatedActionMockController.class.getMethod("action", Request.class, Response.class);
		
		Route route = new Route("GET", "/", controller, method);
		final ProceedInterceptor interceptor = new ProceedInterceptor();
		Interceptors interceptors = new Interceptors() {

			@Override
			public void initialize(ServletConfig servletConfig) {
				add(interceptor);
			}
			
		};
		interceptors.initialize(mock(ServletConfig.class));
		
		MockJoggerServlet joggerServlet = new MockJoggerServlet();
		joggerServlet.setInterceptors(interceptors);
		
		joggerServlet.service( route, mock(Request.class), mock(Response.class) );
		
		Assert.assertFalse( interceptor.getControllerHasAnnotation() );
		Assert.assertTrue( interceptor.getActionHasAnnotation() );
		
	}
	
	class ProceedInterceptor implements Interceptor {
		
		boolean called = false;
		boolean controllerHasAnnotation = false;
		boolean actionHasAnnotation = false;

		@Override
		public void intercept(Request request, Response response, InterceptorExecution execution) throws Exception {
			
			controllerHasAnnotation = execution.getController().getAnnotation(MockAnnotation.class) != null;
			actionHasAnnotation = execution.getAction().getAnnotation(MockAnnotation.class) != null;
			
			execution.proceed();
			called = true;
			
		}
		
		public boolean wasCalled() {
			return called;
		}

		public boolean getControllerHasAnnotation() {
			return controllerHasAnnotation;
		}
		
		public boolean getActionHasAnnotation() {
			return actionHasAnnotation;
		}
		
	}
	
}
