package org.jogger.routes;

import org.jogger.RoutesException;

/**
 * A concrete implementation of {@link AbstractFileRoutesLoader} that loads the controller using the default mechanism 
 * for loading classes. Users can specified a <code>basePackage</code> to avoid repeating the package in the routes 
 * file.
 * 
 * @author German Escobar
 */
public class FileRoutesLoader extends AbstractFileRoutesLoader {
	
	private String basePackage;
	
	/**
	 * Constructor. Initializes the object with an empty <code>basePackage</code>.
	 */
	public FileRoutesLoader() {
		this("");
	}
	
	/**
	 * Constructor. Initializes the object with the specified <code>basePackage</code>.
	 * @param basePackage
	 */
	public FileRoutesLoader(String basePackage) {
		this.basePackage = basePackage;
		
		if (this.basePackage != null && !"".equals(this.basePackage)) {
			if (!this.basePackage.endsWith(".")) {
				this.basePackage += ".";
			}
		}
	}

	@Override
	protected Object loadController(String controllerName) throws RoutesException {
		String className = basePackage + controllerName;
		
		try {
			// load the controller class and instantiate it
			Class<?> controllerClass = getClass().getClassLoader().loadClass(className);
			return controllerClass.newInstance();
		} catch (Exception e) {
			throw new RoutesException(e);
		}
	}
 
	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
}
