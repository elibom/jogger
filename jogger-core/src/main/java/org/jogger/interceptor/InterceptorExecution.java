package org.jogger.interceptor;


/**
 * Used by {@link Interceptor} objects to proceed with the execution of the request. 
 * 
 * @author German Escobar
 */
public interface InterceptorExecution {

	/**
	 * Causes the next interceptor (or controller if there are no more interceptors) to be invoked.
	 * 
	 * @throws Exception
	 */
	void proceed() throws Exception;
	
	/**
	 * Retrieves the controller. Useful to retrieve information from the controller.
	 * 
	 * @return a {@link Controller} implementation.
	 */
	Controller getController();
	
	/**
	 * Retrieves the action. Useful to retrieve information from the action.
	 * 
	 * @return a {@link Action} implementation.
	 */
	Action getAction();
	
}
