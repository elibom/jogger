package com.elibom.jogger.router;

import java.text.ParseException;
import java.util.List;

import com.elibom.jogger.middleware.router.Route;
import com.elibom.jogger.middleware.router.loader.FileSystemRoutesLoader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FileSystemRoutesLoaderTest {

	@Test
	public void shouldParseInput() throws Exception {
		// prepare
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-1.config");
		routesLoader.setBasePackage("com.elibom.jogger");

		// execute
		List<Route> routes = routesLoader.load();

		// validate
		Assert.assertNotNull(routes);
		Assert.assertEquals(routes.size(), 2);
	}

	@Test
	public void shouldParseInputWithSpacesAndComments() throws Exception {
		// prepare
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-2.config");
		routesLoader.setBasePackage("com.elibom.jogger.");

		// execute
		List<Route> routes = routesLoader.load();

		// validate
		Assert.assertNotNull(routes);
		Assert.assertEquals(routes.size(), 2);
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidFormat() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-3.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidHttpMethod() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-4.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithInvalidPath() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-5.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithQuestionMark() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-6.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithPoundSign() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-7.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithSpace() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-8.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithSlashInHolder() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-8.config");
		routesLoader.load();
	}

	@Test(expectedExceptions=ParseException.class)
	public void shouldFailWithOpenKeyHolder() throws Exception {
		FileSystemRoutesLoader routesLoader = new FileSystemRoutesLoader("src/test/resources/routes/routes-10.config");
		routesLoader.load();
	}

}
