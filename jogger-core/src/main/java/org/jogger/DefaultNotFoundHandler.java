package org.jogger;

import org.jogger.http.Request;
import org.jogger.http.Response;

public class DefaultNotFoundHandler implements NotFoundHandler {

	@Override
	public void handle(Request request, Response response) {
		response.notFound();
		renderNotFound(request, response);
	}
	
	private void renderNotFound(Request request, Response response) {
		
		String accept = request.getHeader("Accept");
		
		if (accept == null) {
			response.write("Not Found");
			return;
		}
		
		if (accept.toLowerCase().contains("html")) {
			response.write("<h1>Not Found</h1><p>The page doesn't exists!</p>");
		} else if (accept.toLowerCase().contains("json")) {
			response.write("{ \"message\": \"The resource doesn't exists\" }");
		} else {
			response.write("Not Found");
		}
	}

}
