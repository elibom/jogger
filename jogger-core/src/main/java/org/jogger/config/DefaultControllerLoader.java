package org.jogger.config;

import javax.servlet.ServletConfig;

/**
 * This is the default controller loading strategy. It loads the controller from the classpath using the current 
 * Java loader.
 * 
 * @author German Escobar
 */
public class DefaultControllerLoader implements ControllerLoader {
	
	public static final String BASE_PACKAGE_INIT_PARAM_NAME = "basePackage";
	
	/**
	 * The class loader we are using to load the class.
	 */
	private ClassLoader classLoader = getClass().getClassLoader();
	
	/**
	 * Stores the base package used to load the controller.
	 */
	private String basePackage = "";

	@Override
	public void init(ServletConfig servletConfig) {
		
		// retrieve the base package from the init parameter
		String bp = servletConfig.getInitParameter(BASE_PACKAGE_INIT_PARAM_NAME);	
		if (bp != null) {
			this.basePackage = bp + ".";
		}
		
	}

	@Override
	public Object load(String controllerName) throws ConfigurationException {
		
		String className = basePackage + controllerName;
		
		try {
			
			// load the controller class and instantiate it
			Class<?> controllerClass = classLoader.loadClass(className);
			return controllerClass.newInstance();
			
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
		
	}

	public void setClassLoader(ClassLoader classLoader) {
		
		if (classLoader == null) {
			throw new IllegalStateException("classLoader cannot be null");
		}
		
		this.classLoader = classLoader;
	}
	
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage + ".";
	}

}
