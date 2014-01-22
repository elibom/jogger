package org.jogger;

/**
 * An alternative way of creating the array of middlewares that are going to be used by {@link Jogger}. Useful in development 
 * mode because {@link Jogger} will reload the middleware list on each request.
 * 
 * @author German Escobar
 */
public interface MiddlewaresFactory {

	/**
	 * Called by {@link Jogger} to retrieve the middleware list used to handle requests. Should create a new instance of each 
	 * middleware every time it is called.
	 * 
	 * @return an array of {@link Middleware} instances.
	 */
	Middleware[] create();
	
}
