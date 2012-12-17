package org.jogger.test;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jogger.Jogger;
import org.jogger.RouteExecutor;
import org.jogger.http.AbstractRequest;
import org.jogger.http.Cookie;
import org.jogger.http.FileItem;
import org.jogger.http.Request;
import org.jogger.http.Value;

/**
 * This is a {@link Request} implementation that stores the request state in attributes. Useful for testing Jogger 
 * without a Servlet Container. 
 * 
 * @author German Escobar
 */
public class MockRequest extends AbstractRequest {
	
	private String host;
	
	private String path;
	
	private String queryString;
	
	private Map<String,Value> params;
	
	private String url;
	
	private String method;
	
	private String remoteAddress = "localhost";
	
	private String contentType = "text/html";
	
	private int port;
	
	private boolean secure = false;
	
	private Map<String,Cookie> cookies = new HashMap<String,Cookie>();
	
	private Map<String,String> headers = new HashMap<String,String>();
	
	private List<FileItem> files = new ArrayList<FileItem>();
	
	private String body;
	
	private MockResponse response;
	
	private RouteExecutor routeExecutor;
	
	public MockRequest(Jogger jogger, String method, String url) throws URISyntaxException {

		this.response = new MockResponse(jogger.getTemplateEngine());
		this.routeExecutor = new RouteExecutor(jogger);
		
		this.method = method;
		
		URI uri = new URI(url);
		this.host = uri.getHost();
		this.path = uri.getPath();
		this.queryString = uri.getQuery();
		this.params = buildParams( queryString );
		this.port = uri.getPort() > 0 ? uri.getPort() : 80;
		
		if ( uri.getScheme().equals("https") ) {
			this.secure = true;
		}
		
		String strPort = uri.getPort() > 0 ? ":" + uri.getPort() : "";
		this.url = uri.getScheme() + "://" + uri.getHost() + strPort  + "/" + uri.getPath();
		
	}
	
	private Map<String,Value> buildParams(String queryString) {
		
		Map<String,Value> ret = new HashMap<String,Value>();
		
		if (queryString == null) {
			return ret;
		}
		
		String[] elems = queryString.split("&");
		for (String elem : elems ) {
			String[] pair = elem.split("=");
			ret.put( pair[0], new Value(pair[1]) );
		}
		
		return ret;
		
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
	public Map<String, Value> getParameters() {
		return params;
	}

	@Override
	public Value getParameter(String name) {
		return params.get(name);
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
	
	public MockRequest ajax() {
		headers.put("x-requested-with", "XMLHttpRequest");
		return this;
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
	
	public MockRequest setHeaders(Map<String, String> headers) {
		this.headers = headers;
		return this;
    }

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public MockRequest setHeader(String name, String value) {
		headers.put(name, value);
		return this;
	}

	@Override
	public FileItem[] getFiles() {
		return files.toArray( new FileItem[0] );
	}
	
	public MockRequest addFile(File file, String fileName, String contentType) {
		String fieldName = "file" + files.size();
		files.add( new FileItem(fieldName, fileName, contentType, 0, file, new HashMap<String,String>()) );
		
		return this;
	}

	@Override
	public BodyParser getBody() {
		return new BodyParser() {

			@Override
			public String asString() {
				return body;
			}

			@Override
			public InputStream asInputStream() {
				return null;
			}
			
		};
	}
	
	public MockRequest setBodyAsString(String body) {
		this.body = body;
		
		return this;
	}
	
	public MockResponse run() throws Exception {
		// execute request
		routeExecutor.route(getMethod(), getPath(), this, response);
		return response;
		
	}
	
}
