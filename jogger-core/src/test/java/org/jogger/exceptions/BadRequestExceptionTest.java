package org.jogger.exceptions;

import org.jogger.exception.BadRequestException;
import org.jogger.exception.WebApplicationException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BadRequestExceptionTest {

	@Test
	public void shouldCreateBadRequestException() throws Exception {
		try {
			throw new BadRequestException();
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 400);
			Assert.assertEquals(e.getName(), "Bad Request");
		}
	}
	
	@Test
	public void shouldCreateBadRequestExceptionWithMessage() throws Exception {
		try {
			throw new BadRequestException("this is a test");
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 400);
			Assert.assertEquals(e.getName(), "Bad Request");
			Assert.assertEquals(e.getMessage(), "this is a test");
		}
	}

}
