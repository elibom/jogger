package org.jogger.middleware.router.loader;

import org.jogger.middleware.router.RoutesException;

/**
 * Implementations of this interface provide mechanism to load controllers.
 *
 * @author German Escobar
 */
public interface ControllerLoader {

	/**
	 * Loads the controller with the specified <code>controllerName</code>. It is up to the concrete
	 * implementation to decide how to load the controller and what does <code>controllerName</code> represents.
	 *
	 * @param controllerName the name of the controller.
	 *
	 * @return an Object that represents the controller.
	 * @throws RoutesException if the controller is not found or there is a problem instantiating it.
	 */
	Object load(String controllerName) throws RoutesException;

}
