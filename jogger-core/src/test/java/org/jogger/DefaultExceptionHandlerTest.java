package org.jogger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jogger.exception.NotFoundException;
import org.jogger.exception.WebApplicationException;
import org.jogger.http.Http.Headers;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.testng.annotations.Test;

public class DefaultExceptionHandlerTest {

	@Test
	public void shouldHandleException() throws Exception {
		Request request = mock(Request.class);
		when(request.getHeader(Headers.ACCEPT)).thenReturn("text/html");
		Response response = mock(Response.class);
		
		DefaultExceptionHandler handler = new DefaultExceptionHandler();
		try {
			throw new Exception();
		} catch (Exception e) {
			handler.handle(e, request, response);
		}
		
		verify(response).status(500);
		verify(response).write(any(String.class));
	}
	
	@Test
	public void shouldHandleWebApplicationException() throws Exception {
		Request request = mock(Request.class);
		when(request.getHeader(Headers.ACCEPT)).thenReturn("text/html");
		Response response = mock(Response.class);
		
		DefaultExceptionHandler handler = new DefaultExceptionHandler();
		try {
			throw new NotFoundException();
		} catch (WebApplicationException e) {
			handler.handle(e, request, response);
		}
		
		verify(response).status(404);
		verify(response).write(any(String.class));
	}
	
	@Test
	public void shouldNotWriteBodyIfNotHtmlRequest() throws Exception {
		Request request = mock(Request.class);
		Response response = mock(Response.class);
		
		DefaultExceptionHandler handler = new DefaultExceptionHandler();
		try {
			throw new NotFoundException();
		} catch (WebApplicationException e) {
			handler.handle(e, request, response);
		}
		
		verify(response).status(404);
		verify(response, never()).write(any(String.class));
	}
}
