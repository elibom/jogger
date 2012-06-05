package org.jogger.router;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Collections;

import org.jogger.config.ControllerLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RoutesImplTest {

	@Test
	public void shouldFindRoute() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("users")).thenReturn(new MockController());
		
		RouteDefinition routeDefinition = new RouteDefinition("GET", "/users", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(routeDefinition));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
		Route route = routes.find("GET", "/users");
		Assert.assertNotNull(route);
		Assert.assertNotNull(route.getController());
		Assert.assertNotNull(route.getAction());
		
	}
	
	@Test
	public void shouldFindRouteWithPathVariables() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("users")).thenReturn(new MockController());
		
		RouteDefinition routeDefinition = new RouteDefinition("GET", "/users/{userId}/{hash}", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(routeDefinition));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
		Route route = routes.find("GET", "/users/34/iuue-28484");
		Assert.assertNotNull(route);
		Assert.assertNotNull(route.getController());
		Assert.assertNotNull(route.getAction());
		
	}
	
	@Test
	public void shouldNotFindRoute() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("users")).thenReturn(new MockController());
		
		RouteDefinition route = new RouteDefinition("GET", "/users", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(route));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
		Route routeInstance = routes.find("GET", "/non-existent");
		Assert.assertNull(routeInstance);
	}
	
	@Test
	public void shouldNotFindRouteWithPathVariable() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("users")).thenReturn(new MockController());
		
		RouteDefinition routeDefinition = new RouteDefinition("GET", "/users/{userId}", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(routeDefinition));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
		Route route = routes.find("GET", "/users/34/test");
		Assert.assertNull(route);
		
	}
	
	@Test(expectedExceptions=RoutesException.class)
	public void shouldFailIfControllerNotFound() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		
		RouteDefinition route = new RouteDefinition("GET", "/users", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(route));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
	}
	
	@Test(expectedExceptions=RoutesException.class)
	public void shouldFailIfMethodNotFound() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("users")).thenReturn(new MockController());
		
		RouteDefinition route = new RouteDefinition("GET", "/users", "users", "test");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(route));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
	}
	
	class MockController {
		
		public void show(Request request, Response response) {}
		
	}
	
}
