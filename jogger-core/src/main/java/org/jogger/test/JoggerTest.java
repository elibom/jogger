package org.jogger.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.jogger.Jogger;
import org.jogger.Route;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This is an utility class that you can extend for testing Jogger applications. It provides methods
 * to emulate requests some abstract methods that you need to implement.
 *
 * @author German Escobar
 */
public abstract class JoggerTest {

	@BeforeSuite
	public void init() {
		System.setProperty("JOGGER_ENV", "test");
	}

	@AfterSuite
	public void destroy() {
		System.clearProperty("JOGGER_ENV");
	}

	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'GET' as the HTTP method. Call the
	 * {@link MockRequest#run()} method to execute the request.
	 *
	 * @param path the path of the GET request.
	 *
	 * @return a {@link MockRequest} object.
	 */
	public MockRequest get(String path) throws Exception {
		return service("GET", path);
	}

	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'POST' as the HTTP method. Call the
	 * {@link MockRequest#run()} method to execute the request.
	 *
	 * @param path the path of the POST request.
	 *
	 * @return a {@link MockRequest} object.
	 */
	public MockRequest post(String path) throws Exception {
		return service("POST", path);
	}

	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'DELETE' as the HTTP method. Call the
	 * {@link MockRequest#run()} method to execute the request.
	 *
	 * @param path the path of the DELETE request
	 *
	 * @return a {@link MockRequest} object.
	 * @throws Exception
	 */
	public MockRequest delete(String path) throws Exception {
		return service("DELETE", path);
	}

	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'PUT' as the HTTP method. Call the
	 * {@link MockRequest#run()} method to execute the request.
	 *
	 * @param path the path of the PUT request
	 *
	 * @return a {@link MockRequest} object.
	 * @throws Exception
	 */
	public MockRequest put(String path) throws Exception {
		return service("PUT", path);
	}

	/**
	 * Helper method. Builds a {@link MockRequest} based on the received arguments.
	 *
	 * @param httpMethod the HTTP method.
	 * @param path the requested path.
	 *
	 * @return a {@link MockRequest} object.
	 * @throws Exception
	 */
	private MockRequest service(String httpMethod, String path) throws Exception {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		String url = "http://localhost" + path;
		Route route = getJogger().getRoute(httpMethod, fixPath(path));
		return new MockRequest(getJogger(), route, httpMethod, url);
	}

	/**
	 * Retrieves the path by removing the query string, hash tags, etc from the <code>path</code>.
	 *
	 * @param path the path that we will fix
	 *
	 * @return a String
	 * @throws URISyntaxException
	 */
	private String fixPath(String path) throws URISyntaxException {
		URI uri = new URI(path);
		return uri.getPath();
	}

	/**
	 * Retrieves the {@link Jogger} object used to configure the application.
	 *
	 * @return the {@link Jogger} object.
	 * @throws Exception
	 */
	protected abstract Jogger getJogger() throws Exception;

}
