package ${package};

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jogger.Jogger;
import org.jogger.JoggerFactory;
import org.jogger.RouteHandler;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.template.FreemarkerTemplateEngine;

import freemarker.template.Configuration;

public class ApplicationFactory implements JoggerFactory {

	@Override
	public Jogger configure() throws Exception {
		Jogger app = new Jogger();

		// configure freemarker
		Configuration freemarker = new Configuration();
		freemarker.setDirectoryForTemplateLoading(new File("templates/"));
		app.setTemplateEngine(new FreemarkerTemplateEngine(freemarker));

		// routes
		app.get("/", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				Map<String,Object> root = new HashMap<String,Object>();
				root.put("var", "Hello World");
				response.render("hello.ftl", root);
			}
		});
		app.get("/hello", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				// this is just another example
				response.write("<h1>Hello World</h1>");
			}
		});
		
		return app;
	}

}
