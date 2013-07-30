package org.jogger.template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JadeTemplateEngineTest {

	@Test
	public void shouldRenderExistingTemplate() throws Exception {
		JadeTemplateEngine templateEngine = new JadeTemplateEngine();

		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", "This is a test");

		StringWriter writer = new StringWriter();
		templateEngine.render("src/test/resources/templates/jade/template.jade", root, writer);

		Assert.assertEquals(writer.toString(), "<body>This is a test</body>");
	}

	@Test(expectedExceptions=TemplateException.class)
	public void shouldFailIfTemplateNotFound() throws Exception {
		JadeTemplateEngine templateEngine = new JadeTemplateEngine();
		templateEngine.render("not/existing/template", null, null);
	}

	@Test(expectedExceptions=TemplateException.class)
	public void shouldFailWithTemplateError() throws Exception {
		JadeTemplateEngine templateEngine = new JadeTemplateEngine();
		StringWriter writer = new StringWriter();
		templateEngine.render("src/test/resources/templates/jade/invalid.jade", null, writer);
	}
}
