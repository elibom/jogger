package com.elibom.jogger.test;

import com.elibom.jogger.test.JoggerTest;
import com.elibom.jogger.test.MockResponse;
import static org.mockito.Mockito.mock;

import java.net.URISyntaxException;

import com.elibom.jogger.Jogger;
import com.elibom.jogger.http.Response;
import com.elibom.jogger.middleware.router.RouteHandler;
import com.elibom.jogger.middleware.router.RouterMiddleware;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JoggerTestTest {

	@Test(expectedExceptions=URISyntaxException.class)
	public void shouldFailWithInvalidPath() throws Exception {
		JoggerTest joggerTest = buildJoggerTest(new Jogger());
		joggerTest.get("sd83#j ji/ 485").run();
	}

	@Test
	public void shouldTestGetWithQueryString() throws Exception {
		RouterMiddleware router = new RouterMiddleware();
		router.get("/test", mock(RouteHandler.class));
		Jogger app = new Jogger(router);
		
		JoggerTest joggerTest = buildJoggerTest(app);

		MockResponse response = joggerTest.get("/test?param=value").run();
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getStatus(), Response.OK);
	}

	private JoggerTest buildJoggerTest(final Jogger jogger) {
		return new JoggerTest() {

			@Override
			protected Jogger getJogger() throws Exception {
				return jogger;
			}

		};
	}
}
