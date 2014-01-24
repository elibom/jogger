package org.jogger.exceptions;

import org.jogger.exception.ForbiddenException;
import org.jogger.exception.WebApplicationException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ForbiddenExceptionTest {

	@Test
	public void shouldCreateForbiddenException() throws Exception {
		try {
			throw new ForbiddenException();
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 403);
			Assert.assertEquals(e.getName(), "Forbidden");
		}
	}
	
	@Test
	public void shouldCreateForbiddenExceptionWithMessage() throws Exception {
		try {
			throw new ForbiddenException("this is a test");
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 403);
			Assert.assertEquals(e.getName(), "Forbidden");
			Assert.assertEquals(e.getMessage(), "this is a test");
		}
	}
	
}
