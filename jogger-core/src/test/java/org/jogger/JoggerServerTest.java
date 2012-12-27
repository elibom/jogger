package org.jogger;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JoggerServerTest {

	@Test
	public void shouldStartServer() throws Exception {
		JoggerServer joggerServer = new JoggerServer(new Jogger());
		joggerServer.listen(27773);

		HttpResponse response = Request.Get("http://localhost:27773/").execute().returnResponse();
		Assert.assertEquals(response.getStatusLine().getStatusCode(), 404);
		
		joggerServer.stop();
	}
	
}
