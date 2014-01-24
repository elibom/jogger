package org.jogger.middleware.router;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jogger.AnnotatedActionMockController;
import org.jogger.AnnotatedMockController;
import org.jogger.MiddlewareChain;
import org.jogger.MockAnnotation;
import org.jogger.MockController;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.middleware.router.Route;
import org.jogger.middleware.router.RouteHandler;
import org.jogger.middleware.router.RouterMiddleware;
import org.jogger.middleware.router.Route.HttpMethod;
import org.jogger.middleware.router.interceptor.Interceptor;
import org.jogger.middleware.router.interceptor.InterceptorExecution;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RouterMiddlewareTest {

	@Test
	public void shouldAddRoutes() throws Exception {
		RouterMiddleware jogger = new RouterMiddleware();

		MockController controller = new MockController();
		Method initMethod = MockController.class.getMethod("init", Request.class, Response.class);
		Method showMethod = MockController.class.getMethod("show", Request.class, Response.class);

		List<Route> routes = new ArrayList<Route>();
		routes.add( new Route(HttpMethod.GET, "/", controller, initMethod) );
		routes.add( new Route(HttpMethod.GET, "/", controller, showMethod) );

		jogger.setRoutes(routes);

		jogger.addRoute(HttpMethod.GET, "/test", controller, "init");
		jogger.addRoute(HttpMethod.GET, "/test", controller, showMethod);

		jogger.get("/another", mock(RouteHandler.class));
		jogger.post("/another", mock(RouteHandler.class));

		Assert.assertEquals(jogger.getRoutes().size(), 6);
	}
	
	@Test(dependsOnMethods="shouldAddRoutes")
	public void shouldMatchEmptyRoute() throws Exception {
		shouldMatchRoute(HttpMethod.GET, "", "");
		shouldMatchRoute(HttpMethod.GET, "", "/");
	}
	
	private void shouldMatchRoute(HttpMethod method, String routePath, String path) throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		RouteHandler handler = mock(RouteHandler.class);
		router.addRoute(method, routePath, handler, "handle");
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(method.toString().toUpperCase());
		when(request.getPath()).thenReturn(path);
		
		router.handle(request, mock(Response.class), mock(MiddlewareChain.class));
		verify(handler).handle(any(Request.class), any(Response.class));
		
		reset(handler);
		
		when(request.getMethod()).thenReturn(method.toString().toLowerCase());
		router.handle(request, mock(Response.class), mock(MiddlewareChain.class));
		verify(handler).handle(any(Request.class), any(Response.class));
	}
	
	@Test(dependsOnMethods="shouldAddRoutes")
	public void shouldMatchSimplePath() throws Exception {
		shouldMatchRoute(HttpMethod.GET, "/test", "/test");
		shouldMatchRoute(HttpMethod.GET, "/test", "/test/");
	}
	
	@Test(dependsOnMethods="shouldAddRoutes")
	public void shouldMatchPathWithVariables() throws Exception {
		shouldMatchRoute(HttpMethod.POST, "/test/{id}/", "/Test/1");
		shouldMatchRoute(HttpMethod.POST, "/test/{id}/", "/test/1-234234/");
	}
	
	@Test(dependsOnMethods="shouldAddRoutes")
	public void shouldMatchComplexPath() throws Exception {
		shouldMatchRoute(HttpMethod.POST, "/test/{id_test}/mocks/{id_mock}", "/test/1/mocks/2");
		shouldMatchRoute(HttpMethod.POST, "/test/{id_test}/mocks/{id_mock}", "/test/1/mocks/2/");
	}
	
	@Test(dependsOnMethods="shouldAddRoutes")
	public void shouldNotFindRouteWithWrongHttpMethod() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		RouteHandler handler = mock(RouteHandler.class);
		router.addRoute(HttpMethod.GET, "", handler, "handle");
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn("post");
		when(request.getPath()).thenReturn("/");
		Response response = mock(Response.class);
		
		router.handle(request, response, mock(MiddlewareChain.class));
		verify(handler, never()).handle(any(Request.class), any(Response.class));
		verify(response, never()).notFound();
		verify(response, never()).status(Response.NOT_FOUND);
	}
	
	@Test(dependsOnMethods="shouldAddRoutes")
	public void shouldNotFindRouteThatMatchesNoPath() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		RouteHandler handler = mock(RouteHandler.class);
		router.addRoute(HttpMethod.GET, "", handler, "handle");
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn("post");
		when(request.getPath()).thenReturn("/");
		Response response = mock(Response.class);
		
		router.handle(request, response, mock(MiddlewareChain.class));
		verify(handler, never()).handle(any(Request.class), any(Response.class));
		verify(response, never()).notFound();
		verify(response, never()).status(Response.NOT_FOUND);
	}
	
	@Test
	public void shouldCallInterceptors() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		RouteHandler handler = mock(RouteHandler.class);
		router.get("/", handler);
		
		ProceedInterceptor interceptor1 = new ProceedInterceptor();
		ProceedInterceptor interceptor2 = new ProceedInterceptor();
		router.addInterceptor(interceptor1);
		router.addInterceptor(interceptor2);
		
		router.handle(mockRequest("get", "/"), mock(Response.class), mock(MiddlewareChain.class));
		
		Assert.assertTrue(interceptor1.wasCalled());
		Assert.assertFalse(interceptor1.getControllerHasAnnotation());
		Assert.assertFalse(interceptor1.getActionHasAnnotation());
		Assert.assertTrue(interceptor2.wasCalled());

		verify(handler).handle(any(Request.class), any(Response.class));
	}
	
	private Request mockRequest(String httpMethod, String path) {
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(httpMethod);
		when(request.getPath()).thenReturn(path);

		return request;
	}
	
	@Test
	public void shouldRetrieveControllerAnnotation() throws Exception {
		AnnotatedMockController controller = new AnnotatedMockController();
		Method method = AnnotatedMockController.class.getMethod("action", org.jogger.http.Request.class, Response.class);
		Route route = new Route(HttpMethod.GET, "/", controller, method);
		
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(route);

		ProceedInterceptor interceptor = new ProceedInterceptor();
		router.addInterceptor(interceptor);
		
		router.handle(mockRequest("get", "/"), mock(Response.class), mock(MiddlewareChain.class));

		Assert.assertTrue(interceptor.getControllerHasAnnotation());
		Assert.assertFalse(interceptor.getActionHasAnnotation());
	}
	
	@Test
	public void shouldRetrieveActionAnnotation() throws Exception {
		AnnotatedActionMockController controller = new AnnotatedActionMockController();
		Method method = AnnotatedActionMockController.class.getMethod("action", org.jogger.http.Request.class, Response.class);
		Route route = new Route(HttpMethod.GET, "/", controller, method);
		
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(route);

		ProceedInterceptor interceptor = new ProceedInterceptor();
		router.addInterceptor(interceptor);
		
		router.handle(mockRequest("get", "/"), mock(Response.class), mock(MiddlewareChain.class));

		Assert.assertFalse(interceptor.getControllerHasAnnotation());
		Assert.assertTrue(interceptor.getActionHasAnnotation());
	}
	
	@Test
	public void shouldRetrieveActionAnnotationInSuperclass() throws Exception {
		AnnotatedActionSubclassMockController controller = new AnnotatedActionSubclassMockController();
		Method method = AnnotatedActionSubclassMockController.class.getMethod("action", org.jogger.http.Request.class, Response.class);
		Route route = new Route(HttpMethod.GET, "/", controller, method);
		
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(route);

		ProceedInterceptor interceptor = new ProceedInterceptor();
		router.addInterceptor(interceptor);
		
		router.handle(mockRequest("get", "/"), mock(Response.class), mock(MiddlewareChain.class));

		Assert.assertFalse(interceptor.getControllerHasAnnotation());
		Assert.assertTrue(interceptor.getActionHasAnnotation());
	}
	
	private class AnnotatedActionSubclassMockController extends AnnotatedActionMockController {
		@Override
		public void action(Request request, Response response) {}
	}

	@Test
	public void shouldAddRouteUsingObjectAndMethod() throws Exception {
		RouterMiddleware router = new RouterMiddleware();

		MockController controller = new MockController();
		Method initMethod = MockController.class.getMethod("init", Request.class, Response.class);

		router.addRoute(HttpMethod.GET, "", controller, initMethod); // also check that empty path maps to root

		Assert.assertEquals(router.getRoutes().size(), 1);

		Route route = router.getRoutes().iterator().next();
		Assert.assertNotNull(route);
		Assert.assertEquals(route.getHttpMethod(), HttpMethod.GET);
		Assert.assertEquals(route.getPath(), "/");
		Assert.assertEquals(route.getController(), controller);
		Assert.assertEquals(route.getAction(), initMethod);
	}

	@Test
	public void shouldAddRouteUsingObjectAndMethodName() throws Exception {
		RouterMiddleware router = new RouterMiddleware();

		MockController controller = new MockController();
		router.addRoute(HttpMethod.GET, "/", controller, "init");

		Assert.assertEquals(router.getRoutes().size(), 1);

		Route route = router.getRoutes().iterator().next();
		Assert.assertNotNull(route);
		Assert.assertEquals(route.getHttpMethod(), HttpMethod.GET);
		Assert.assertEquals(route.getPath(), "/");
		Assert.assertEquals(route.getController(), controller);

		Method method = route.getAction();
		Assert.assertNotNull(method);
		Assert.assertEquals(method.getName(), "init");
	}

	@Test(expectedExceptions=NoSuchMethodException.class)
	public void shouldFailToAddRouteWithInvalidMethodName() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(HttpMethod.GET, "", new MockController(), "invalidMethod");
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddRouteWithNullHttpMethod() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(null, "", new MockController(), "init");
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddRouteWithNullPath() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(null, "", new MockController(), "init");
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddRouteWithNullObject() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(HttpMethod.GET, "", null, "init");
	}

	@Test
	public void shouldAddRouteUsingGetMethod() throws Exception {
		RouterMiddleware router = new RouterMiddleware();

		RouteHandler handler = mock(RouteHandler.class);
		router.get("/", handler);

		Assert.assertEquals(router.getRoutes().size(), 1);

		Route route = router.getRoutes().iterator().next();
		Assert.assertNotNull(route);
		Assert.assertEquals(route.getHttpMethod(), HttpMethod.GET);
		Assert.assertEquals(route.getPath(), "/");
		Assert.assertEquals(route.getController(), handler);

		Method method = route.getAction();
		Assert.assertNotNull(method);
		Assert.assertEquals(method.getName(), "handle");
	}

	@Test
	public void shouldAddRouteUsingPostMethod() throws Exception {
		RouterMiddleware router = new RouterMiddleware();

		RouteHandler handler = mock(RouteHandler.class);
		router.get("/", handler);

		Assert.assertEquals(router.getRoutes().size(), 1);

		Route route = router.getRoutes().iterator().next();
		Assert.assertNotNull(route);
		Assert.assertEquals(route.getHttpMethod(), HttpMethod.GET);
		Assert.assertEquals(route.getPath(), "/");
		Assert.assertEquals(route.getController(), handler);

		Method method = route.getAction();
		Assert.assertNotNull(method);
		Assert.assertEquals(method.getName(), "handle");
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToSetEmptyRoutes() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.setRoutes(null);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddNullRoute() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.addRoute(null);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToSetNullInterceptors() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.setInterceptors(null);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddNullInterceptor() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.addInterceptor(null);
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
