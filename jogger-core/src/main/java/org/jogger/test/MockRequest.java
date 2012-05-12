package org.jogger.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.jogger.http.Cookie;
import org.jogger.http.Request;

/**
 * This is a {@link Request} implementation that stores the request state in attributes. Useful for testing Jogger 
 * without a Servlet Container. 
 * 
 * @author German Escobar
 */
public class MockRequest implements Request {
	
	private String host;
	
	private String path;
	
	private String queryString;
	
	private String url;
	
	private String method;
	
	private String remoteAddress = "localhost";
	
	private String contentType = "text/html";
	
	private int port;
	
	private boolean secure = false;
	
	private Map<String,Cookie> cookies = new HashMap<String,Cookie>();
	
	private Map<String,String> headers = new HashMap<String,String>();
	
	private MockJoggerServlet joggerServlet;
	
	public MockRequest(MockJoggerServlet joggerServlet, String method, String url) throws URISyntaxException {
		
		this.joggerServlet = joggerServlet;
		this.method = method;
		
		URI uri = new URI(url);
		this.host = uri.getHost();
		this.path = uri.getPath();
		this.queryString = uri.getQuery();
		this.port = uri.getPort() > 0 ? uri.getPort() : 80;
		
		if ( uri.getScheme().equals("https") ) {
			this.secure = true;
		}
		
		String strPort = uri.getPort() > 0 ? ":" + uri.getPort() : "";
		this.url = uri.getScheme() + "://" + uri.getHost() + strPort  + "/" + uri.getPath();
		
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public boolean isAjax() {
		if (headers.get("x-requested-with") == null) {
            return false;
        }
        return "XMLHttpRequest".equals(headers.get("x-requested-with"));
	}
	
	public MockRequest addCookie(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
		
		return this;
	}

	@Override
	public Map<String, Cookie> getCookies() {
		return cookies;
	}

	@Override
	public Cookie getCookie(String name) {
		return cookies.get(name);
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public BodyParser getBody() {
		return null;
	}

	public MockResponse run() throws Exception {
		
		// mock the response and call the JoggerServlet
		MockResponse response = new MockResponse(joggerServlet.getFreeMarkerConfig());
		joggerServlet.service(this, response);
		
		return response;
		
	}
}
