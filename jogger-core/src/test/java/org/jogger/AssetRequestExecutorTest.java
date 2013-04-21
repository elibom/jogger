package org.jogger;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.jogger.asset.Asset;
import org.jogger.asset.AssetLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AssetRequestExecutorTest {

	@Test
	public void shouldFindExistingAsset() throws Exception {
		
		Asset asset = new Asset(null, "test.css", "text/css", 34);
		
		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("/css/test.css")).thenReturn(asset);
		
		Jogger jogger = new Jogger();
		jogger.setAssetLoader(assetLoader);
		
		AssetRequestExecutor assetExecutor = new AssetRequestExecutor(jogger);
		
		Request request = mockRequest("get", "/css/test.css");
		Response response = mock(Response.class);
		
		assetExecutor.execute(request, response);
		
		verify(assetLoader).load("/css/test.css");
		verify(response).write(asset);
		
	}
	
	@Test
	public void shouldNotFindMissingAsset() throws Exception {
		
		AssetLoader assetLoader = mock(AssetLoader.class);
		
		Jogger jogger = new Jogger();
		jogger.setAssetLoader(assetLoader);
		
		AssetRequestExecutor assetExecutor = new AssetRequestExecutor(jogger);
		
		Request request = mockRequest("get", "/css/test.css");
		Response response = mock(Response.class);
		
		assetExecutor.execute(request, response);
		
		verify(response, never()).write(any(Asset.class));
		verify(response).notFound();
	}
	
	@Test
	public void shouldNotFindAssetWithInvalidHttpMethod() throws Exception {
		
		Asset asset = new Asset(null, "test.css", "text/css", 34);
		
		AssetLoader assetLoader = mock(AssetLoader.class);
		when(assetLoader.load("/css/test.css")).thenReturn(asset);
		
		Jogger jogger = new Jogger();
		jogger.setAssetLoader(assetLoader);
		
		AssetRequestExecutor assetExecutor = new AssetRequestExecutor(jogger);
		
		Request request = mockRequest("post", "/css/test.css");
		Response response = mock(Response.class);
		
		assetExecutor.execute(request, response);
		
		verify(response, never()).write(any(Asset.class));
		verify(response).notFound();
		
	}

    @Test
    public void should304WithCorrectIfModifiedSinceHeader() throws Exception {
        String dateString = "Wed, 09 Apr 2008 23:55:38 GMT";
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date d = format.parse(dateString);

        Asset asset = new Asset(null, "test.css", "text/css", 34, d.getTime());
        AssetLoader assetLoader = mock(AssetLoader.class);
        when(assetLoader.load("/css/test.css")).thenReturn(asset);

        Jogger jogger = new Jogger();
        jogger.setAssetLoader(assetLoader);

        AssetRequestExecutor assetExecutor = new AssetRequestExecutor(jogger);

        Request request = mockRequest("get", "/css/test.css");
        when(request.getHeader("If-Modified-Since")).thenReturn(dateString);

        Response response = mock(Response.class);

        assetExecutor.execute(request, response);
        verify(response).status(Response.NOT_MODIFIED);
    }

    @Test
    public void shouldReturnModifiedAsset() throws Exception {

        String dateString = "Wed, 09 Apr 2008 23:55:38 GMT";
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date d = format.parse(dateString);

        Asset asset = new Asset(null, "test.css", "text/css", 34, d.getTime() + 3600);
        AssetLoader assetLoader = mock(AssetLoader.class);
        when(assetLoader.load("/css/test.css")).thenReturn(asset);

        Jogger jogger = new Jogger();
        jogger.setAssetLoader(assetLoader);

        AssetRequestExecutor assetExecutor = new AssetRequestExecutor(jogger);

        Request request = mockRequest("get", "/css/test.css");
        when(request.getHeader("If-Modified-Since")).thenReturn(dateString);

        Response response = mock(Response.class);

        assetExecutor.execute(request, response);

        verify(assetLoader).load("/css/test.css");
        verify(response).write(asset);

    }
	
	private Request mockRequest(String httpMethod, String path) {
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(httpMethod);
		when(request.getPath()).thenReturn(path);
		
		return request;
	}
	
}
