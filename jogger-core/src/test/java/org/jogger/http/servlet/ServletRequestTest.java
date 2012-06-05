package org.jogger.http.servlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jogger.http.Cookie;
import org.jogger.http.Request;
import org.jogger.http.Value;
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
	public void shouldRetrievePathVariables() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getContextPath()).thenReturn("");
		when(servletRequest.getRequestURI()).thenReturn("/users/1/edit/th1s1s4hAsh");
		
		ServletRequest request = new ServletRequest(servletRequest);
		request.setRoutePath("/users/{userId}/edit/{hash}");
		
		Map<String,Value> pathVariables = request.getPathVariables();
		Assert.assertNotNull( pathVariables );
		Assert.assertEquals( pathVariables.size(), 2 );
		Assert.assertEquals( pathVariables.get("userId").asLong(), new Long(1) );
		Assert.assertEquals( pathVariables.get("hash").asString(), "th1s1s4hAsh");
		
	}
	
	@Test
	public void shouldNotRetrieveNonExistingPathVariable() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getContextPath()).thenReturn("");
		when(servletRequest.getRequestURI()).thenReturn("/users/1/edit/th1s1s4hAsh");
		
		ServletRequest request = new ServletRequest(servletRequest);
		request.setRoutePath("/users/1/edit/th1s1s4hAsh");
		
		Map<String,Value> pathVariables = request.getPathVariables();
		Assert.assertNotNull( pathVariables );
		Assert.assertEquals( pathVariables.size(), 0 );
		
	}
	
	@Test
	public void shouldRetrieveQueryString() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getQueryString()).thenReturn("method=test&action=success");
		
		Request request = new ServletRequest(servletRequest);
		Assert.assertEquals(request.getQueryString(), "method=test&action=success");
		
	}
	
	@Test
	public void shouldRetrieveParams() throws Exception {
		
		Map<String,String[]> mockParams = new HashMap<String,String[]>();
		mockParams.put( "param1", new String[] { "value1" } );
		mockParams.put( "param2", new String[] { "val1", "val2" } );
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterMap()).thenReturn(mockParams);
		
		Request request = new ServletRequest(servletRequest);
		
		Map<String,Value> params = request.getParameters();
		Assert.assertNotNull( params );
		Assert.assertEquals( params.size(), 2 );
		
		Value param1 = params.get("param1");
		Assert.assertNotNull( param1 );
		Assert.assertEquals( param1.asString(), "value1" );
		
		List<Value> param1Values = param1.asList();
		Assert.assertNotNull( param1Values );
		Assert.assertEquals( param1Values.size(), 1 );
		Assert.assertEquals( param1Values.get(0).asString(), "value1" );
		
		Value param2 = params.get("param2");
		Assert.assertNotNull( param2 );
		Assert.assertEquals( param2.asString(), "val1,val2" );
		
		List<Value> param2Values = param2.asList();
		Assert.assertNotNull( param2Values );
		Assert.assertEquals( param2Values.size(), 2);
		Assert.assertEquals( param2Values.get(0).asString(), "val1" );
		Assert.assertEquals( param2Values.get(1).asString(), "val2" );
		
		Assert.assertNull( params.get("notexistnet") );
		
	}
	
	@Test
	public void shouldRetrieveStringParam() throws Exception {
		
		Map<String,String[]> mockParams = new HashMap<String,String[]>();
		mockParams.put( "param1", new String[] { "value1" } );
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterMap()).thenReturn(mockParams);
		
		Request request = new ServletRequest(servletRequest);
		
		Assert.assertNotNull( request.getParameter("param1") );
		Assert.assertEquals( request.getParameter("param1").asString(), "value1" );
		
	}
	
	@Test
	public void shouldNotRetrieveNonExistingParam() throws Exception {
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		
		Request request = new ServletRequest(servletRequest);
		
		Assert.assertNull( request.getParameter("nonexisting") );
		
	}
	
	@Test
	public void shouldRetrieveLongParam() throws Exception {
		
		Map<String,String[]> mockParams = new HashMap<String,String[]>();
		mockParams.put( "param1", new String[] { "1" } );
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterMap()).thenReturn(mockParams);
		
		Request request = new ServletRequest(servletRequest);
		
		Assert.assertNotNull( request.getParameter("param1") );
		Assert.assertEquals( request.getParameter("param1").asString(), "1" );
		
		long val = request.getParameter("param1").asLong();
		Assert.assertEquals( val, 1L);
		
	}
	
	@Test(expectedExceptions=NumberFormatException.class)
	public void shouldFailRetrievingUnparseableLongParam() throws Exception {
		
		Map<String,String[]> mockParams = new HashMap<String,String[]>();
		mockParams.put( "param1", new String[] { "notALong" } );
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterMap()).thenReturn(mockParams);
		
		Request request = new ServletRequest(servletRequest);
		
		Assert.assertNotNull( request.getParameter("param1") );
		request.getParameter("param1").asLong();
		
	}
	
	@Test
	public void shouldRetrieveBooleanParam() throws Exception {
		
		Map<String,String[]> mockParams = new HashMap<String,String[]>();
		mockParams.put( "param1", new String[] { "true" } );
		mockParams.put( "param2", new String[] { "false" } );
		mockParams.put( "param3", new String[] { "1" } );
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterMap()).thenReturn(mockParams);
		
		Request request = new ServletRequest(servletRequest);
		
		Assert.assertEquals( request.getParameter("param1").asBoolean(), Boolean.TRUE );
		Assert.assertEquals( request.getParameter("param2").asBoolean(), Boolean.FALSE );
		Assert.assertEquals( request.getParameter("param3").asBoolean(), Boolean.FALSE );
		
	}
	
	@Test
	public void shouldRetrieveListParam() throws Exception {
		
		Map<String,String[]> mockParams = new HashMap<String,String[]>();
		mockParams.put( "param1", new String[] { "value1" } );
		mockParams.put( "param2", new String[] { "value1", "2" } );
		
		HttpServletRequest servletRequest = mock(HttpServletRequest.class);
		when(servletRequest.getParameterMap()).thenReturn(mockParams);
		
		Request request = new ServletRequest(servletRequest);
		
		Assert.assertNotNull( request.getParameter("param1") );
		Assert.assertNotNull( request.getParameter("param2") );
		
		List<Value> param1Values = request.getParameter("param1").asList();
		Assert.assertEquals( param1Values.size(), 1 );
		Assert.assertEquals( param1Values.get(0).asString(), "value1");
		
		List<Value> param2Values = request.getParameter("param2").asList();
		Assert.assertEquals( param2Values.size(), 2 );
		Assert.assertEquals( param2Values.get(0).asString(), "value1");
		Assert.assertEquals( param2Values.get(1).asLong(), new Long(2) );
		
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
