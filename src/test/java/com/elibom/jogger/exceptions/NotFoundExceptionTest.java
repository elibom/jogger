package com.elibom.jogger.exceptions;

import com.elibom.jogger.exception.NotFoundException;
import com.elibom.jogger.exception.WebApplicationException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NotFoundExceptionTest {

	@Test
	public void shouldCreateNotFoundException() throws Exception {
		try {
			throw new NotFoundException();
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 404);
			Assert.assertEquals(e.getName(), "Not Found");
		}
	}
	
	@Test
	public void shouldCreateNotFoundExceptionWithMessage() throws Exception {
		try {
			throw new NotFoundException("this is a test");
		} catch (WebApplicationException e) {
			Assert.assertEquals(e.getStatus(), 404);
			Assert.assertEquals(e.getName(), "Not Found");
			Assert.assertEquals(e.getMessage(), "this is a test");
		}
	}
	
}
