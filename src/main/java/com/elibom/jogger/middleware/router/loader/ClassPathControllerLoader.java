package com.elibom.jogger.middleware.router.loader;

import com.elibom.jogger.middleware.router.RoutesException;

/**
 * A concrete implementation of {@link ControllerLoader} that loads controllers from the classpath using a
 * <code>ClassLoader</code>. You can specified a <code>basePackage</code> to avoid repeating the package in the routes
 * file.
 *
 * @author German Escobar
 */
public class ClassPathControllerLoader implements ControllerLoader {

	private String basePackage;

	private ClassLoader classLoader = ClassPathControllerLoader.class.getClassLoader();

	/**
	 * Constructor. Initializes the object with an empty <code>basePackage</code>.
	 */
	public ClassPathControllerLoader() {
		this("");
	}

	/**
	 * Constructor. Initializes the object with the specified <code>basePackage</code>.
	 * @param basePackage
	 */
	public ClassPathControllerLoader(String basePackage) {
		this.basePackage = basePackage;

		if (this.basePackage != null && !"".equals(this.basePackage)) {
			if (!this.basePackage.endsWith(".")) {
				this.basePackage += ".";
			}
		}
	}

	@Override
	public Object load(String controllerName) throws RoutesException {
		String className = basePackage + controllerName;

		try {
			// load the controller class and instantiate it
			Class<?> controllerClass = classLoader.loadClass(className);
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

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
