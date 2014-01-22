package org.jogger.test;

import static org.mockito.Mockito.mock;

import java.net.URISyntaxException;

import org.jogger.Jogger;
import org.jogger.RouteHandler;
import org.jogger.RouterMiddleware;
import org.jogger.http.Response;
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
