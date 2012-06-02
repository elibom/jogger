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
		
		return new RunnableMockRequest(joggerServlet, "GET", url);
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
		
		return new RunnableMockRequest(joggerServlet, "POST", url);
	}

	/**
	 * Retrieves the mock servlet that we are going to use to process the request.
	 * 
	 * @return an initialized {@link MockJoggerServlet} object.
	 */
	protected MockJoggerServlet getJoggerServlet() {
		return new MockJoggerServlet();
	}
	
	class RunnableMockRequest extends MockRequest {
		
		private MockJoggerServlet joggerServlet;

		public RunnableMockRequest(MockJoggerServlet joggerServlet, String method, String url) throws URISyntaxException {
			super(method, url);
			this.joggerServlet = joggerServlet;
			
		}
		
		public MockResponse run() throws Exception {
			
			// mock the response and call the JoggerServlet
			MockResponse response = new MockResponse(joggerServlet.getFreeMarkerConfig());
			joggerServlet.service(this, response);
			
			return response;
			
		}
		
	}
	 
}
