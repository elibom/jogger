package org.jogger.routes;


public class FileRoutesLoader extends AbstractFileRoutesLoader {
	
	private String basePackage;
	
	public FileRoutesLoader() {
		this("");
	}
	
	public FileRoutesLoader(String basePackage) {
		this.basePackage = basePackage;
		
		if (this.basePackage != null && !"".equals(this.basePackage)) {
			if (!this.basePackage.endsWith(".")) {
				this.basePackage += ".";
			}
		}
	}

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
