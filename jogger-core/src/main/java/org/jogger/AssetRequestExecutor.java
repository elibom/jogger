package org.jogger;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
			// check if asset has been modified
			String ifModifiedSince = request.getHeader("If-Modified-Since");
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
				if (ifModifiedSince != null) {
					Date dt = sdf.parse(ifModifiedSince);
					if (dt.getTime() == asset.getLastModified()) {
						response.status(Response.NOT_MODIFIED);
						return;
					}
				}
			} catch (IllegalArgumentException ex) {
			} catch (ParseException ex) {
			}

			response.status(Response.OK);
			response.write(asset);
		} else {
			response.notFound();
		}
	}

}
