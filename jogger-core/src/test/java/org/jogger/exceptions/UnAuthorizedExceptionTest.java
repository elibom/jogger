package org.jogger.exceptions;

import org.jogger.exception.UnAuthorizedException;
import org.jogger.exception.WebApplicationException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UnAuthorizedExceptionTest {

	@Test
	public void shouldCreateUnAuthorizedException() throws Exception {
		try {
			throw new UnAuthorizedException();
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 401);
			Assert.assertEquals(e.getName(), "Unauthorized");
		}
	}
	
	@Test
	public void shouldCreateUnAuthorizedExceptionWithMessage() throws Exception {
		try {
			throw new UnAuthorizedException("this is a test");
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 401);
			Assert.assertEquals(e.getName(), "Unauthorized");
			Assert.assertEquals(e.getMessage(), "this is a test");
		}
	}
	
}
