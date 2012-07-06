package ${package}.controllers;

import org.jogger.config.Interceptors;
import org.jogger.http.Response;
import org.jogger.test.JoggerTest;
import org.jogger.test.MockResponse;

import ${package}.interceptors.AppInterceptors;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PagesTest extends JoggerTest {
	
	@Test
	public void shouldRenderIndex() throws Exception {
		
		MockResponse response = get("/").run();
		
		Assert.assertEquals( response.getStatus(), Response.OK );
		Assert.assertEquals( response.getRenderedTemplate(), "index.ftl" );
		Assert.assertTrue( response.getOutputAsString().contains("Jogger") );
		
	}

	@Override
	protected String getBasePackage() {
		return "${package}.controllers";
	}

	@Override
	protected Interceptors getInterceptors() {
		AppInterceptors interceptors = new AppInterceptors();
		interceptors.initialize(null);
		
		return interceptors;
	}

}