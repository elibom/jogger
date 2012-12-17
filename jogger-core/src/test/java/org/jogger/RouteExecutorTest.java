package org.jogger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.jogger.Route.HttpMethod;
import org.jogger.http.AbstractRequest;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.interceptor.Interceptor;
import org.jogger.interceptor.InterceptorExecution;
import org.testng.annotations.Test;

public class RouteExecutorTest {
	
	@Test
	public void shouldCallAction() throws Exception {
		
		Jogger jogger = new Jogger();
		
		MockController controller = mock(MockController.class);
		jogger.addRoute(HttpMethod.GET, "/", controller, "show");
		
		RouteExecutor routeExecutor = new RouteExecutor(jogger);
		routeExecutor.route("get", "/", mock(AbstractRequest.class), mock(Response.class));
		
		verify(controller).show(any(Request.class), any(Response.class));
		verify(controller, never()).init(any(Request.class), any(Response.class));
		
	}
	
	@Test
	public void shouldCallActionWithInterceptors() throws Exception {
		
	}
	
	@Test
	public void shouldRetrieveControllerAnnotation() throws Exception {
		
	}
	
	@Test
	public void shouldRetrieveActionAnnotation() throws Exception {
		
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
