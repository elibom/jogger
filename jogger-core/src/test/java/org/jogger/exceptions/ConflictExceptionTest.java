package org.jogger.exceptions;

import org.jogger.exception.ConflictException;
import org.jogger.exception.WebApplicationException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConflictExceptionTest {

	@Test
	public void shouldCreateConflictException() throws Exception {
		try {
			throw new ConflictException();
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 409);
			Assert.assertEquals(e.getName(), "Conflict");
		}
	}
	
	@Test
	public void shouldCreateConflictExceptionWithMessage() throws Exception {
		try {
			throw new ConflictException("this is a test");
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 409);
			Assert.assertEquals(e.getName(), "Conflict");
			Assert.assertEquals(e.getMessage(), "this is a test");
		}
	}
	
}
