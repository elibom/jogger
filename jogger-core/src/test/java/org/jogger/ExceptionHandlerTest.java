package org.jogger;

import static org.mockito.Mockito.mock;

import org.jogger.http.Request;
import org.jogger.http.Response;
import org.testng.annotations.Test;

public class ExceptionHandlerTest {

	@Test
	public void shouldRender500Page() throws Exception {
		Request request = mock(Request.class);
		Response response = mock(Response.class);
		
		ExceptionHandler exceptionHandler = new ExceptionHandler();
		exceptionHandler.handle(new NullPointerException(), request, response);
	}
}
