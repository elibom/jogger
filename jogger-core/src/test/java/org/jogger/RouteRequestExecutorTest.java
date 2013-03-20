package org.jogger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.jogger.Route.HttpMethod;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorExecution;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RouteRequestExecutorTest {
	
	@Test
	public void shouldCallAction() throws Exception {
		
		RouteRequestExecutor routeExecutor = new RouteRequestExecutor( new Jogger() );

		// create the route
		MockController controller = mock(MockController.class);
		Route route = new Route(HttpMethod.GET, "/", controller, 
				MockController.class.getMethod("show", Request.class, Response.class));
		
		routeExecutor.execute(route, mockRequest("get", "/"), mock(Response.class));
		
		verify(controller).show(any(Request.class), any(Response.class));
		verify(controller, never()).init(any(Request.class), any(Response.class));
		
	}

	@Test
	public void shouldCallActionWithInterceptors() throws Exception {
		
		Jogger jogger = new Jogger();
		
		MockController controller = mock(MockController.class);
		Route route = new Route(HttpMethod.GET, "/", controller, 
				MockController.class.getMethod("show", Request.class, Response.class));
		
		ProceedInterceptor interceptor1 = new ProceedInterceptor();
		ProceedInterceptor interceptor2 = new ProceedInterceptor();
		jogger.addInterceptor(interceptor1);
		jogger.addInterceptor(interceptor2);
		
		RouteRequestExecutor routeExecutor = new RouteRequestExecutor(jogger);
		routeExecutor.execute(route, mockRequest("get", "/"), mock(Response.class));
		
		Assert.assertTrue( interceptor1.wasCalled() );
		Assert.assertFalse( interceptor1.getControllerHasAnnotation() );
		Assert.assertFalse( interceptor1.getActionHasAnnotation() );
		Assert.assertTrue( interceptor2.wasCalled() );
		
		verify(controller).show(any(Request.class), any(Response.class));
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldFailWithNoRoute() throws Exception {
		
		RouteRequestExecutor routeExecutor = new RouteRequestExecutor( new Jogger() );
		routeExecutor.execute(null, mockRequest("get", "/"), mock(Response.class));
		
	}
	
	@Test
	public void shouldRetrieveControllerAnnotation() throws Exception {
		
		Jogger jogger = new Jogger();
		
		AnnotatedMockController controller = new AnnotatedMockController();
		Method method = AnnotatedMockController.class.getMethod("action", Request.class, Response.class);
		Route route = new Route(HttpMethod.GET, "/", controller, method);
		
		ProceedInterceptor interceptor = new ProceedInterceptor();
		jogger.addInterceptor(interceptor);
		
		RouteRequestExecutor routeExecutor = new RouteRequestExecutor( jogger);
		routeExecutor.execute(route, mockRequest("get", "/"), mock(Response.class));
		
		Assert.assertTrue( interceptor.getControllerHasAnnotation() );
		Assert.assertFalse( interceptor.getActionHasAnnotation() );
		
	}
	
	@Test
	public void shouldRetrieveActionAnnotation() throws Exception {
		
		Jogger jogger = new Jogger();
		
		AnnotatedActionMockController controller = new AnnotatedActionMockController();
		Method method = AnnotatedActionMockController.class.getMethod("action", Request.class, Response.class);
		Route route = new Route(HttpMethod.GET, "/", controller, method);
		
		ProceedInterceptor interceptor = new ProceedInterceptor();
		jogger.addInterceptor(interceptor);
		
		RouteRequestExecutor routeExecutor = new RouteRequestExecutor(jogger);
		routeExecutor.execute(route, mockRequest("get", "/"), mock(Response.class));
		
		Assert.assertFalse( interceptor.getControllerHasAnnotation() );
		Assert.assertTrue( interceptor.getActionHasAnnotation() );
		
	}
	
	@Test
	public void shouldRetrieveActionAnnotationInSuperclass() throws Exception {
		
		Jogger jogger = new Jogger();
		
		AnnotatedActionSubclassMockController controller = new AnnotatedActionSubclassMockController();
		Method method = AnnotatedActionMockController.class.getMethod("action", Request.class, Response.class);
		Route route = new Route(HttpMethod.GET, "/", controller, method);
		
		ProceedInterceptor interceptor = new ProceedInterceptor();
		jogger.addInterceptor(interceptor);
		
		RouteRequestExecutor routeExecutor = new RouteRequestExecutor(jogger);
		routeExecutor.execute(route, mockRequest("get", "/"), mock(Response.class));
		
		Assert.assertFalse( interceptor.getControllerHasAnnotation() );
		Assert.assertTrue( interceptor.getActionHasAnnotation() );
		
	}
	
	private class AnnotatedActionSubclassMockController extends AnnotatedActionMockController {
		@Override
		public void action(Request request, Response response) {}
	}
	
	private Request mockRequest(String httpMethod, String path) {
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(httpMethod);
		when(request.getPath()).thenReturn(path);
		
		return request;
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
