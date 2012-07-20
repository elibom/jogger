package org.jogger.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import org.jogger.interceptor.Interceptor;

/**
 * This is the mechanism that web applications use for registering interceptors. Just subclass this abstract class and 
 * call the {@link #add(Interceptor, String...)} method to add interceptors.  
 * 
 * @author German Escobar
 */
public abstract class Interceptors {
	
	/**
	 * Stores the list of interceptors entries
	 */
	private List<InterceptorEntry> entries = new ArrayList<InterceptorEntry>();
	
	/**
	 * Stores a new {@link Interceptor} implementation with the specified paths.
	 * 
	 * @param interceptor the interceptor to be added.
	 * @param paths the paths for which this interceptor will be executed.
	 * 
	 * @return this instance for method chaining.
	 */
	public Interceptors add(Interceptor interceptor, String ... paths) {
		entries.add(new InterceptorEntry(interceptor, paths));
		
		return this;
	}
	
	/**
	 * Returns a list of interceptors that match a path
	 * 
	 * @param path
	 * @return a list of {@link Interceptor} objects.
	 */
	public List<Interceptor> getInterceptors(String path) {
		
		List<Interceptor> interceptors = new ArrayList<Interceptor>();
		
		for (InterceptorEntry entry : entries) {
			if (matches(path, entry.getPaths())) {
				interceptors.add(entry.getInterceptor());
			}
		}
		
		return interceptors;
	}
	
	private boolean matches(String path, String ... paths) {
		
		if (paths.length == 0) {
			return true;
		}
		
		for (String p : paths) {
		
			/* TODO we should use the same mechanism servlets use to match paths */
			if (p.equalsIgnoreCase(path)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * This is the method that subclasses implement to add interceptors.
	 * 
	 * @param servletConfig has information from the servlet and servlet context.
	 */
	public abstract void initialize(ServletConfig servletConfig);
	
	/**
	 * Helper class. Represents a interceptor entry (i.e. an interceptor and the matching paths).
	 * 
	 * @author German Escobar
	 */
	private class InterceptorEntry {
		
		private Interceptor interceptor;
		
		private String[] paths;
		
		public InterceptorEntry(Interceptor interceptor, String ... paths) {
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
	
}
