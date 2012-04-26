package org.jogger.router;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.List;

import org.jogger.Controller;
import org.jogger.config.ControllerLoader;
import org.jogger.router.Route;
import org.jogger.router.RoutesException;
import org.jogger.router.RoutesParserImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RoutesParserImplTest {

	@Test
	public void shouldParseInput() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("MockController")).thenReturn(new MockController());
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	"GET 	/			MockController#init\n";
		line += 		"POST 	/sessions	MockController#show\n";
		
		List<Route> routes = routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
		Assert.assertEquals(routes.size(), 2);
		
	}
	
	@Test
	public void shouldParseInputWithSpacesAndComments() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("MockController")).thenReturn(new MockController());
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	" \n";
		line +=			"GET 	/			MockController#init\n";
		line +=			"# this is a comment \n";
		line += 		"POST 	/sessions	MockController#show\n";
		
		List<Route> routes = routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
		Assert.assertEquals(routes.size(), 2);
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidFormat() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	"bad format exception";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidHttpMethod() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	"BAD 	/			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidPath() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	"GET	invalidpath			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=RoutesException.class)
	public void shouldFailWithNonExistentController() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	"GET 	/			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=RoutesException.class)
	public void shouldFailWithNonExistentMethod() throws Exception {
		
		ControllerLoader controllerLoader = mock(ControllerLoader.class);
		when(controllerLoader.load("MockController")).thenReturn(new MockController());
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		routesParser.setControllerLoader(controllerLoader);
		
		// sample routes configuration
		String line = 	"GET 	/			MockController#nonExistent\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	private class MockController extends Controller {
		
		@SuppressWarnings("unused")
		public void init() {}
		
		@SuppressWarnings("unused")
		public void show() {}
		
	}
	
}
