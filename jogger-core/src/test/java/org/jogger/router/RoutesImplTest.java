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
		
		Route route = new Route("GET", "/users", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(route));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
		RouteInstance routeInstance = routes.find("GET", "/users");
		Assert.assertNotNull(routeInstance);
		Assert.assertNotNull(routeInstance.getController());
		Assert.assertNotNull(routeInstance.getMethod());
		
	}
	
	@Test
	public void shouldNotFindRoute() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("users")).thenReturn(new MockController());
		
		Route route = new Route("GET", "/users", "users", "show");
		
		RoutesParser routesParser = mock(RoutesParser.class);
		when(routesParser.parse(any(InputStream.class))).thenReturn(Collections.singletonList(route));
		
		RoutesImpl routes = new RoutesImpl();
		routes.setControllerLoader(controllerLoader);
		routes.setRoutesParser(routesParser);
		
		routes.load(null);
		
		RouteInstance routeInstance = routes.find("GET", "/non-existent");
		Assert.assertNull(routeInstance);
	}
	
	@Test(expectedExceptions=RoutesException.class)
	public void shouldFailIfControllerNotFound() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		
		Route route = new Route("GET", "/users", "users", "show");
		
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
		
		Route route = new Route("GET", "/users", "users", "test");
		
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
