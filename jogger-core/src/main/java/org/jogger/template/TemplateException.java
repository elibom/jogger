package org.jogger.template;

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
