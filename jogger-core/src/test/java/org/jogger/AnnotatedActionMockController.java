package org.jogger;

import org.jogger.http.Request;
import org.jogger.http.Response;

/**
 * Class used for testing by {@link JoggerServletTest}.
 * 
 * @author German Escobar
 */
public class AnnotatedActionMockController {

	@MockAnnotation
	public void action(Request request, Response response) {}
	
}
