package org.jogger.template;

import java.io.Writer;
import java.util.Map;

/**
 * Provides an abstraction for loading and rendering templates.
 *
 * @author German Escobar
 */
public interface TemplateEngine {

	/**
	 * Renders the <code>templateName</code> to the <code>writer</code> passing the <code>root</code> map to the
	 * template.
	 *
	 * @param templateName the name of the template to render.
	 * @param root a Map of properties to pass to the template.
	 * @param writer where we will write the output of the rendering.
	 *
	 * @throws TemplateException wraps any exception thrown by the template engine implementation.
	 */
	void render(String templateName, Map<String,Object> root, Writer writer) throws TemplateException;

}
