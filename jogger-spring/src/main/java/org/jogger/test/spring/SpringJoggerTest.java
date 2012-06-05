package org.jogger.test.spring;

import org.jogger.config.ControllerLoader;
import org.jogger.config.Interceptors;
import org.jogger.config.spring.SpringControllerLoader;
import org.jogger.config.spring.SpringInterceptors;
import org.jogger.test.JoggerTest;
import org.jogger.test.MockJoggerServlet;
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
	public void init() throws Exception {
		springContext = new FileSystemXmlApplicationContext( getConfigLocations() );
	}
	
	@AfterSuite
	public void destroy() throws Exception {	
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
	protected MockJoggerServlet getJoggerServlet() {
		
		Interceptors interceptors = getInterceptors();
		if (SpringInterceptors.class.isInstance(interceptors)) {
			SpringInterceptors springInterceptors = (SpringInterceptors) interceptors;
			springInterceptors.setApplicationContext(springContext);
		}
		
		MockJoggerServlet joggerServlet = new MockJoggerServlet();
		joggerServlet.setInterceptors(interceptors);
		
		return joggerServlet;
		
	}

	@Override
	protected ControllerLoader getControllerLoader() {
		
		SpringControllerLoader controllerLoader = new SpringControllerLoader();
		controllerLoader.setApplicationContext(springContext);
		
		return controllerLoader;
	}
	
	protected abstract String[] getConfigLocations();

}
