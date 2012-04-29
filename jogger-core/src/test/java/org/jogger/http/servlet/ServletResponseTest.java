package org.jogger.http.servlet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jogger.http.Cookie;
import org.jogger.http.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ServletResponseTest {

	@Test
	public void shouldRetrieveStatus() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getStatus()).thenReturn(400);
		
		Response response = new ServletResponse(servletResponse, null);
		Assert.assertEquals(response.getStatus(), 400);
		
	}
	
	@Test
	public void shouldSetStatus() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.status(400);
		
		verify(servletResponse).setStatus(400);
		
	}
	
	@Test
	public void shouldSetBadRequest() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.badRequest();
		
		verify(servletResponse).setStatus(Response.BAD_REQUEST);
		
	}
	
	@Test
	public void shouldSetUnauthorized() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.unauthorized();
		
		verify(servletResponse).setStatus(Response.UNAUTHORIZED);
		
	}
	
	@Test
	public void shouldSetNotFound() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.notFound();
		
		verify(servletResponse).setStatus(Response.NOT_FOUND);
		
	}
	
	@Test
	public void shouldRetrieveContentType() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getContentType()).thenReturn("application/json");
		
		Response response = new ServletResponse(servletResponse, null);
		Assert.assertEquals(response.getContentType(), "application/json");
		
	}
	
	@Test
	public void shouldSetContentType() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.contentType("application/json");
		
		verify(servletResponse).setContentType("application/json");
		
	}
	
	@Test
	public void shouldRetrieveHeader() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getHeader("Authorization")).thenReturn("Basic");
		
		Response response = new ServletResponse(servletResponse, null);
		Assert.assertEquals(response.getHeader("Authorization"), "Basic");
		
	}
	
	@Test
	public void shouldSetHeader() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.setHeader("Authorization", "Basic");
		
		verify(servletResponse).setHeader("Authorization", "Basic");
		
	}
	
	@Test
	public void shouldSetCookie() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.setCookie(new Cookie("test-1", "1"));
		
		verify(servletResponse).addCookie(any(javax.servlet.http.Cookie.class));
		
	}
	
	@Test
	public void shouldRemoveCookie() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.removeCookie(new Cookie("test-1", "1"));
		
		verify(servletResponse).addCookie(any(javax.servlet.http.Cookie.class));
		
	}
	
	@Test
	public void shouldSetAndGetAttributes() throws Exception {
		
		Response response = new ServletResponse(null, null);
		response.setAttribute("test-1", "1");
		
		Map<String,Object> atts = response.getAttributes();
		
		Assert.assertNotNull(atts);
		Assert.assertEquals(atts.size(), 1);
		Assert.assertEquals(atts.get("test-1"), "1");
		
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailWhenTryingToSetNullAttributeName() throws Exception {
		
		Response response = new ServletResponse(null, null);
		response.setAttribute(null, "1");
		
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldFailWhenTryingToSetNullAttributeValue() throws Exception {
		
		Response response = new ServletResponse(null, null);
		response.setAttribute("test-1", null);
		
	}
	
	@Test
	public void shouldPrint() throws Exception {
		
		PrintWriter writer = mock(PrintWriter.class);
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getWriter()).thenReturn(writer);
		
		Response response = new ServletResponse(servletResponse, null);
		response.print("test");
		
		verify(writer).print("test");
		
	}
	
	@Test
	public void shouldRedirect() throws Exception {
		
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		
		Response response = new ServletResponse(servletResponse, null);
		response.redirect("/");
		
		verify(servletResponse).sendRedirect("/");
		
	}
	
}
