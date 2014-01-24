package org.jogger.exceptions;

import org.jogger.exception.UnprocessableEntityException;
import org.jogger.exception.WebApplicationException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UnprocessableEntityExceptionTest {
	
	@Test
	public void shouldCreateUnprocessableEntityException() throws Exception {
		try {
			throw new UnprocessableEntityException();
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 422);
			Assert.assertEquals(e.getName(), "Unprocessable Entity");
		}
	}
	
	@Test
	public void shouldCreateUnprocessableEntityExceptionWithMessage() throws Exception {
		try {
			throw new UnprocessableEntityException("this is a test");
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 422);
			Assert.assertEquals(e.getName(), "Unprocessable Entity");
			Assert.assertEquals(e.getMessage(), "this is a test");
		}
	}

}
