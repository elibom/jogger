package org.jogger;

import java.net.URLDecoder;

import org.jogger.asset.Asset;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.util.Preconditions;

/**
 * This class is responsible of executing requests for static assets.
 * 
 * @author German Escobar
 */
public class AssetRequestExecutor {
	
	private Jogger jogger;

	public AssetRequestExecutor(Jogger jogger) {
		Preconditions.notNull(jogger, "no jogger provided.");
		this.jogger = jogger;
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
			response.notFound();
			return;
		}
		
		Asset asset = jogger.getAssetLoader().load(URLDecoder.decode(request.getPath(), "UTF-8"));
		if (asset != null) {
			response.status(Response.OK);
			response.write(asset);
		} else {
			response.notFound();
		}
	}

}
