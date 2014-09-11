package com.elibom.jogger.test;

import static com.elibom.jogger.http.Http.Headers.CONTENT_TYPE;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elibom.jogger.Jogger;
import com.elibom.jogger.http.AbstractRequest;
import com.elibom.jogger.http.Cookie;
import com.elibom.jogger.http.FileItem;
import com.elibom.jogger.middleware.router.Route;
import com.elibom.jogger.util.Preconditions;

/**
 * This is a {@link com.elibom.jogger.http.Request} implementation that stores the request state in attributes. Useful for testing Jogger
 * without a Servlet Container.
 *
 * @author German Escobar
 */
public class MockRequest extends AbstractRequest {

	private String host;

	private String path;

	private String queryString;

	private Map<String,String> params;

	private String url;

	private String method;

	private String remoteAddress = "localhost";

	private int port;

	private boolean secure = false;

	private Map<String,Cookie> cookies = new HashMap<String,Cookie>();

	private Map<String,String> headers = new HashMap<String,String>();

	private List<FileItem> files = new ArrayList<FileItem>();

	private String body;

	private MockResponse response;
	
	private Jogger jogger;

	public MockRequest(Jogger jogger, String method, String url) throws URISyntaxException {
		this.jogger = jogger;
		this.response = new MockResponse(jogger.getTemplateEngine());

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

	private Map<String,String> buildParams(String queryString) {
		Map<String,String> ret = new HashMap<String,String>();

		if (queryString == null) {
			return ret;
		}

		String[] elems = queryString.split("&");
		for (String elem : elems ) {
			String[] pair = elem.split("=");
			ret.put( pair[0], pair[1] );
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
	public Map<String, String> getParameters() {
		return params;
	}

	@Override
	public String getParameter(String name) {
		return params.get(name);
	}

	public MockRequest addParameter(String name, String value) {
		params.put(name, value);
		return this;
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
		return headers.get(CONTENT_TYPE);
	}

	public MockRequest withContentType(String contentType) {
		headers.put(CONTENT_TYPE, contentType);
		return this;
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

	@Override
	public void setRoute(Route route) {
		Preconditions.notNull(route, "no route provided.");
		this.route = route;
		
		this.initPathVariables(route.getPath());
	}

	public MockRequest setBodyAsString(String body) {
		this.body = body;
		return this;
	}

	public MockResponse run() throws Exception {
		// execute request
		jogger.handle(this, response);
		return response;

	}

}
