package org.jogger.http.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jogger.Route;
import org.jogger.http.AbstractRequest;
import org.jogger.http.Cookie;
import org.jogger.http.FileItem;
import org.jogger.http.HttpException;
import org.jogger.http.Request;
import org.jogger.http.servlet.multipart.Multipart;
import org.jogger.http.servlet.multipart.MultipartException;
import org.jogger.http.servlet.multipart.PartHandler;

/**
 * A {@link Request} implementation based on the Servlet API.
 * 
 * @author German Escobar
 */
public class ServletRequest extends AbstractRequest {
	
	/**
	 * The underlying Servlet Request.
	 */
	private HttpServletRequest request;
	
	private Map<String,String> parameters = new HashMap<String,String>();
	
	private List<FileItem> files = new ArrayList<FileItem>();
	
	/**
	 * Constructor.
	 * 
	 * @param route the {@link Route} object which can be null if there is no route for this request.
	 * @param request the Servlet request object.
	 */
	public ServletRequest(Route route, HttpServletRequest request) {
		super(route);
		this.request = request;
		
	}
	
	/**
	 * Initializes the path variables and the multipart content.
	 * 
	 * @throws FileUploadException if there is a problem parsing the multipart/form-data.
	 * @throws IOException if there is a problem parsing the multipart/form-data.
	 */
	public ServletRequest init() throws MultipartException, IOException {
		
		if (getRoute() != null) {
			initPathVariables( getRoute().getPath() );
		}
		
		// retrieve query and post params
		Map<String,String[]> requestParams = request.getParameterMap();
		for (Map.Entry<String,String[]> entry : requestParams.entrySet()) {
			parameters.put( entry.getKey(), join(entry.getValue()) );
		}
		
		// retrieve multipart/form-data parameters
		if (Multipart.isMultipartContent(request)) {
			
			Multipart multipart = new Multipart();
			multipart.parse(request, new PartHandler() {

				@Override
				public void handleFormItem(String name, String value) {
					parameters.put( name, value );
				}

				@Override
				public void handleFileItem(String name, FileItem fileItem) {
					files.add(fileItem);
				}
				
			});
			
		}
		
		return this;
	}
	
	private String join(String[] arr) {
		
		String ret = "";
		for (String item : arr) {
			ret += "," + item;
		}
		
		if (ret.length() > 0) { 
			ret = ret.substring(1);
		}
		
		return ret;
	}

	@Override
	public String getHost() {
		return request.getServerName();
	}
	
	@Override
	public String getUrl() {
		return request.getRequestURL().toString();
	}

	@Override
	public String getPath() {
		String path = request.getRequestURI();
		
		if (path == null) {
			return "/";
		}
		
		return path;
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public Map<String,String> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public String getRemoteAddress() {
		return request.getRemoteAddr();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public int getPort() {
		return request.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public boolean isAjax() {
		if (request.getHeader("x-requested-with") == null) {
            return false;
        }
        return "XMLHttpRequest".equals(request.getHeader("x-requested-with"));
	}

	@Override
	public Map<String,Cookie> getCookies() {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		
		Map<String,Cookie> cookies = new HashMap<String,Cookie>();
		for (javax.servlet.http.Cookie c : servletCookies) {
			cookies.put( c.getName(), map(c) );
		}
		
		return Collections.unmodifiableMap(cookies);
	}

	@Override
	public Cookie getCookie(String name) {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		
		if (servletCookies == null) {
			return null;
		}
		
		for (javax.servlet.http.Cookie c : servletCookies) {
			if (c.getName().equals(name)) {
				return map(c);
			}
		}
		
		return null;
	}
	
	private Cookie map(javax.servlet.http.Cookie servletCookie) {
		Cookie cookie = new Cookie(servletCookie.getName(), servletCookie.getValue(), servletCookie.getMaxAge(), servletCookie.isHttpOnly());
		
		cookie.setPath(servletCookie.getPath());
		cookie.setDomain(servletCookie.getDomain());
		cookie.setSecure(servletCookie.getSecure());
		
		return cookie;
	}

	@Override
	public Map<String, String> getHeaders() {
		Enumeration<String> servletHeaders = request.getHeaderNames();
		
		Map<String,String> headers = new HashMap<String,String>();
		
		while(servletHeaders.hasMoreElements()) {
			String headerName = servletHeaders.nextElement();
			
			headers.put(headerName, request.getHeader(headerName));
		}
		
		return headers;
	}

	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public FileItem[] getFiles() {
		
		FileItem[] fileParts = new FileItem[files.size()];
		for (int i=0; i < files.size(); i++) {
			fileParts[i] = files.get(i);
		}
		return fileParts;
		
	}

	@Override
	public BodyParser getBody() throws HttpException {
		
		BodyParser bodyParser = new BodyParser() {

			@Override
			public String asString() throws HttpException {
				try {
					BufferedReader reader = new BufferedReader( new InputStreamReader(request.getInputStream()) );
				    StringBuilder sb = new StringBuilder();
				    String line = reader.readLine();
				    while (line != null) {
				        sb.append(line + "\n");
				        line = reader.readLine();
				    }
				    reader.close();
				    String data = sb.toString();
				    
				    return data; 
				} catch (IOException e) {
					throw new HttpException(e);
				}
			}

			@Override
			public InputStream asInputStream() throws HttpException {
				try {
					return request.getInputStream();
				} catch (IOException e) {
					throw new HttpException(e);
				}
			}
			
		};
		
		return bodyParser;
	}
	
}
