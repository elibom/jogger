package org.jogger.http;

/**
 *
 * @author German Escobar
 */
public class Path {

	/**
	 * The regular expression to find holders in a path (e.g. {userId}).
	 */
	public static final String VAR_REGEXP = "\\{([^{}]+)\\}";

	public static final String VAR_REPLACE = "([^#/?]+)";

	public static String fixPath(String path) {
		if (path == null) {
			return "/";
		}

		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		if (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

}
