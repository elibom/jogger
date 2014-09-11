package com.elibom.jogger.template;

import com.elibom.jogger.template.TemplateException;
import com.elibom.jogger.template.FreemarkerTemplateEngine;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FreemarkerTemplateEngineTest {

	@Test
	public void shouldRenderExistingTemplate() throws Exception {
		FreemarkerTemplateEngine templateEngine = new FreemarkerTemplateEngine();

		Map<String,Object> root = new HashMap<String,Object>();
		root.put("title", "This is a test");

		StringWriter writer = new StringWriter();
		templateEngine.render("src/test/resources/templates/freemarker/template.ftl", root, writer);

		Assert.assertEquals(writer.toString(), "Title: This is a test");
	}

	@Test(expectedExceptions=TemplateException.class)
	public void shouldFailIfTemplateNotFound() throws Exception {
		FreemarkerTemplateEngine templateEngine = new FreemarkerTemplateEngine();
		templateEngine.render("not/existing/template", null, null);
	}

	@Test(expectedExceptions=TemplateException.class)
	public void shouldFailWithTemplateError() throws Exception {
		FreemarkerTemplateEngine templateEngine = new FreemarkerTemplateEngine();

		Map<String,Object> root = new HashMap<String,Object>();
		StringWriter writer = new StringWriter();
		templateEngine.render("src/test/resources/templates/freemarker/template.ftl", root, writer);
	}

}
