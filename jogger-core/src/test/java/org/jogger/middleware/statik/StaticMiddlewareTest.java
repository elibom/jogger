package org.jogger.middleware.statik;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jogger.MiddlewareChain;
import org.jogger.asset.Asset;
import org.jogger.asset.AssetLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.middleware.statik.StaticMiddleware;
import org.testng.annotations.Test;

public class StaticMiddlewareTest {

	@Test
	public void shouldFindExistingAsset() throws Exception {
		Asset asset = new Asset(null, "test.css", "text/css", 34);

		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("test.css")).thenReturn(asset);

		StaticMiddleware middleware = new StaticMiddleware(assetLoader, "assets");
		
		Request request = mockRequest("get", "/assets/test.css");
		Response response = mock(Response.class);

		middleware.handle(request, response, mock(MiddlewareChain.class));

		verify(assetLoader).load("test.css");
		verify(response).write(asset);
	}
	
	@Test
	public void shouldFindExistingAssetWithEmptyPrefix() throws Exception {
		Asset asset = new Asset(null, "test.css", "text/css", 34);

		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("assets/test.css")).thenReturn(asset);

		StaticMiddleware middleware = new StaticMiddleware(assetLoader, "");
		
		Request request = mockRequest("get", "/assets/test.css");
		Response response = mock(Response.class);

		middleware.handle(request, response, mock(MiddlewareChain.class));

		verify(assetLoader).load("assets/test.css");
		verify(response).write(asset);
	}
	
	private Request mockRequest(String httpMethod, String path) {
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(httpMethod);
		when(request.getPath()).thenReturn(path);

		return request;
	}
	
	@Test
	public void shouldNotFindMissingAsset() throws Exception {
		AssetLoader assetLoader = mock(AssetLoader.class);
		StaticMiddleware middleware = new StaticMiddleware(assetLoader, "assets");

		Request request = mockRequest("get", "/assets/test.css");
		Response response = mock(Response.class);

		MiddlewareChain chain = mock(MiddlewareChain.class);
		middleware.handle(request, response, chain);

		verify(response, never()).write(any(Asset.class));
		verify(chain).next();
	}
	
	@Test
	public void shouldNotFindAssetWithInvalidHttpMethod() throws Exception {
		Asset asset = new Asset(null, "test.css", "text/css", 34);

		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("test.css")).thenReturn(asset);

		StaticMiddleware middleware = new StaticMiddleware(assetLoader, "assets");

		Request request = mockRequest("post", "/assets/test.css");
		Response response = mock(Response.class);

		MiddlewareChain chain = mock(MiddlewareChain.class);
		middleware.handle(request, response, chain);

		verify(response, never()).write(any(Asset.class));
		verify(chain).next();
	}
	
	@Test
	public void should304WithCorrectIfModifiedSinceHeader() throws Exception {
		String dateString = "Wed, 09 Apr 2008 23:55:38 GMT";
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		Date d = format.parse(dateString);

		Asset asset = new Asset(null, "test.css", "text/css", 34, d.getTime());
		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("test.css")).thenReturn(asset);

		StaticMiddleware middleware = new StaticMiddleware(assetLoader, "assets");

		Request request = mockRequest("get", "/assets/test.css");
		when(request.getHeader("If-Modified-Since")).thenReturn(dateString);

		Response response = mock(Response.class);

		middleware.handle(request, response, mock(MiddlewareChain.class));
		verify(response).status(Response.NOT_MODIFIED);
	}
	
	@Test
	public void shouldReturnModifiedAsset() throws Exception {
		String dateString = "Wed, 09 Apr 2008 23:55:38 GMT";
		SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		Date d = format.parse(dateString);

		Asset asset = new Asset(null, "test.css", "text/css", 34, d.getTime() + 3600);
		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("test.css")).thenReturn(asset);

		StaticMiddleware middleware = new StaticMiddleware(assetLoader, "assets");
		
		Request request = mockRequest("get", "/assets/test.css");
		when(request.getHeader("If-Modified-Since")).thenReturn(dateString);

		Response response = mock(Response.class);

		middleware.handle(request, response, mock(MiddlewareChain.class));

		verify(assetLoader).load("test.css");
		verify(response).write(asset);
	}
}
