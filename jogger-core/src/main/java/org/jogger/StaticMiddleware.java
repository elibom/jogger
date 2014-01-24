package org.jogger;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jogger.asset.Asset;
import org.jogger.asset.AssetLoader;
import org.jogger.asset.FileAssetLoader;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.util.Preconditions;

/**
 * This middleware serve static files, usually from (but not limited to) the file system. Keep in mind the following when 
 * using this middleware:
 * 
 * <ol>
 * 	<li>It will only handle requests where its path matches the prefix configured in the middleware (e.g. the request with 
 * path "/assets/my_asset.gif" will be handled if the path prefix of the middleware is "assets").</li>
 *  <li>If the path matches but the method is not GET, a <strong>404 Not Found</strong> will be returned (i.e. it won't 
 *  recognized other HTTP method different to GET).</li>
 *  <li>If the asset is not found, a <strong>404 Not Found</strong> will be returned.</li>
 *  <li>If the asset hasn't been modified (i.e. using the If-Modified-Since header), a <strong>304 Not Modified</strong> will 
 *  be returned.</li>
 * </ol>
 * 
 * @author German Escobar
 */
public class StaticMiddleware implements Middleware {
	
	/**
	 * Used to load the assets.
	 */
	private AssetLoader assetLoader;
	
	/**
	 * The path prefix this middleware uses to decide to match a request.
	 */
	private String prefix;

	/**
	 * Constructor. Creates a new instance with the provided path and with a {@link FileAssetLoader} as the 
	 * {@link AssetLoader} implementation.
	 * 
	 * @param path the prefix used to match the request path; also the path where the static files are located in the file system. 
	 */
	public StaticMiddleware(String path) {
		Preconditions.notNull(path, "no path provided.");
		this.prefix = fixPrefix(path);
		this.assetLoader = new FileAssetLoader(path);
	}
	
	/**
	 * Helper method. Adds a leading and trailing slash if they are not present (i.e. "assets" becomes "/assets/")
	 * 
	 * @param prefix the path to be fixed, can't be null.
	 * 
	 * @return the prefix with leading and trailing slashes.
	 */
	private String fixPrefix(String prefix) {
		String fixedPrefix = prefix.startsWith("/") ? prefix : "/" + prefix;
		return fixedPrefix.endsWith("/") ? fixedPrefix : fixedPrefix + "/";
	}
	
	/**
	 * Constructor. Creates a new instance with a {@link FileAssetLoader} with the provided path, and the prefix.
	 * 
	 * @param path the path where the static files are located in the file system.
	 * @param prefix the prefix used to match the request path
	 */
	public StaticMiddleware(String path, String prefix) {
		Preconditions.notNull(path, "no path provided.");
		Preconditions.notNull(prefix, "no prefix provided.");
		
		this.prefix = fixPrefix(prefix);
		this.assetLoader = new FileAssetLoader(path);
	}
	
	/**
	 * Constructor. Creates a new instance with the provided asset loader and prefix.
	 * 
	 * @param assetLoader the object used to load the assets.
	 * @param prefix the prefix used to match the request path.
	 */
	public StaticMiddleware(AssetLoader assetLoader, String prefix) {
		Preconditions.notNull(assetLoader, "no assetLoader provided.");
		Preconditions.notNull(prefix, "no prefix provided.");
		
		this.prefix = fixPrefix(prefix);
		this.assetLoader = assetLoader;
	}

	@Override
	public void handle(Request request, Response response, MiddlewareChain chain) throws Exception {
		// check if we have to handle the request or not
		String requestPath = fixRequestPath(request.getPath());
		if (!requestPath.startsWith(prefix)) {
			chain.next();
			return;
		}
		
		// only handle GET requests
		if (!request.getMethod().equalsIgnoreCase("get")) {
			chain.next();
			return;
		}
		
		// load the asset
		requestPath = requestPath.replace(prefix, "");
		Asset asset = assetLoader.load(URLDecoder.decode(requestPath, "UTF-8"));
		if (asset == null) {
			chain.next();
			return;
		}
		
		// check if asset has been modified
		if (!assetHasBeenModified(request, asset)) {
			response.status(Response.NOT_MODIFIED);
			return;
		}

		response.status(Response.OK);
		response.write(asset);
	}
	
	/**
	 * Helper method. The request path shouldn't have a trailing slash.
	 * 
	 * @param path the path to fix.
	 * @return the path with its trailing slash
	 */
	private String fixRequestPath(String path) {
		return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
	}

	private boolean assetHasBeenModified(Request request, Asset asset) throws ParseException {
		String ifModifiedSince = request.getHeader("If-Modified-Since");
		if (ifModifiedSince != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
			Date dt = sdf.parse(ifModifiedSince);
			if (dt.getTime() == asset.getLastModified()) {
				return false;
			}
		}
		
		return true;
	}

}
