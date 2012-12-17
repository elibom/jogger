package org.jogger.router;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.util.List;

import org.jogger.Route;
import org.jogger.routes.FileRoutesLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileRoutesLoaderTest {

	@Test
	public void shouldParseInput() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader("org.jogger");
		
		// sample routes configuration
		String line = 	"GET 	/			MockController#init\n";
		line += 		"POST 	/sessions	MockController#show\n";
		
		List<Route> routes = routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
		Assert.assertEquals(routes.size(), 2);
		
	}
	
	@Test
	public void shouldParseInputWithSpacesAndComments() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader("org.jogger.");
		
		// sample routes configuration
		String line = 	" \n";
		line +=			"GET 	/							MockController#init\n";
		line +=			"# this is a comment \n";
		line += 		"POST 	/sessions/{userId}/{hash}	MockController#show\n";
		
		List<Route> routes = routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
		Assert.assertEquals(routes.size(), 2);
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidFormat() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"bad format exception";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidHttpMethod() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"BAD 	/			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidPath() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"GET	invalidpath			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithQuestionMark() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"GET	/inva?lidpath			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithPoundSign() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"GET	/inva#lidpath			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithSpace() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"GET	/inva lidpath			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithSlashInHolder() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"GET	/{invalid/holder}			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithOpenKeyHolder() throws Exception {
		
		FileRoutesLoader routesLoader = new FileRoutesLoader();
		
		// sample routes configuration
		String line = 	"GET	/{invalid{holder}			MockController#init\n";
		
		routesLoader.load(new ByteArrayInputStream(line.getBytes()));
		
	}
	
}
