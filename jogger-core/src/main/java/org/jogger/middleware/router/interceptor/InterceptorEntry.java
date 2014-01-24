package org.jogger.middleware.router.interceptor;

/**
 * Helper class. Represents a interceptor entry (i.e. an interceptor and the matching paths).
 *
 * @author German Escobar
 */
public class InterceptorEntry {

	private Interceptor interceptor;

	private String[] paths;

	public InterceptorEntry(Interceptor interceptor, String... paths) {
		this.interceptor = interceptor;
		this.paths = paths;
	}

	public Interceptor getInterceptor() {
		return interceptor;
	}

	public String[] getPaths() {
		return paths;
	}

}
