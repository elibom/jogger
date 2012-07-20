package org.jogger.interceptor;

import java.lang.annotation.Annotation;

/**
 * This interface provides access to some features of the controller to an {@link org.jogger.interceptor.Interceptor} 
 * implementation.
 * 
 * @author German Escobar
 */
public interface Controller {

	/**
	 * Retrieves an annotation from the controller if the annotation is present.
	 * 
	 * @param <A> the type of the annotation to be retrieved.
	 * @param annotation the class of the annotation to be retrieved.
	 * 
	 * @return a java.lang.annotation.Annotation object or null if not found.
	 */
	<A extends Annotation> A getAnnotation(Class<A> annotationClass);
	
}
