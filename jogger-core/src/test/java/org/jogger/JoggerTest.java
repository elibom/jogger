package org.jogger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.jogger.http.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JoggerTest {

	@Test
	public void shouldStartStopServer() throws Exception {
		Jogger app = new Jogger();
		app.listen(27773);

		try {
			HttpResponse response = Request.Get("http://localhost:27773/").execute().returnResponse();
			Assert.assertEquals(response.getStatusLine().getStatusCode(), 404);
		} finally {
			app.stop();
			try {
				new Socket("localhost", 27773);
				Assert.fail("Server is still running");
			} catch (ConnectException e) {}
		}
	}

	@Test
	public void shouldExecuteMiddleware() throws Exception {
		Middleware middleware = mock(Middleware.class);
		Jogger app = new Jogger(middleware);
		app.listen(27773);

		try {
			Request.Get("http://localhost:27773/").execute().returnResponse();
			verify(middleware).handle(any(org.jogger.http.Request.class), any(Response.class), any(MiddlewareChain.class));
		} finally {
			app.stop();
		}
	}

	@Test
	public void shouldHandleException() throws Exception {
		Middleware middleware = mock(Middleware.class);
		doThrow(new RuntimeException()).when(middleware).handle(any(org.jogger.http.Request.class), any(Response.class), any(MiddlewareChain.class));
		
		Jogger app = new Jogger(middleware);
		app.listen(27773);

		try {
			HttpResponse response = Request.Get("http://localhost:27773/").execute().returnResponse();
			Assert.assertEquals(response.getStatusLine().getStatusCode(), 500);
		} finally {
			app.stop();
		}
	}

	@Test
	public void shouldJoinServerThread() throws Exception {
		final AtomicBoolean running = new AtomicBoolean(false);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Jogger app = new Jogger();
				app.listen(27773);

				try {
					running.getAndSet(true);

					try {
						app.join();
					} catch (InterruptedException e) {}
				} finally {
					app.stop();
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
