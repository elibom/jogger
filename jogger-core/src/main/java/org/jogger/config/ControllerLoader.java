package org.jogger.config;

import javax.servlet.ServletConfig;


/**
 * This interface is implemented by classes that provide a controller loading strategy. For example:
 * 
 * <ul>
 * 	<li>From the classpath (i.e. the {@link DefaultControllerLoader}).</li>
 * 	<li>From a Spring Application Context.</li>
 * 	<li>From a Google Guice Module.</li>
 * </ul>
 * 
 * @author German Escobar
 */
public interface ControllerLoader {

	/**
	 * This method is called to initialize the class.
	 * 
	 * @param servletConfig the servlet configuration.
	 */
	void init(ServletConfig servletConfig);
	
	/**
	 * Returns a controller based on the received argument. 
	 * 
	 * @param controllerName can be the name of a class, a Spring bean or any other string that
	 * identifies a controller uniquely.
	 * 
	 * @return a controller object or null if the controller is not found.
	 * 
	 * @throws ConfigurationException if something goes wrong loading the controller.
	 */
	Object load(String controllerName) throws ConfigurationException;
	
}
