package org.jogger.interceptor;

import java.lang.annotation.Annotation;

/**
 * This interface provides access to some features of an action to an {@link org.jogger.interceptor.Interceptor} 
 * implementation.
 * 
 * @author German Escobar
 */
public interface Action {

	/**
	 * Retrieves an annotation from the action if the annotation is present.
	 * 
	 * @param <A> the type of the annotation to be retrieved.
	 * @param annotation the class of the annotation to be retrieved.
	 * 
	 * @return a java.lang.annotation.Annotation object or null if not found.
	 */
	<A extends Annotation> A getAnnotation(Class<A> annotation);
}
