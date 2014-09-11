package com.elibom.jogger.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * A {@link TemplateEngine} implementation for <a href="http://freemarker.sourceforge.net/">FreeMarker</a>.
 *
 * @author German Escobar
 */
public class FreemarkerTemplateEngine implements TemplateEngine {

	private Configuration freeMarker;

	public FreemarkerTemplateEngine() {
		this.freeMarker = new Configuration();
	}

	public FreemarkerTemplateEngine(Configuration freeMarker) {
		this.freeMarker = freeMarker;
	}

	@Override
	public void render(String template, Map<String, Object> root, Writer writer) throws TemplateException {
		try {
			Template tmpl = freeMarker.getTemplate(template);
			tmpl.process(root, writer);
		} catch (IOException e) {
			throw new TemplateException(e);
		} catch (freemarker.template.TemplateException e) {
			throw new TemplateException(e.getMessage(), e);
		}
	}

	public Configuration getFreeMarker() {
		return freeMarker;
	}

	public void setFreeMarker(Configuration freeMarker) {
		this.freeMarker = freeMarker;
	}

}
