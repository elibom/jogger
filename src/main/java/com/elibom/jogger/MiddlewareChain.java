package com.elibom.jogger;

/**
 * Used in the {@link Middleware#handle(...)} method to call the next middleware registered in {@link Jogger}.
 * 
 * @author German Escobar
 */
public interface MiddlewareChain {

	/**
	 * Calls the next middleware.
	 * 
	 * @throws Exception
	 */
	void next() throws Exception;
	
}
