package org.jogger.template;

import java.io.Writer;
import java.util.Map;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.JadeTemplate;

/**
 * A {@link TemplateEngine} implementation for <a href="https://github.com/neuland/jade4j">Jade</a>.
 *
 * @author German Escobar
 */
public class JadeTemplateEngine implements TemplateEngine {

	private JadeConfiguration jadeConfig;

	public JadeTemplateEngine() {
		this(new JadeConfiguration());
	}

	public JadeTemplateEngine(JadeConfiguration jadeConfig) {
		this.jadeConfig = jadeConfig;
	}

	@Override
	public void render(String templateName, Map<String, Object> root, Writer writer) throws TemplateException {
		try {
			JadeTemplate template = jadeConfig.getTemplate(templateName);
			jadeConfig.renderTemplate(template, root, writer);
		} catch(Exception e) {
			throw new TemplateException(e);
		}
	}

}
