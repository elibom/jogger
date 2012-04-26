package org.jogger.config;

import javax.servlet.ServletConfig;

import org.jogger.Controller;

/**
 * This is the default controller loading strategy. It loads the controller from the classpath using the current 
 * Java loader.
 * 
 * @author German Escobar
 */
public class DefaultControllerLoader implements ControllerLoader {
	
	public static final String BASE_PACKAGE_INIT_PARAM_NAME = "basePackage";
	
	/**
	 * Stores the base package used to load the controller.
	 */
	private String basePackage = "";

	@Override
	public void init(ServletConfig servletConfig) {
		
		// retrieve the base package from the init parameter
		String basePackage = servletConfig.getInitParameter(BASE_PACKAGE_INIT_PARAM_NAME);	
		if (basePackage != null) {
			this.basePackage = basePackage + ".";
		}
		
	}

	@Override
	public Controller load(String controllerName) throws Exception {
		
		String className = basePackage + controllerName;
		
		// load the controller class and instantiate it
		Class<? extends Controller> controllerClass = Class.forName(className).asSubclass(Controller.class);
		return controllerClass.newInstance();
		
	}

}
