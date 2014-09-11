package com.elibom.jogger.template;

/**
 * Wraps any other exception thrown by {@link TemplateEngine} implementations. It extends from
 * <code>java.lang.RuntimeException</code> because we don't want to have to throw the exception in every controller
 * action that renders templates. Besides, an exception in a template is actually a <em>runtime exception</em>.
 *
 * @author German Escobar
 */
public class TemplateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TemplateException() {
		super();
	}

	public TemplateException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateException(String message) {
		super(message);
	}

	public TemplateException(Throwable cause) {
		super(cause);
	}

}
