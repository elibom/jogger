package org.jogger;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jogger.Route.HttpMethod;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JoggerTest {

	@Test
	public void shouldAddRoutes() throws Exception {
		
		Jogger jogger = new Jogger();
		
		MockController controller = new MockController();
		Method initMethod = MockController.class.getMethod("init", Request.class, Response.class);
		Method showMethod = MockController.class.getMethod("init", Request.class, Response.class);
		
		List<Route> routes = new ArrayList<Route>();
		routes.add( new Route(HttpMethod.GET, "/", controller, initMethod) );
		routes.add( new Route(HttpMethod.GET, "/", controller, showMethod) );
		
		jogger.setRoutes(routes);
		
		jogger.addRoute(HttpMethod.GET, "/test", controller, "init");
		jogger.addRoute(HttpMethod.GET, "/test", controller, showMethod);
		
		jogger.get("/another", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {}
		});
		jogger.post("/another", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {}
		});
		
		Assert.assertEquals(jogger.getRoutes().size(), 6);
		
	}
	
	@Test
	public void shouldAddRouteUsingObjectAndMethod() throws Exception {
		
		Jogger jogger = new Jogger();
		
		MockController controller = new MockController();
		Method initMethod = MockController.class.getMethod("init", Request.class, Response.class);
		
		jogger.addRoute(HttpMethod.GET, "", controller, initMethod); // also check that empty path maps to root
		
		Assert.assertEquals(jogger.getRoutes().size(), 1);
		
		Route route = jogger.getRoutes().iterator().next();
		Assert.assertNotNull(route);
		Assert.assertEquals(route.getHttpMethod(), HttpMethod.GET);
		Assert.assertEquals(route.getPath(), "/");
		Assert.assertEquals(route.getController(), controller);
		Assert.assertEquals(route.getAction(), initMethod);
		
	}
	
	@Test
	public void shouldAddRouteUsingObjectAndMethodName() throws Exception {

		Jogger jogger = new Jogger();
		
		MockController controller = new MockController();
		jogger.addRoute(HttpMethod.GET, "/", controller, "init");
		
		Assert.assertEquals(jogger.getRoutes().size(), 1);
		
		Route route = jogger.getRoutes().iterator().next();
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
		Jogger jogger = new Jogger();
		jogger.addRoute(HttpMethod.GET, "", new MockController(), "invalidMethod");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddRouteWithNullHttpMethod() throws Exception {
		Jogger jogger = new Jogger();
		jogger.addRoute(null, "", new MockController(), "init");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddRouteWithNullPath() throws Exception {
		Jogger jogger = new Jogger();
		jogger.addRoute(null, "", new MockController(), "init");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddRouteWithNullObject() throws Exception {
		Jogger jogger = new Jogger();
		jogger.addRoute(HttpMethod.GET, "", null, "init");
	}
	
	@Test
	public void shouldAddRouteUsingGetMethod() throws Exception {

		Jogger jogger = new Jogger();
		
		RouteHandler handler = mock(RouteHandler.class);
		jogger.get("/", handler);
		
		Assert.assertEquals(jogger.getRoutes().size(), 1);
		
		Route route = jogger.getRoutes().iterator().next();
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
		
		Jogger jogger = new Jogger();
		
		RouteHandler handler = mock(RouteHandler.class);
		jogger.get("/", handler);
		
		Assert.assertEquals(jogger.getRoutes().size(), 1);
		
		Route route = jogger.getRoutes().iterator().next();
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
		Jogger jogger = new Jogger();
		jogger.setRoutes(null);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddNullRoute() throws Exception {
		Jogger jogger = new Jogger();
		jogger.addRoute(null);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToSetNullInterceptors() throws Exception {
		Jogger jogger = new Jogger();
		jogger.setInterceptors(null);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToAddNullInterceptor() throws Exception {
		Jogger jogger = new Jogger();
		jogger.addInterceptor(null);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToSetNullAssetLoader() throws Exception {
		Jogger jogger = new Jogger();
		jogger.setAssetLoader(null);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailToSetNullTemplateEngine() throws Exception {
		Jogger jogger = new Jogger();
		jogger.setTemplateEngine(null);
	}
	
}
