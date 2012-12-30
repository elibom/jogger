package org.jogger;

import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.jogger.http.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JoggerServerTest {

	@Test
	public void shouldStartStopServer() throws Exception {
		JoggerServer joggerServer = new JoggerServer(new Jogger());
		joggerServer.listen(27773);

		try {
			HttpResponse response = Request.Get("http://localhost:27773/").execute().returnResponse();
			Assert.assertEquals(response.getStatusLine().getStatusCode(), 404);
		} finally {
			joggerServer.stop();
			try {
				new Socket("localhost", 27773);
				Assert.fail("Server is still running");
			} catch (ConnectException e) {}
		}
	}
	
	@Test
	public void shouldExecuteRoute() throws Exception {
		Jogger app = new Jogger();
		app.get("/", new RouteHandler() {
			@Override
			public void handle(org.jogger.http.Request request, Response response) {
				
			}
		});
		
		JoggerServer joggerServer = new JoggerServer(app);
		joggerServer.listen(27773);

		try {
			HttpResponse response = Request.Get("http://localhost:27773/").execute().returnResponse();
			Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
		} finally {
			joggerServer.stop();
		}
	}
	
	@Test
	public void shouldHandleException() throws Exception {
		Jogger app = new Jogger();
		app.get("/", new RouteHandler() {
			@Override
			public void handle(org.jogger.http.Request request, Response response) {
				throw new RuntimeException();
			}
		});
		
		JoggerServer joggerServer = new JoggerServer(app);
		joggerServer.listen(27773);

		try {
			HttpResponse response = Request.Get("http://localhost:27773/").execute().returnResponse();
			Assert.assertEquals(response.getStatusLine().getStatusCode(), 500);
		} finally {
			joggerServer.stop();
		}
	}
	
	@Test
	public void shouldJoinServerThread() throws Exception {
		final AtomicBoolean running = new AtomicBoolean(false);
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				JoggerServer joggerServer = new JoggerServer(new Jogger());
				joggerServer.listen(27773);
				
				try {
					running.getAndSet(true);
					
					try {
						joggerServer.join();
					} catch (InterruptedException e) {}
				} finally {
					joggerServer.stop();
					running.getAndSet(false);
				}
			}
		});
		thread.start();
		
		Thread.sleep(100);
		Assert.assertTrue(running.get());
		thread.interrupt();
		Thread.sleep(100);
		Assert.assertFalse(running.get());
	}
	
}
