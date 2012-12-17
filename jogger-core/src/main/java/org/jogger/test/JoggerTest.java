package org.jogger.test;

import org.jogger.Jogger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This is an utility class that you can extend when testing Jogger applications. It provides methods
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
	 * Helper method. Builds a {@link MockRequest} based on the received arguments.
	 * 
	 * @param httpMethod the HTTP method.
	 * @param path the requested path.
	 * 
	 * @return a {@link MockRequest} object.
	 * @throws Exception
	 */
	private MockRequest service(String httpMethod, String path) throws Exception {
		String url = "http://localhost" + path;
		return new MockRequest(getJogger(), httpMethod, url);
	}

	protected abstract Jogger getJogger();
	
}
