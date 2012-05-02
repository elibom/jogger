package ${package}.controllers;

import org.jogger.http.Request;
import org.jogger.http.Response;

public class Pages {

	public void index(Request request, Response response) {
		
		response.render("index.ftl");
		
		// instead of a template, you can do one of the following:
		// response.print("This is a <strong>test</strong>");
		// response.notFound();
		// response.unathorized();
		// response.redirect("/otherpage");
		// and many more (including cookies, headers, etc. manipulation)
		
	}
	
}
