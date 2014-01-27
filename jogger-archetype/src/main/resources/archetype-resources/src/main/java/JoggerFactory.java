package ${package};

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jogger.Jogger;
import org.jogger.middleware.router.RouterMiddleware;
import org.jogger.middleware.statik.StaticMiddleware;
import org.jogger.http.Request;
import org.jogger.http.Response;
import org.jogger.middleware.router.RouteHandler;
import org.jogger.template.FreemarkerTemplateEngine;

import freemarker.template.Configuration;

public class JoggerFactory {

	public static Jogger create() throws Exception {
		// configure freemarker
		Configuration freemarker = new Configuration();
		freemarker.setDirectoryForTemplateLoading(new File("templates/"));
		
		// routes
		RouterMiddleware router = new RouterMiddleware();
		router.get("/", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				Map<String,Object> root = new HashMap<String,Object>();
				root.put("var", "Hello World");
				response.render("hello.ftl", root);
			}
		});
		router.get("/hello", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				// this is just another example
				response.write("<h1>Hello World</h1>");
			}
		});
		
		// create the app
		Jogger app = new Jogger(new StaticMiddleware("assets"), router);
		app.setTemplateEngine(new FreemarkerTemplateEngine(freemarker));
		
		return app;
	}
}
