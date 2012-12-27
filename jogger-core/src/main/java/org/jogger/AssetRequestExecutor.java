package org.jogger;

import java.net.URLDecoder;

import org.jogger.asset.Asset;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible of executing requests for static assets.
 * 
 * @author German Escobar
 */
public class AssetRequestExecutor {
	
	private Logger log = LoggerFactory.getLogger(AssetRequestExecutor.class);
	
	private Jogger jogger;
	
	private NotFoundHandler defaultNotFoundHandler;

	public AssetRequestExecutor(Jogger jogger) {
		if (jogger == null) {
			throw new IllegalArgumentException("No jogger provided.");
		}
		
		this.jogger = jogger;
		this.defaultNotFoundHandler = new DefaultNotFoundHandler();
	}
	
	/**
	 * Tries to load and write the requested asset to the response.
	 * 
	 * @param request the Jogger HTTP request object.
	 * @param response the Jogger HTTP response object.
	 * 
	 * @throws Exception
	 */
	public void execute(Request request, Response response) throws Exception {
		
		// only handle GET requests
		if (!request.getMethod().equalsIgnoreCase("get")) {
			handleNotFound(request, response);
			return;
		}
		
		Asset asset = jogger.getAssetLoader().load(URLDecoder.decode(request.getPath(), "UTF-8"));
		if (asset != null) {
			response.status(Response.OK);
			response.write(asset);
		} else {
			handleNotFound(request, response);
		}
	}
	
	/**
	 * Helper method. Called when the requested asset was not found.
	 * 
	 * @param request the Jogger HTTP request object.
	 * @param response the Jogger HTTP response object.
	 */
	private void handleNotFound(Request request, Response response) {
		
		NotFoundHandler notFoundHandler = jogger.getNotFoundHandler();
		if (notFoundHandler == null) {
			defaultNotFoundHandler.handle(request, response);
			return;
		}
		
		try {
			notFoundHandler.handle(request, response);
		} catch (Exception e) {
			log.error("Exception running the not found handler ... using the default one: " + e.getMessage(), e);
			defaultNotFoundHandler.handle(request, response);
		}
		
	}

}
