package org.jogger;

import java.util.HashMap;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * Subclasses act as controllers that handle HTTP requests.
 * 
 * @author German Escobar
 */
public abstract class Controller {
	
	/**
	 * The HTTP request object. 
	 */
	protected Request request;
	
	/**
	 * The HTTP response object.
	 */
	protected Response response;
	
	/**
	 * Tells if the {@link #init(Request, Response)} method has already been called.
	 */
	private boolean initialized = false;
	
	/**
	 * Initializes the controller. This method is called from the {@link JoggerServlet} after the controller is 
	 * instantiated.
	 * 
	 * @param context contains information needed by the controller.
	 * @throws IllegalStateException if the controller was already initialized.
	 */
	public void init(Request request, Response response) throws IllegalStateException {
		
		// we should set the context only once
		if (initialized) {
			throw new IllegalStateException("Controller was already initialized.");
		}

		this.request = request;
		this.response = response;
		
		this.initialized = true;
		
	}
	
	/**
	 * Returns a map with method chaining capabilities.
	 * 
	 * @return an {@link FluentMap}.
	 */
	protected FluentMap<String,Object> map() {
		return new FluentMap<String,Object>();
	}
	
	/**
	 * A map with a method "set" that supports method chaining.
	 * 
	 * @author German Escobar
	 */
	protected class FluentMap<K,T> extends HashMap<K,T> {
		private static final long serialVersionUID = 1L;
		
		public FluentMap<K,T> set(K key, T value) {
			super.put(key, value);
			
			return this;
		}
		
	}
}
