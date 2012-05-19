package org.jogger.test;

import java.net.URISyntaxException;

/**
 * This is an utility class that you can extend when testing Jogger applications. It provides methods
 * to emulate requests and a {@link #getJoggerServlet()} method to configure the {@link MockJoggerServlet}.
 * 
 * @author German Escobar
 */
public class JoggerTest {
	
	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'GET' as the HTTP method. Call the 
	 * {@link MockRequest#run()} method to execute the request. 
	 * 
	 * @param path the path of the GET request.
	 * 
	 * @return a {@link MockRequest} object.
	 */
	public MockRequest get(String path) throws URISyntaxException {
		
		MockJoggerServlet joggerServlet = getJoggerServlet();
		String url = "http://localhost" + path;
		
		return new MockRequest(joggerServlet, "GET", url);
	}
	
	/**
	 * Helper method. Creates and returns a {@link MockRequest} with 'POST' as the HTTP method. Call the 
	 * {@link MockRequest#run()} method to execute the request. 
	 * 
	 * @param path the path of the POST request.
	 * 
	 * @return a {@link MockRequest} object.
	 */
	public MockRequest post(String path) throws URISyntaxException {
		
		MockJoggerServlet joggerServlet = getJoggerServlet();
		String url = "http://localhost" + path;
		
		return new MockRequest(joggerServlet, "POST", url);
	}

	/**
	 * Retrieves the mock servlet that we are going to use to process the request.
	 * 
	 * @return an initialized {@link MockJoggerServlet} object.
	 */
	protected MockJoggerServlet getJoggerServlet() {
		return new MockJoggerServlet();
	}
	 
}
