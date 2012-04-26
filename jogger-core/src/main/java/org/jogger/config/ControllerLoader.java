package org.jogger.config;

import javax.servlet.ServletConfig;

import org.jogger.Controller;
import org.jogger.JoggerServlet;


/**
 * This interface is implemented by classes that provide a controller loading strategy. For example:
 * 
 * <ul>
 * 	<li>From the classpath (i.e. the {@DefaultControllerLoader}).</li>
 * 	<li>From a Spring Application Context.</li>
 * 	<li>From a Google Guice Module.</li>
 * </ul>
 * 
 * @author German Escobar
 */
public interface ControllerLoader {

	/**
	 * This method is called from the {@link JoggerServlet} to initialize the class.
	 * 
	 * @param servletConfig the servlet configuration.
	 */
	void init(ServletConfig servletConfig);
	
	/**
	 * Returns a controller based on the received argument. 
	 * 
	 * @param controllerName can be the name of a class, a Spring bean or any other string that
	 * identifies a {@link Controller} implementation uniquely.
	 * 
	 * @return an {@link Controller} implementation or null if the controller is not found.
	 * 
	 * @throws Exception we will catch any exception in the {@link JoggerServlet} to show it as
	 * a status 500.
	 */
	Controller load(String controllerName) throws Exception;
	
}
