package org.jogger.router;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RoutesParserImplTest {

	@Test
	public void shouldParseInput() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET 	/			MockController#init\n";
		line += 		"POST 	/sessions	MockController#show\n";
		
		List<RouteDefinition> routes = routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
		Assert.assertEquals(routes.size(), 2);
		
	}
	
	@Test
	public void shouldParseInputWithSpacesAndComments() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	" \n";
		line +=			"GET 	/							MockController#init\n";
		line +=			"# this is a comment \n";
		line += 		"POST 	/sessions/{userId}/{hash}	MockController#show\n";
		
		List<RouteDefinition> routes = routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
		Assert.assertEquals(routes.size(), 2);
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidFormat() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"bad format exception";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidHttpMethod() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"BAD 	/			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidPath() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET	invalidpath			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithQuestionMark() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET	/inva?lidpath			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithPoundSign() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET	/inva#lidpath			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithSpace() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET	/inva lidpath			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithSlashInHolder() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET	/{invalid/holder}			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithOpenKeyHolder() throws Exception {
		
		RoutesParserImpl routesParser = new RoutesParserImpl();
		
		// sample routes configuration
		String line = 	"GET	/{invalid{holder}			MockController#init\n";
		
		routesParser.parse(new ByteArrayInputStream(line.getBytes()));
		
	}
	
}
