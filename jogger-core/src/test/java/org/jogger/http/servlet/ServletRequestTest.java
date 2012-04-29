package org.jogger.http.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jogger.http.Cookie;
import org.jogger.http.Request;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ServletRequestTest {

	@Test
	public void shouldRetrieveHost() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getServerName()).thenReturn("localhost");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getHost(), "localhost");
		
	}
	
	@Test
	public void shouldRetrievePath() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getContextPath()).thenReturn("/context");
		when(servletRequest.getRequestURI()).thenReturn("/users/edit/1");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getPath(), "/users/edit/1");
		
	}
	
	@Test
	public void shouldRetrieveQueryString() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getQueryString()).thenReturn("method=test&action=success");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getQueryString(), "method=test&action=success");
		
	}
	
	@Test
	public void shouldRetrieveUrl() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getRequestURL()).thenReturn(new StringBuffer("http://www.google.com:81/test"));
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getUrl(), "http://www.google.com:81/test");
		
	}
	
	@Test
	public void shouldRetrieveMethod() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getMethod()).thenReturn("GET");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getMethod(), "GET");
		
	}
	
	@Test
	public void shouldRetrieveRemoteAddress() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getRemoteAddr()).thenReturn("localhost");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getRemoteAddress(), "localhost");
		
	}
	
	@Test
	public void shouldRetrieveContentType() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getContentType()).thenReturn("application/json");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getContentType(), "application/json");
		
	}
	
	@Test
	public void shouldRetrievePort() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getServerPort()).thenReturn(1);
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getPort(), 1);
		
	}
	
	@Test
	public void shouldRetrieveIsSecure() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.isSecure()).thenReturn(true);
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.isSecure(), true);
		
	}
	
	@Test
	public void shouldRetrieveIsAjax() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getHeader("x-requested-with")).thenReturn("XMLHttpRequest");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.isAjax(), true);
		
		when(servletRequest.getHeader("x-requested-with")).thenReturn(null);
		Assert.assertEquals(request.isAjax(), false);
		
		when(servletRequest.getHeader("x-requested-with")).thenReturn("Another");
		Assert.assertEquals(request.isAjax(), false);
		
	}
	
	@Test
	public void shouldRetrieveCookie() throws Exception {
		
		javax.servlet.http.Cookie[] servletCookies = { new javax.servlet.http.Cookie("test-1", "1") };  
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getCookies()).thenReturn(servletCookies);
		
		Request request = new ServletRequest(servletRequest);
		Map<String,Cookie> cookies = request.getCookies();
		
		Assert.assertEquals(cookies.size(), 1);
		
		Cookie cookie = cookies.get("test-1");
		Assert.assertNotNull(cookie);
		Assert.assertEquals(cookie.getValue(), "1");
		Assert.assertEquals(cookie.getMaxAge(), -1);
		Assert.assertNull(cookie.getDomain());
		Assert.assertNull(cookie.getPath());
		
		Assert.assertNotNull(request.getCookie("test-1"));
		Assert.assertNull(request.getCookie("not-existent"));
		
	}
	
	@Test
	public void shouldRetrieveEmptyCookies() throws Exception {
		
		javax.servlet.http.Cookie[] servletCookies = {};
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getCookies()).thenReturn(servletCookies);
		
		Request request = new ServletRequest(servletRequest);
		Map<String,Cookie> cookies = request.getCookies();
		
		Assert.assertEquals(cookies.size(), 0);
		
	}
	
	@Test
	public void shoudlRetrieveHeader() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getHeader("Authorization")).thenReturn("Basic ...");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getHeader("Authorization"), "Basic ...");
		
	}
	
}
