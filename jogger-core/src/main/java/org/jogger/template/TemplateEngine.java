package org.jogger.template;

import java.io.Writer;
import java.util.Map;

public interface TemplateEngine {

	void render(String templateName, Map<String,Object> root, Writer writer) throws TemplateException;

}
