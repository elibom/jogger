package org.jogger.test.spring;

import org.jogger.config.ControllerLoader;
import org.jogger.config.spring.SpringControllerLoader;
import org.jogger.test.JoggerTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This is a utility class that you extend when testing Jogger applications using Spring. It initializes the Spring 
 * application context before the suite starts and destroys it at the end. 
 * 
 * @author German Escobar
 */
public abstract class SpringJoggerTest extends JoggerTest {
	
	private static ConfigurableApplicationContext springContext;
	
	@BeforeSuite
	public void init() {
		System.setProperty("JOGGER_ENV", "test");
		springContext = new FileSystemXmlApplicationContext( getConfigLocations() );
	}
	
	@AfterSuite
	public void destroy() {	
		System.clearProperty("JOGGER_ENV");
		springContext.close();
	}
	
	protected ApplicationContext getSpringContext() {
		return springContext;
	}

	/**
	 * We dont need this method
	 */
	@Override
	protected String getBasePackage() {
		return null;
	}

	@Override
	protected ControllerLoader getControllerLoader() {
		
		SpringControllerLoader controllerLoader = new SpringControllerLoader();
		controllerLoader.setApplicationContext(springContext);
		
		return controllerLoader;
	}
	
	protected abstract String[] getConfigLocations();

}
