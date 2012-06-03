package org.jogger.test;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MockRequestTest {

	@Test
	public void shouldRetrieveQueryParams() throws Exception {
		
		MockRequest request = new MockRequest("GET", "http://localhost/?param1=value1&param2=2");
		
		String param1 = request.getParameter("param1").asString();
		Assert.assertNotNull( param1 );
		Assert.assertEquals( param1, "value1" );
		
		long param2 = request.getParameter("param2").asLong();
		Assert.assertNotNull( param2 );
		Assert.assertEquals( param2 , 2L);
		
	}
	
}
