package com.elibom.jogger.middleware.router.loader;

import com.elibom.jogger.middleware.router.RoutesException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A concrete implementation of {@link ControllerLoader} that loads controllers from a Spring context using an
 * <code>ApplicationContext</code>. Notice that this class implements <code>ApplicationContextAware</code>, so if you
 * configure this class as a Spring bean it will have access to that <code>ApplicatonContext</code>.
 *
 * @author German Escobar
 */
public class SpringControllerLoader implements ControllerLoader, ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public Object load(String controllerName) throws RoutesException {
		Object bean = applicationContext.getBean(controllerName);
		if (bean == null) {
			throw new RoutesException("Bean '" + controllerName + "' was not found.");
		}

		return bean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
